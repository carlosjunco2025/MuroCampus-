package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Fábrica [E] entregada: recibe una función que construye el ViewModel.
 * ViewModelProvider guarda la instancia creada, así que al rotar no se pierde el estado.
 */
class PostsViewModelFactory(
    private val crearViewModel: () -> ViewModel
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = crearViewModel() as T
}
