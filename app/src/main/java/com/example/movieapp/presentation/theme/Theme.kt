package com.example.movieapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors =
    darkColorScheme(
        primary = Gold,
        onPrimary = OnGold,
        primaryContainer = GoldContainerDark,
        onPrimaryContainer = GoldContainerLight,
        background = Ink,
        surface = Ink,
        surfaceContainer = InkSurface,
        surfaceContainerLow = InkSurface,
        surfaceContainerHigh = InkSurfaceVariant,
        surfaceVariant = InkSurfaceVariant,
    )

private val LightColors =
    lightColorScheme(
        primary = GoldDark,
        primaryContainer = GoldContainerLight,
        onPrimaryContainer = OnGold,
        background = Paper,
        surface = Paper,
        surfaceVariant = PaperVariant,
    )

@Composable
fun CineScopeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
