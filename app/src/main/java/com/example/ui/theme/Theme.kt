package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = ResQRed,
    onPrimary = Color.White,
    primaryContainer = ResQRedLight,
    onPrimaryContainer = ResQRedDark,
    secondary = PoliceFg,
    onSecondary = Color.White,
    secondaryContainer = PoliceBg,
    onSecondaryContainer = PoliceFg,
    tertiary = FireFg,
    onTertiary = Color.White,
    tertiaryContainer = FireBg,
    onTertiaryContainer = FireFg,
    background = Color.White,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
