package com.tecsup.murocampus.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.ui.componentes.AvatarUsuario
import com.tecsup.murocampus.ui.componentes.SeccionTitulo
import com.tecsup.murocampus.ui.theme.Dimens
import com.tecsup.murocampus.viewmodel.PostsViewModel

@Composable
private fun TarjetaNumero(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(Dimens.xs))
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PantallaAutores(
    vm: PostsViewModel,
    onAbrirPerfil: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val panel by vm.panel.collectAsStateWithLifecycle()
    val autores by vm.autoresLista.collectAsStateWithLifecycle()

    val maxCantidad = panel.porUsuario.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimens.md),
        verticalArrangement = Arrangement.spacedBy(Dimens.lg)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
            ) {
                TarjetaNumero(
                    titulo = "Posts",
                    valor = panel.totalPosts.toString(),
                    modifier = Modifier.weight(1f)
                )
                TarjetaNumero(
                    titulo = "Favoritos",
                    valor = panel.totalFavoritos.toString(),
                    modifier = Modifier.weight(1f)
                )
                TarjetaNumero(
                    titulo = "Top Autor",
                    valor = if (panel.autorMasActivo != null) "U${panel.autorMasActivo}" else "-",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.md),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.lg),
                    verticalArrangement = Arrangement.spacedBy(Dimens.md)
                ) {
                    SeccionTitulo(titulo = "Actividad por autor")

                    panel.porUsuario.forEach { (userId, cantidad) ->
                        val (fondoColor, _) = coloresDeUsuario(userId)
                        val fraccion = (cantidad.toFloat() / maxCantidad.toFloat()).coerceIn(0.05f, 1.0f)
                        val autor = autores.find { it.id == userId }
                        val nombreAutor = autor?.name ?: "Usuario $userId"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAbrirPerfil(userId) }
                                .padding(vertical = Dimens.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
                        ) {
                            AvatarUsuario(userId = userId, size = 28.dp)
                            Text(
                                text = nombreAutor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(90.dp),
                                maxLines = 1
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(12.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraccion)
                                        .height(12.dp)
                                        .background(fondoColor, RoundedCornerShape(50))
                                )
                            }

                            Text(
                                text = cantidad.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.width(24.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}
