package com.iptvpro.player.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CrimsonRed = Color(0xFFDC143C)
val DarkRed = Color(0xFF8B0000)
val DeepBlack = Color(0xFF0A0A0A)
val SurfaceBlack = Color(0xFF1A1A1A)
val CardBlack = Color(0xFF252525)
val DimWhite = Color(0xFFE0E0E0)
val BrightWhite = Color(0xFFFFFFFF)
val AccentRed = Color(0xFFFF4444)
val SubtleGray = Color(0xFF888888)
val DarkGray = Color(0xFF333333)
val EpgNow = Color(0xFF00C853)
val EpgNext = Color(0xFFFFAB00)

private val Scheme = darkColorScheme(
    primary = CrimsonRed, onPrimary = BrightWhite,
    primaryContainer = DarkRed, onPrimaryContainer = BrightWhite,
    secondary = AccentRed, onSecondary = BrightWhite,
    background = DeepBlack, onBackground = DimWhite,
    surface = SurfaceBlack, onSurface = DimWhite,
    surfaceVariant = CardBlack, onSurfaceVariant = SubtleGray,
    outline = DarkGray, error = Color(0xFFFF6B6B)
)

@Composable
fun IPTVProTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
