package com.example.checkcalendar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    primaryContainer = LightGreen,
    onPrimaryContainer = Green,

    background = LightBackground,
    onBackground = LightText,

    surface = LightCard,
    onSurface = LightText,

    surfaceVariant = LightCell,
    onSurfaceVariant = LightSecondary,

    outline = Color(0xFFD5DBD4)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    onPrimary = Color(0xFF183321),
    primaryContainer = DarkTodayBackground,
    onPrimaryContainer = DarkTodayText,

    background = DarkBackground,
    onBackground = DarkText,

    surface = DarkCard,
    onSurface = DarkText,

    surfaceVariant = DarkCell,
    onSurfaceVariant = DarkSecondary,

    outline = Color(0xFF465148)
)

@Composable
fun CheckCalendarTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        },
        typography = Typography,
        content = content
    )
}
