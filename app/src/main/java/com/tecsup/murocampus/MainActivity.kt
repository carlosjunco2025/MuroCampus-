package com.tecsup.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tecsup.murocampus.data.PostRepositoryRemote
import com.tecsup.murocampus.data.PreferenciasRepositoryImpl
import com.tecsup.murocampus.data.UsuarioRepositoryRemote
import com.tecsup.murocampus.ui.PantallaAjustes
import com.tecsup.murocampus.ui.PantallaComunidad
import com.tecsup.murocampus.ui.PantallaCrear
import com.tecsup.murocampus.ui.PantallaDetalle
import com.tecsup.murocampus.ui.PantallaEditar
import com.tecsup.murocampus.ui.PantallaExplorar
import com.tecsup.murocampus.ui.PantallaFavoritos
import com.tecsup.murocampus.ui.PantallaInicio
import com.tecsup.murocampus.ui.PantallaOnboarding
import com.tecsup.murocampus.ui.PantallaPerfil
import com.tecsup.murocampus.ui.PantallaPerfilAutor
import com.tecsup.murocampus.ui.Ruta
import com.tecsup.murocampus.ui.componentes.DockFlotante
import com.tecsup.murocampus.ui.componentes.WordmarkPop
import com.tecsup.murocampus.ui.theme.MuroCampusTheme
import com.tecsup.murocampus.viewmodel.ModoTema
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.PostsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: PostsViewModel = viewModel(
                factory = PostsViewModelFactory {
                    PostsViewModel(
                        PostRepositoryRemote.crear(),
                        UsuarioRepositoryRemote.crear(),
                        PreferenciasRepositoryImpl(applicationContext)
                    )
                }
            )

            val modoTema by vm.modoTema.collectAsStateWithLifecycle()
            val acento by vm.acento.collectAsStateWithLifecycle()
            val onboardingVisto by vm.onboardingVisto.collectAsStateWithLifecycle()
            val guardados by vm.guardados.collectAsStateWithLifecycle()
            val animacionesActivas by vm.animacionesActivas.collectAsStateWithLifecycle()

            val esOscuro = when (modoTema) {
                ModoTema.SISTEMA -> isSystemInDarkTheme()
                ModoTema.CLARO -> false
                ModoTema.OSCURO -> true
            }

            MuroCampusTheme(darkTheme = esOscuro, acento = acento) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(Unit) {
                    vm.mensajes.collect { mensaje ->
                        snackbarHostState.showSnackbar(mensaje)
                    }
                }

                LaunchedEffect(onboardingVisto) {
                    if (onboardingVisto) {
                        val currentRouteName = navController.currentDestination?.route
                        if (currentRouteName?.contains("Onboarding") == true) {
                            navController.navigate(Ruta.Inicio) {
                                popUpTo(Ruta.Onboarding) { inclusive = true }
                            }
                        }
                    }
                }

                val mostrarDock = currentRoute?.let { route ->
                    route.contains("Inicio") || route.contains("Explorar") || route.contains("Guardados") || route.contains("Perfil")
                } ?: true

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        AnimatedVisibility(
                            visible = mostrarDock && onboardingVisto,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            DockFlotante(
                                rutaActual = currentRoute,
                                cantidadGuardados = guardados.size,
                                animacionesActivas = animacionesActivas,
                                onNavegar = { ruta ->
                                    navController.navigate(ruta) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onCrear = {
                                    vm.limpiarPublicacion()
                                    navController.navigate(Ruta.Crear())
                                }
                            )
                        }
                    },
                    topBar = {
                        if (onboardingVisto && mostrarDock) {
                            TopAppBar(
                                title = { WordmarkPop() },
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (onboardingVisto) Ruta.Inicio else Ruta.Onboarding,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Ruta.Onboarding> {
                            PantallaOnboarding(
                                vm = vm,
                                onCompletado = {
                                    navController.navigate(Ruta.Inicio) {
                                        popUpTo(Ruta.Onboarding) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<Ruta.Inicio> {
                            PantallaInicio(
                                vm = vm,
                                onAbrirDetalle = { id -> navController.navigate(Ruta.Detalle(id)) },
                                onAbrirPerfilAutor = { uid -> navController.navigate(Ruta.PerfilAutor(uid)) },
                                onAceptarReto = { comNombre, sug ->
                                    navController.navigate(Ruta.Crear(comunidadNombre = comNombre, textoSugerido = sug))
                                },
                                onEditar = { id, tit -> navController.navigate(Ruta.Editar(id, tit)) }
                            )
                        }

                        composable<Ruta.Explorar> {
                            PantallaExplorar(
                                vm = vm,
                                onAbrirComunidad = { comNombre -> navController.navigate(Ruta.ComunidadVista(comNombre)) },
                                onAbrirDetalle = { id -> navController.navigate(Ruta.Detalle(id)) }
                            )
                        }

                        composable<Ruta.ComunidadVista> { backStackEntry ->
                            val route = backStackEntry.toRoute<Ruta.ComunidadVista>()
                            PantallaComunidad(
                                nombreComunidad = route.nombreComunidad,
                                vm = vm,
                                onVolver = { navController.popBackStack() },
                                onAbrirDetalle = { id -> navController.navigate(Ruta.Detalle(id)) },
                                onAbrirPerfilAutor = { uid -> navController.navigate(Ruta.PerfilAutor(uid)) },
                                onEditar = { id, tit -> navController.navigate(Ruta.Editar(id, tit)) }
                            )
                        }

                        composable<Ruta.Guardados> {
                            PantallaFavoritos(
                                vm = vm,
                                onAbrirDetalle = { id -> navController.navigate(Ruta.Detalle(id)) },
                                onAbrirPerfilAutor = { uid -> navController.navigate(Ruta.PerfilAutor(uid)) }
                            )
                        }

                        composable<Ruta.Perfil> {
                            PantallaPerfil(
                                vm = vm,
                                onAbrirAjustes = { navController.navigate(Ruta.Ajustes) }
                            )
                        }

                        composable<Ruta.Ajustes> {
                            PantallaAjustes(
                                vm = vm,
                                onVerOnboarding = {
                                    vm.marcarOnboardingVisto()
                                    navController.navigate(Ruta.Onboarding)
                                }
                            )
                        }

                        composable<Ruta.Crear> { backStackEntry ->
                            val route = backStackEntry.toRoute<Ruta.Crear>()
                            PantallaCrear(
                                vm = vm,
                                comunidadInicial = route.comunidadNombre,
                                textoSugerido = route.textoSugerido,
                                onVerPerfil = {
                                    navController.navigate(Ruta.Perfil) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable<Ruta.Detalle> { backStackEntry ->
                            val route = backStackEntry.toRoute<Ruta.Detalle>()
                            LaunchedEffect(route.id) {
                                vm.abrir(route.id)
                            }
                            PantallaDetalle(
                                vm = vm,
                                id = route.id,
                                onVolver = { navController.popBackStack() },
                                onEditar = { id, tit -> navController.navigate(Ruta.Editar(id, tit)) },
                                onAbrirPerfilAutor = { uid -> navController.navigate(Ruta.PerfilAutor(uid)) }
                            )
                        }

                        composable<Ruta.Editar> { backStackEntry ->
                            val route = backStackEntry.toRoute<Ruta.Editar>()
                            PantallaEditar(
                                vm = vm,
                                id = route.id,
                                tituloInicial = route.titulo,
                                onVolver = { navController.popBackStack() }
                            )
                        }

                        composable<Ruta.PerfilAutor> { backStackEntry ->
                            val route = backStackEntry.toRoute<Ruta.PerfilAutor>()
                            PantallaPerfilAutor(
                                vm = vm,
                                userId = route.userId,
                                onVolver = { navController.popBackStack() },
                                onAbrirDetalle = { id -> navController.navigate(Ruta.Detalle(id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
