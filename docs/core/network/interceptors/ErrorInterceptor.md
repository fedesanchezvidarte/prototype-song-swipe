# ErrorInterceptor

`ErrorInterceptor` es un **interceptor de OkHttp** que centraliza el manejo de errores HTTP, transformando códigos de error en excepciones personalizadas o logging.

Se ejecuta **después** de cada respuesta HTTP, permitiendo interceptar y procesar errores de forma consistente antes de que lleguen a los repositorios.

### Ventajas de usar ErrorInterceptor
- **Manejo centralizado de errores HTTP**: 400, 401, 403, 404, 500, etc.
- **Logging automático**: Registra todos los errores para debugging
- **Transformación de errores**: Convierte respuestas de error en excepciones tipadas
- **Reintentos automáticos**: Puede reintentar requests que fallaron por timeout
- **Mensajes de error personalizados**: Parsea JSON de error de APIs como Spotify


## Definición

```kotlin
// core/network/interceptors/ErrorInterceptor.kt

class ErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        return try {
            val response = chain.proceed(request)
            
            // Si la respuesta no es exitosa, logueamos el error
            if (!response.isSuccessful) {
                handleErrorResponse(response)
            }
            
            response
        } catch (e: Exception) {
            // Errores de red (timeout, sin conexión, etc.)
            Log.e("ErrorInterceptor", "Network error: ${request.url}", e)
            throw NetworkException("Error de conexión: ${e.message}")
        }
    }
    
    private fun handleErrorResponse(response: Response) {
        val errorBody = response.body?.string()
        val errorMessage = parseErrorMessage(errorBody, response.code)
        
        Log.e(
            "ErrorInterceptor",
            """HTTP ${response.code} - ${response.message}
               |URL: ${response.request.url}
               |Error: $errorMessage
            """.trimMargin()
        )
        
        // Lanzar excepción según el código de error
        when (response.code) {
            401 -> throw UnauthorizedException("Token inválido o expirado")
            403 -> throw ForbiddenException("No tienes permisos para esta acción")
            404 -> throw NotFoundException("Recurso no encontrado")
            429 -> throw TooManyRequestsException("Demasiadas peticiones")
            in 500..599 -> throw ServerException("Error del servidor: $errorMessage")
            else -> throw HttpException(response.code, errorMessage)
        }
    }
    
    private fun parseErrorMessage(errorBody: String?, code: Int): String {
        return try {
            if (errorBody != null) {
                // Parsear JSON de error de Spotify
                val json = JSONObject(errorBody)
                json.getJSONObject("error").getString("message")
            } else {
                "Error HTTP $code"
            }
        } catch (e: Exception) {
            errorBody ?: "Error desconocido"
        }
    }
}
```


## Excepciones Personalizadas

```kotlin
// core/network/exceptions/NetworkExceptions.kt

/** Excepción base para errores de red */
open class NetworkException(message: String) : Exception(message)

/** Error HTTP genérico */
class HttpException(val code: Int, message: String) : NetworkException("HTTP $code: $message")

/** 401 - Token inválido o expirado */
class UnauthorizedException(message: String) : NetworkException(message)

/** 403 - Sin permisos */
class ForbiddenException(message: String) : NetworkException(message)

/** 404 - Recurso no encontrado */
class NotFoundException(message: String) : NetworkException(message)

/** 429 - Rate limit excedido */
class TooManyRequestsException(message: String) : NetworkException(message)

/** 500+ - Error del servidor */
class ServerException(message: String) : NetworkException(message)
```


## Configuración en NetworkModule

```kotlin
// di/NetworkModule.kt

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideErrorInterceptor(): ErrorInterceptor {
        return ErrorInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        spotifyAuthInterceptor: SpotifyAuthInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(spotifyAuthInterceptor)  // Primero: inyecta token
            .addInterceptor(errorInterceptor)         // Segundo: maneja errores
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}
```


## Uso en Repositorios

Las excepciones lanzadas por el interceptor se capturan en los repositorios:

