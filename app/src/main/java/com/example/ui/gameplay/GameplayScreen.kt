package com.example.ui.gameplay

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import com.example.ads.BannerAdView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdManager
import com.example.audio.SoundManager
import com.example.data.repository.GameRepository
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurface
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GameplayScreen(
    levelNumber: Int,
    repository: GameRepository,
    onNavigateBack: () -> Unit,
    onNavigateToNextLevel: (Int) -> Unit,
    onNavigateToGrandFinalVictory: () -> Unit,
    viewModel: GameplayViewModel = androidx.lifecycle.viewmodel.compose.viewModel {
        GameplayViewModel(repository, levelNumber)
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val progress by repository.progress.collectAsState()
    val activity = LocalContext.current as? Activity

    BackHandler {
        onNavigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PuzzleDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Bar
            GameplayTopBar(
                levelNumber = uiState.levelNumber,
                isGrandFinal = uiState.isGrandFinal,
                coins = progress.totalCoins,
                moveCount = uiState.moveCount,
                onBack = {
                    SoundManager.playButtonClick()
                    onNavigateBack()
                }
            )

            // Center: Tubes Puzzle Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val totalTubes = uiState.tubes.size
                    val isTwoRows = totalTubes > 5

                    val columns = if (isTwoRows) {
                        (totalTubes + 1) / 2
                    } else {
                        totalTubes.coerceAtLeast(3)
                    }

                    // Dynamically calculate responsive dimensions
                    val availableWidth = maxWidth - 16.dp
                    val calculatedTubeWidth = ((availableWidth / columns) - 12.dp).coerceIn(40.dp, 64.dp)
                    val calculatedBallSize = (calculatedTubeWidth - 10.dp).coerceIn(30.dp, 50.dp)
                    val calculatedTubeHeight = (calculatedBallSize * 4 + 20.dp).coerceIn(140.dp, 210.dp)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    ) {
                        items(uiState.tubes, key = { it.id }) { tube ->
                            val isSelected = uiState.selectedTubeId == tube.id
                            val isHintSrc = uiState.hintMove?.fromTubeId == tube.id
                            val isHintDst = uiState.hintMove?.toTubeId == tube.id

                            TubeView(
                                tube = tube,
                                isSelected = isSelected,
                                isHintSource = isHintSrc,
                                isHintTarget = isHintDst,
                                tubeWidth = calculatedTubeWidth,
                                tubeHeight = calculatedTubeHeight,
                                ballSize = calculatedBallSize,
                                onTubeClicked = { viewModel.onTubeClicked(it) }
                            )
                        }
                    }
                }
            }

            // Bottom Controls Bar
            GameplayControlsBar(
                undoAvailable = uiState.undoAvailable,
                hintsCount = progress.hintsInventory,
                extraTubesCount = progress.extraTubesInventory,
                isExtraTubeAdded = uiState.extraTubeAdded,
                isCalculatingHint = uiState.isCalculatingHint,
                onUndo = {
                    SoundManager.playButtonClick()
                    viewModel.undo()
                },
                onHint = {
                    SoundManager.playButtonClick()
                    viewModel.requestHint()
                },
                onExtraTube = {
                    SoundManager.playButtonClick()
                    viewModel.addExtraTube()
                },
                onWatchAdForReward = {
                    SoundManager.playButtonClick()
                    activity?.let { act ->
                        AdManager.showRewarded(
                            activity = act,
                            onUserEarnedReward = { _: Int, _: String ->
                                repository.addHintFromReward(1)
                                repository.addExtraTubeFromReward(1)
                                SoundManager.playCoinSound()
                                Toast.makeText(act, "Reward Received: +1 Hint & +1 Extra Tube! 🎁", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                onRestart = {
                    SoundManager.playButtonClick()
                    viewModel.showRestartConfirmation(true)
                }
            )

            // Real Banner Ad during gameplay
            BannerAdView(modifier = Modifier.padding(top = 4.dp))
        }

        // Restart Confirmation Dialog
        if (uiState.showRestartDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showRestartConfirmation(false) },
                title = { Text("Restart Level?") },
                text = { Text("Are you sure you want to reset this puzzle back to the initial state?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.showRestartConfirmation(false)
                            viewModel.restartLevel()
                        },
                        modifier = Modifier.testTag("confirm_restart_button")
                    ) {
                        Text("Restart", color = AccentEmerald, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showRestartConfirmation(false) }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // Win Dialog / Grand Final Victory trigger
        if (uiState.isGameWon) {
            if (uiState.isGrandFinal) {
                // Navigate immediately to Grand Final Victory flow
                onNavigateToGrandFinalVictory()
            } else {
                WinDialog(
                    levelNumber = uiState.levelNumber,
                    movesTaken = uiState.moveCount,
                    coinsEarned = uiState.earnedCoinsOnWin,
                    onNextLevel = {
                        activity?.let { act ->
                            // Show interstitial with frequency cooldown
                            if (uiState.levelNumber % 3 == 0) {
                                AdManager.showInterstitial(act)
                            }
                        }
                        onNavigateToNextLevel(uiState.levelNumber + 1)
                    },
                    onMap = { onNavigateBack() },
                    onReplay = { viewModel.restartLevel() }
                )
            }
        }
    }
}

@Composable
private fun GameplayTopBar(
    levelNumber: Int,
    isGrandFinal: Boolean,
    coins: Int,
    moveCount: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("gameplay_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }

        // Level title + Grand Final badge
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isGrandFinal) {
                Text(
                    text = "🏆 GRAND FINAL 🏆",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentGold
                )
            }
            Text(
                text = "Level $levelNumber",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isGrandFinal) AccentGold else TextPrimary
            )
            Text(
                text = "Moves: $moveCount",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Coins Chip
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PuzzleSurfaceVariant,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(text = "🪙", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$coins",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                )
            }
        }
    }
}

@Composable
private fun GameplayControlsBar(
    undoAvailable: Boolean,
    hintsCount: Int,
    extraTubesCount: Int,
    isExtraTubeAdded: Boolean,
    isCalculatingHint: Boolean,
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onExtraTube: () -> Unit,
    onWatchAdForReward: () -> Unit,
    onRestart: () -> Unit
) {
    Surface(
        color = PuzzleSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo Button
            ControlButton(
                icon = Icons.Default.Undo,
                label = "Undo",
                badge = null,
                enabled = undoAvailable,
                testTag = "undo_button",
                onClick = onUndo
            )

            // Hint Button
            ControlButton(
                icon = Icons.Default.Lightbulb,
                label = "Hint",
                badge = if (hintsCount > 0) "$hintsCount" else "25🪙",
                enabled = !isCalculatingHint,
                isLoading = isCalculatingHint,
                testTag = "hint_button",
                onClick = onHint
            )

            // Extra Tube Button
            ControlButton(
                icon = Icons.Default.Add,
                label = "+Tube",
                badge = if (extraTubesCount > 0) "$extraTubesCount" else "50🪙",
                enabled = !isExtraTubeAdded,
                testTag = "extra_tube_button",
                onClick = onExtraTube
            )

            // Free Ad Button (Real Rewarded Ad)
            ControlButton(
                icon = Icons.Default.PlayCircle,
                label = "Free Ad",
                badge = "🎁",
                enabled = true,
                testTag = "watch_rewarded_ad_button",
                onClick = onWatchAdForReward
            )

            // Restart Button
            ControlButton(
                icon = Icons.Default.Refresh,
                label = "Restart",
                badge = null,
                enabled = true,
                testTag = "restart_button",
                onClick = onRestart
            )
        }
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    badge: String?,
    enabled: Boolean,
    testTag: String,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            IconButton(
                onClick = onClick,
                enabled = enabled,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (enabled) PuzzleSurfaceVariant else PuzzleSurfaceVariant.copy(alpha = 0.4f))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = AccentGold
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (enabled) TextPrimary else TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryIndigo)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (enabled) TextSecondary else TextSecondary.copy(alpha = 0.5f)
        )
    }
}
