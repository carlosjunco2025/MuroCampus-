package com.tecsup.murocampus.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.Reaccion
import com.tecsup.murocampus.model.comunidadDe
import com.tecsup.murocampus.model.horasDesde
import com.tecsup.murocampus.model.minutosLectura
import com.tecsup.murocampus.model.reaccionesBase
import com.tecsup.murocampus.model.urlFoto
import com.tecsup.murocampus.ui.componentes.AvatarFoto
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.FotoRemota
import com.tecsup.murocampus.ui.componentes.InsigniaPop
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.DetalleUi
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

/** Globo de chat colorido para un comentario. */
@Composable
private fun GloboComentario(
    comentario: Comentario,
    indice: Int,
    modifier: Modifier = Modifier
) {
    val esPar = indice % 2 == 0
    val colorFondo = if (esPar) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val colorTexto = if (esPar) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pegatina(
                colorFondo = colorFondo,
                bordeColor = MaterialTheme.colorScheme.outline,
                radio = 18.dp,
                bordeAncho = 2.dp,
                sombraDesplazamiento = 3.dp
            )
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvatarFoto(semilla = comentario.email, tamano = 28.dp)
                Column {
                    Text(
                        text = comentario.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = colorTexto
                    )
                    Text(
                        text = comentario.email,
                        fontSize = 10.sp,
                        color = colorTexto.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comentario.body,
                style = MaterialTheme.typography.bodySmall,
                color = colorTexto,
                fontSize = 11.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    vm: PostsViewModel,
    id: Int,
    onVolver: () -> Unit,
    onEditar: (id: Int, titulo: String) -> Unit,
    onAbrirPerfilAutor: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val estadoBorrado by vm.borrado.collectAsStateWithLifecycle()
    val guardados by vm.guardados.collectAsStateWithLifecycle()
    val reacciones by vm.reacciones.collectAsStateWithLifecycle()
    val autores by vm.autores.collectAsStateWithLifecycle()
    val comentandoState by vm.comentando.collectAsStateWithLifecycle()

    var mostrarDialogoBorrar by rememberSaveable { mutableStateOf(false) }
    var textoComentario by rememberSaveable { mutableStateOf("") }

    if (mostrarDialogoBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrar = false },
            title = { Text(text = "¿Eliminar esta publicación?", fontWeight = FontWeight.Black) },
            text = { Text(text = "Esta acción eliminará la publicación del muro. ¿Deseas continuar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoBorrar = false
                        vm.borrar(id)
                        onVolver()
                    }
                ) {
                    Text(text = "Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBorrar = false }) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Barra inferior fija para comentar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .pegatina(
                        colorFondo = MaterialTheme.colorScheme.surface,
                        bordeColor = MaterialTheme.colorScheme.outline,
                        radio = 0.dp,
                        bordeAncho = 2.dp,
                        sombraDesplazamiento = 0.dp
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = textoComentario,
                        onValueChange = { textoComentario = it },
                        placeholder = { Text("Escribe un comentario...", fontSize = 12.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    BotonPop(
                        onClick = {
                            if (textoComentario.isNotBlank()) {
                                vm.comentar(id, textoComentario)
                                textoComentario = ""
                            }
                        },
                        habilitado = textoComentario.isNotBlank() && comentandoState !is UiState.Cargando,
                        colorFondo = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text(text = "💬", fontSize = 20.sp)
                    }
                }
            }
        }
    ) { innerPadding ->
        when (val estadoActual = estado) {
            UiState.Cargando -> Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Cargando publicación...", style = MaterialTheme.typography.bodyMedium)
            }

            is UiState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = estadoActual.mensaje, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(12.dp))
                BotonPop(onClick = { vm.abrir(id) }) { Text("Reintentar 🔄") }
            }

            is UiState.Exito<DetalleUi> -> {
                val post = estadoActual.datos.post
                val comentarios = estadoActual.datos.comentarios
                val comunidad = comunidadDe(post.id)
                val autor = autores[post.userId]
                val esFav = guardados.any { it.id == post.id }
                val reaccionMia = reacciones[post.id]

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Foto 16:9 con botones de acción flotantes
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .pegatina(
                                    colorFondo = comunidad.color,
                                    bordeColor = MaterialTheme.colorScheme.outline,
                                    radio = 24.dp,
                                    bordeAncho = 2.dp,
                                    sombraDesplazamiento = 4.dp
                                )
                        ) {
                            FotoRemota(
                                url = urlFoto(post.id, 800, 450),
                                comunidad = comunidad,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Botón Volver flotante
                            BotonPop(
                                onClick = onVolver,
                                colorFondo = Color.White,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                                    .size(40.dp)
                            ) {
                                Text("⬅️", fontSize = 18.sp)
                            }

                            // Botones de guardar y compartir flotantes
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BotonPop(
                                    onClick = { vm.alternarGuardado(post) },
                                    colorFondo = if (esFav) Color(0xFFFF4D9D) else Color.White,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Text(if (esFav) "❤️" else "🔖", fontSize = 18.sp)
                                }

                                BotonPop(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, post.title)
                                            putExtra(Intent.EXTRA_TEXT, "${post.title}\n\n${post.body}")
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Compartir"))
                                    },
                                    colorFondo = Color.White,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Text("📤", fontSize = 18.sp)
                                }
                            }
                        }
                    }

                    // Detalle del Post
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pegatina(
                                    colorFondo = MaterialTheme.colorScheme.surface,
                                    bordeColor = MaterialTheme.colorScheme.outline,
                                    radio = 20.dp,
                                    bordeAncho = 2.dp,
                                    sombraDesplazamiento = 4.dp
                                )
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Fila del autor
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onAbrirPerfilAutor(post.userId) },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    AvatarFoto(semilla = autor?.email ?: "user${post.userId}", tamano = 44.dp)
                                    Column {
                                        Text(
                                            text = autor?.name ?: "Usuario ${post.userId}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "${comunidad.emoji} ${comunidad.nombre} · hace ${horasDesde(post.id)} h",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = post.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black
                                )

                                Text(
                                    text = post.body,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    InsigniaPop(
                                        texto = "≈ ${minutosLectura(post.body)} min de lectura",
                                        emoji = "⏱️",
                                        colorFondo = MaterialTheme.colorScheme.primaryContainer,
                                        colorTexto = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        BotonPop(onClick = { onEditar(post.id, post.title) }, colorFondo = MaterialTheme.colorScheme.primary) {
                                            Text("✏️ Editar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                        BotonPop(onClick = { mostrarDialogoBorrar = true }, colorFondo = MaterialTheme.colorScheme.error) {
                                            Text("🗑️ Borrar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Fila de las 6 reacciones como pegatinas seleccionables
                                Text(
                                    text = "Reacciones",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Reaccion.entries.forEach { reac ->
                                        val seleccionada = reaccionMia == reac
                                        val cont = reaccionesBase(post.id) / 6 + if (seleccionada) 1 else 0

                                        Box(
                                            modifier = Modifier
                                                .pegatina(
                                                    colorFondo = if (seleccionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                                    bordeColor = MaterialTheme.colorScheme.outline,
                                                    radio = 12.dp,
                                                    bordeAncho = 1.5.dp,
                                                    sombraDesplazamiento = 2.dp
                                                )
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    vm.reaccionar(post.id, reac)
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Text(text = reac.emoji, fontSize = 16.sp)
                                                Text(
                                                    text = "$cont",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (seleccionada) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Lista de comentarios como globos de chat
                    item {
                        Text(
                            text = "Comentarios (${comentarios.size}) 💬",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }

                    items(comentarios.reversed(), key = { it.id }) { com ->
                        GloboComentario(comentario = com, indice = com.id)
                    }
                }
            }
        }
    }
}
