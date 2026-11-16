package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.window.Dialog
import com.example.audio.SoundManager
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldLight
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GrandPrizeAnnouncementDialog(
    onStartPlaying: () -> Unit,
    onMaybeLater: () -> Unit
) {
    Dialog(onDismissRequest = onMaybeLater) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PuzzleDarkBg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(2.dp, AccentGold.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                // Trophy Badge
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(AccentGoldLight, AccentGold, Color(0xFFB45309))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Grand Prize Challenge",
                        tint = Color(0xFF451A03),
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🎉 GRAND PRIZE CHALLENGE 🎉",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PuzzleSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = "Complete all 1,000,000 levels and become eligible to claim the Bumper Prize!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your journey starts at Level 1. Can you reach the final level?",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Start Playing Button
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        onStartPlaying()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("grand_prize_start_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color(0xFF451A03))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START PLAYING",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF451A03)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Maybe Later Button
                OutlinedButton(
                    onClick = {
                        SoundManager.playButtonClick()
                        onMaybeLater()
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("grand_prize_maybe_later_button")
                ) {
                    Text(text = "MAYBE LATER", color = TextSecondary, fontSize = 13.sp)
                }
            }
        }
    }
}
