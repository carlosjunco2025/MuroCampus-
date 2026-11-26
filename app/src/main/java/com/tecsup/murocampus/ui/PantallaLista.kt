package com.tecsup.murocampus.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comunidad
import com.tecsup.murocampus.ui.componentes.EsqueletoTarjeta
import com.tecsup.murocampus.ui.componentes.EstadoError
import com.tecsup.murocampus.ui.componentes.EstadoVacio
import com.tecsup.murocampus.ui.componentes.TarjetaPublicacion
import com.tecsup.murocampus.ui.theme.Dimens
import com.tecsup.murocampus.viewmodel.Orden
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState
import kotlinx.coroutines.launch

/** Pantalla principal del Muro estilo red social para MuroCampus 2.0. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.listaVisible.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val guardados by vm.guardados.collectAsStateWithLifecycle()
    val autores by vm.autores.collectAsStateWithLifecycle()
    val vistaCompacta by vm.vistaCompacta.collectAsStateWithLifecycle()
    val comunidadSeleccionada by vm.comunidadSeleccionada.collectAsStateWithLifecycle()
    val ordenActual by vm.orden.collectAsStateWithLifecycle()

    var mostrarBuscador by remember { mutableStateOf(false) }
    var expandirOrden by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val mostrarScrollArriba by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 3 }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera de búsqueda y filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.md, vertical = Dimens.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
            ) {
                if (mostrarBuscador) {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { vm.buscar(it) },
                        placeholder = { Text(text = "Buscar por título...", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = {
                                vm.buscar("")
                                mostrarBuscador = false
                            }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar búsqueda")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { mostrarBuscador = true }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                    }
                }

                Box {
                    Button(
                        onClick = { expandirOrden = true },
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when (ordenActual) {
                                Orden.RECIENTES -> "Recientes"
                                Orden.MAS_REACCIONES -> "Reacciones"
                                Orden.TITULO_AZ -> "A-Z"
                            },
                            fontSize = 11.sp
                        )
                    }
                    DropdownMenu(
                        expanded = expandirOrden,
                        onDismissRequest = { expandirOrden = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Recientes") },
                            onClick = {
                                vm.cambiarOrden(Orden.RECIENTES)
                                expandirOrden = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Más reacciones") },
                            onClick = {
                                vm.cambiarOrden(Orden.MAS_REACCIONES)
                                expandirOrden = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Título A-Z") },
                            onClick = {
                                vm.cambiarOrden(Orden.TITULO_AZ)
                                expandirOrden = false
                            }
                        )
                    }
                }
            }

            // Chips de comunidades
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.xs),
                contentPadding = PaddingValues(horizontal = Dimens.md),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
            ) {
                item {
                    FilterChip(
                        selected = comunidadSeleccionada == null,
                        onClick = { vm.seleccionarComunidad(null) },
                        label = { Text("Todas") },
                        shape = RoundedCornerShape(50)
                    )
                }
                items(Comunidad.entries) { comunidad ->
                    FilterChip(
                        selected = comunidadSeleccionada == comunidad,
                        onClick = { vm.seleccionarComunidad(comunidad) },
                        label = { Text("${comunidad.emoji} ${comunidad.nombre}") },
                        shape = RoundedCornerShape(50)
                    )
                }
            }

            // Contenido del muro
            Crossfade(targetState = estado, label = "EstadoMuro") { currentState ->
                when (currentState) {
                    is UiState.Cargando -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(Dimens.md),
                            verticalArrangement = Arrangement.spacedBy(Dimens.md)
                        ) {
                            items(4) {
                                EsqueletoTarjeta()
                            }
                        }
                    }
                    is UiState.Error -> {
                        EstadoError(
                            mensaje = currentState.mensaje,
                            onReintentar = { vm.cargar() }
                        )
                    }
                    is UiState.Exito -> {
                        val posts = currentState.datos
                        if (posts.isEmpty()) {
                            EstadoVacio(
                                mensaje = if (busqueda.isNotBlank()) "Sin resultados para \"$busqueda\"" else "No hay publicaciones",
                                subtitulo = "Intenta con otra búsqueda o comunidad."
                            )
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(Dimens.md),
                                verticalArrangement = Arrangement.spacedBy(Dimens.md)
                            ) {
                                items(posts, key = { it.id }) { post ->
                                    val autor = autores[post.userId]
                                    val esGuardado = guardados.any { it.id == post.id }

                                    TarjetaPublicacion(
                                        post = post,
                                        autorEmail = autor?.email,
                                        esFavorito = esGuardado,
                                        compacta = vistaCompacta,
                                        onAlternarFavorito = { vm.alternarGuardado(post) },
                                        onClick = { onAbrir(post.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Botón flotante para subir al inicio
        AnimatedVisibility(
            visible = mostrarScrollArriba,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimens.xl)
        ) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Subir al inicio")
            }
        }
    }
}
