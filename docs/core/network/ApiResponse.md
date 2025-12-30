# ApiResponse

`ApiResponse` es una **clase wrapper** que envuelve las respuestas HTTP de Retrofit (`Response<T>`), facilitando el manejo de respuestas exitosas y errores de forma consistente.

Se usa en la **capa de datos** (repositorios y datasources) como paso intermedio antes de convertir a `NetworkResult`.

- **Abstrae la complejidad de Retrofit**: Evita repetir `response.isSuccessful` y `response.body()` en cada llamada
- **Manejo centralizado de errores HTTP**: 400, 401, 404, 500, etc.
- **Conversión simple a NetworkResult**: Facilita la transformación de respuestas HTTP a estados de UI
- **Type-safe**: Mantiene el tipado genérico de Retrofit

## Definición

```kotlin
// core/network/ApiResponse.kt

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    
    data class Error(
        val code: Int,
        val message: String,
        val errorBody: String? = null
    ) : ApiResponse<Nothing>()
    
    companion object {
        /** Convierte Response<T> de Retrofit a ApiResponse<T> */
        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Success(body)
                } else {
                    Error(
                        code = response.code(),
                        message = "Respuesta vacía del servidor"
                    )
                }
            } else {
                Error(
                    code = response.code(),
                    message = response.message(),
                    errorBody = response.errorBody()?.string()
                )
            }
        }
        
        /** Maneja excepciones de red */
        fun <T> create(error: Throwable): ApiResponse<T> {
            return Error(
                code = -1,
                message = error.message ?: "Error de conexión"
            )
        }
    }
}
```

## Uso en DataSources

```kotlin
// data/remote/impl/SpotifyDataSourceImpl.kt

class SpotifyDataSourceImpl(
    private val spotifyApi: SpotifyApi
) {
    suspend fun searchTracks(query: String): ApiResponse<SearchTracksResponse> {
        return try {
            val response = spotifyApi.searchTracks(query)
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
    }
    
    suspend fun getTrack(trackId: String): ApiResponse<TrackDTO> {
        return try {
            val response = spotifyApi.getTrack(trackId)
            ApiResponse.create(response)
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
    }
}
```

## Conversión a NetworkResult

En los **repositorios**, convertimos `ApiResponse` a `NetworkResult` para exponerlo a capas superiores:

```kotlin
// data/repository/impl/SongRepositoryImpl.kt

class SongRepositoryImpl(
    private val spotifyDataSource: SpotifyDataSourceImpl,
    private val songMapper: SongMapper
) : SongRepository {

    override suspend fun searchSongs(query: String): NetworkResult<List<Song>> {
        return when (val apiResponse = spotifyDataSource.searchTracks(query)) {
            is ApiResponse.Success -> {
                val songs = apiResponse.data.tracks.items.map { 
                    songMapper.toDomain(it) 
                }
                NetworkResult.Success(songs)
            }
            
            is ApiResponse.Error -> {
                NetworkResult.Error(
                    message = handleErrorMessage(apiResponse.code),
                    code = apiResponse.code
                )
            }
        }
    }
    
    private fun handleErrorMessage(code: Int): String {
        return when (code) {
            401 -> "Token de Spotify expirado"
            404 -> "Canción no encontrada"
            429 -> "Demasiadas peticiones, intenta más tarde"
            500, 502, 503 -> "Error del servidor de Spotify"
            -1 -> "Error de conexión a internet"
            else -> "Error al obtener canciones"
        }
    }
}
```

## Extensión útil: `toNetworkResult()`

```kotlin
// core/network/ApiResponseExtensions.kt

fun <T> ApiResponse<T>.toNetworkResult(): NetworkResult<T> {
    return when (this) {
        is ApiResponse.Success -> NetworkResult.Success(data)
        is ApiResponse.Error -> NetworkResult.Error(message, code)
    }
}

// Uso simplificado:
override suspend fun getSong(id: String): NetworkResult<Song> {
    return spotifyDataSource.getTrack(id)
        .map { dto -> songMapper.toDomain(dto) }
        .toNetworkResult()
}
```

## Manejo de errores específicos de API

```kotlin
// core/network/ApiResponse.kt (extensión)

/** Parsea errores JSON de APIs como Spotify */
fun ApiResponse.Error.parseSpotifyError(): String {
    return try {
        val json = JSONObject(errorBody ?: "{}")
        val error = json.getJSONObject("error")
        error.getString("message")
    } catch (e: Exception) {
        message
    }
}

// Uso en repositorio:
is ApiResponse.Error -> {
    val errorMessage = apiResponse.parseSpotifyError()
    NetworkResult.Error(errorMessage, apiResponse.code)
}
```

## Diferencias clave: ApiResponse vs NetworkResult

| **ApiResponse** | **NetworkResult** |
|-----------------|-------------------|
| **Capa Data** (repositorios, datasources) | **Capa Presentation** (ViewModels, UI) |
| Envuelve **respuestas HTTP** de Retrofit | Envuelve **estados de operaciones** completas |
| **No tiene Loading** | **Incluye Loading** para UI |
| Solo Success/Error | Success/Error/Loading |
| Contiene detalles HTTP (código, errorBody) | Mensajes simplificados para usuario |


## Flujo completo: Retrofit → ApiResponse → NetworkResult

```kotlin
// 1. API de Retrofit
interface SpotifyApi {
    @GET("tracks/{id}")
    suspend fun getTrack(@Path("id") id: String): Response<TrackDTO>
}

// 2. DataSource retorna ApiResponse
class SpotifyDataSourceImpl(private val api: SpotifyApi) {
    suspend fun getTrack(id: String): ApiResponse<TrackDTO> {
        return try {
            ApiResponse.create(api.getTrack(id))  // Response<T> → ApiResponse<T>
        } catch (e: Exception) {
            ApiResponse.create(e)
        }
    }
}

// 3. Repository convierte a NetworkResult
class SongRepositoryImpl(
    private val dataSource: SpotifyDataSourceImpl,
    private val mapper: SongMapper
) : SongRepository {
    override suspend fun getSong(id: String): NetworkResult<Song> {
        return when (val response = dataSource.getTrack(id)) {
            is ApiResponse.Success -> {
                NetworkResult.Success(mapper.toDomain(response.data))
            }
            is ApiResponse.Error -> {
                NetworkResult.Error(response.message, response.code)
            }
        }
    }
}

// 4. ViewModel expone NetworkResult a la UI
class SongViewModel(private val repo: SongRepository) : ViewModel() {
    private val _songState = MutableStateFlow<NetworkResult<Song>>(NetworkResult.Loading)
    val songState: StateFlow<NetworkResult<Song>> = _songState
    
    fun loadSong(id: String) {
        viewModelScope.launch {
            _songState.value = NetworkResult.Loading
            _songState.value = repo.getSong(id)
        }
    }
}
```

## Cuándo usar cada uno?

### Usar **ApiResponse** cuando:
- Estás en la **capa de datos** (datasources, repositorios)
- Necesitas **detalles HTTP** (códigos, error bodies)
- Conviertes respuestas de Retrofit

### Usar **NetworkResult** cuando:
- Estás en **ViewModels o UI**
- Necesitas mostrar **estados de carga**
- Quieres mensajes **simplificados para usuarios**