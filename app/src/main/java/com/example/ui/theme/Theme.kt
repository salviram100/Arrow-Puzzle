package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ArrowPuzzleColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF00363A),
    primaryContainer = Color(0xFF004F55),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = NeonAmber,
    onSecondary = Color(0xFF432C00),
    secondaryContainer = Color(0xFF5F4100),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = NeonMagenta,
    onTertiary = Color(0xFF5E002B),
    tertiaryContainer = Color(0xFF86003E),
    onTertiaryContainer = Color(0xFFFFD9E2),
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF1E293B)
)

@Composable
fun ArrowPuzzleTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArrowPuzzleColorScheme,
        typography = Typography,
        content = content
    )
}
