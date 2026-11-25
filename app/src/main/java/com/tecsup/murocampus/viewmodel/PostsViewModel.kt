package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Publicación junto con sus comentarios, listos para dibujar. */
data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

/** ViewModel de la lista y el detalle: solo conoce el repositorio, nunca la capa de red. */
class PostsViewModel(private val repo: PostRepository) : ViewModel() {
    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista = _lista.asStateFlow()

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle = _detalle.asStateFlow()

    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro = _usuarioFiltro.asStateFlow()

    init {
        cargar()
    }

    /** null significa "Todos"; cualquier otro valor filtra por ese usuario. */
    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            val userId = _usuarioFiltro.value
            val resultado = if (userId == null) repo.listar() else repo.porUsuario(userId)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Pide el post y sus comentarios a la vez; si uno falla, todo el detalle es error. */
    fun abrir(id: Int) {
        viewModelScope.launch {
            _detalle.value = UiState.Cargando
            try {
                coroutineScope {
                    val postPendiente = async { repo.detalle(id) }
                    val comentariosPendientes = async { repo.comentarios(id) }

                    val post = postPendiente.await().getOrThrow()
                    val comentarios = comentariosPendientes.await().getOrThrow()
                    _detalle.value = UiState.Exito(DetalleUi(post, comentarios))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detalle.value = UiState.Error(mensajeDe(e))
            }
        }
    }
}
