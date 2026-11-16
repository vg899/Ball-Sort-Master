package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.repository.GameRepository
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    repository: GameRepository,
    onNavigateBack: () -> Unit,
    onViewPrivacy: () -> Unit,
    onViewTerms: () -> Unit
) {
    val progress by repository.progress.collectAsState()

    BackHandler {
        onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PuzzleDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    SoundManager.playButtonClick()
                    onNavigateBack()
                },
                modifier = Modifier.testTag("settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Audio Section
            Text(
                text = "AUDIO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGold,
                modifier = Modifier.padding(start = 4.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PuzzleSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    // Sound effects toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Sound", tint = TextPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Sound Effects", fontSize = 15.sp, color = TextPrimary)
                        }
                        Switch(
                            checked = progress.soundEnabled,
                            onCheckedChange = {
                                SoundManager.playButtonClick()
                                repository.setSoundEnabled(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryIndigo),
                            modifier = Modifier.testTag("sound_toggle")
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )

                    // Music toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = TextPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Ambient Music", fontSize = 15.sp, color = TextPrimary)
                        }
                        Switch(
                            checked = progress.musicEnabled,
                            onCheckedChange = {
                                SoundManager.playButtonClick()
                                repository.setMusicEnabled(it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryIndigo),
                            modifier = Modifier.testTag("music_toggle")
                        )
                    }
                }
            }

            // Statistics Section
            Text(
                text = "STATISTICS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGold,
                modifier = Modifier.padding(start = 4.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PuzzleSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatRow("Current Level", "Level ${progress.currentLevel}")
                    StatRow("Highest Level Solved", "Level ${progress.highestCompletedLevel}")
                    StatRow("Total Puzzles Completed", "${progress.totalPuzzlesSolved}")
                    StatRow("Total Moves Made", "${progress.totalMovesCount}")
                    StatRow("Coins", "🪙 ${progress.totalCoins}")
                }
            }

            // Legal & About Section
            Text(
                text = "ABOUT & LEGAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGold,
                modifier = Modifier.padding(start = 4.dp)
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PuzzleSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    NavigationItem(
                        icon = Icons.Default.PrivacyTip,
                        title = "Privacy Policy",
                        onClick = onViewPrivacy,
                        testTag = "settings_privacy_item"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                    NavigationItem(
                        icon = Icons.Default.Description,
                        title = "Grand Prize Terms & Conditions",
                        onClick = onViewTerms,
                        testTag = "settings_terms_item"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Version", tint = TextSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Ball Sort Master", fontSize = 15.sp, color = TextPrimary)
                            Text(text = "Version 1.0 • 1,000,000 Levels", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = TextSecondary)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun NavigationItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = TextPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontSize = 15.sp, color = TextPrimary)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Go",
            tint = TextSecondary,
            modifier = Modifier.size(14.dp)
        )
    }
}
