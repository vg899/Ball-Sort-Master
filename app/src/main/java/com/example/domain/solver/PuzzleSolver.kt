package com.example.domain.solver

import com.example.domain.model.BallColor
import com.example.domain.model.GameMove
import com.example.domain.model.Tube
import java.util.ArrayDeque

object PuzzleSolver {

    /**
     * Compact state representation for fast hashing and visited-state tracking.
     */
    private data class State(
        val tubes: List<List<BallColor>>,
        val parent: State? = null,
        val moveLeadingHere: GameMove? = null
    ) {
        val hashKey: String by lazy {
            tubes.joinToString(";") { tube ->
                tube.joinToString(",") { it.id.toString() }
            }
        }

        fun isSolved(capacity: Int): Boolean {
            return tubes.all { tube ->
                tube.isEmpty() || (tube.size == capacity && tube.all { it == tube[0] })
            }
        }
    }

    /**
     * Finds the next best move (hint) from the current list of tubes.
     * Uses Breadth-First Search with visited state memoization up to a max step limit.
     */
    fun findHintMove(tubes: List<Tube>, maxSearchNodes: Int = 15_000): GameMove? {
        val capacity = tubes.firstOrNull()?.capacity ?: 4
        val initialTubes = tubes.map { it.balls }
        val initialState = State(initialTubes)

        if (initialState.isSolved(capacity)) return null

        val queue = ArrayDeque<State>()
        val visited = HashSet<String>()

        queue.add(initialState)
        visited.add(initialState.hashKey)

        var nodesExplored = 0
        var winningState: State? = null

        while (queue.isNotEmpty() && nodesExplored < maxSearchNodes) {
            val current = queue.poll() ?: break
            nodesExplored++

            if (current.isSolved(capacity)) {
                winningState = current
                break
            }

            // Generate successor moves
            val validMoves = getPossibleMoves(current.tubes, capacity, tubes)
            for (move in validMoves) {
                val nextTubes = applyMove(current.tubes, move.fromTubeId, move.toTubeId)
                val nextState = State(nextTubes, parent = current, moveLeadingHere = move)
                if (visited.add(nextState.hashKey)) {
                    queue.add(nextState)
                }
            }
        }

        // Trace back the first move that leads toward solution
        if (winningState != null) {
            var curr = winningState
            while (curr?.parent != null && curr.parent != initialState) {
                curr = curr.parent
            }
            return curr?.moveLeadingHere
        }

        // Fallback: heuristic greedy useful move if search limit reached
        return getHeuristicBestMove(tubes)
    }

    private fun getPossibleMoves(tubes: List<List<BallColor>>, capacity: Int, originalTubes: List<Tube>): List<GameMove> {
        val moves = mutableListOf<GameMove>()
        for (i in tubes.indices) {
            val fromTube = tubes[i]
            if (fromTube.isEmpty()) continue
            // Skip moving from a tube that is already completed
            if (fromTube.size == capacity && fromTube.all { it == fromTube[0] }) continue

            val topBall = fromTube.last()

            for (j in tubes.indices) {
                if (i == j) continue
                val toTube = tubes[j]
                if (toTube.size >= capacity) continue

                // Can place if destination is empty OR top ball matches
                if (toTube.isEmpty() || toTube.last() == topBall) {
                    // Do not move from an all-uniform tube to an empty tube (wasteful)
                    if (toTube.isEmpty() && fromTube.all { it == topBall }) continue

                    moves.add(
                        GameMove(
                            fromTubeId = originalTubes[i].id,
                            toTubeId = originalTubes[j].id,
                            ballColor = topBall
                        )
                    )
                }
            }
        }
        return moves
    }

    private fun applyMove(tubes: List<List<BallColor>>, fromId: Int, toId: Int): List<List<BallColor>> {
        return tubes.mapIndexed { idx, balls ->
            when (idx) {
                fromId -> balls.dropLast(1)
                toId -> balls + tubes[fromId].last()
                else -> balls
            }
        }
    }

    private fun getHeuristicBestMove(tubes: List<Tube>): GameMove? {
        // Find a move that places a ball onto a matching color in another non-empty tube
        for (from in tubes) {
            if (from.isEmpty) continue
            if (from.isSolved) continue
            val top = from.topBall ?: continue

            for (to in tubes) {
                if (to.id == from.id) continue
                if (!to.isFull && to.topBall == top) {
                    return GameMove(from.id, to.id, top)
                }
            }
        }

        // Otherwise move to an empty tube if not already uniform
        for (from in tubes) {
            if (from.isEmpty) continue
            val top = from.topBall ?: continue
            if (from.balls.all { it == top }) continue

            for (to in tubes) {
                if (to.id != from.id && to.isEmpty) {
                    return GameMove(from.id, to.id, top)
                }
            }
        }

        return null
    }
}
