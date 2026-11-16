package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BallSortColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PuzzleSurfaceVariant,
    onPrimaryContainer = PrimaryLight,
    secondary = AccentGold,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = AccentGoldLight,
    tertiary = AccentCyan,
    onTertiary = Color.Black,
    background = PuzzleDarkBg,
    onBackground = TextPrimary,
    surface = PuzzleSurface,
    onSurface = TextPrimary,
    surfaceVariant = PuzzleSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TubeGlassBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BallSortColorScheme,
        typography = Typography,
        content = content
    )
}
