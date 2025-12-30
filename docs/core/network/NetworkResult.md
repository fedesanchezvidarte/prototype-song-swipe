# NetworkResult

`NetworkResult` es una **sealed class** que envuelve las respuestas de operaciones de red, permitiendo manejar los tres estados posibles: éxito, error y carga.

Es el tipo de retorno estándar para todas las funciones que hacen llamadas HTTP (Retrofit) o acceden a datos remotos.

- **Manejo explícito de estados**: Obliga a manejar todos los casos (`Success`, `Error`, `Loading`)
- **Type-safe**: El compilador garantiza que manejas todos los estados
- **Consistencia**: Todas las llamadas de red siguen el mismo patrón
- **Facilita UI reactiva**: Perfecto para mostrar loaders, errores y datos

## Definición

```kotlin
// core/network/NetworkResult.kt

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
```

### Estados:

- **`Success`**: Operación exitosa, contiene los datos de tipo `T`
- **`Error`**: Fallo en la operación, incluye mensaje y código HTTP opcional
- **`Loading`**: Operación en progreso (útil para mostrar indicadores de carga)

## Uso en Repository

```kotlin
// data/repository/impl/SongRepositoryImpl.kt

class SongRepositoryImpl(
    private val spotifyApi: SpotifyApi
) : SongRepository {

    override suspend fun searchSongs(query: String): NetworkResult<List<Song>> {
        return try {
            val response = spotifyApi.searchTracks(query)
            
            if (response.isSuccessful && response.body() != null) {
                val songs = response.body()!!.tracks.items.map { it.toSong() }
                NetworkResult.Success(songs)
            } else {
                NetworkResult.Error(
                    message = "Error al buscar canciones",
                    code = response.code()
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.message ?: "Error desconocido"
            )
        }
    }
}
```


## Uso en ViewModels

```kotlin
// presentation/viewmodels/SearchViewModel.kt

class SearchViewModel(
    private val searchSongsUseCase: SearchSongsUseCase
) : ViewModel() {

    private val _songsState = MutableStateFlow<NetworkResult<List<Song>>>(NetworkResult.Loading)
    val songsState: StateFlow<NetworkResult<List<Song>>> = _songsState

    fun searchSongs(query: String) {
        viewModelScope.launch {
            _songsState.value = NetworkResult.Loading
            _songsState.value = searchSongsUseCase(query)
        }
    }
}
```


## Uso en UI (Jetpack Compose)

```kotlin
// presentation/screens/search/SearchScreen.kt

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    val songsState by viewModel.songsState.collectAsState()

    when (songsState) {
        is NetworkResult.Loading -> {
            CircularProgressIndicator()
        }
        
        is NetworkResult.Success -> {
            val songs = (songsState as NetworkResult.Success).data
            LazyColumn {
                items(songs) { song ->
                    SongCard(song = song)
                }
            }
        }
        
        is NetworkResult.Error -> {
            val error = (songsState as NetworkResult.Error)
            ErrorMessage(
                message = error.message,
                onRetry = { viewModel.searchSongs("query") }
            )
        }
    }
}
```


## Extensión útil: `onSuccess` / `onError`

```kotlin
// core/network/NetworkResultExtensions.kt

inline fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

inline fun <T> NetworkResult<T>.onError(action: (String) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(message)
    return this
}

// Uso encadenado:
viewModel.searchSongs("rock")
    .onSuccess { songs -> println("Encontradas: ${songs.size}") }
    .onError { error -> println("Error: $error") }
```


## Transformación de datos

```kotlin
// Mapear un NetworkResult<A> a NetworkResult<B>
fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> NetworkResult.Error(message, code)
        is NetworkResult.Loading -> NetworkResult.Loading
    }
}

// Uso:
val songResult: NetworkResult<List<SongDTO>> = repository.getSongs()
val mappedResult: NetworkResult<List<Song>> = songResult.map { dtos ->
    dtos.map { it.toDomain() }
}
```


## Comparación con ApiResponse

| **NetworkResult** | **ApiResponse** |
|-------------------|-----------------|
| Para **flujo completo** (Loading/Success/Error) | Para **respuesta HTTP** únicamente |
| Incluye estado `Loading` | No tiene Loading |
| Usado en **ViewModels y UI** | Usado en **llamadas de Retrofit** |
| Sealed class genérica | Wrapper de `Response<T>` |