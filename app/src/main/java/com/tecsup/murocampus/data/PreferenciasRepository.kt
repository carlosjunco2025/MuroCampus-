package com.tecsup.murocampus.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.murocampus.model.Acento
import com.tecsup.murocampus.model.Comunidad
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.model.PublicacionPropia
import com.tecsup.murocampus.model.Reaccion
import com.tecsup.murocampus.viewmodel.ModoTema
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "preferencias_murocampus_pop")

interface PreferenciasRepository {
    val guardados: Flow<List<Post>>
    val reacciones: Flow<Map<Int, Reaccion>>
    val comunidadesUnidas: Flow<Set<Comunidad>>
    val misPublicaciones: Flow<List<PublicacionPropia>>
    val comentariosEscritos: Flow<Int>
    val puntos: Flow<Int>
    val racha: Flow<Int>
    val ultimoDia: Flow<Long>
    val modoTema: Flow<ModoTema>
    val acento: Flow<Acento>
    val nombrePerfil: Flow<String>
    val avatarSemilla: Flow<String>
    val vistaCuadricula: Flow<Boolean>
    val animacionesActivas: Flow<Boolean>
    val onboardingVisto: Flow<Boolean>

    suspend fun guardarGuardados(guardados: List<Post>)
    suspend fun guardarReacciones(reacciones: Map<Int, Reaccion>)
    suspend fun guardarComunidadesUnidas(comunidades: Set<Comunidad>)
    suspend fun guardarMisPublicaciones(publicaciones: List<PublicacionPropia>)
    suspend fun incrementarComentarios()
    suspend fun sumarPuntos(n: Int)
    suspend fun registrarVisita()
    suspend fun guardarModoTema(modo: ModoTema)
    suspend fun guardarAcento(acento: Acento)
    suspend fun guardarNombrePerfil(nombre: String)
    suspend fun guardarAvatarSemilla(semilla: String)
    suspend fun guardarVistaCuadricula(cuadricula: Boolean)
    suspend fun guardarAnimacionesActivas(activas: Boolean)
    suspend fun guardarOnboardingVisto(visto: Boolean)
    suspend fun borrarDatos()
}

class PreferenciasRepositoryImpl(private val context: Context) : PreferenciasRepository {
    private object Keys {
        val GUARDADOS_JSON = stringPreferencesKey("guardados_json")
        val REACCIONES_JSON = stringPreferencesKey("reacciones_json")
        val COMUNIDADES_JSON = stringPreferencesKey("comunidades_json")
        val MIS_POSTS_JSON = stringPreferencesKey("mis_posts_json")
        val COMENTARIOS = intPreferencesKey("comentarios_escritos")
        val PUNTOS = intPreferencesKey("puntos")
        val RACHA = intPreferencesKey("racha")
        val ULTIMO_DIA = longPreferencesKey("ultimo_dia")
        val MODO_TEMA = stringPreferencesKey("modo_tema")
        val ACENTO = stringPreferencesKey("acento")
        val NOMBRE_PERFIL = stringPreferencesKey("nombre_perfil")
        val AVATAR_SEMILLA = stringPreferencesKey("avatar_semilla")
        val VISTA_CUADRICULA = booleanPreferencesKey("vista_cuadricula")
        val ANIMACIONES_ACTIVAS = booleanPreferencesKey("animaciones_activas")
        val ONBOARDING_VISTO = booleanPreferencesKey("onboarding_visto")
    }

    private val json = Json { ignoreUnknownKeys = true }

    override val guardados: Flow<List<Post>> = context.dataStore.data.map { prefs ->
        try {
            val str = prefs[Keys.GUARDADOS_JSON]
            if (str.isNullOrBlank()) emptyList() else json.decodeFromString(str)
        } catch (e: CancellationException) { throw e } catch (e: Exception) { emptyList() }
    }

    override val reacciones: Flow<Map<Int, Reaccion>> = context.dataStore.data.map { prefs ->
        try {
            val str = prefs[Keys.REACCIONES_JSON]
            if (str.isNullOrBlank()) emptyMap() else json.decodeFromString(str)
        } catch (e: CancellationException) { throw e } catch (e: Exception) { emptyMap() }
    }

    override val comunidadesUnidas: Flow<Set<Comunidad>> = context.dataStore.data.map { prefs ->
        try {
            val str = prefs[Keys.COMUNIDADES_JSON]
            if (str.isNullOrBlank()) emptySet() else json.decodeFromString(str)
        } catch (e: CancellationException) { throw e } catch (e: Exception) { emptySet() }
    }

