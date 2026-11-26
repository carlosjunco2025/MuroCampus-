package com.tecsup.murocampus.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comunidad
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.model.comunidadDe
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.ConfetiCanvas
import com.tecsup.murocampus.ui.componentes.EmojiAnimado
import com.tecsup.murocampus.ui.componentes.FondoFlotante
import com.tecsup.murocampus.ui.componentes.InsigniaPop
import com.tecsup.murocampus.ui.componentes.TarjetaPolaroid
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaExplorar(
    vm: PostsViewModel,
    onAbrirComunidad: (String) -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val comunidadesUnidas by vm.comunidadesUnidas.collectAsStateWithLifecycle()
    val estadoMuro by vm.lista.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    var confetiActivo by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        FondoFlotante(animacionesActivas = animacionesActivas)
        ConfetiCanvas(activo = confetiActivo)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pegatina(
                            colorFondo = MaterialTheme.colorScheme.primaryContainer,
                            bordeColor = MaterialTheme.colorScheme.outline,
                            radio = 24.dp,
                            bordeAncho = 2.dp,
                            sombraDesplazamiento = 4.dp
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Explora Comunidades",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            EmojiAnimado(emoji = "🧭", tamano = 28.dp, animacionesActivas = animacionesActivas)
                        }
                        Text(
                            text = "Únete a tus grupos favoritos del campus",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Tendencias ahora (Top 5 publicaciones con más reacciones)
            item {
                if (estadoMuro is UiState.Exito) {
                    val posts = (estadoMuro as UiState.Exito<List<Post>>).datos
                    val topPosts = remember(posts) {
                        posts.sortedByDescending { ((it.id * 37) % 90) + 5 }.take(5)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InsigniaPop(texto = "TENDENCIAS AHORA", emoji = "🔥", colorFondo = Color(0xFFFF9F1C), colorTexto = Color.White)

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(topPosts) { post ->
                                val com = comunidadDe(post.id)
                                Box(
                                    modifier = Modifier
                                        .size(width = 160.dp, height = 110.dp)
                                        .pegatina(
                                            colorFondo = com.color.copy(alpha = 0.9f),
                                            bordeColor = MaterialTheme.colorScheme.outline,
                                            radio = 16.dp,
                                            bordeAncho = 2.dp,
                                            sombraDesplazamiento = 3.dp
                                        )
                                        .clickable { onAbrirDetalle(post.id) }
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                                        Text(text = com.emoji, fontSize = 20.sp)
                                        Text(
                                            text = post.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Mosaico bento de las 8 comunidades
            item {
                Text(
                    text = "Todas las Comunidades (8)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }

            item {
                val postsList = (estadoMuro as? UiState.Exito)?.datos ?: emptyList()

                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(520.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(Comunidad.entries) { comunidad ->
                        val estaUnido = comunidadesUnidas.contains(comunidad)
                        val cantPosts = remember(postsList) { postsList.count { comunidadDe(it.id) == comunidad } }
                        val esGiga = comunidad.ordinal < 2

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (esGiga) 180.dp else 120.dp)
                                .pegatina(
                                    colorFondo = comunidad.color,
                                    bordeColor = MaterialTheme.colorScheme.outline,
                                    radio = 20.dp,
                                    bordeAncho = 2.dp,
                                    sombraDesplazamiento = 4.dp
                                )
                                .clickable { onAbrirComunidad(comunidad.name) }
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    EmojiAnimado(
                                        emoji = comunidad.emoji,
                                        tamano = if (esGiga) 36.dp else 28.dp,
                                        animacionesActivas = animacionesActivas
                                    )
                                    InsigniaPop(
                                        texto = "$cantPosts posts",
                                        colorFondo = Color.White.copy(alpha = 0.85f),
                                        colorTexto = Color(0xFF1B1B2F)
                                    )
                                }

                                Column {
                                    Text(
                                        text = comunidad.nombre,
                                        fontSize = if (esGiga) 15.sp else 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    BotonPop(
                                        onClick = {
                                            if (!estaUnido) confetiActivo = true
                                            vm.unirseComunidad(comunidad)
                                        },
                                        colorFondo = if (estaUnido) Color.White.copy(alpha = 0.3f) else Color.White
                                    ) {
                                        Text(
                                            text = if (estaUnido) "Unido ✓" else "Unirme",
                                            color = if (estaUnido) Color.White else Color(0xFF1B1B2F),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Pantalla de detalle de una comunidad seleccionada con su muro filtrado. */
@Composable
fun PantallaComunidad(
    nombreComunidad: String,
    vm: PostsViewModel,
    onVolver: () -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    onAbrirPerfilAutor: (Int) -> Unit,
    onEditar: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val comunidad = remember(nombreComunidad) {
        try { Comunidad.valueOf(nombreComunidad) } catch (e: Exception) { Comunidad.CHARLA }
    }
    val estadoMuro by vm.lista.collectAsStateWithLifecycle()
    val comunidadesUnidas by vm.comunidadesUnidas.collectAsStateWithLifecycle()
    val guardados by vm.guardados.collectAsStateWithLifecycle()
    val reacciones by vm.reacciones.collectAsStateWithLifecycle()
    val autores by vm.autores.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    val estaUnido = comunidadesUnidas.contains(comunidad)
    val postsComunidad = (estadoMuro as? UiState.Exito)?.datos?.filter { comunidadDe(it.id) == comunidad } ?: emptyList()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pegatina(
                            colorFondo = comunidad.color,
                            bordeColor = MaterialTheme.colorScheme.outline,
                            radio = 24.dp,
                            bordeAncho = 2.dp,
                            sombraDesplazamiento = 4.dp
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        IconButton(onClick = onVolver) {
                            Text(text = "⬅️", fontSize = 24.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            EmojiAnimado(emoji = comunidad.emoji, tamano = 44.dp, animacionesActivas = animacionesActivas)
                            Spacer(modifier = Modifier.size(10.dp))
                            Column {
                                Text(
                                    text = comunidad.nombre,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = comunidad.descripcion,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InsigniaPop(texto = "${postsComunidad.size} publicaciones", colorFondo = Color.White, colorTexto = Color(0xFF1B1B2F))
                            BotonPop(
                                onClick = { vm.unirseComunidad(comunidad) },
                                colorFondo = Color.White
                            ) {
                                Text(
                                    text = if (estaUnido) "Unido ✓" else "+ Unirme",
                                    color = Color(0xFF1B1B2F),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            items(postsComunidad, key = { it.id }) { post ->
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
                    onCompartir = {},
                    onEditar = { onEditar(post.id, post.title) },
                    onBorrar = { vm.borrar(post.id) }
                )
            }
        }
    }
}
