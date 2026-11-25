package com.tecsup.murocampus.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

/** Lista de publicaciones: un único when dibuja los tres estados posibles. */
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.lista.collectAsStateWithLifecycle()

    when (val estadoActual = estado) {
        UiState.Cargando -> Cargando()

        is UiState.Error -> ErrorConReintento(
            mensaje = estadoActual.mensaje,
            onReintentar = { vm.cargar() }
        )

        is UiState.Exito<List<Post>> -> LazyColumn(modifier = modifier) {
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
