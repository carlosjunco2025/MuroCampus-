package com.tecsup.murocampus.ui.componentes

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.tecsup.murocampus.model.Comunidad
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.model.Reaccion
import com.tecsup.murocampus.model.Usuario
import com.tecsup.murocampus.model.comunidadDe
import com.tecsup.murocampus.model.horasDesde
import com.tecsup.murocampus.model.reaccionesBase
import com.tecsup.murocampus.model.urlAvatar
import com.tecsup.murocampus.model.urlEmojiAnimado
import com.tecsup.murocampus.model.urlFoto
import kotlin.math.roundToInt
import kotlin.random.Random

/** Calcula automáticamente el color de texto (blanco o tinta oscura) con contraste óptimo. */
fun colorTextoParaFondo(fondo: Color): Color {
    val luminancia = (0.299f * fondo.red + 0.587f * fondo.green + 0.114f * fondo.blue)
    return if (luminancia > 0.62f) Color(0xFF1B1B2F) else Color.White
}

/** Cargador único de imágenes Coil con límite de caché en memoria al 15% y soporte de GIFs. */
object CoilLoader {
    private var loader: ImageLoader? = null

    fun get(context: Context): ImageLoader {
        return loader ?: synchronized(this) {
            val appCtx = context.applicationContext
            val builder = ImageLoader.Builder(appCtx)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .memoryCache {
                    MemoryCache.Builder(appCtx)
                        .maxSizePercent(0.15)
                        .build()
                }
                .crossfade(true)
                .build()
            loader = builder
            builder
        }
    }
}

/** Modifier estilo "Pegatina Pop": dibuja una sombra dura desplazada, fondo y borde oscuro. */
fun Modifier.pegatina(
    colorFondo: Color,
    bordeColor: Color = Color(0xFF1B1B2F),
    radio: Dp = 20.dp,
    bordeAncho: Dp = 2.dp,
    sombraDesplazamiento: Dp = 4.dp,
    rotacion: Float = 0f
): Modifier = this
    .graphicsLayer { rotationZ = rotacion }
    .drawBehind {
        val radioPx = radio.toPx()
        val bordePx = bordeAncho.toPx()
        val sombraPx = sombraDesplazamiento.toPx()

        if (sombraPx > 0) {
            drawRoundRect(
                color = bordeColor,
                topLeft = Offset(sombraPx, sombraPx),
                size = size,
                cornerRadius = CornerRadius(radioPx)
            )
        }

        drawRoundRect(
            color = colorFondo,
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(radioPx)
        )

        if (bordePx > 0) {
            drawRoundRect(
                color = bordeColor,
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = CornerRadius(radioPx),
                style = Stroke(width = bordePx)
            )
        }
    }

/** Modifier para pegatinas interactivas: al presionar, la pegatina se "hunde" hacia la sombra. */
@Composable
fun Modifier.pegatinaPresionable(
    colorFondo: Color,
    bordeColor: Color = MaterialTheme.colorScheme.outline,
    radio: Dp = 20.dp,
    bordeAncho: Dp = 2.dp,
    sombraDesplazamiento: Dp = 4.dp,
    rotacion: Float = 0f,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val desplazaAnim by animateFloatAsState(
        targetValue = if (isPressed) 0f else 1f,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.6f),
        label = "desplazamiento_pegatina"
    )

    val offsetPx = sombraDesplazamiento * desplazaAnim

    return this
        .graphicsLayer {
            rotationZ = rotacion
            translationX = (1f - desplazaAnim) * sombraDesplazamiento.toPx()
            translationY = (1f - desplazaAnim) * sombraDesplazamiento.toPx()
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
        .drawBehind {
            val radioPx = radio.toPx()
            val bordePx = bordeAncho.toPx()
            val sombraPx = offsetPx.toPx()

            if (sombraPx > 0.5f) {
                drawRoundRect(
                    color = bordeColor,
                    topLeft = Offset(sombraPx, sombraPx),
                    size = size,
                    cornerRadius = CornerRadius(radioPx)
                )
            }

            drawRoundRect(
                color = colorFondo,
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = CornerRadius(radioPx)
            )

            if (bordePx > 0) {
                drawRoundRect(
                    color = bordeColor,
                    topLeft = Offset.Zero,
                    size = size,
                    cornerRadius = CornerRadius(radioPx),
                    style = Stroke(width = bordePx)
                )
            }
        }
}

