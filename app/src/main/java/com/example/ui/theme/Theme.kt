package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccent,
    secondary = MutedText,
    tertiary = ColorFace,
    background = SlateDark,
    surface = SlateCard,
    onBackground = LightText,
    onSurface = LightText,
    surfaceVariant = SlateOverlay,
    outline = ColorCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark mode for a premium sports management feel
    dynamicColor: Boolean = false, // Use our handcrafted tactical palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
