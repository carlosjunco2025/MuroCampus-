package com.tecsup.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.murocampus.model.Acento

val MuroCampusPopShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

val MuroCampusPopTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontWeight = FontWeight.Black),
    displayMedium = Typography().displayMedium.copy(fontWeight = FontWeight.Black),
    headlineLarge = Typography().headlineLarge.copy(fontWeight = FontWeight.Black),
    headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.Black),
    titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Black),
    titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.Bold),
    titleSmall = Typography().titleSmall.copy(fontWeight = FontWeight.Bold)
)

@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    acento: Acento = Acento.MANGO,
    content: @Composable () -> Unit
) {
    val colorScheme = obtenerEsquemaColoresPop(acento, darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MuroCampusPopTypography,
        shapes = MuroCampusPopShapes,
        content = content
    )
}