/** Botón principal "Pop": bordes gruesos, sombra dura que rebota al presionar. */
@Composable
fun BotonPop(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorFondo: Color = MaterialTheme.colorScheme.primary,
    habilitado: Boolean = true,
    rotacion: Float = 0f,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .pegatinaPresionable(
                colorFondo = if (habilitado) colorFondo else Color.LightGray,
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = 20.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 4.dp,
                rotacion = rotacion,
                onClick = {
                    if (habilitado) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                }
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/** Chip seleccionable "Pop" con rotación alterna ±1.5°. */
@Composable
fun ChipPop(
    texto: String,
    emoji: String? = null,
    seleccionado: Boolean,
    onClick: () -> Unit,
    colorFondo: Color = MaterialTheme.colorScheme.primary,
    rotacion: Float = 0f,
    modifier: Modifier = Modifier
) {
    val fondoActual = if (seleccionado) colorFondo else MaterialTheme.colorScheme.surface
    val textoColor = if (seleccionado) colorTextoParaFondo(fondoActual) else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .pegatinaPresionable(
                colorFondo = fondoActual,
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = 50.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 3.dp,
                rotacion = rotacion,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (emoji != null) {
                Text(text = emoji, fontSize = 14.sp)
            }
            Text(
                text = texto,
                fontSize = 12.sp,
                fontWeight = if (seleccionado) FontWeight.ExtraBold else FontWeight.Medium,
                color = textoColor
            )
        }
    }
}

/** Etiqueta pequeña tipo pegatina con contraste automático. */
@Composable
fun InsigniaPop(
    texto: String,
    emoji: String? = null,
    colorFondo: Color = MaterialTheme.colorScheme.primaryContainer,
    colorTexto: Color? = null,
    modifier: Modifier = Modifier
) {
    val textoColorCalculado = colorTexto ?: colorTextoParaFondo(colorFondo)

    Box(
        modifier = modifier
            .pegatina(
                colorFondo = colorFondo,
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = 12.dp,
                bordeAncho = 1.5.dp,
                sombraDesplazamiento = 2.dp
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (emoji != null) {
                Text(text = emoji, fontSize = 11.sp)
            }
            Text(
                text = texto,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textoColorCalculado
            )
        }
    }
}

/** Ticker / marquesina de texto horizontal infinito. */
@Composable
fun TextoMarquesina(
    textos: List<String>,
    animacionesActivas: Boolean = true,
    modifier: Modifier = Modifier
) {
    val cadenaCompleta = if (textos.isEmpty()) "MuroCampus Pop" else textos.joinToString("   ·   ") + "   ·   "
    val transicion = rememberInfiniteTransition(label = "marquesina_infinitas")
    val desplazamiento by transicion.animateFloat(
        initialValue = 0f,
        targetValue = -300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset_marquesina"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pegatina(
                colorFondo = MaterialTheme.colorScheme.surfaceVariant,
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = 12.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 2.dp
            )
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Text(
            text = cadenaCompleta + cadenaCompleta,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.offset {
                if (animacionesActivas) IntOffset(desplazamiento.roundToInt(), 0) else IntOffset.Zero
            }
        )
    }
}

/** Fondo decorativo flotante con 8-12 emojis desfasados en alfa 0.15-0.35. */
@Composable
fun FondoFlotante(
    emojis: List<String> = listOf("✨", "🚀", "🎨", "🔥", "⭐", "🎉", "🍕", "🎮", "📚", "⚽"),
    animacionesActivas: Boolean = true,
    modifier: Modifier = Modifier
) {
    val transicion = rememberInfiniteTransition(label = "fondo_flotante")
    val offsetY by transicion.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_y"
    )
    val rotacion by transicion.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotacion_flotante"
    )

    Box(modifier = modifier.fillMaxSize()) {
        emojis.take(10).forEachIndexed { i, emoji ->
            val alignX = ((i * 37) % 90 + 5) / 100f
            val alignY = ((i * 53) % 90 + 5) / 100f
            val tamano = ((i * 7) % 16 + 18).sp
            val alfa = if (i % 2 == 0) 0.18f else 0.28f

            Text(
                text = emoji,
                fontSize = tamano,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = alfa
                        if (animacionesActivas) {
                            translationY = if (i % 2 == 0) offsetY.dp.toPx() else -offsetY.dp.toPx()
                            rotationZ = if (i % 3 == 0) rotacion else -rotacion
                        }
                    }
                    .align(
                        androidx.compose.ui.BiasAlignment(
                            horizontalBias = (alignX * 2) - 1f,
                            verticalBias = (alignY * 2) - 1f
                        )
                    )
            )
        }
    }
}

/** Carga de foto remota con AsyncImage y portada ilustrada de respaldo si falla o no hay red. */
@Composable
fun FotoRemota(
    url: String,
    comunidad: Comunidad,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var cargoConExito by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(comunidad.color.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .build(),
            imageLoader = CoilLoader.get(context),
            contentDescription = "Foto de ${comunidad.nombre}",
            onSuccess = { cargoConExito = true },
            onError = { cargoConExito = false },
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (!cargoConExito) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(comunidad.color, comunidad.color.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = comunidad.emoji, fontSize = 44.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = comunidad.nombre,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = comunidad.colorTexto
                    )
                }
            }
        }
    }
}

