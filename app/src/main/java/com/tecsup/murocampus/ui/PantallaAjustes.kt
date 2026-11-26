package com.tecsup.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Acento
import com.tecsup.murocampus.ui.componentes.AvatarFoto
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.ChipPop
import com.tecsup.murocampus.ui.componentes.EmojiAnimado
import com.tecsup.murocampus.ui.componentes.FondoFlotante
import com.tecsup.murocampus.ui.componentes.InsigniaPop
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.ModoTema
import com.tecsup.murocampus.viewmodel.PostsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(
    vm: PostsViewModel,
    onVerOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modoTema by vm.modoTema.collectAsStateWithLifecycle()
    val acento by vm.acento.collectAsStateWithLifecycle()
    val nombrePerfil by vm.nombrePerfil.collectAsStateWithLifecycle()
    val avatarSemilla by vm.avatarSemilla.collectAsStateWithLifecycle()
    val vistaCuadricula by vm.vistaCuadricula.collectAsStateWithLifecycle()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

    var textoNombre by remember(nombrePerfil) { mutableStateOf(nombrePerfil) }
    var mostrarDialogoBorrar by rememberSaveable { mutableStateOf(false) }

    val semillasAvatares = listOf("campus", "estudiante", "gato", "robot", "superman", "star", "ninja", "coder")

    if (mostrarDialogoBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrar = false },
            title = { Text("¿Borrar todos los datos locales?", fontWeight = FontWeight.Black) },
            text = { Text("Se restablecerán tus guardados, reacciones, puntos, nivel y configuraciones.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoBorrar = false
                        vm.borrarDatosLocales()
                    }
                ) {
                    Text("Borrar todo", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBorrar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Ajustes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.size(6.dp))
                        EmojiAnimado(emoji = "⚙️", tamano = 28.dp, animacionesActivas = animacionesActivas)
                    }
                }
            }

            // Apariencia
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
                        Text(text = "Apariencia 🎨", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)

                        // Modo de Tema
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ChipPop(texto = "Sistema 📱", seleccionado = modoTema == ModoTema.SISTEMA, onClick = { vm.cambiarModoTema(ModoTema.SISTEMA) })
                            ChipPop(texto = "Claro ☀️", seleccionado = modoTema == ModoTema.CLARO, onClick = { vm.cambiarModoTema(ModoTema.CLARO) })
                            ChipPop(texto = "Oscuro 🌙", seleccionado = modoTema == ModoTema.OSCURO, onClick = { vm.cambiarModoTema(ModoTema.OSCURO) })
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Color de Acento
                        Text(text = "Color de Acento:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Acento.entries.forEach { ac ->
                                val esSeleccionado = acento == ac
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ac.primary)
                                        .border(
                                            width = if (esSeleccionado) 3.dp else 1.dp,
                                            color = if (esSeleccionado) Color(0xFF1B1B2F) else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { vm.cambiarAcento(ac) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Vista por defecto del muro
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Vista en cuadrícula (mosaico)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Switch(checked = vistaCuadricula, onCheckedChange = { vm.cambiarVistaCuadricula(it) })
                        }
                    }
                }
            }

            // Perfil
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
                        Text(text = "Tu Perfil 😎", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)

                        OutlinedTextField(
                            value = textoNombre,
                            onValueChange = {
                                textoNombre = it
                                vm.cambiarNombrePerfil(it)
                            },
                            label = { Text("Nombre de usuario", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Text(text = "Elige tu avatar:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(semillasAvatares) { sem ->
                                val seleccionado = avatarSemilla == sem
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .border(
                                            width = if (seleccionado) 3.dp else 0.dp,
                                            color = if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { vm.cambiarAvatarSemilla(sem) }
                                        .padding(2.dp)
                                ) {
                                    AvatarFoto(semilla = sem, tamano = 44.dp)
                                }
                            }
                        }
                    }
                }
            }

            // Animaciones
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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Rendimiento y Animaciones ⚡", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Emojis animados e imágenes decorativas", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Usa internet para cargar GIFs y avatares remote", fontSize = 10.sp, color = Color.Gray)
                            }
                            Switch(checked = animacionesActivas, onCheckedChange = { vm.cambiarAnimacionesActivas(it) })
                        }
                    }
                }
            }

            // Datos
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
                        Text(text = "Gestión de Datos 🧹", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BotonPop(onClick = {
                                vm.resetearOnboarding()
                                onVerOnboarding()
                            }, colorFondo = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                                Text("Ver bienvenida 👋", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            BotonPop(onClick = { mostrarDialogoBorrar = true }, colorFondo = MaterialTheme.colorScheme.error, modifier = Modifier.weight(1f)) {
                                Text("Borrar mis datos 🗑️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Acerca de
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pegatina(
                            colorFondo = MaterialTheme.colorScheme.surfaceVariant,
                            bordeColor = MaterialTheme.colorScheme.outline,
                            radio = 20.dp,
                            bordeAncho = 2.dp,
                            sombraDesplazamiento = 4.dp
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        InsigniaPop(texto = "MuroCampus Pop · Fase 2", emoji = "⚡", colorFondo = MaterialTheme.colorScheme.primary, colorTexto = Color.White)
                        Text(text = "• Datos de negocio: JSONPlaceholder API (posts, comments, users)", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text(text = "• Arquitectura: MVVM + Repository + Retrofit + kotlinx.serialization", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text(text = "• Nota: Comunidades, reacciones, puntos e insignias son simulados localmente; fotos, avatares y GIFs vienen de picsum.photos, DiceBear y Google Fonts con respaldo offline.", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