    override val misPublicaciones: Flow<List<PublicacionPropia>> = context.dataStore.data.map { prefs ->
        try {
            val str = prefs[Keys.MIS_POSTS_JSON]
            if (str.isNullOrBlank()) emptyList() else json.decodeFromString(str)
        } catch (e: CancellationException) { throw e } catch (e: Exception) { emptyList() }
    }

    override val comentariosEscritos: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.COMENTARIOS] ?: 0
    }

    override val puntos: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.PUNTOS] ?: 0
    }

    override val racha: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[Keys.RACHA] ?: 1
    }

    override val ultimoDia: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[Keys.ULTIMO_DIA] ?: 0L
    }

    override val modoTema: Flow<ModoTema> = context.dataStore.data.map { prefs ->
        try { ModoTema.valueOf(prefs[Keys.MODO_TEMA] ?: ModoTema.SISTEMA.name) } catch (e: Exception) { ModoTema.SISTEMA }
    }

    override val acento: Flow<Acento> = context.dataStore.data.map { prefs ->
        try { Acento.valueOf(prefs[Keys.ACENTO] ?: Acento.MANGO.name) } catch (e: Exception) { Acento.MANGO }
    }

    override val nombrePerfil: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.NOMBRE_PERFIL] ?: "Tú"
    }

    override val avatarSemilla: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.AVATAR_SEMILLA] ?: "campus"
    }

    override val vistaCuadricula: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.VISTA_CUADRICULA] ?: true
    }

    override val animacionesActivas: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ANIMACIONES_ACTIVAS] ?: true
    }

    override val onboardingVisto: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_VISTO] ?: false
    }

    override suspend fun guardarGuardados(guardados: List<Post>) {
        context.dataStore.edit { it[Keys.GUARDADOS_JSON] = json.encodeToString(guardados) }
    }

    override suspend fun guardarReacciones(reacciones: Map<Int, Reaccion>) {
        context.dataStore.edit { it[Keys.REACCIONES_JSON] = json.encodeToString(reacciones) }
    }

    override suspend fun guardarComunidadesUnidas(comunidades: Set<Comunidad>) {
        context.dataStore.edit { it[Keys.COMUNIDADES_JSON] = json.encodeToString(comunidades) }
    }

    override suspend fun guardarMisPublicaciones(publicaciones: List<PublicacionPropia>) {
        context.dataStore.edit { it[Keys.MIS_POSTS_JSON] = json.encodeToString(publicaciones) }
    }

    override suspend fun incrementarComentarios() {
        context.dataStore.edit { prefs ->
            val actual = prefs[Keys.COMENTARIOS] ?: 0
            prefs[Keys.COMENTARIOS] = actual + 1
        }
    }

    override suspend fun sumarPuntos(n: Int) {
        context.dataStore.edit { prefs ->
            val actual = prefs[Keys.PUNTOS] ?: 0
            prefs[Keys.PUNTOS] = actual + n
        }
    }

    override suspend fun registrarVisita() {
        val hoy = System.currentTimeMillis() / 86_400_000
        context.dataStore.edit { prefs ->
            val ultimo = prefs[Keys.ULTIMO_DIA] ?: 0L
            val rachaActual = prefs[Keys.RACHA] ?: 1
            if (hoy > ultimo) {
                if (hoy == ultimo + 1) {
                    prefs[Keys.RACHA] = rachaActual + 1
                } else if (ultimo == 0L) {
                    prefs[Keys.RACHA] = 1
                } else {
                    prefs[Keys.RACHA] = 1
                }
                prefs[Keys.ULTIMO_DIA] = hoy
            }
        }
    }

    override suspend fun guardarModoTema(modo: ModoTema) {
        context.dataStore.edit { it[Keys.MODO_TEMA] = modo.name }
    }

    override suspend fun guardarAcento(acento: Acento) {
        context.dataStore.edit { it[Keys.ACENTO] = acento.name }
    }

    override suspend fun guardarNombrePerfil(nombre: String) {
        context.dataStore.edit { it[Keys.NOMBRE_PERFIL] = nombre }
    }

    override suspend fun guardarAvatarSemilla(semilla: String) {
        context.dataStore.edit { it[Keys.AVATAR_SEMILLA] = semilla }
    }

    override suspend fun guardarVistaCuadricula(cuadricula: Boolean) {
        context.dataStore.edit { it[Keys.VISTA_CUADRICULA] = cuadricula }
    }

    override suspend fun guardarAnimacionesActivas(activas: Boolean) {
        context.dataStore.edit { it[Keys.ANIMACIONES_ACTIVAS] = activas }
    }

    override suspend fun guardarOnboardingVisto(visto: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_VISTO] = visto }
    }

    override suspend fun borrarDatos() {
        context.dataStore.edit { it.clear() }
    }
}
