package com.tecsup.murocampus.model

import kotlinx.serialization.Serializable

// Ejemplo [E] resuelto del laboratorio: sirve de guía para los modelos de Post.
@Serializable
data class Usuario(
    val id: Int,
    val name: String,
    val email: String
)

/** Publicación que devuelve el servicio (GET posts). */
@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

/** Comentario de una publicación (GET posts/{id}/comments). */
@Serializable
data class Comentario(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)

/** Cuerpo que enviamos al crear (POST posts): todavía no tiene id. */
@Serializable
data class NuevoPost(
    val userId: Int,
    val title: String,
    val body: String
)
