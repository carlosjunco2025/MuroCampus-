package com.tecsup.murocampus.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comunidad
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.model.retoDelDia
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.ChipPop
import com.tecsup.murocampus.ui.componentes.EmojiAnimado
import com.tecsup.murocampus.ui.componentes.EsqueletoPop
import com.tecsup.murocampus.ui.componentes.EstadoErrorPop
import com.tecsup.murocampus.ui.componentes.EstadoVacioPop
import com.tecsup.murocampus.ui.componentes.FondoFlotante
import com.tecsup.murocampus.ui.componentes.InsigniaPop
import com.tecsup.murocampus.ui.componentes.TarjetaPolaroid
import com.tecsup.murocampus.ui.componentes.TextoMarquesina
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.Orden
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaInicio(
    vm: PostsViewModel,
    onAbrirDetalle: (Int) -> Unit,
    onAbrirPerfilAutor: (Int) -> Unit,
    onAceptarReto: (comunidad: String, sugerencia: String) -> Unit,
    onEditar: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val estado by vm.listaVisible.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val guardados by vm.guardados.collectAsStateWithLifecycle()
    val reacciones by vm.reacciones.collectAsStateWithLifecycle()
    val autores by vm.autores.collectAsStateWithLifecycle()
    val comunidadSeleccionada by vm.comunidadSeleccionada.collectAsStateWithLifecycle()
    val ordenActual by vm.orden.collectAsStateWithLifecycle()
    val nombrePerfil by vm.nombrePerfil.collectAsStateWithLifecycle()
    val puntos by vm.puntos.collectAsStateWithLifecycle()
    val racha by vm.racha.collectAsStateWithLifecycle()
    val vistaCuadricula by vm.vistaCuadricula.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    var mostrarBuscador by remember { mutableStateOf(false) }
    var expandirOrden by remember { mutableStateOf(false) }

    val staggeredGridState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    val mostrarBotonSubir by remember {
        derivedStateOf { staggeredGridState.firstVisibleItemIndex > 4 }
    }

    val reto = remember { retoDelDia(System.currentTimeMillis() / 86_400_000) }

    val saludoHora = remember {
        val hora = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hora) {
            in 6..11 -> "Buenos días"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }

    LaunchedEffect(Unit) {
        vm.registrarVisita()
    }

    // Composables de cabecera reutilizables
    @Composable
    fun CabeceraPopView() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pegatina(
                    colorFondo = MaterialTheme.colorScheme.primaryContainer,
                    bordeColor = MaterialTheme.colorScheme.outline,
                    radio = 20.dp,
                    bordeAncho = 2.dp,
                    sombraDesplazamiento = 4.dp
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "¡Hola, $nombrePerfil!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        EmojiAnimado(emoji = "👋", tamano = 22.dp, animacionesActivas = animacionesActivas)
                    }
                    Text(
                        text = "$saludoHora, ¿qué compartes hoy?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    InsigniaPop(
                        texto = "$racha d",
                        emoji = "🔥",
                        colorFondo = Color(0xFFFF9F1C),
                        colorTexto = Color.White
                    )
                    InsigniaPop(
                        texto = "$puntos pts",
                        emoji = "⭐",
                        colorFondo = Color(0xFFFFD93D),
                        colorTexto = Color(0xFF1B1B2F)
                    )
                }
            }
        }
    }

    @Composable
    fun RetoDelDiaView() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pegatina(
                    colorFondo = reto.comunidad.color,
                    bordeColor = MaterialTheme.colorScheme.outline,
                    radio = 18.dp,
                    bordeAncho = 2.dp,
                    sombraDesplazamiento = 3.dp
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    InsigniaPop(
                        texto = "RETO DEL DÍA",
                        emoji = "🎯",
                        colorFondo = Color.White,
                        colorTexto = Color(0xFF1B1B2F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reto.titulo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = reto.comunidad.colorTexto
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                BotonPop(
                    onClick = { onAceptarReto(reto.comunidad.name, reto.sugerencia) },
                    colorFondo = Color.White
                ) {
                    Text("¡Aceptar! 🚀", color = Color(0xFF1B1B2F), fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }
        }
    }

    @Composable
    fun ChipsComunidadView() {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ChipPop(
                    texto = "Para ti",
                    emoji = "✨",
                    seleccionado = comunidadSeleccionada == null,
                    rotacion = -1f,
                    onClick = { vm.seleccionarComunidad(null) }
                )
            }
            item {
                ChipPop(
                    texto = "Todas",
                    emoji = "🌐",
                    seleccionado = comunidadSeleccionada == null,
                    rotacion = 1f,
                    onClick = { vm.seleccionarComunidad(null) }
                )
            }
            items(Comunidad.entries) { comunidad ->
                val rot = if (comunidad.ordinal % 2 == 0) 1.5f else -1.5f
                ChipPop(
                    texto = comunidad.nombre,
                    emoji = comunidad.emoji,
                    seleccionado = comunidadSeleccionada == comunidad,
                    colorFondo = comunidad.color,
                    rotacion = rot,
                    onClick = { vm.seleccionarComunidad(comunidad) }
                )
            }
        }
    }

    @Composable
    fun BarraAccionesView() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (mostrarBuscador) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { vm.buscar(it) },
                    placeholder = { Text("Buscar en el muro...", fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            } else {
                Text(
                    text = "El Muro",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { mostrarBuscador = !mostrarBuscador }) {
                    Text(if (mostrarBuscador) "❌" else "🔍", fontSize = 18.sp)
                }

                Box {
                    ChipPop(
                        texto = when (ordenActual) {
                            Orden.RECIENTES -> "Recientes"
                            Orden.MAS_REACCIONES -> "Popular"
                            Orden.TITULO_AZ -> "A-Z"
                        },
                        emoji = "↕️",
                        seleccionado = true,
                        onClick = { expandirOrden = true }
                    )

                    DropdownMenu(
                        expanded = expandirOrden,
                        onDismissRequest = { expandirOrden = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Recientes ⏱️") },
                            onClick = {
                                vm.cambiarOrden(Orden.RECIENTES)
                                expandirOrden = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Más reacciones 🔥") },
                            onClick = {
                                vm.cambiarOrden(Orden.MAS_REACCIONES)
                                expandirOrden = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Título A-Z 🔤") },
                            onClick = {
                                vm.cambiarOrden(Orden.TITULO_AZ)
                                expandirOrden = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.size(4.dp))

                IconButton(onClick = { vm.cambiarVistaCuadricula(!vistaCuadricula) }) {
                    Text(if (vistaCuadricula) "🖼️" else "📜", fontSize = 18.sp)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        FondoFlotante(animacionesActivas = animacionesActivas)

        // Contenido continuo scrolleable
        Crossfade(targetState = estado, label = "EstadoMuroPop") { currentState ->
            when (currentState) {
                UiState.Cargando -> Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CabeceraPopView()
                    repeat(3) { EsqueletoPop() }
                }

                is UiState.Error -> Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CabeceraPopView()
                    EstadoErrorPop(
                        mensaje = currentState.mensaje,
                        onReintentar = { vm.cargar() }
                    )
                }

                is UiState.Exito -> {
                    val posts = currentState.datos
                    val titulosTop = remember(posts) {
                        posts.sortedByDescending { ((it.id * 37) % 90) + 5 }.take(5).map { it.title }
                    }

                    if (vistaCuadricula) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            state = staggeredGridState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 90.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalItemSpacing = 12.dp
                        ) {
                            item(span = StaggeredGridItemSpan.FullLine) { CabeceraPopView() }
                            item(span = StaggeredGridItemSpan.FullLine) { TextoMarquesina(textos = titulosTop, animacionesActivas = animacionesActivas) }
                            item(span = StaggeredGridItemSpan.FullLine) { RetoDelDiaView() }
                            item(span = StaggeredGridItemSpan.FullLine) { ChipsComunidadView() }
                            item(span = StaggeredGridItemSpan.FullLine) { BarraAccionesView() }

                            if (posts.isEmpty()) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    EstadoVacioPop(
                                        emoji = if (busqueda.isNotBlank()) "🕵️" else "😴",
                                        texto = if (busqueda.isNotBlank()) "Sin resultados para \"$busqueda\"" else "No hay publicaciones aquí"
                                    )
                                }
                            } else {
                                items(posts, key = { it.id }) { post ->
                                    val autor = autores[post.userId]
                                    val esFav = guardados.any { it.id == post.id }
                                    val reac = reacciones[post.id]

                                    TarjetaPolaroid(
                                        post = post,
                                        autor = autor,
                                        esFavorito = esFav,
                                        reaccionActual = reac,
                                        compacta = true,
                                        animacionesActivas = animacionesActivas,
                                        onAlternarFavorito = { vm.alternarGuardado(post) },
                                        onReaccionar = { r -> vm.reaccionar(post.id, r) },
                                        onClick = { onAbrirDetalle(post.id) },
                                        onAbrirPerfil = { onAbrirPerfilAutor(post.userId) },
                                        onCompartir = {
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, post.title)
                                                putExtra(Intent.EXTRA_TEXT, "${post.title}\n\n${post.body}")
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Compartir post"))
                                        },
                                        onEditar = { onEditar(post.id, post.title) },
                                        onBorrar = { vm.borrar(post.id) }
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item { CabeceraPopView() }
                            item { TextoMarquesina(textos = titulosTop, animacionesActivas = animacionesActivas) }
                            item { RetoDelDiaView() }
                            item { ChipsComunidadView() }
                            item { BarraAccionesView() }

                            if (posts.isEmpty()) {
                                item {
                                    EstadoVacioPop(
                                        emoji = if (busqueda.isNotBlank()) "🕵️" else "😴",
                                        texto = if (busqueda.isNotBlank()) "Sin resultados para \"$busqueda\"" else "No hay publicaciones aquí"
                                    )
                                }
                            } else {
                                items(posts, key = { it.id }) { post ->
                                    val autor = autores[post.userId]
                                    val esFav = guardados.any { it.id == post.id }
                                    val reac = reacciones[post.id]

                                    TarjetaPolaroid(
                                        post = post,
                                        autor = autor,
                                        esFavorito = esFav,
                                        reaccionActual = reac,
                                        compacta = false,
                                        animacionesActivas = animacionesActivas,
                                        onAlternarFavorito = { vm.alternarGuardado(post) },
                                        onReaccionar = { r -> vm.reaccionar(post.id, r) },
                                        onClick = { onAbrirDetalle(post.id) },
                                        onAbrirPerfil = { onAbrirPerfilAutor(post.userId) },
                                        onCompartir = {
                                            val intent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, post.title)
                                                putExtra(Intent.EXTRA_TEXT, "${post.title}\n\n${post.body}")
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Compartir post"))
                                        },
                                        onEditar = { onEditar(post.id, post.title) },
                                        onBorrar = { vm.borrar(post.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botón flotante para subir al inicio 🚀
        AnimatedVisibility(
            visible = mostrarBotonSubir,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
        ) {
            BotonPop(
                onClick = {
                    coroutineScope.launch {
                        staggeredGridState.animateScrollToItem(0)
                    }
                },
                colorFondo = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            ) {
                Text(text = "🚀", fontSize = 20.sp)
            }
        }
    }
}
