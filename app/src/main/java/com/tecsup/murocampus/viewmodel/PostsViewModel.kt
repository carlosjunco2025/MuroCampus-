package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.data.PreferenciasRepository
import com.tecsup.murocampus.data.UsuarioRepository
import com.tecsup.murocampus.model.Acento
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.Comunidad
import com.tecsup.murocampus.model.NuevoComentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.model.PublicacionPropia
import com.tecsup.murocampus.model.Reaccion
import com.tecsup.murocampus.model.Usuario
import com.tecsup.murocampus.model.comunidadDe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Orden {
    RECIENTES,
    MAS_REACCIONES,
    TITULO_AZ
}

enum class ModoTema {
    SISTEMA,
    CLARO,
    OSCURO
}

data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

data class ResumenUi(
    val total: Int = 0,
    val favoritas: Int = 0,
    val porUsuario: List<Pair<Int, Int>> = emptyList(),
    val usuarioTop: Int? = null
) {
    val totalPosts: Int get() = total
    val totalFavoritos: Int get() = favoritas
    val autorMasActivo: Int? get() = usuarioTop
}

data class Insignia(
    val id: String,
    val titulo: String,
    val emoji: String,
    val desbloqueada: Boolean,
    val descripcion: String
)

class PostsViewModel(
    private val repo: PostRepository,
    private val usuarioRepo: UsuarioRepository,
    private val prefRepo: PreferenciasRepository
) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista = _lista.asStateFlow()

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle = _detalle.asStateFlow()

    private val _publicacion = MutableStateFlow<UiState<Post>?>(null)
    val publicacion = _publicacion.asStateFlow()

    private val _borrado = MutableStateFlow<UiState<Unit>?>(null)
    val borrado = _borrado.asStateFlow()

    private val _edicion = MutableStateFlow<UiState<Post>?>(null)
    val edicion = _edicion.asStateFlow()

    private val _comentando = MutableStateFlow<UiState<Comentario>?>(null)
    val comentando = _comentando.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda = _busqueda.asStateFlow()

    private val _orden = MutableStateFlow(Orden.RECIENTES)
    val orden = _orden.asStateFlow()

    private val _comunidadSeleccionada = MutableStateFlow<Comunidad?>(null)
    val comunidadSeleccionada = _comunidadSeleccionada.asStateFlow()

    private val _autores = MutableStateFlow<Map<Int, Usuario>>(emptyMap())
    val autores = _autores.asStateFlow()

    val autoresLista: StateFlow<List<Usuario>> = autores.map { it.values.toList() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _postsPerfil = MutableStateFlow<List<Post>>(emptyList())
    val postsPerfil = _postsPerfil.asStateFlow()

    // Preferencias y gamificación desde DataStore
    val guardados: StateFlow<List<Post>> = prefRepo.guardados.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val panel: StateFlow<ResumenUi> = combine(
        lista,
        guardados
    ) { estadoLista, listaGuardados ->
        if (estadoLista is UiState.Exito) {
            val posts = estadoLista.datos
            val total = posts.size
            val cantidadFavs = listaGuardados.size
            val porUsuario = (1..10).map { uid ->
                uid to posts.count { it.userId == uid }
            }
            val topUser = porUsuario.filter { it.second > 0 }.maxByOrNull { it.second }?.first
            ResumenUi(
                total = total,
                favoritas = cantidadFavs,
                porUsuario = porUsuario,
                usuarioTop = topUser
            )
        } else {
            ResumenUi(
                total = 0,
                favoritas = guardados.value.size,
                porUsuario = (1..10).map { it to 0 },
                usuarioTop = null
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResumenUi()
    )

    val resumen = panel

    private val _usuarioPorDefecto = MutableStateFlow(1)
    val usuarioPorDefecto = _usuarioPorDefecto.asStateFlow()
    fun cambiarUsuarioPorDefecto(userId: Int) { _usuarioPorDefecto.value = userId }

    fun cambiarVistaCompacta(compacta: Boolean) {
        viewModelScope.launch { prefRepo.guardarVistaCuadricula(!compacta) }
    }

    val reacciones: StateFlow<Map<Int, Reaccion>> = prefRepo.reacciones.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val comunidadesUnidas: StateFlow<Set<Comunidad>> = prefRepo.comunidadesUnidas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val misPublicaciones: StateFlow<List<PublicacionPropia>> = prefRepo.misPublicaciones.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val comentariosEscritos: StateFlow<Int> = prefRepo.comentariosEscritos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val puntos: StateFlow<Int> = prefRepo.puntos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val racha: StateFlow<Int> = prefRepo.racha.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    val nivel: StateFlow<Int> = puntos.map { (it / 50) + 1 }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    val modoTema: StateFlow<ModoTema> = prefRepo.modoTema.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ModoTema.SISTEMA
    )

    val acento: StateFlow<Acento> = prefRepo.acento.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Acento.MANGO
    )

    val nombrePerfil: StateFlow<String> = prefRepo.nombrePerfil.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Tú"
    )

    val avatarSemilla: StateFlow<String> = prefRepo.avatarSemilla.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "campus"
    )

    val vistaCuadricula: StateFlow<Boolean> = prefRepo.vistaCuadricula.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val vistaCompacta: StateFlow<Boolean> = vistaCuadricula.map { !it }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val animacionesActivas: StateFlow<Boolean> = prefRepo.animacionesActivas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val onboardingVisto: StateFlow<Boolean> = prefRepo.onboardingVisto.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Insignias derivadas
    val insignias: StateFlow<List<Insignia>> = combine(
        misPublicaciones,
        comentariosEscritos,
        racha,
        comunidadesUnidas,
        puntos
    ) { misPosts, comentarios, rachaActual, unidas, pts ->
        listOf(
            Insignia("primera", "Primera publicación", "🥇", misPosts.isNotEmpty(), "Publica tu primer post en el muro"),
            Insignia("comentarista", "Comentarista", "💬", comentarios >= 3, "Escribe al menos 3 comentarios"),
            Insignia("racha3", "Racha de 3 días", "🔥", rachaActual >= 3, "Mantén la app abierta 3 días seguidos"),
            Insignia("sociable", "Sociable", "🤝", unidas.size >= 3, "Únete a 3 o más comunidades"),
            Insignia("nivel3", "Nivel 3", "⭐", pts >= 100, "Alcanza los 100 puntos en la app")
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _mensajes = MutableSharedFlow<String>()
    val mensajes = _mensajes.asSharedFlow()

    val listaVisible: StateFlow<UiState<List<Post>>> = combine(
        lista,
        busqueda,
        orden,
        comunidadSeleccionada,
        comunidadesUnidas
    ) { estadoLista, textoBusqueda, ordenActual, comunidadActual, unidas ->
        when (estadoLista) {
            is UiState.Exito -> {
                var posts = estadoLista.datos

                if (comunidadActual != null) {
                    posts = posts.filter { comunidadDe(it.id) == comunidadActual }
                }

                val query = textoBusqueda.trim()
                if (query.isNotEmpty()) {
                    posts = posts.filter { it.title.contains(query, ignoreCase = true) }
                }

                posts = when (ordenActual) {
                    Orden.RECIENTES -> posts.sortedByDescending { it.id }
                    Orden.MAS_REACCIONES -> posts.sortedByDescending { ((it.id * 37) % 90) + 5 }
                    Orden.TITULO_AZ -> posts.sortedBy { it.title.lowercase() }
                }

                UiState.Exito(posts)
            }
            else -> estadoLista
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Cargando
    )

    init {
        cargarAutores()
        cargar()
        registrarVisita()
    }

    fun registrarVisita() {
        viewModelScope.launch {
            prefRepo.registrarVisita()
        }
    }

    private fun cargarAutores() {
        viewModelScope.launch {
            usuarioRepo.listar()
                .onSuccess { listaUsuarios ->
                    _autores.value = listaUsuarios.associateBy { it.id }
                }
                .onFailure {
                    _mensajes.emit(mensajeDe(it))
                }
        }
    }

    fun cargar() {
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            repo.listar()
                .onSuccess { posts ->
                    _lista.value = UiState.Exito(posts)
                }
                .onFailure {
                    _lista.value = UiState.Error(mensajeDe(it))
                }
        }
    }

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

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarOrden(nuevoOrden: Orden) {
        _orden.value = nuevoOrden
    }

    fun seleccionarComunidad(comunidad: Comunidad?) {
        _comunidadSeleccionada.value = comunidad
    }

    fun alternarGuardado(post: Post) {
        viewModelScope.launch {
            val actual = guardados.value.toMutableList()
            if (actual.any { it.id == post.id }) {
                actual.removeIf { it.id == post.id }
                _mensajes.emit("Eliminado de guardados 🔖")
            } else {
                actual.add(post)
                _mensajes.emit("¡Guardado en favoritos! 🔖")
            }
            prefRepo.guardarGuardados(actual)
        }
    }

    fun reaccionar(postId: Int, reaccion: Reaccion) {
        viewModelScope.launch {
            val actual = reacciones.value.toMutableMap()
            val yaReacciono = actual.containsKey(postId)
            if (actual[postId] == reaccion) {
                actual.remove(postId)
            } else {
                actual[postId] = reaccion
                if (!yaReacciono) {
                    prefRepo.sumarPuntos(5)
                }
            }
            prefRepo.guardarReacciones(actual)
        }
    }

    fun unirseComunidad(comunidad: Comunidad) {
        viewModelScope.launch {
            val actual = comunidadesUnidas.value.toMutableSet()
            if (actual.contains(comunidad)) {
                actual.remove(comunidad)
                _mensajes.emit("Te saliste de ${comunidad.nombre}")
            } else {
                actual.add(comunidad)
                prefRepo.sumarPuntos(10)
                _mensajes.emit("¡Te uniste a ${comunidad.nombre}! 🎉 (+10 pts)")
            }
            prefRepo.guardarComunidadesUnidas(actual)
        }
    }

    fun publicar(titulo: String, texto: String, comunidad: Comunidad) {
        viewModelScope.launch {
            _publicacion.value = UiState.Cargando
            repo.crear(NuevoPost(userId = 1, title = titulo, body = texto))
                .onSuccess { postCreado ->
                    _publicacion.value = UiState.Exito(postCreado)
                    val nuevaPropia = PublicacionPropia(
                        titulo = titulo,
                        texto = texto,
                        comunidad = comunidad,
                        idServidor = postCreado.id
                    )
                    val actualMisPosts = misPublicaciones.value.toMutableList()
                    actualMisPosts.add(0, nuevaPropia)
                    prefRepo.guardarMisPublicaciones(actualMisPosts)
                    prefRepo.sumarPuntos(20)
                }
                .onFailure {
                    _publicacion.value = UiState.Error(mensajeDe(it))
                }
        }
    }

    fun limpiarPublicacion() {
        _publicacion.value = null
    }

    fun borrar(id: Int) {
        viewModelScope.launch {
            _borrado.value = UiState.Cargando
            repo.borrar(id)
                .onSuccess {
                    val listaActual = _lista.value
                    if (listaActual is UiState.Exito) {
                        _lista.value = UiState.Exito(listaActual.datos.filterNot { it.id == id })
                    }
                    _borrado.value = UiState.Exito(Unit)
                }
                .onFailure {
                    _borrado.value = UiState.Error(mensajeDe(it))
                }
        }
    }

    fun limpiarBorrado() {
        _borrado.value = null
    }

    fun editarTitulo(id: Int, titulo: String) {
        viewModelScope.launch {
            _edicion.value = UiState.Cargando
            repo.actualizarTitulo(id, titulo)
                .onSuccess { postActualizado ->
                    val listaActual = _lista.value
                    if (listaActual is UiState.Exito) {
                        _lista.value = UiState.Exito(
                            listaActual.datos.map { if (it.id == id) it.copy(title = postActualizado.title) else it }
                        )
                    }
                    val detalleActual = _detalle.value
                    if (detalleActual is UiState.Exito && detalleActual.datos.post.id == id) {
                        val postMod = detalleActual.datos.post.copy(title = postActualizado.title)
                        _detalle.value = UiState.Exito(detalleActual.datos.copy(post = postMod))
                    }
                    _edicion.value = UiState.Exito(postActualizado)
                }
                .onFailure {
                    _edicion.value = UiState.Error(mensajeDe(it))
                }
        }
    }

    fun limpiarEdicion() {
        _edicion.value = null
    }

    fun borrarFavoritos() {
        viewModelScope.launch {
            prefRepo.guardarGuardados(emptyList())
            _mensajes.emit("Guardados borrados 🧹")
        }
    }

    fun comentar(postId: Int, texto: String) {
        viewModelScope.launch {
            _comentando.value = UiState.Cargando
            val nombre = nombrePerfil.value
            val nuevo = NuevoComentario(postId = postId, name = nombre, email = "tu.correo@campus.edu", body = texto)
            repo.crearComentario(postId, nuevo)
                .onSuccess { comentarioCreado ->
                    _comentando.value = UiState.Exito(comentarioCreado)
                    val detalleActual = _detalle.value
                    if (detalleActual is UiState.Exito && detalleActual.datos.post.id == postId) {
                        val nuevosComentarios = detalleActual.datos.comentarios + comentarioCreado
                        _detalle.value = UiState.Exito(detalleActual.datos.copy(comentarios = nuevosComentarios))
                    }
                    prefRepo.incrementarComentarios()
                    prefRepo.sumarPuntos(10)
                }
                .onFailure {
                    _comentando.value = UiState.Error(mensajeDe(it))
                }
        }
    }

    fun limpiarComentar() {
        _comentando.value = null
    }

    fun cargarPostsPerfil(userId: Int) {
        viewModelScope.launch {
            repo.porUsuario(userId)
                .onSuccess { _postsPerfil.value = it }
                .onFailure { _postsPerfil.value = emptyList() }
        }
    }

    fun cambiarModoTema(modo: ModoTema) {
        viewModelScope.launch { prefRepo.guardarModoTema(modo) }
    }

    fun cambiarAcento(acento: Acento) {
        viewModelScope.launch { prefRepo.guardarAcento(acento) }
    }

    fun cambiarNombrePerfil(nombre: String) {
        viewModelScope.launch { prefRepo.guardarNombrePerfil(nombre) }
    }

    fun cambiarAvatarSemilla(semilla: String) {
        viewModelScope.launch { prefRepo.guardarAvatarSemilla(semilla) }
    }

    fun cambiarVistaCuadricula(cuadricula: Boolean) {
        viewModelScope.launch { prefRepo.guardarVistaCuadricula(cuadricula) }
    }

    fun cambiarAnimacionesActivas(activas: Boolean) {
        viewModelScope.launch { prefRepo.guardarAnimacionesActivas(activas) }
    }

    fun marcarOnboardingVisto() {
        viewModelScope.launch { prefRepo.guardarOnboardingVisto(true) }
    }

    fun resetearOnboarding() {
        viewModelScope.launch { prefRepo.guardarOnboardingVisto(false) }
    }

    fun borrarDatosLocales() {
        viewModelScope.launch {
            prefRepo.borrarDatos()
            _mensajes.emit("¡Datos locales borrados con éxito! 🧹")
        }
    }
}
