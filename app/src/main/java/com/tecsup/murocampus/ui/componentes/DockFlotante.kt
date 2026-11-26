package com.tecsup.murocampus.ui.componentes

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.murocampus.ui.Ruta

/** Tab individual del Dock Flotante Pop. */
@Composable
private fun ItemDock(
    emoji: String,
    etiqueta: String,
    seleccionado: Boolean,
    badgeCount: Int = 0,
    animacionesActivas: Boolean = true,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .animateContentSize()
            .clip(RoundedCornerShape(50))
            .background(if (seleccionado) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = if (seleccionado) 12.dp else 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (badgeCount > 0 && !seleccionado) {
                BadgedBox(badge = { Badge { Text(text = badgeCount.toString()) } }) {
                    if (seleccionado) {
                        EmojiAnimado(emoji = emoji, tamano = 22.dp, animacionesActivas = animacionesActivas)
                    } else {
                        Text(text = emoji, fontSize = 20.sp)
                    }
                }
            } else {
                if (seleccionado) {
                    EmojiAnimado(emoji = emoji, tamano = 22.dp, animacionesActivas = animacionesActivas)
                } else {
                    Text(text = emoji, fontSize = 20.sp)
                }
            }

            if (seleccionado) {
                Text(
                    text = etiqueta,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/** Dock flotante estilo pegatina con 4 pestañas por emoji y botón central para Crear. */
@Composable
fun DockFlotante(
    rutaActual: String?,
    cantidadGuardados: Int = 0,
    animacionesActivas: Boolean = true,
    onNavegar: (Ruta) -> Unit,
    onCrear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Píldora del dock
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .pegatina(
                    colorFondo = MaterialTheme.colorScheme.surface,
                    bordeColor = MaterialTheme.colorScheme.outline,
                    radio = 50.dp,
                    bordeAncho = 2.dp,
                    sombraDesplazamiento = 4.dp
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemDock(
                    emoji = "🏠",
                    etiqueta = "Inicio",
                    seleccionado = rutaActual?.contains("Inicio") == true,
                    animacionesActivas = animacionesActivas,
                    onClick = { onNavegar(Ruta.Inicio) }
                )

                ItemDock(
                    emoji = "🧭",
                    etiqueta = "Explorar",
                    seleccionado = rutaActual?.contains("Explorar") == true || rutaActual?.contains("Comunidad") == true,
                    animacionesActivas = animacionesActivas,
                    onClick = { onNavegar(Ruta.Explorar) }
                )

                // Espacio para el botón central que sobresale
                Box(modifier = Modifier.size(52.dp))

                ItemDock(
                    emoji = "🔖",
                    etiqueta = "Guardados",
                    seleccionado = rutaActual?.contains("Guardados") == true,
                    badgeCount = cantidadGuardados,
                    animacionesActivas = animacionesActivas,
                    onClick = { onNavegar(Ruta.Guardados) }
                )

                ItemDock(
                    emoji = "😎",
                    etiqueta = "Yo",
                    seleccionado = rutaActual?.contains("Perfil") == true,
                    animacionesActivas = animacionesActivas,
                    onClick = { onNavegar(Ruta.Perfil) }
                )
            }
        }

        // Botón circular flotante central "✏️" que sobresale
        Box(
            modifier = Modifier
                .offset(y = (-14).dp)
                .align(Alignment.Center)
        ) {
            BotonPop(
                onClick = onCrear,
                colorFondo = MaterialTheme.colorScheme.primary,
                rotacion = -3f,
                modifier = Modifier.size(56.dp)
            ) {
                Text(text = "✏️", fontSize = 24.sp)
            }
        }
    }
}
