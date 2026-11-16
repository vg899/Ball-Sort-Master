package com.example.ui.home

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdManager
import com.example.ads.BannerAdView
import com.example.audio.SoundManager
import com.example.data.repository.GameRepository
import com.example.domain.model.BallColor
import com.example.ui.gameplay.BallView
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldLight
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurface
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    repository: GameRepository,
    onPlayLevel: (Int) -> Unit,
    onOpenLevelMap: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPrizeInfo: () -> Unit
) {
    val progress by repository.progress.collectAsState()
    val isDailyAvailable = remember(progress) { repository.checkDailyRewardAvailable() }
    val activity = LocalContext.current as? Activity

    var showDailyRewardDialog by remember { mutableStateOf(false) }
    var showGrandPrizeDialog by remember { mutableStateOf(!progress.grandPrizePopupShown) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PuzzleDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coins Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = PuzzleSurfaceVariant,
                    modifier = Modifier.testTag("home_coins_display")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "🪙", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${progress.totalCoins}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }
                }

                // Audio & Settings Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val newSound = !progress.soundEnabled
                            repository.setSoundEnabled(newSound)
                        },
                        modifier = Modifier.testTag("home_sound_button")
                    ) {
                        Icon(
                            imageVector = if (progress.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Sound toggle",
                            tint = if (progress.soundEnabled) AccentGold else TextSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            val newMusic = !progress.musicEnabled
                            repository.setMusicEnabled(newMusic)
                        },
                        modifier = Modifier.testTag("home_music_button")
                    ) {
                        Icon(
                            imageVector = if (progress.musicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = "Music toggle",
                            tint = if (progress.musicEnabled) PrimaryLight else TextSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            SoundManager.playButtonClick()
                            onOpenSettings()
                        },
                        modifier = Modifier.testTag("home_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextPrimary
                        )
                    }
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Branding Logo & Decorative Balls
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    // Decorative mini tubes with colored balls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        BallView(ballColor = BallColor.RED, size = 26.dp)
                        BallView(ballColor = BallColor.YELLOW, size = 32.dp)
                        BallView(ballColor = BallColor.BLUE, size = 36.dp)
                        BallView(ballColor = BallColor.GREEN, size = 32.dp)
                        BallView(ballColor = BallColor.PURPLE, size = 26.dp)
                    }

                    Text(
                        text = "BALL SORT",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "MASTER",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = AccentGold,
                        letterSpacing = 4.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PrimaryIndigo.copy(alpha = 0.25f),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            text = "1,000,000 LEVELS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryLight,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }

                // Grand Prize Promotion Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            SoundManager.playButtonClick()
                            onOpenPrizeInfo()
                        }
                        .testTag("grand_prize_banner_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF311042),
                                        Color(0xFF1E1B4B),
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .border(1.5.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(AccentGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Trophy",
                                    tint = Color(0xFF451A03),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🏆 BUMPER PRIZE CHALLENGE",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AccentGold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Complete all 1M levels to unlock the Grand Prize claim!",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Current Level Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PuzzleSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT LEVEL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                text = "Level ${progress.currentLevel}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "HIGHEST SOLVED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                text = "Level ${progress.highestCompletedLevel}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald
                            )
                        }
                    }
                }

                // Main CTA: PLAY / CONTINUE BUTTON
                val ctaText = if (progress.highestCompletedLevel == 0) "PLAY LEVEL 1" else "CONTINUE LEVEL ${progress.currentLevel}"
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        onPlayLevel(progress.currentLevel)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp))
                        .testTag("home_play_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ctaText,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Secondary Row: Level Map & Daily Reward
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Level Map Button
                    OutlinedButton(
                        onClick = {
                            SoundManager.playButtonClick()
                            onOpenLevelMap()
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("home_map_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = "Map", tint = TextPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "LEVEL MAP", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    // Daily Reward Button with notification badge
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = {
                                SoundManager.playButtonClick()
                                showDailyRewardDialog = true
                            },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("home_daily_reward_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CardGiftcard, contentDescription = "Gift", tint = AccentGold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "DAILY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                            }
                        }

                        if (isDailyAvailable) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                        }
                    }
                }

                // Watch Ad for Free Coins Button (Real Rewarded Ad)
                OutlinedButton(
                    onClick = {
                        SoundManager.playButtonClick()
                        activity?.let { act ->
                            AdManager.showRewarded(
                                activity = act,
                                onUserEarnedReward = { _: Int, _: String ->
                                    repository.addCoins(50)
                                    SoundManager.playCoinSound()
                                    Toast.makeText(act, "Reward Received: +50 Coins! 🪙", Toast.LENGTH_SHORT).show()
                                },
                                onAdFailed = {
                                    AdManager.loadRewarded(act)
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("home_watch_ad_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "📺 ", fontSize = 16.sp)
                        Text(
                            text = "WATCH AD FOR +50 COINS",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGold
                        )
                    }
                }
            }

            // Bottom Banner Ad
            BannerAdView(modifier = Modifier.navigationBarsPadding())
        }

        // Daily Reward Dialog
        if (showDailyRewardDialog) {
            DailyRewardDialog(
                currentDayStreak = progress.consecutiveDays,
                isAvailable = isDailyAvailable,
                onClaim = {
                    repository.claimDailyReward()
                    showDailyRewardDialog = false
                },
                onDismiss = { showDailyRewardDialog = false }
            )
        }

        // Grand Prize Announcement Popup on first launch
        if (showGrandPrizeDialog) {
            GrandPrizeAnnouncementDialog(
                onStartPlaying = {
                    repository.markGrandPrizePopupViewed()
                    showGrandPrizeDialog = false
                    onPlayLevel(progress.currentLevel)
                },
                onMaybeLater = {
                    repository.markGrandPrizePopupViewed()
                    showGrandPrizeDialog = false
                }
            )
        }
    }
}
