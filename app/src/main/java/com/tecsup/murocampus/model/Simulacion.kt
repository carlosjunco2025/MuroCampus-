package com.tecsup.murocampus.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

/** Comunidades temáticas de MuroCampus Pop con colores planos vibrantes. */
@Serializable
enum class Comunidad(
    val nombre: String,
    val emoji: String,
    val descripcion: String,
    val hexColor: String
) {
    COMIDAS("Comidas del campus", "🍔", "Comparte recetas, datos de cafeterías y antojos.", "#FFB347"),
    JUEGOS("Juegos", "🎮", "Torneos, partidas en línea y quedadas presenciales.", "#8B6BFF"),
    ESTUDIOS("Estudios", "📚", "Grupos de repaso, apuntes y consejos académicos.", "#4FC3F7"),
    DEPORTES("Deportes", "⚽", "Organiza pichangas, entrenamientos y torneos.", "#4ADE80"),
    MUSICA("Música y arte", "🎵", "Bandas, tocatas, diseño y expresiones artísticas.", "#FF6FB5"),
    EVENTOS("Eventos", "🎉", "Fiestas, charlas, talleres y vida universitaria.", "#FFD93D"),
    TRANSPORTE("Transporte", "🚌", "Colectivos, rutas, datos de tráfico y estacionamiento.", "#FF8A65"),
    CHARLA("Charla libre", "💬", "Debates, memes y anécdotas de la universidad.", "#B388FF");

    val color: Color
        get() = Color(android.graphics.Color.parseColor(hexColor))

    val colorTexto: Color
        get() = when (this) {
            COMIDAS, DEPORTES, EVENTOS, ESTUDIOS, TRANSPORTE -> Color(0xFF1B1B2F)
            JUEGOS, MUSICA, CHARLA -> Color.White
        }
}

/** Reacciones disponibles para las publicaciones en MuroCampus Pop. */
@Serializable
enum class Reaccion(val emoji: String, val etiqueta: String) {
    ME_GUSTA("👍", "Me gusta"),
    ME_ENCANTA("❤️", "Me encanta"),
    ME_DIVIERTE("😂", "Me divierte"),
    ME_ASOMBRA("😮", "Me asombra"),
    ME_TRISTEZA("😢", "Me entristece"),
    ME_ENOJA("😡", "Me enoja")
}

/** Paletas de acento Pop disponibles. */
enum class Acento(val nombre: String, val primary: Color) {
    MANGO("Mango", Color(0xFFFF9F1C)),
    CHICLE("Chicle", Color(0xFFFF4D9D)),
    MENTA("Menta", Color(0xFF14C38E)),
    CIELO("Cielo", Color(0xFF3AA7FF))
}

/** Estructura para publicaciones creadas localmente. */
@Serializable
data class PublicacionPropia(
    val id: String = java.util.UUID.randomUUID().toString(),
    val titulo: String,
    val texto: String,
    val comunidad: Comunidad,
    val idServidor: Int? = null,
    val fecha: String = "Hoy"
)

/** Estructura para el Reto del Día. */
data class Reto(
    val titulo: String,
    val comunidad: Comunidad,
    val sugerencia: String
)

/** Funciones de simulación determinista y auxiliares. */
fun comunidadDe(postId: Int): Comunidad {
    val entries = Comunidad.entries
    val index = ((postId - 1) % entries.size + entries.size) % entries.size
    return entries[index]
}

fun reaccionesBase(postId: Int): Int {
    val p = kotlin.math.abs(postId)
    return ((p * 37) % 90) + 5
}

fun horasDesde(postId: Int): Int {
    val p = kotlin.math.abs(postId)
    return ((p * 7) % 23) + 1
}

fun minutosLectura(body: String): Int {
    val palabras = body.split("\\s+".toRegex()).size
    return (palabras / 200).coerceAtLeast(1)
}

fun retoDelDia(diaEpoch: Long): Reto {
    val retos = listOf(
        Reto("Recomienda tu snack favorito del campus 🍟", Comunidad.COMIDAS, "Mi snack favorito es..."),
        Reto("¿Cuál es el mejor juego para una pausa? 🎮", Comunidad.JUEGOS, "Recomiendo jugar..."),
        Reto("Comparte tu mejor tip de estudio 📚", Comunidad.ESTUDIOS, "Para estudiar mejor yo..."),
        Reto("¿Qué deporte practicas en la U? ⚽", Comunidad.DEPORTES, "Me gusta practicar..."),
        Reto("¿Qué canción suena en tus audífonos hoy? 🎵", Comunidad.MUSICA, "Estoy escuchando..."),
        Reto("¡El evento del año en el campus! 🎉", Comunidad.EVENTOS, "No te pierdas..."),
        Reto("¿Cuál es la peor ruta para llegar tarde? 🚌", Comunidad.TRANSPORTE, "Siempre sufro en..."),
        Reto("Un meme que defina tu ciclo académico 💬", Comunidad.CHARLA, "Mi vida académica en resumen...")
    )
    val index = (diaEpoch % retos.size).toInt()
    return retos[index]
}

/** Funciones centralizadas para recursos remotos (Fotos, Avatares, Emojis animados). */
fun urlFoto(postId: Int, ancho: Int = 600, alto: Int = 400): String {
    return "https://picsum.photos/seed/mcp/$ancho/$alto"
}

fun urlAvatar(semilla: String): String {
    return "https://api.dicebear.com/9.x/fun-emoji/png?seed=$semilla&size=128"
}

fun urlEmojiAnimado(emoji: String): String {
    // Convierte el emoji a puntos de código en hexadecimal minúscula separados por _
    val puntos = emoji.codePoints().toArray().joinToString("_") { java.lang.Integer.toHexString(it) }
    return "https://fonts.gstatic.com/s/e/notoemoji/latest/$puntos/512.gif"
}
