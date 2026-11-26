package com.tecsup.murocampus.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.comunidadDe
import com.tecsup.murocampus.ui.componentes.AvatarFoto
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.EmojiAnimado
import com.tecsup.murocampus.ui.componentes.EsqueletoPop
import com.tecsup.murocampus.ui.componentes.EstadoErrorPop
import com.tecsup.murocampus.ui.componentes.EstadoVacioPop
import com.tecsup.murocampus.ui.componentes.FondoFlotante
import com.tecsup.murocampus.ui.componentes.InsigniaPop
import com.tecsup.murocampus.ui.componentes.TarjetaPolaroid
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.Insignia
import com.tecsup.murocampus.viewmodel.PostsViewModel

@Composable
fun PantallaPerfil(
    vm: PostsViewModel,
    onAbrirAjustes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nombrePerfil by vm.nombrePerfil.collectAsStateWithLifecycle()
    val avatarSemilla by vm.avatarSemilla.collectAsStateWithLifecycle()
    val puntos by vm.puntos.collectAsStateWithLifecycle()
    val nivel by vm.nivel.collectAsStateWithLifecycle()
    val racha by vm.racha.collectAsStateWithLifecycle()
    val guardados by vm.guardados.collectAsStateWithLifecycle()
    val comunidadesUnidas by vm.comunidadesUnidas.collectAsStateWithLifecycle()
    val misPublicaciones by vm.misPublicaciones.collectAsStateWithLifecycle()
    val insignias by vm.insignias.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    val progresoNivel = remember(puntos) { (puntos % 50) / 50f }
    val progresoAnim by animateFloatAsState(targetValue = progresoNivel, animationSpec = tween(1000), label = "progreso_nivel")

    Box(modifier = modifier.fillMaxSize()) {
        FondoFlotante(animacionesActivas = animacionesActivas)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Portada de Perfil
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
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            BotonPop(onClick = onAbrirAjustes, colorFondo = Color.White) {
                                Text("⚙️ Ajustes", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        AvatarFoto(semilla = avatarSemilla, tamano = 88.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = nombrePerfil,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        InsigniaPop(
                            texto = "Nivel $nivel · $puntos pts",
                            emoji = "⭐",
                            colorFondo = MaterialTheme.colorScheme.primary,
                            colorTexto = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Barra de progreso de nivel
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progresoAnim)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }

            // Estadísticas en pegatinas de números
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .pegatina(
                                colorFondo = MaterialTheme.colorScheme.surface,
                                bordeColor = MaterialTheme.colorScheme.outline,
                                radio = 16.dp,
                                bordeAncho = 2.dp,
                                sombraDesplazamiento = 3.dp
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${misPublicaciones.size}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Posts", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .pegatina(
                                colorFondo = MaterialTheme.colorScheme.surface,
                                bordeColor = MaterialTheme.colorScheme.outline,
                                radio = 16.dp,
                                bordeAncho = 2.dp,
                                sombraDesplazamiento = 3.dp
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${guardados.size}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Guardados", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .pegatina(
                                colorFondo = MaterialTheme.colorScheme.surface,
                                bordeColor = MaterialTheme.colorScheme.outline,
                                radio = 16.dp,
                                bordeAncho = 2.dp,
                                sombraDesplazamiento = 3.dp
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${comunidadesUnidas.size}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Grupos", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .pegatina(
                                colorFondo = MaterialTheme.colorScheme.surface,
                                bordeColor = MaterialTheme.colorScheme.outline,
                                radio = 16.dp,
                                bordeAncho = 2.dp,
                                sombraDesplazamiento = 3.dp
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$racha d", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Racha 🔥", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Logros e Insignias
            item {
                Text(text = "Logros e Insignias 🏆", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(insignias) { ins ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pegatina(
                                    colorFondo = if (ins.desbloqueada) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
                                    bordeColor = MaterialTheme.colorScheme.outline,
                                    radio = 16.dp,
                                    bordeAncho = 2.dp,
                                    sombraDesplazamiento = 3.dp
                                )
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (ins.desbloqueada) {
                                    EmojiAnimado(emoji = ins.emoji, tamano = 28.dp, animacionesActivas = animacionesActivas)
                                } else {
                                    Text(text = "🔒", fontSize = 24.sp)
                                }

                                Column {
                                    Text(
                                        text = ins.titulo,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (ins.desbloqueada) MaterialTheme.colorScheme.onSurface else Color.Gray
                                    )
                                    Text(
                                        text = ins.descripcion,
                                        fontSize = 9.sp,
                                        color = Color.Gray,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Mis Publicaciones
            item {
                Text(text = "Mis Publicaciones Creadas 📝", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }

            if (misPublicaciones.isEmpty()) {
                item {
                    EstadoVacioPop(emoji = "😴", texto = "Aún no has publicado nada", subtitulo = "Toca el botón ✏️ para crear tu primera publicación.")
                }
            } else {
                items(misPublicaciones) { pub ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pegatina(
                                colorFondo = pub.comunidad.color,
                                bordeColor = MaterialTheme.colorScheme.outline,
                                radio = 18.dp,
                                bordeAncho = 2.dp,
                                sombraDesplazamiento = 3.dp
                            )
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InsigniaPop(texto = pub.comunidad.nombre, emoji = pub.comunidad.emoji, colorFondo = Color.White, colorTexto = Color(0xFF1B1B2F))
                                Text(text = "Publicada · id ${pub.idServidor ?: "local"}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Text(text = pub.titulo, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text(text = pub.texto, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfilAutor(
    vm: PostsViewModel,
    userId: Int,
    onVolver: () -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val autores by vm.autoresLista.collectAsStateWithLifecycle()
    val autor = autores.find { it.id == userId }
    val guardados by vm.guardados.collectAsStateWithLifecycle()
    val reacciones by vm.reacciones.collectAsStateWithLifecycle()
    val posts by vm.postsPerfil.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    val comunidadPrincipal = remember(posts) {
        if (posts.isNotEmpty()) comunidadDe(posts.first().id) else com.tecsup.murocampus.model.Comunidad.CHARLA
    }

    LaunchedEffect(userId) {
        vm.cargarPostsPerfil(userId)
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pegatina(
                            colorFondo = comunidadPrincipal.color,
                            bordeColor = MaterialTheme.colorScheme.outline,
                            radio = 24.dp,
                            bordeAncho = 2.dp,
                            sombraDesplazamiento = 4.dp
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            IconButton(onClick = onVolver) {
                                Text("⬅️", fontSize = 20.sp)
                            }
                        }

                        AvatarFoto(semilla = autor?.email ?: "user$userId", tamano = 80.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = autor?.name ?: "Usuario $userId",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = autor?.email ?: "user$userId@campus.edu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        InsigniaPop(
                            texto = "${posts.size} publicaciones en el muro",
                            colorFondo = Color.White,
                            colorTexto = Color(0xFF1B1B2F)
                        )
                    }
                }
            }

            items(posts, key = { it.id }) { post ->
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
                    onAbrirPerfil = {},
                    onCompartir = {},
                    onEditar = { vm.editarTitulo(post.id, post.title) },
                    onBorrar = { vm.borrar(post.id) }
                )
            }
        }
    }
}
