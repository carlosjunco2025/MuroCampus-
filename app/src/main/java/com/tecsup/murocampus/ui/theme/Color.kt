package com.tecsup.murocampus.ui.theme

import androidx.compose.ui.graphics.Color
import com.tecsup.murocampus.model.Acento

// Colores base de MuroCampus Pop
val LightBackground = Color(0xFFFFF7E8)
val LightSurface = Color(0xFFFFFFFF)
val LightTinta = Color(0xFF1B1B2F)
val LightSurfaceVariant = Color(0xFFF0E6D2)

val DarkBackground = Color(0xFF14122B)
val DarkSurface = Color(0xFF1F1C3D)
val DarkTinta = Color(0xFFF5F0FF)
val DarkSurfaceVariant = Color(0xFF2D2952)

fun obtenerEsquemaColoresPop(acento: Acento, darkTheme: Boolean) = if (darkTheme) {
    androidx.compose.material3.darkColorScheme(
        primary = acento.primary,
        onPrimary = Color(0xFF1B1B2F),
        primaryContainer = acento.primary.copy(alpha = 0.3f),
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurface = DarkTinta,
        onBackground = DarkTinta,
        outline = DarkTinta
    )
} else {
    androidx.compose.material3.lightColorScheme(
        primary = acento.primary,
        onPrimary = Color(0xFF1B1B2F),
        primaryContainer = acento.primary.copy(alpha = 0.2f),
        background = LightBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurface = LightTinta,
        onBackground = LightTinta,
        outline = LightTinta
    )
}
