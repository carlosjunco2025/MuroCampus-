package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.EmojiAnimado
import com.tecsup.murocampus.ui.componentes.EstadoVacioPop
import com.tecsup.murocampus.ui.componentes.FondoFlotante
import com.tecsup.murocampus.ui.componentes.InsigniaPop
import com.tecsup.murocampus.ui.componentes.TarjetaPolaroid
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.PostsViewModel

@Composable
fun PantallaFavoritos(
    vm: PostsViewModel,
    onAbrirDetalle: (Int) -> Unit,
    onAbrirPerfilAutor: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val guardados by vm.guardados.collectAsStateWithLifecycle()
    val reacciones by vm.reacciones.collectAsStateWithLifecycle()
    val autores by vm.autores.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        FondoFlotante(animacionesActivas = animacionesActivas)

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mis Guardados",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                EmojiAnimado(emoji = "🔖", tamano = 28.dp, animacionesActivas = animacionesActivas)
                            }
                            Text(
                                text = "Tus publicaciones preferidas en un solo lugar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        if (guardados.isNotEmpty()) {
                            BotonPop(onClick = { vm.borrarFavoritos() }, colorFondo = MaterialTheme.colorScheme.error) {
                                Text("Limpiar 🧹", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            if (guardados.isEmpty()) {
                item {
                    EstadoVacioPop(
                        emoji = "🔖",
                        texto = "Aún no tienes publicaciones guardadas",
                        subtitulo = "Toca el ícono 🔖 en cualquier tarjeta del muro para guardarla aquí."
                    )
                }
            } else {
                items(guardados, key = { it.id }) { post ->
                    val autor = autores[post.userId]
                    val reac = reacciones[post.id]

                    TarjetaPolaroid(
                        post = post,
                        autor = autor,
                        esFavorito = true,
                        reaccionActual = reac,
                        compacta = false,
                        animacionesActivas = animacionesActivas,
                        onAlternarFavorito = { vm.alternarGuardado(post) },
                        onReaccionar = { r -> vm.reaccionar(post.id, r) },
                        onClick = { onAbrirDetalle(post.id) },
                        onAbrirPerfil = { onAbrirPerfilAutor(post.userId) },
                        onCompartir = {},
                        onEditar = { vm.editarTitulo(post.id, post.title) },
                        onBorrar = { vm.borrar(post.id) }
                    )
                }
            }
        }
    }
}
