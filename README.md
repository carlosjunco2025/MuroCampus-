# MuroCampus

Aplicación Android (Kotlin + Jetpack Compose + Material 3) que consume el servicio REST
[JSONPlaceholder](https://jsonplaceholder.typicode.com/). Muestra un muro de publicaciones,
abre el detalle con sus comentarios, filtra por usuario y permite publicar una nueva.
Si falla la red, muestra un mensaje claro con un botón **Reintentar** en lugar de cerrarse.

Fase 1 del laboratorio: arquitectura MVVM + Repository, un commit por paso (11 en total).

## Estructura de paquetes

```text
com.tecsup.murocampus/
├── MainActivity.kt
├── model/
│   └── Modelos.kt              (Usuario [E], Post, Comentario, NuevoPost)
├── data/
│   ├── remote/
│   │   ├── PostApi.kt          (interface Retrofit: GET y POST)
│   │   └── RetrofitClient.kt   (OkHttp + logging + conversor JSON)
│   └── PostRepository.kt       (interface + PostRepositoryRemote, devuelve Result)
├── viewmodel/
│   ├── PostsViewModel.kt       (lista, detalle, filtro y crear)
│   ├── PostsViewModelFactory.kt [E]
│   └── UiState.kt [E]          (Cargando / Exito / Error + mensajeDe)
├── ui/
│   ├── Componentes.kt [E]      (TarjetaPost, Cargando, ErrorConReintento)
│   ├── PantallaLista.kt
│   ├── PantallaDetalle.kt
│   ├── PantallaCrear.kt
│   └── theme/ [E]
```

## Diagrama MVVM + Repository

```text
View (PantallaLista, PantallaDetalle, PantallaCrear)
| eventos: cargar, filtrar, abrir, publicar
^ collectAsStateWithLifecycle()
v
ViewModel (PostsViewModel) --- StateFlow<UiState> (Cargando / Exito / Error) ---> View
| viewModelScope.launch
v
PostRepository (interface)  <--- devuelve Result<...>
|
v
PostRepositoryRemote
|
v
PostApi (Retrofit) --- OkHttp ---> https://jsonplaceholder.typicode.com/
```

La View solo habla con su ViewModel. El ViewModel solo conoce la interface `PostRepository`:
no usa Retrofit, `PostApi`, `HttpException` ni `Context`. El Repository captura los errores y
los convierte en `Result`, y relanza `CancellationException` para no romper la cancelación.
`UiState.kt` es entregado [E]: su función `mensajeDe` menciona `HttpException` porque traduce
el error a texto para la pantalla.

## Diseño

La interfaz usa solo colores de `MaterialTheme`: barra superior azul acero, fondo blanco,
tarjetas gris azulado claro, error con `errorContainer` y mensaje de éxito con `tertiaryContainer`
y borde verde punteado. Los chips son píldoras (`RoundedCornerShape(50)`), el activo va relleno
de primario con texto blanco y sin icono de check.

## Capturas

Las imágenes se colocan después de ejecutar la app en el celular. La carpeta `capturas/`
ya existe con un `.gitkeep`.

| Pantalla | Captura |
| --- | --- |
| Lista de publicaciones | ![Lista](capturas/lista.png) |
| Detalle con comentarios | ![Detalle](capturas/detalle.png) |
| Filtro por usuario | ![Filtro](capturas/filtro.png) |
| Crear publicación | ![Crear](capturas/crear.png) |
| Error de conexión | ![Error de conexión](capturas/error-conexion.png) |

## Para pensar

**1. ¿Qué diferencia hay entre GET y POST? ¿Por qué crear lleva `@Body` y porUsuario `@Query`?**

GET lee datos y no cambia nada en el servidor; POST envía datos nuevos. En `crear` usamos
`@Body` porque el título y el contenido viajan dentro del cuerpo JSON (`NuevoPost`). En
`porUsuario` usamos `@Query` porque el usuario va en la URL: `posts?userId=2`.

**2. ¿Por qué la ruta es `"posts"` y no `"/posts"`?**

La URL base ya termina en `/`. Si la ruta empezara con `/`, reemplazaría el camino de la URL
base y la petición iría a la raíz del dominio. Retrofit espera rutas relativas.

**3. ¿Por qué el Repository devuelve `Result` en vez de dejar pasar la excepción?**

Así el error llega como valor: el ViewModel lo convierte en `UiState` y la pantalla dibuja
`Cargando`, `Exito` o `Error`. La interfaz no necesita saber si el fallo fue de red, del
servidor o del JSON, y la app no se cierra.

**4. ¿Qué pasa si olvidas el permiso INTERNET? ¿Y si olvidas `ignoreUnknownKeys` y el servidor agrega un campo nuevo?**

Sin el permiso la app compila y abre, pero nunca conecta: siempre muestra error de red. Sin
`ignoreUnknownKeys`, un campo nuevo del servidor provoca `SerializationException` y se ve
"Respuesta inesperada".

**5. ¿Por qué la pantalla usa un `when` sobre `UiState` y no `if` con tres variables?**

Con `UiState` solo un estado está activo a la vez: no hay combinaciones imposibles. El
compilador verifica que cubramos todos los casos, así que no se olvida ninguno.

**6. JSONPlaceholder no guarda los POST. ¿Cómo cambiaría la app con un servicio real?**

Tras publicar habría que recargar la lista o agregar el post devuelto para ver el cambio.
También haría falta enviar autenticación (por ejemplo un token), mostrar las validaciones del
servidor y evitar publicaciones duplicadas mientras se envía.
