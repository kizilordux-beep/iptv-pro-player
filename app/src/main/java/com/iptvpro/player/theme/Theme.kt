package com.iptvpro.player.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NetflixRed = Color(0xFFE50914)
val NetflixDarkBackground = Color(0xFF141414)
val NetflixCardSurface = Color(0xFF1F1F1F)
val NetflixTextWhite = Color(0xFFFFFFFF)
val NetflixTextGray = Color(0xFFB3B3B3)

private val DarkColorScheme = darkColorScheme(
    primary = NetflixRed,
    onPrimary = Color.White,
    background = NetflixDarkBackground,
    onBackground = NetflixTextWhite,
    surface = NetflixCardSurface,
    onSurface = NetflixTextWhite,
    surfaceVariant = Color(0xFF2B2B2B),
    onSurfaceVariant = NetflixTextGray
)

@Composable
fun IPTVProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
