package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

/** Lista con fila de chips; los chips se ven también durante Cargando y Error. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()

    val chipForma = RoundedCornerShape(50)
    val chipColores = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primary,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.surface,
        labelColor = MaterialTheme.colorScheme.onSurface
    )

    Column(modifier = modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = filtro == null,
                    onClick = { vm.filtrar(null) },
                    label = {
                        Text(
                            "Todos",
                            fontWeight = if (filtro == null) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = chipForma,
                    colors = chipColores,
                    leadingIcon = {}
                )
            }
            items((1..10).toList()) { numero ->
                val seleccionado = filtro == numero
                FilterChip(
                    selected = seleccionado,
                    onClick = { vm.filtrar(numero) },
                    label = {
                        Text(
                            "Usuario $numero",
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = chipForma,
                    colors = chipColores,
                    leadingIcon = {}
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when (val estadoActual = estado) {
                UiState.Cargando -> Cargando()

                is UiState.Error -> ErrorConReintento(
                    mensaje = estadoActual.mensaje,
                    onReintentar = { vm.cargar() }
                )

                is UiState.Exito<List<Post>> -> {
                    if (estadoActual.datos.isEmpty()) {
                        Text(
                            text = "Sin publicaciones",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(estadoActual.datos) { post ->
                                TarjetaPost(
                                    usuario = post.userId,
                                    titulo = post.title,
                                    cuerpo = post.body,
                                    onClick = { onAbrir(post.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
