package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CarpenterDarkColorScheme = darkColorScheme(
    primary = TimberAmber,
    onPrimary = Color.White,
    primaryContainer = TimberAmberContainer,
    onPrimaryContainer = TimberGold,
    secondary = TimberWood,
    onSecondary = Color.Black,
    secondaryContainer = OperatorButtonBg,
    onSecondaryContainer = TextPrimary,
    tertiary = TimberGold,
    background = SlateDark,
    onBackground = TextPrimary,
    surface = SlateSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateCard,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun CarpenterCalculatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CarpenterDarkColorScheme,
        typography = Typography,
        content = content
    )
}
