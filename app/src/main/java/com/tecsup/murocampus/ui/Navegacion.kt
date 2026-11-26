package com.tecsup.murocampus.ui

import kotlinx.serialization.Serializable

/** Rutas tipadas para la navegación de MuroCampus Pop. */
@Serializable
sealed interface Ruta {
    @Serializable
    data object Inicio : Ruta

    @Serializable
    data object Explorar : Ruta

    @Serializable
    data object Guardados : Ruta

    @Serializable
    data object Perfil : Ruta

    @Serializable
    data class Detalle(val id: Int) : Ruta

    @Serializable
    data class ComunidadVista(val nombreComunidad: String) : Ruta

    @Serializable
    data class Crear(
        val comunidadNombre: String? = null,
        val textoSugerido: String? = null
    ) : Ruta

    @Serializable
    data class Editar(val id: Int, val titulo: String) : Ruta

    @Serializable
    data class PerfilAutor(val userId: Int) : Ruta

    @Serializable
    data object Ajustes : Ruta

    @Serializable
    data object Onboarding : Ruta
}
