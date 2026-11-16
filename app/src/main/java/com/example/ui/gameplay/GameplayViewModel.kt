package com.example.ui.gameplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.repository.GameRepository
import com.example.domain.level.LevelGenerator
import com.example.domain.model.GameMove
import com.example.domain.model.LevelPuzzle
import com.example.domain.model.Tube
import com.example.domain.solver.PuzzleSolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GameplayUiState(
    val levelNumber: Int = 1,
    val tubes: List<Tube> = emptyList(),
    val selectedTubeId: Int? = null,
    val moveCount: Int = 0,
    val undoAvailable: Boolean = false,
    val isGameWon: Boolean = false,
    val isGrandFinal: Boolean = false,
    val hintMove: GameMove? = null,
    val isCalculatingHint: Boolean = false,
    val extraTubeAdded: Boolean = false,
    val showRestartDialog: Boolean = false,
    val earnedCoinsOnWin: Int = 0
)

class GameplayViewModel(
    private val repository: GameRepository,
    private val levelNumber: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameplayUiState(levelNumber = levelNumber))
    val uiState: StateFlow<GameplayUiState> = _uiState.asStateFlow()

    private val moveHistory = mutableListOf<GameMove>()
    private var initialTubes: List<Tube> = emptyList()

    init {
        loadLevel(levelNumber)
    }

    fun loadLevel(level: Int) {
        val puzzle: LevelPuzzle = LevelGenerator.generateLevel(level)
        initialTubes = puzzle.tubes
        moveHistory.clear()

        _uiState.value = GameplayUiState(
            levelNumber = puzzle.levelNumber,
            tubes = puzzle.tubes,
            selectedTubeId = null,
            moveCount = 0,
            undoAvailable = false,
            isGameWon = false,
            isGrandFinal = puzzle.isGrandFinal,
            hintMove = null,
            extraTubeAdded = false
        )
    }

    fun onTubeClicked(tube: Tube) {
        val state = _uiState.value
        if (state.isGameWon) return

        val selectedId = state.selectedTubeId

        if (selectedId == null) {
            // Nothing currently selected: select this tube if it has balls
            if (tube.balls.isNotEmpty()) {
                SoundManager.playBallLift()
                _uiState.value = state.copy(
                    selectedTubeId = tube.id,
                    hintMove = null // clear hint highlight
                )
            }
        } else if (selectedId == tube.id) {
            // Tapped the same tube: deselect
            SoundManager.playBallDrop()
            _uiState.value = state.copy(selectedTubeId = null)
        } else {
            // Tapped a different tube: attempt to move ball
            val sourceTube = state.tubes.firstOrNull { it.id == selectedId }
            if (sourceTube != null && sourceTube.balls.isNotEmpty()) {
                val ballToMove = sourceTube.topBall!!

                if (tube.canAccept(ballToMove)) {
                    // Valid Move
                    val (updatedSource, ball) = sourceTube.removeTopBall()
                    val updatedTarget = tube.addBall(ball)

                    val newTubes = state.tubes.map {
                        when (it.id) {
                            updatedSource.id -> updatedSource
                            updatedTarget.id -> updatedTarget
                            else -> it
                        }
                    }

                    val move = GameMove(
                        fromTubeId = sourceTube.id,
                        toTubeId = tube.id,
                        ballColor = ballToMove
                    )
                    moveHistory.add(move)
                    SoundManager.playBallDrop()

                    val isWon = newTubes.all { it.isSolved }
                    val earnedCoins = if (isWon) {
                        when {
                            levelNumber % 100 == 0 -> 100
                            levelNumber % 10 == 0 -> 40
                            else -> 20
                        }
                    } else 0

                    if (isWon) {
                        if (state.isGrandFinal) {
                            SoundManager.playGrandFinalCelebration()
                        } else {
                            SoundManager.playLevelWin()
                        }
                        repository.completeLevel(levelNumber, state.moveCount + 1)
                    }

                    _uiState.value = state.copy(
                        tubes = newTubes,
                        selectedTubeId = null,
                        moveCount = state.moveCount + 1,
                        undoAvailable = true,
                        isGameWon = isWon,
                        hintMove = null,
                        earnedCoinsOnWin = earnedCoins
                    )
                } else {
                    // Cannot place on destination: if destination has balls, switch selection
                    if (tube.balls.isNotEmpty()) {
                        SoundManager.playBallLift()
                        _uiState.value = state.copy(selectedTubeId = tube.id)
                    } else {
                        SoundManager.playInvalidMove()
                        _uiState.value = state.copy(selectedTubeId = null)
                    }
                }
            } else {
                _uiState.value = state.copy(selectedTubeId = null)
            }
        }
    }

    fun undo() {
        val state = _uiState.value
        if (state.isGameWon || moveHistory.isEmpty()) return

        val lastMove = moveHistory.removeAt(moveHistory.size - 1)
        val sourceTube = state.tubes.firstOrNull { it.id == lastMove.toTubeId } ?: return
        val targetTube = state.tubes.firstOrNull { it.id == lastMove.fromTubeId } ?: return

        if (sourceTube.balls.isNotEmpty()) {
            val (updatedSource, ball) = sourceTube.removeTopBall()
            val updatedTarget = targetTube.addBall(ball)

            val newTubes = state.tubes.map {
                when (it.id) {
                    updatedSource.id -> updatedSource
                    updatedTarget.id -> updatedTarget
                    else -> it
                }
            }

            SoundManager.playBallDrop()
            _uiState.value = state.copy(
                tubes = newTubes,
                selectedTubeId = null,
                moveCount = maxOf(0, state.moveCount - 1),
                undoAvailable = moveHistory.isNotEmpty(),
                hintMove = null
            )
        }
    }

    fun requestHint() {
        val state = _uiState.value
        if (state.isGameWon || state.isCalculatingHint) return

        val hasHintsOrCoins = repository.consumeHint()
        if (!hasHintsOrCoins) return

        _uiState.value = state.copy(isCalculatingHint = true)
        viewModelScope.launch(Dispatchers.Default) {
            val hint = PuzzleSolver.findHintMove(state.tubes)
            withContext(Dispatchers.Main) {
                _uiState.value = _uiState.value.copy(
                    hintMove = hint,
                    isCalculatingHint = false
                )
            }
        }
    }

    fun addExtraTube() {
        val state = _uiState.value
        if (state.isGameWon || state.extraTubeAdded) return

        val hasExtraTubeOrCoins = repository.consumeExtraTube()
        if (!hasExtraTubeOrCoins) return

        val nextId = (state.tubes.maxOfOrNull { it.id } ?: 0) + 1
        val newTube = Tube(
            id = nextId,
            capacity = 4,
            balls = emptyList(),
            isExtra = true
        )
        _uiState.value = state.copy(
            tubes = state.tubes + newTube,
            extraTubeAdded = true
        )
        SoundManager.playButtonClick()
    }

    fun showRestartConfirmation(show: Boolean) {
        _uiState.value = _uiState.value.copy(showRestartDialog = show)
    }

    fun restartLevel() {
        loadLevel(levelNumber)
        SoundManager.playButtonClick()
    }
}