```kotlin
// data/repository/impl/SongRepositoryImpl.kt

class SongRepositoryImpl(
    private val spotifyApi: SpotifyApi
) : SongRepository {

    override suspend fun searchSongs(query: String): NetworkResult<List<Song>> {
        return try {
            val response = spotifyApi.searchTracks(query)
            // Si hay error HTTP, ErrorInterceptor ya lanzó excepción
            
            val songs = response.body()?.tracks?.items?.map { it.toSong() } ?: emptyList()
            NetworkResult.Success(songs)
            
        } catch (e: UnauthorizedException) {
            NetworkResult.Error("Tu sesión expiró, inicia sesión nuevamente")
            
        } catch (e: NotFoundException) {
            NetworkResult.Error("No se encontraron canciones")
            
        } catch (e: TooManyRequestsException) {
            NetworkResult.Error("Demasiadas búsquedas, espera un momento")
            
        } catch (e: ServerException) {
            NetworkResult.Error("Error del servidor de Spotify")
            
        } catch (e: NetworkException) {
            NetworkResult.Error("Error de conexión: ${e.message}")
            
        } catch (e: Exception) {
            NetworkResult.Error("Error inesperado: ${e.message}")
        }
    }
}
```


## Logging con HttpLoggingInterceptor (Debug)

Para desarrollo, añade logging detallado:

```kotlin
// di/NetworkModule.kt

@Provides
@Singleton
fun provideOkHttpClient(
    spotifyAuthInterceptor: SpotifyAuthInterceptor,
    errorInterceptor: ErrorInterceptor
): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
            // Logging solo en modo debug
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(loggingInterceptor)
            }
        }
        .addInterceptor(spotifyAuthInterceptor)
        .addInterceptor(errorInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
}
```

## Parseo de Errores de Spotify

Spotify devuelve errores en formato JSON específico:

```json
{
  "error": {
    "status": 401,
    "message": "The access token expired"
  }
}
```

El interceptor los parsea automáticamente:

```kotlin
private fun parseErrorMessage(errorBody: String?, code: Int): String {
    return try {
        if (errorBody != null) {
            val json = JSONObject(errorBody)
            val error = json.getJSONObject("error")
            val status = error.getInt("status")
            val message = error.getString("message")
            "Spotify Error $status: $message"
        } else {
            "HTTP Error $code"
        }
    } catch (e: Exception) {
        errorBody ?: "Unknown error"
    }
}
```


## Orden de Ejecución de Interceptores

```
Request →
  1. SpotifyAuthInterceptor (añade Authorization header)
  2. HttpLoggingInterceptor (logging en debug)
  3. OkHttp realiza la llamada HTTP
  4. ErrorInterceptor (maneja errores de respuesta)
← Response
```


## Comparación: ErrorInterceptor vs try-catch manual

### ❌ Sin ErrorInterceptor (código repetitivo):
```kotlin
override suspend fun searchSongs(query: String): NetworkResult<List<Song>> {
    return try {
        val response = spotifyApi.searchTracks(query)
        when {
            response.code() == 401 -> NetworkResult.Error("Token expirado")
            response.code() == 404 -> NetworkResult.Error("No encontrado")
            response.code() in 500..599 -> NetworkResult.Error("Error servidor")
            response.isSuccessful -> NetworkResult.Success(response.body()!!.toSongs())
            else -> NetworkResult.Error("Error: ${response.code()}")
        }
    } catch (e: Exception) {
        NetworkResult.Error("Error de red")
    }
}
```

### ✅ Con ErrorInterceptor (código limpio):
```kotlin
override suspend fun searchSongs(query: String): NetworkResult<List<Song>> {
    return try {
        val response = spotifyApi.searchTracks(query)
        NetworkResult.Success(response.body()!!.toSongs())
    } catch (e: UnauthorizedException) {
        NetworkResult.Error("Tu sesión expiró")
    } catch (e: NetworkException) {
        NetworkResult.Error(e.message ?: "Error de conexión")
    }
}
```