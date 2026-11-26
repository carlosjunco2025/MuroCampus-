package com.tecsup.murocampus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.EmojiAnimado
import com.tecsup.murocampus.ui.componentes.FondoFlotante
import com.tecsup.murocampus.ui.componentes.RecuadroErrorPop
import com.tecsup.murocampus.ui.componentes.RecuadroExitoPop
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditar(
    vm: PostsViewModel,
    id: Int,
    tituloInicial: String,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable { mutableStateOf(tituloInicial) }
    val estadoEdicion by vm.edicion.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    BackHandler {
        vm.limpiarEdicion()
        onVolver()
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.limpiarEdicion()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        FondoFlotante(animacionesActivas = animacionesActivas)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        vm.limpiarEdicion()
                        onVolver()
                    }) {
                        Text("⬅️", fontSize = 20.sp)
                    }
                    Text(text = "Editar Título", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.size(6.dp))
                    EmojiAnimado(emoji = "✏️", tamano = 28.dp, animacionesActivas = animacionesActivas)
                }
            }

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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título de la publicación", fontSize = 12.sp) },
                        singleLine = false,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    BotonPop(
                        onClick = { vm.editarTitulo(id, titulo) },
                        habilitado = titulo.isNotBlank() && titulo != tituloInicial && estadoEdicion !is UiState.Cargando,
                        colorFondo = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💾 Guardar Cambios en Servidor", color = Color.White, fontWeight = FontWeight.Black)
                    }

                    when (val estadoActual = estadoEdicion) {
                        null -> Unit
                        UiState.Cargando -> Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Guardando en servidor...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        is UiState.Error -> RecuadroErrorPop(
                            mensaje = estadoActual.mensaje,
                            onReintentar = { vm.editarTitulo(id, titulo) }
                        )

                        is UiState.Exito<Post> -> RecuadroExitoPop(
                            texto = "Título actualizado correctamente: ${estadoActual.datos.title}"
                        )
                    }
                }
            }
        }
    }
}
