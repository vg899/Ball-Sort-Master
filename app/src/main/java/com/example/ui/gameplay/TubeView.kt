package com.example.ui.gameplay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.BallColor
import com.example.domain.model.Tube
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGoldLight
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryLight
import com.example.ui.theme.TubeGlassBorder
import com.example.ui.theme.TubeGlassInner

@Composable
fun BallView(
    ballColor: BallColor,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ballColor.highlightColor,
                        ballColor.primaryColor,
                        ballColor.shadowColor
                    ),
                    radius = 50f
                )
            ),
        contentAlignment = Alignment.TopStart
    ) {
        // Gloss highlight reflection dot
        Box(
            modifier = Modifier
                .padding(start = size * 0.2f, top = size * 0.15f)
                .size(size * 0.28f)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.55f))
        )
    }
}

@Composable
fun TubeView(
    tube: Tube,
    isSelected: Boolean,
    isHintSource: Boolean,
    isHintTarget: Boolean,
    tubeWidth: Dp,
    tubeHeight: Dp,
    ballSize: Dp,
    onTubeClicked: (Tube) -> Unit,
    modifier: Modifier = Modifier
) {
    // Pulse animation for hint
    val hintPulse = remember { Animatable(1f) }
    LaunchedEffect(isHintSource || isHintTarget) {
        if (isHintSource || isHintTarget) {
            hintPulse.animateTo(
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            hintPulse.snapTo(1f)
        }
    }

    val borderColor = when {
        isSelected -> PrimaryLight
        isHintTarget -> AccentGoldLight
        isHintSource -> AccentGold
        tube.isSolved && !tube.isEmpty -> Color(0xFF34D399) // Solved emerald glow
        else -> TubeGlassBorder
    }

    val borderWidth = if (isSelected || isHintSource || isHintTarget) 3.5.dp else 2.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(tubeWidth + 12.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onTubeClicked(tube)
            }
            .testTag("tube_${tube.id}")
    ) {
        // Space above tube where lifted ball floats
        Box(
            modifier = Modifier
                .height(ballSize + 12.dp)
                .width(ballSize),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (isSelected && tube.topBall != null) {
                val liftY = remember { Animatable(15f) }
                LaunchedEffect(isSelected) {
                    liftY.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(180, easing = FastOutSlowInEasing)
                    )
                }
                Box(
                    modifier = Modifier.offset { IntOffset(0, liftY.value.toInt()) }
                ) {
                    BallView(
                        ballColor = tube.topBall!!,
                        size = ballSize
                    )
                }
            } else if (isHintTarget) {
                // Indicator arrow pointing into destination tube
                Text(
                    text = "⬇",
                    fontSize = 18.sp,
                    color = AccentGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // The glass test tube itself
        Box(
            modifier = Modifier
                .width(tubeWidth)
                .height(tubeHeight)
                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                .background(TubeGlassInner)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp)
                )
                .padding(bottom = 6.dp, start = 3.dp, end = 3.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Glass reflection highlight stripe on the left edge
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawLine(
                            color = Color.White.copy(alpha = 0.2f),
                            start = androidx.compose.ui.geometry.Offset(x = 6f, y = 10f),
                            end = androidx.compose.ui.geometry.Offset(x = 6f, y = size.height - 30f),
                            strokeWidth = 3f
                        )
                    }
            )

            // Balls stacked inside tube from bottom to top
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // If this tube is currently selected, the top ball is lifted above the tube
                val ballsToDisplay = if (isSelected && tube.balls.isNotEmpty()) {
                    tube.balls.dropLast(1)
                } else {
                    tube.balls
                }

                for (ball in ballsToDisplay) {
                    BallView(
                        ballColor = ball,
                        size = ballSize,
                        modifier = Modifier.padding(vertical = 1.5.dp)
                    )
                }
            }
        }

        // Tube base rim / extra tube label
        Spacer(modifier = Modifier.height(4.dp))
        if (tube.isExtra) {
            Text(
                text = "+EXTRA",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AccentGold
            )
        }
    }
}
