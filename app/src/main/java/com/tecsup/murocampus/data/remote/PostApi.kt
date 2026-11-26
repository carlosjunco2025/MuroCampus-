package com.tecsup.murocampus.data.remote

import com.tecsup.murocampus.model.ActualizarTitulo
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoComentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Contrato HTTP del servicio. Rutas relativas: ninguna empieza con "/". */
interface PostApi {
    @GET("posts")
    suspend fun listar(): List<Post>

    @GET("posts")
    suspend fun listarPagina(
        @Query("_start") inicio: Int,
        @Query("_limit") limite: Int
    ): List<Post>

    @GET("posts/{id}")
    suspend fun detalle(@Path("id") id: Int): Post

    @GET("posts/{id}/comments")
    suspend fun comentarios(@Path("id") id: Int): List<Comentario>

    @GET("posts")
    suspend fun porUsuario(@Query("userId") userId: Int): List<Post>

    @POST("posts")
    suspend fun crear(@Body nuevo: NuevoPost): Post

    @POST("posts/{postId}/comments")
    suspend fun crearComentario(
        @Path("postId") postId: Int,
        @Body nuevo: NuevoComentario
    ): Comentario

    @DELETE("posts/{id}")
    suspend fun borrar(@Path("id") id: Int): Response<Unit>

    @PATCH("posts/{id}")
    suspend fun actualizarTitulo(@Path("id") id: Int, @Body cambios: ActualizarTitulo): Post
}
