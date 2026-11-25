package com.tecsup.murocampus.model

import kotlinx.serialization.Serializable

// Ejemplo [E] resuelto del laboratorio: sirve de guía para los modelos de Post.
@Serializable
data class Usuario(
    val id: Int,
    val name: String,
    val email: String
)