/** Avatar de usuario con foto remota DiceBear o iniciales de respaldo. */
@Composable
fun AvatarFoto(
    semilla: String,
    tamano: Dp = 36.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val url = urlAvatar(semilla)

    Box(
        modifier = modifier
            .size(tamano)
            .pegatina(
                colorFondo = MaterialTheme.colorScheme.primaryContainer,
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = tamano / 2,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 2.dp
            )
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            imageLoader = CoilLoader.get(context),
            contentDescription = "Avatar de $semilla",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/** Emoji animado de Google Noto con fallback a Text animado si animacionesActivas es false. */
@Composable
fun EmojiAnimado(
    emoji: String,
    tamano: Dp = 32.dp,
    animacionesActivas: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var falloCarga by remember { mutableStateOf(false) }

    if (animacionesActivas && !falloCarga) {
        AsyncImage(
            model = urlEmojiAnimado(emoji),
            imageLoader = CoilLoader.get(context),
            contentDescription = emoji,
            onError = { falloCarga = true },
            modifier = modifier.size(tamano)
        )
    } else {
        val transicion = rememberInfiniteTransition(label = "emoji_bounce")
        val rebote by transicion.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale_emoji"
        )

        Text(
            text = emoji,
            fontSize = (tamano.value * 0.75f).sp,
            modifier = modifier.graphicsLayer {
                if (animacionesActivas) {
                    scaleX = rebote
                    scaleY = rebote
                }
            }
        )
    }
}

/** Wordmark principal "MuroCampus" con pegatina Pop. */
@Composable
fun WordmarkPop(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MuroCampus",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(6.dp))
        InsigniaPop(
            texto = "POP",
            emoji = "⚡",
            colorFondo = MaterialTheme.colorScheme.primary,
            colorTexto = Color.White
        )
    }
}

/** Esqueleto Pop con shimmer para estado de carga. */
@Composable
fun EsqueletoPop(modifier: Modifier = Modifier) {
    val transicion = rememberInfiniteTransition(label = "shimmer_pop")
    val alfa by transicion.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alfa_shimmer"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .pegatina(
                colorFondo = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alfa),
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = 20.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 4.dp
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(12.dp)
                    .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            )
        }
    }
}

