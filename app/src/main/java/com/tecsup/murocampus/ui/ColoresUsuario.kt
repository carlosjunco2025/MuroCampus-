package com.tecsup.murocampus.ui

import androidx.compose.ui.graphics.Color

/**
 * Retorna un par (Fondo, Texto) para personalizar la etiqueta "Usuario N"
 * según el ID de usuario recibido.
 */
fun coloresDeUsuario(userId: Int): Pair<Color, Color> = when (userId) {
    1 -> Pair(Color(0xFF2F78C4), Color.White)
    2 -> Pair(Color(0xFFF5A800), Color(0xFF1F1F1F))
    3 -> Pair(Color(0xFF2E7D32), Color.White)
    4 -> Pair(Color(0xFF7B1FA2), Color.White)
    5 -> Pair(Color(0xFF00796B), Color.White)
    6 -> Pair(Color(0xFFE65100), Color.White)
    7 -> Pair(Color(0xFFC62828), Color.White)
    8 -> Pair(Color(0xFF283593), Color.White)
    9 -> Pair(Color(0xFFAD1457), Color.White)
    10 -> Pair(Color(0xFFD84315), Color.White)
    else -> Pair(Color(0xFF616161), Color.White)
}
