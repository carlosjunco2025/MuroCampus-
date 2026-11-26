package com.tecsup.murocampus.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comunidad
import com.tecsup.murocampus.ui.componentes.AvatarFoto
import com.tecsup.murocampus.ui.componentes.BotonPop
import com.tecsup.murocampus.ui.componentes.EmojiAnimado
import com.tecsup.murocampus.ui.componentes.FondoFlotante
import com.tecsup.murocampus.ui.componentes.InsigniaPop
import com.tecsup.murocampus.ui.componentes.pegatina
import com.tecsup.murocampus.viewmodel.PostsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PantallaOnboarding(
    vm: PostsViewModel,
    onCompletado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()
    val comunidadesUnidas by vm.comunidadesUnidas.collectAsStateWithLifecycle()
    val nombrePerfil by vm.nombrePerfil.collectAsStateWithLifecycle()
    val avatarSemilla by vm.avatarSemilla.collectAsStateWithLifecycle()

    var textoNombre by remember(nombrePerfil) { mutableStateOf(nombrePerfil) }
    val semillasAvatares = listOf("campus", "estudiante", "gato", "robot", "superman", "star", "ninja", "coder")

    Box(modifier = modifier.fillMaxSize()) {
        FondoFlotante(animacionesActivas = animacionesActivas)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fila superior: Título y Saltar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InsigniaPop(texto = "MuroCampus Pop", emoji = "⚡", colorFondo = MaterialTheme.colorScheme.primary, colorTexto = Color.White)
                Text(
                    text = "Saltar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        vm.marcarOnboardingVisto()
                        onCompletado()
                    }
                )
            }

            // HorizontalPager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        EmojiAnimado(emoji = "🎉", tamano = 80.dp, animacionesActivas = animacionesActivas)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "¡Bienvenido a\nMuroCampus Pop!",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "La red social universitaria más divertida, con pegatinas, comunidades y reacciones en vivo.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    1 -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Elige tus comunidades 🧭",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Selecciona tus temas de interés:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(Comunidad.entries) { com ->
                                val estaUnido = comunidadesUnidas.contains(com)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .pegatina(
                                            colorFondo = if (estaUnido) com.color else MaterialTheme.colorScheme.surface,
                                            bordeColor = MaterialTheme.colorScheme.outline,
                                            radio = 16.dp,
                                            bordeAncho = 2.dp,
                                            sombraDesplazamiento = 3.dp
                                        )
                                        .clickable { vm.unirseComunidad(com) }
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = com.emoji, fontSize = 20.sp)
                                        Text(
                                            text = com.nombre,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (estaUnido) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Crea tu perfil 😎",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = textoNombre,
                            onValueChange = {
                                textoNombre = it
                                vm.cambiarNombrePerfil(it)
                            },
                            label = { Text("Tu nombre en el campus", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Selecciona tu avatar:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(semillasAvatares) { sem ->
                                val sel = avatarSemilla == sem
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .border(
                                            width = if (sel) 3.dp else 0.dp,
                                            color = if (sel) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { vm.cambiarAvatarSemilla(sem) }
                                        .padding(2.dp)
                                ) {
                                    AvatarFoto(semilla = sem, tamano = 48.dp)
                                }
                            }
                        }
                    }
                }
            }

            // Indicadores de página y botón siguiente
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        val sel = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .size(if (sel) 24.dp else 10.dp, 10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (sel) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f))
                        )
                    }
                }

                BotonPop(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            vm.marcarOnboardingVisto()
                            onCompletado()
                        }
                    },
                    colorFondo = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (pagerState.currentPage < 2) "Siguiente ➡️" else "¡Empezar en el Muro! 🚀",
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
