package com.example.coopwidget.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HudDarkColorScheme = darkColorScheme(
    primary = HudBlue,
    onPrimary = Color.Black,
    secondary = HudPink,
    onSecondary = Color.White,
    tertiary = HudGreen,
    background = HudBackground,
    onBackground = HudTextPrimary,
    surface = HudPanel,
    onSurface = HudTextPrimary,
    surfaceVariant = HudCard,
    onSurfaceVariant = HudTextSecondary,
    outline = HudBorder
)

@Composable
fun CoOpWidgetTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HudDarkColorScheme,
        typography = Typography,
        content = content
    )
}
