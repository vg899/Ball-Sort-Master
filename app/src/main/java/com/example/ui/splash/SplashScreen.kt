package com.example.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.repository.GameRepository
import com.example.domain.model.BallColor
import com.example.ui.gameplay.BallView
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.PuzzleDarkBg
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    repository: GameRepository,
    onSplashFinished: () -> Unit
) {
    // Bouncing balls animation
    val bounceY1 = remember { Animatable(0f) }
    val bounceY2 = remember { Animatable(0f) }
    val bounceY3 = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Start background tasks
        repository.initializeAuth()
        SoundManager.startBackgroundMusic()

        // Animation sequence
        delay(1200)
        onSplashFinished()
    }

    LaunchedEffect(Unit) {
        bounceY1.animateTo(
            targetValue = -30f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(100)
        bounceY2.animateTo(
            targetValue = -30f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        delay(200)
        bounceY3.animateTo(
            targetValue = -30f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PuzzleDarkBg, Color(0xFF1E1B4B), PuzzleDarkBg)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Three bouncing colored balls
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.offset { IntOffset(0, bounceY1.value.toInt()) }) {
                    BallView(ballColor = BallColor.RED, size = 36.dp)
                }
                Box(modifier = Modifier.offset { IntOffset(0, bounceY2.value.toInt()) }) {
                    BallView(ballColor = BallColor.YELLOW, size = 36.dp)
                }
                Box(modifier = Modifier.offset { IntOffset(0, bounceY3.value.toInt()) }) {
                    BallView(ballColor = BallColor.BLUE, size = 36.dp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "1,000,000 Levels • Grand Prize Challenge",
                fontSize = 13.sp,
                color = PrimaryLight
            )

            Spacer(modifier = Modifier.height(36.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = AccentGold,
                strokeWidth = 2.5.dp
            )
        }
    }
}
