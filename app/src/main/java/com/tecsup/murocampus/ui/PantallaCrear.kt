package com.tecsup.murocampus.ui

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comunidad
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.ConfetiCanvas
import com.tecsup.murocampus.ui.componentes.EmojiAnimado
import com.tecsup.murocampus.ui.componentes.InsigniaPop
import com.tecsup.murocampus.ui.componentes.RecuadroErrorPop
import com.tecsup.murocampus.ui.componentes.RecuadroExitoPop
import com.tecsup.murocampus.ui.componentes.TarjetaPolaroid
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrear(
    vm: PostsViewModel,
    comunidadInicial: String? = null,
    textoSugerido: String? = null,
    onVerPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estadoPublicacion by vm.publicacion.collectAsStateWithLifecycle()
    val nombrePerfil by vm.nombrePerfil.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    var pasoActual by rememberSaveable { mutableStateOf(if (comunidadInicial != null) 2 else 1) }
    var comunidadSeleccionada by remember {
        mutableStateOf(
            comunidadInicial?.let {
                try { Comunidad.valueOf(it) } catch (e: Exception) { Comunidad.CHARLA }
            } ?: Comunidad.CHARLA
        )
    }

    var titulo by rememberSaveable { mutableStateOf("") }
    var contenido by rememberSaveable { mutableStateOf(textoSugerido ?: "") }
    var confetiActivo by remember { mutableStateOf(false) }

    LaunchedEffect(estadoPublicacion) {
        if (estadoPublicacion is UiState.Exito) {
            confetiActivo = true
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ConfetiCanvas(activo = confetiActivo)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
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
                                text = "Crear Publicación",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            EmojiAnimado(emoji = "✏️", tamano = 28.dp, animacionesActivas = animacionesActivas)
                        }
                        Text(
                            text = if (pasoActual == 1) "Paso 1: Elige la comunidad" else "Paso 2: Escribe tu post",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (pasoActual == 1) {
                item {
                    Text(
                        text = "¿De qué quieres hablar hoy? 💬",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }

                item {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(Comunidad.entries) { com ->
                            val esSeleccionada = comunidadSeleccionada == com
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(85.dp)
                                    .pegatina(
                                        colorFondo = if (esSeleccionada) com.color else MaterialTheme.colorScheme.surface,
                                        bordeColor = MaterialTheme.colorScheme.outline,
                                        radio = 18.dp,
                                        bordeAncho = 2.dp,
                                        sombraDesplazamiento = 3.dp
                                    )
                                    .clickable {
                                        comunidadSeleccionada = com
                                        pasoActual = 2
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    EmojiAnimado(emoji = com.emoji, tamano = 28.dp, animacionesActivas = animacionesActivas)
                                    Text(
                                        text = com.nombre,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (esSeleccionada) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Paso 2: Formulario de título y contenido + Vista previa
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
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InsigniaPop(
                                    texto = comunidadSeleccionada.nombre,
                                    emoji = comunidadSeleccionada.emoji,
                                    colorFondo = comunidadSeleccionada.color,
                                    colorTexto = Color.White
                                )
                                BotonPop(
                                    onClick = { pasoActual = 1 },
                                    colorFondo = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text("Cambiar 🔄", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedTextField(
                                value = titulo,
                                onValueChange = { titulo = it.take(60) },
                                label = { Text("Título de la publicación (máx. 60)", fontSize = 12.sp) },
                                supportingText = { Text("${titulo.length}/60", fontSize = 10.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            OutlinedTextField(
                                value = contenido,
                                onValueChange = { contenido = it.take(280) },
                                label = { Text("¿Qué quieres compartir? (máx. 280)", fontSize = 12.sp) },
                                supportingText = { Text("${contenido.length}/280", fontSize = 10.sp) },
                                minLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            BotonPop(
                                onClick = {
                                    if (titulo.isNotBlank() && contenido.isNotBlank()) {
                                        vm.publicar(titulo, contenido, comunidadSeleccionada)
                                    }
                                },
                                habilitado = titulo.isNotBlank() && contenido.isNotBlank() && estadoPublicacion !is UiState.Cargando,
                                colorFondo = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("🚀 Publicar en MuroCampus (+20 pts)", color = Color.White, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                // Vista previa en vivo como TarjetaPolaroid
                item {
                    Text(
                        text = "Vista previa en vivo 👁️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }

                item {
                    val postPrevio = Post(
                        userId = 1,
                        id = 999,
                        title = if (titulo.isBlank()) "Tu título aquí..." else titulo,
                        body = if (contenido.isBlank()) "Tu mensaje aparecerá aquí..." else contenido
                    )

                    TarjetaPolaroid(
                        post = postPrevio,
                        autor = null,
                        esFavorito = false,
                        reaccionActual = null,
                        compacta = false,
                        animacionesActivas = animacionesActivas,
                        onAlternarFavorito = {},
                        onReaccionar = {},
                        onClick = {},
                        onAbrirPerfil = {},
                        onCompartir = {},
                        onEditar = {},
                        onBorrar = {}
                    )
                }

                // Resultado del POST
                item {
                    when (val est = estadoPublicacion) {
                        null -> Unit
                        UiState.Cargando -> Text("Publicando en el servidor...", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        is UiState.Error -> RecuadroErrorPop(
                            mensaje = est.mensaje,
                            onReintentar = { vm.publicar(titulo, contenido, comunidadSeleccionada) }
                        )
                        is UiState.Exito -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            RecuadroExitoPop(texto = "¡Publicado con éxito! Servidor respondió id ${est.datos.id}")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BotonPop(onClick = onVerPerfil, colorFondo = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f)) {
                                    Text("Ver en mi perfil 👤", color = Color.White, fontWeight = FontWeight.Black)
                                }
                                BotonPop(
                                    onClick = {
                                        vm.limpiarPublicacion()
                                        titulo = ""
                                        contenido = ""
                                        pasoActual = 1
                                    },
                                    colorFondo = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Publicar otra ✏️", fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
