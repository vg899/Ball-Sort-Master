package com.example.ui.gameplay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WinDialog(
    levelNumber: Int,
    movesTaken: Int,
    coinsEarned: Int,
    onNextLevel: () -> Unit,
    onMap: () -> Unit,
    onReplay: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PuzzleDarkBg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                // Trophy badge
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AccentGold, Color(0xFFD97706))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "LEVEL COMPLETE!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentGold
                )

                Text(
                    text = "Level $levelNumber Solved",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PuzzleSurfaceVariant)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Moves",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$movesTaken",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Reward",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙 ", fontSize = 14.sp)
                            Text(
                                text = "+$coinsEarned",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Next Level Button
                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_level_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "NEXT LEVEL",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Level"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary buttons: Map and Replay
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onMap,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("map_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Level Map",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Map", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onReplay,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("replay_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Replay",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Replay", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
