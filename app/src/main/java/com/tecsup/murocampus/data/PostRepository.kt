package com.tecsup.murocampus.data

import com.tecsup.murocampus.data.remote.PostApi
import com.tecsup.murocampus.data.remote.RetrofitClient
import com.tecsup.murocampus.model.ActualizarTitulo
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoComentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

/** Contrato de datos del ViewModel: no conoce la UI. */
interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun listarPagina(inicio: Int, limite: Int): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>
    suspend fun crearComentario(postId: Int, nuevo: NuevoComentario): Result<Comentario>
    suspend fun borrar(id: Int): Result<Unit>
    suspend fun actualizarTitulo(id: Int, titulo: String): Result<Post>
}

/** Implementación remota: captura los errores de red y los devuelve como Result. */
class PostRepositoryRemote(private val api: PostApi) : PostRepository {
    override suspend fun listar(): Result<List<Post>> = llamar { api.listar() }

    override suspend fun listarPagina(inicio: Int, limite: Int): Result<List<Post>> =
        llamar { api.listarPagina(inicio, limite) }

    override suspend fun detalle(id: Int): Result<Post> = llamar { api.detalle(id) }

    override suspend fun comentarios(id: Int): Result<List<Comentario>> = llamar { api.comentarios(id) }

    override suspend fun porUsuario(userId: Int): Result<List<Post>> = llamar { api.porUsuario(userId) }

    override suspend fun crear(nuevo: NuevoPost): Result<Post> = llamar { api.crear(nuevo) }

    override suspend fun crearComentario(postId: Int, nuevo: NuevoComentario): Result<Comentario> =
        llamar { api.crearComentario(postId, nuevo) }

    override suspend fun borrar(id: Int): Result<Unit> =
        try {
            val response = api.borrar(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun actualizarTitulo(id: Int, titulo: String): Result<Post> =
        llamar { api.actualizarTitulo(id, ActualizarTitulo(titulo)) }

    /** Único punto de captura; la cancelación nunca se traga. */
    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    companion object {
        /** Construye el repositorio con el cliente único de la app. */
        fun crear(): PostRepositoryRemote = PostRepositoryRemote(RetrofitClient.api)
    }
}
