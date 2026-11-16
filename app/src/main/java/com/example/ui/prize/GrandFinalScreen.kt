package com.example.ui.prize

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldLight
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Screen displayed before starting Level 1,000,000 or upon winning Level 1,000,000.
 */
@Composable
fun GrandFinalScreen(
    isCompleted: Boolean,
    onStartFinalLevel: () -> Unit,
    onClaimPrize: () -> Unit,
    onBackToHome: () -> Unit
) {
    BackHandler {
        onBackToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PuzzleDarkBg,
                        Color(0xFF2E1065),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Trophy Glow Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(AccentGoldLight, AccentGold, Color(0xFFB45309))
                        )
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Grand Final Trophy",
                    tint = Color(0xFF451A03),
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isCompleted) {
                // Intro mode before playing Level 1,000,000
                Text(
                    text = "🏆 GRAND FINAL 🏆",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentGold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "LEVEL 1,000,000",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PuzzleSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Complete the final challenge to finish your Ball Sort Master journey and become eligible to claim the Bumper Prize!",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        onStartFinalLevel()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_final_level_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color(0xFF451A03)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "START FINAL LEVEL",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF451A03)
                        )
                    }
                }
            } else {
                // Won Level 1,000,000!
                Text(
                    text = "🎉 CONGRATULATIONS! 🎉",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You completed all 1,000,000 levels!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PuzzleSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🏆 BUMPER PRIZE CLAIM",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Your achievement is recorded. Submit your claim details to begin the eligibility verification process.",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        onClaimPrize()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("claim_your_prize_button")
                ) {
                    Text(
                        text = "CLAIM YOUR PRIZE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF451A03)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    SoundManager.playButtonClick()
                    onBackToHome()
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("grand_final_home_button")
            ) {
                Text(text = "BACK TO HOME", color = TextPrimary)
            }
        }
    }
}
