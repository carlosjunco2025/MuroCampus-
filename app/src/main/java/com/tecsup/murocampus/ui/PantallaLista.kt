package com.tecsup.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

private data class InsigniaUsuario(val fondo: Color, val texto: Color)

private fun obtenerColorUsuario(userId: Int): InsigniaUsuario = when (userId) {
    1 -> InsigniaUsuario(Color(0xFF2F78C4), Color.White)
    2 -> InsigniaUsuario(Color(0xFFF5A800), Color(0xFF1F1F1F))
    3 -> InsigniaUsuario(Color(0xFF2E7D32), Color.White)
    4 -> InsigniaUsuario(Color(0xFF7B1FA2), Color.White)
    5 -> InsigniaUsuario(Color(0xFF00796B), Color.White)
    6 -> InsigniaUsuario(Color(0xFFE65100), Color.White)
    7 -> InsigniaUsuario(Color(0xFFC62828), Color.White)
    8 -> InsigniaUsuario(Color(0xFF283593), Color.White)
    9 -> InsigniaUsuario(Color(0xFFAD1457), Color.White)
    10 -> InsigniaUsuario(Color(0xFFD84315), Color.White)
    else -> InsigniaUsuario(Color(0xFF616161), Color.White)
}

/** Tarjeta blanca de cargando con borde gris claro. */
@Composable
private fun RecuadroCargando(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Cargando publicaciones…",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

/** Recuadro rosado de error con mensaje centrado y botón píldora azul. */
@Composable
private fun RecuadroError(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "$mensaje\nRevisa tu red e inténtalo otra vez.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onReintentar,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Reintentar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/** Tarjeta individual de una publicación en la lista. */
@Composable
private fun TarjetaPostItem(
    post: Post,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coloresUsuario = obtenerColorUsuario(post.userId)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = coloresUsuario.fondo
            ) {
                Text(
                    text = "Usuario ${post.userId}",
                    color = coloresUsuario.texto,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = post.body.replace("\n", " "),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Lista de publicaciones con filtro por usuario. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        // Fila de chips de filtro (siempre visible)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val seleccionado = filtro == null
                FilterChip(
                    selected = seleccionado,
                    onClick = { vm.filtrar(null) },
                    label = {
                        Text(
                            text = "Todos",
                            fontSize = 12.sp,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(50),
                    leadingIcon = null,
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = seleccionado,
                        borderColor = Color(0xFFE0E0E0),
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        labelColor = Color(0xFF333333),
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items((1..10).toList()) { numero ->
                val seleccionado = filtro == numero
                FilterChip(
                    selected = seleccionado,
                    onClick = { vm.filtrar(numero) },
                    label = {
                        Text(
                            text = "Usuario $numero",
                            fontSize = 12.sp,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(50),
                    leadingIcon = null,
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = seleccionado,
                        borderColor = Color(0xFFE0E0E0),
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        labelColor = Color(0xFF333333),
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val estadoActual = estado) {
                UiState.Cargando -> RecuadroCargando()

                is UiState.Error -> RecuadroError(
                    mensaje = estadoActual.mensaje,
                    onReintentar = { vm.cargar() }
                )

                is UiState.Exito<List<Post>> -> {
                    if (estadoActual.datos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin publicaciones",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(estadoActual.datos) { post ->
                                TarjetaPostItem(
                                    post = post,
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
