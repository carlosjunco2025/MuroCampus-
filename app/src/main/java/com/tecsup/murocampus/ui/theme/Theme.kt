package com.tecsup.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Azul80,
    onPrimary = Color(0xFF003258),
    secondary = Ambar80,
    onSecondary = Color(0xFF4A2C00),
    tertiary = Color(0xFFA6CBA8),
    onTertiary = Color(0xFF003C16),
    tertiaryContainer = Color(0xFF1B4332),
    onTertiaryContainer = VerdeClaro,
    surfaceVariant = Color(0xFF2A3A44),
    onSurfaceVariant = Color(0xFFC2DEED),
    outlineVariant = Color(0xFF4A5C66),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val LightColorScheme = lightColorScheme(
    primary = Azul40,
    onPrimary = Color.White,
    secondary = Ambar40,
    onSecondary = Color.White,
    secondaryContainer = Ambar80,
    onSecondaryContainer = Color(0xFF4A2C00),
    tertiary = Verde40,
    onTertiary = Color.White,
    tertiaryContainer = VerdeClaro,
    onTertiaryContainer = Verde40,
    surface = Color.White,
    onSurface = Color(0xFF1F2933),
    surfaceVariant = GrisAzuladoClaro,
    onSurfaceVariant = TextoGris,
    outline = BordeGris,
    outlineVariant = BordeGris,
    error = RojoError,
    onError = Color.White,
    errorContainer = RosaError,
    onErrorContainer = RojoError
)

@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
