package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.audio.SoundManager
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.PuzzleSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DailyRewardDialog(
    currentDayStreak: Int,
    isAvailable: Boolean,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    val dailyRewards = listOf(50, 75, 100, 125, 150, 200, 300)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PuzzleDarkBg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(2.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AccentGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Daily Reward",
                        tint = Color(0xFF451A03),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "DAILY REWARD",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = AccentGold
                )

                Text(
                    text = "Log in every day for greater bonus rewards!",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Days grid (1 through 7)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Days 1-4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (day in 1..4) {
                            DayRewardCard(
                                dayNumber = day,
                                coins = dailyRewards[day - 1],
                                isClaimed = !isAvailable && day <= currentDayStreak,
                                isToday = isAvailable && day == (currentDayStreak % 7) + 1,
                                modifier = Modifier.weight(1f).padding(horizontal = 3.dp)
                            )
                        }
                    }

                    // Days 5-7
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (day in 5..7) {
                            DayRewardCard(
                                dayNumber = day,
                                coins = dailyRewards[day - 1],
                                isClaimed = !isAvailable && day <= currentDayStreak,
                                isToday = isAvailable && day == (currentDayStreak % 7) + 1,
                                isMajor = day == 7,
                                modifier = Modifier.weight(1f).padding(horizontal = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (isAvailable) {
                            SoundManager.playCoinSound()
                            onClaim()
                        } else {
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAvailable) AccentGold else PrimaryIndigo
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("claim_daily_button")
                ) {
                    Text(
                        text = if (isAvailable) "CLAIM REWARD" else "COME BACK TOMORROW",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAvailable) Color(0xFF451A03) else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun DayRewardCard(
    dayNumber: Int,
    coins: Int,
    isClaimed: Boolean,
    isToday: Boolean,
    isMajor: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgModifier = when {
        isToday -> Modifier.background(
            Brush.verticalGradient(listOf(AccentGold, Color(0xFFD97706)))
        )
        isClaimed -> Modifier.background(Color(0xFF065F46))
        else -> Modifier.background(PuzzleSurfaceVariant)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .then(bgModifier)
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                text = "Day $dayNumber",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isToday) Color(0xFF451A03) else TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (isClaimed) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Claimed",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "🪙$coins",
                    fontSize = if (isMajor) 12.sp else 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isToday) Color(0xFF451A03) else AccentGold
                )
            }
        }
    }
}
