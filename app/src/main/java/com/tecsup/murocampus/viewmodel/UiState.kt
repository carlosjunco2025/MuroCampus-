package com.tecsup.murocampus.viewmodel

import java.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

/** Estados posibles de una pantalla. Solo uno de ellos está activo a la vez. */
sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Exito<T>(val datos: T) : UiState<T>
    data class Error(val mensaje: String) : UiState<Nothing>
}

/** Convierte el error de la capa de datos en un mensaje para la interfaz. */
fun mensajeDe(e: Throwable): String = when (e) {
    is IOException -> "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."
    is HttpException -> "Error del servidor (${e.code()})"
    is SerializationException -> "Respuesta inesperada"
    else -> "Error desconocido"
}
