package com.tecsup.murocampus.data

import com.tecsup.murocampus.data.remote.RetrofitClient
import com.tecsup.murocampus.data.remote.UsuarioApi
import com.tecsup.murocampus.model.Usuario
import kotlinx.coroutines.CancellationException

/** Contrato de repositorio de usuarios. */
interface UsuarioRepository {
    suspend fun listar(): Result<List<Usuario>>
}

/** Implementación remota del repositorio de usuarios. */
class UsuarioRepositoryRemote(private val api: UsuarioApi) : UsuarioRepository {
    override suspend fun listar(): Result<List<Usuario>> =
        try {
            Result.success(api.listar())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    companion object {
        /** Construye el repositorio de usuarios con el cliente único de la app. */
        fun crear(): UsuarioRepositoryRemote = UsuarioRepositoryRemote(RetrofitClient.usuarioApi)
    }
}