/** Estado vacío divertido con Emoji animado y mensaje. */
@Composable
fun EstadoVacioPop(
    emoji: String = "😴",
    texto: String,
    subtitulo: String = "Intenta buscar algo más o explorar otras comunidades.",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Estado de error con botón "Reintentar" estilo BotonPop. */
@Composable
fun EstadoErrorPop(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pegatina(
                colorFondo = MaterialTheme.colorScheme.errorContainer,
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = 20.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 4.dp
            )
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "💥", fontSize = 36.sp)
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            BotonPop(
                onClick = onReintentar,
                colorFondo = MaterialTheme.colorScheme.primary
            ) {
                Text(text = "🔄 Reintentar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Recuadro verde para éxito. */
@Composable
fun RecuadroExitoPop(
    texto: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pegatina(
                colorFondo = Color(0xFFE8F5E9),
                bordeColor = Color(0xFF2E7D32),
                radio = 16.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 3.dp
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎉 $texto",
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}

/** Recuadro rosado para error. */
@Composable
fun RecuadroErrorPop(
    mensaje: String,
    onReintentar: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pegatina(
                colorFondo = MaterialTheme.colorScheme.errorContainer,
                bordeColor = MaterialTheme.colorScheme.error,
                radio = 16.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 3.dp
            )
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚠️ $mensaje",
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            if (onReintentar != null) {
                BotonPop(onClick = onReintentar, colorFondo = MaterialTheme.colorScheme.error) {
                    Text("Reintentar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

/** Efecto visual de confeti con Canvas. */
@Composable
fun ConfetiCanvas(
    activo: Boolean,
    modifier: Modifier = Modifier
) {
    if (!activo) return

    val colores = listOf(
        Color(0xFFFF9F1C), Color(0xFFFF4D9D), Color(0xFF14C38E),
        Color(0xFF3AA7FF), Color(0xFFFFD93D), Color(0xFF8B6BFF)
    )
    val anim = remember { Animatable(0f) }

    LaunchedEffect(activo) {
        anim.snapTo(0f)
        anim.animateTo(1f, animationSpec = tween(2000, easing = LinearEasing))
    }

    val progreso = anim.value

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val random = Random(42)
        for (i in 0..50) {
            val startX = random.nextFloat() * width
            val color = colores[i % colores.size]
            val speedY = (random.nextFloat() * 0.8f + 0.4f) * height
            val speedX = (random.nextFloat() - 0.5f) * 200f

            val currentY = (progreso * speedY) % height
            val currentX = startX + (progreso * speedX)

            drawCircle(
                color = color.copy(alpha = 1f - progreso),
                radius = 6.dp.toPx(),
                center = Offset(currentX, currentY)
            )
        }
    }
}

/** Tarjeta Polaroid "Pop": tarjeta tipo polaroid con foto, sticker de comunidad, reacciones y menú. */
@Composable
fun TarjetaPolaroid(
    post: Post,
    autor: Usuario?,
    esFavorito: Boolean,
    reaccionActual: Reaccion?,
    compacta: Boolean = false,
    animacionesActivas: Boolean = true,
    onAlternarFavorito: () -> Unit,
    onReaccionar: (Reaccion) -> Unit,
    onClick: () -> Unit,
    onAbrirPerfil: () -> Unit,
    onCompartir: () -> Unit,
    onEditar: () -> Unit,
    onBorrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val comunidad = comunidadDe(post.id)
    val rotacion = if (post.id % 2 == 0) 1.2f else -1.2f
    val altoFoto = if (compacta) 110.dp else 160.dp

    var mostrarPanelReacciones by remember { mutableStateOf(false) }
    var mostrarMenuAcciones by remember { mutableStateOf(false) }

    val conReaccion = reaccionesBase(post.id) + if (reaccionActual != null) 1 else 0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pegatina(
                colorFondo = MaterialTheme.colorScheme.surface,
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = 20.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 4.dp,
                rotacion = rotacion
            )
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            // Foto tipo polaroid arriba
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(altoFoto)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                FotoRemota(
                    url = urlFoto(post.id, 600, 400),
                    comunidad = comunidad,
                    modifier = Modifier.fillMaxSize()
                )

                // Sticker de comunidad con contraste optimizado
                InsigniaPop(
                    texto = comunidad.nombre,
                    emoji = comunidad.emoji,
                    colorFondo = comunidad.color,
                    colorTexto = comunidad.colorTexto,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )

                // Menú "⋯" de opciones
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                    Surface(
                        onClick = { mostrarMenuAcciones = true },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "⋯", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    DropdownMenu(
                        expanded = mostrarMenuAcciones,
                        onDismissRequest = { mostrarMenuAcciones = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (esFavorito) "Quitar de guardados 🔖" else "Guardar 🔖") },
                            onClick = {
                                mostrarMenuAcciones = false
                                onAlternarFavorito()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Compartir 📤") },
                            onClick = {
                                mostrarMenuAcciones = false
                                onCompartir()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Editar ✏️") },
                            onClick = {
                                mostrarMenuAcciones = false
                                onEditar()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar 🗑️", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                mostrarMenuAcciones = false
                                onBorrar()
                            }
                        )
                    }
                }

                // Avatar que se superpone al borde inferior de la foto
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 6.dp, bottom = 4.dp)
                ) {
                    AvatarFoto(
                        semilla = autor?.email ?: "user${post.userId}",
                        tamano = 32.dp,
                        modifier = Modifier.clickable { onAbrirPerfil() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título claro y legible
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                maxLines = 2,
                lineHeight = 18.sp,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${autor?.name ?: "Usuario ${post.userId}"} · hace ${horasDesde(post.id)} h",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Texto del cuerpo siempre visible con alta legibilidad
            Text(
                text = post.body.replace("\n", " "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fila inferior: Chip de reacción + contador de comentarios
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    ChipPop(
                        texto = "$conReaccion",
                        emoji = reaccionActual?.emoji ?: "👍",
                        seleccionado = reaccionActual != null,
                        colorFondo = MaterialTheme.colorScheme.primary,
                        onClick = {
                            mostrarPanelReacciones = !mostrarPanelReacciones
                        }
                    )

                    // Panel flotante tipo pegatina con las 6 reacciones
                    androidx.compose.animation.AnimatedVisibility(
                        visible = mostrarPanelReacciones,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Surface(
                            modifier = Modifier
                                .offset(y = (-45).dp)
                                .pegatina(
                                    colorFondo = MaterialTheme.colorScheme.surface,
                                    bordeColor = MaterialTheme.colorScheme.outline,
                                    radio = 50.dp,
                                    bordeAncho = 2.dp,
                                    sombraDesplazamiento = 4.dp
                                ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Reaccion.entries.forEach { reac ->
                                    Text(
                                        text = reac.emoji,
                                        fontSize = 20.sp,
                                        modifier = Modifier
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                onReaccionar(reac)
                                                mostrarPanelReacciones = false
                                            }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                InsigniaPop(
                    texto = "💬 5",
                    colorFondo = MaterialTheme.colorScheme.surfaceVariant,
                    colorTexto = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
