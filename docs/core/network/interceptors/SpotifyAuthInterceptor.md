# SpotifyAuthInterceptor

`SpotifyAuthInterceptor` es un **interceptor de OkHttp** que inyecta automáticamente el **token de autenticación de Spotify** en todas las peticiones HTTP hacia la API de Spotify.

Este interceptor se ejecuta **antes** de cada request, añadiendo el header `Authorization: Bearer <token>` necesario para autenticarse con Spotify Web API.

### Ventajas de usar un Interceptor
- **Centraliza la autenticación**: No tienes que añadir el token manualmente en cada llamada
- **Código más limpio**: Las interfaces de Retrofit no necesitan `@Header("Authorization")`
- **Supabase maneja el refresh automático**: El interceptor siempre obtiene el token actualizado
- **Separación de responsabilidades**: La lógica de auth está aislada


## Definición

```kotlin
// core/network/interceptors/SpotifyAuthInterceptor.kt

class SpotifyAuthInterceptor(
    private val supabaseClient: SupabaseClient
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Obtener el token de Spotify desde Supabase
        val spotifyToken = getSpotifyToken()
        
        // Si no hay token, continuar sin modificar el request
        if (spotifyToken.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // Añadir header de autorización
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $spotifyToken")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
    
    private fun getSpotifyToken(): String? {
        return try {
            // Obtener el provider_token (token de Spotify) desde Supabase
            supabaseClient.auth.currentSessionOrNull()?.providerToken
        } catch (e: Exception) {
            null
        }
    }
}
```


## Flujo de Autenticación

```
1. Usuario hace login con Spotify vía Supabase OAuth
   ↓
2. Supabase guarda el provider_token (token de Spotify)
   ↓
3. Interceptor obtiene el token en cada request
   ↓
4. Añade header: Authorization: Bearer <spotify_token>
   ↓
5. Spotify API valida el token y responde
```


## Configuración en NetworkModule

```kotlin
// di/NetworkModule.kt

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSpotifyAuthInterceptor(
        supabaseClient: SupabaseClient
    ): SpotifyAuthInterceptor {
        return SpotifyAuthInterceptor(supabaseClient)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        spotifyAuthInterceptor: SpotifyAuthInterceptor,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(spotifyAuthInterceptor)  // ← Inyecta token
            .addInterceptor(errorInterceptor)         // ← Maneja errores
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(okHttpClient: OkHttpClient): SpotifyApi {
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(okHttpClient)  // ← Usa OkHttpClient con interceptores
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApi::class.java)
    }
}
```


## Uso en SpotifyApi (sin @Header)

Gracias al interceptor, **no necesitas** añadir `@Header` en cada endpoint:

```kotlin
// data/remote/api/SpotifyApi.kt

interface SpotifyApi {
    
    // ❌ SIN interceptor tendrías que hacer esto:
    // @GET("me")
    // suspend fun getCurrentUser(
    //     @Header("Authorization") token: String
    // ): Response<UserDTO>
    
    // ✅ CON interceptor es mucho más simple:
    @GET("me")
    suspend fun getCurrentUser(): Response<UserDTO>
    
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String = "track"
    ): Response<SearchTracksResponse>
    
    @GET("tracks/{id}")
    suspend fun getTrack(
        @Path("id") trackId: String
    ): Response<TrackDTO>
    
    @GET("me/playlists")
    suspend fun getUserPlaylists(): Response<PlaylistsResponse>
}
```


## Logging para Debug (solo en desarrollo)

> **Nota**: Supabase maneja automáticamente el refresh de tokens. Este interceptor simplemente obtiene el token actual, que Supabase mantiene actualizado.

```kotlin
class SpotifyAuthInterceptor(
    private val supabaseClient: SupabaseClient
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val spotifyToken = getSpotifyToken()
        
        if (BuildConfig.DEBUG) {
            Log.d("SpotifyAuth", "Token: ${spotifyToken?.take(20)}...")  // Solo primeros 20 chars
            Log.d("SpotifyAuth", "Request: ${originalRequest.url}")
        }
        
        if (spotifyToken.isNullOrEmpty()) {
            Log.w("SpotifyAuth", "No token available")
            return chain.proceed(originalRequest)
        }
        
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $spotifyToken")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }
    
    private fun getSpotifyToken(): String? {
        return try {
            supabaseClient.auth.currentSessionOrNull()?.providerToken
        } catch (e: Exception) {
            Log.e("SpotifyAuth", "Error getting token", e)
            null
        }
    }
}
```


## Puntos Clave

- ✅ **Supabase provee y refresca el token automáticamente**: Después del login OAuth, `providerToken` contiene el token de Spotify siempre actualizado
- ✅ **Se inyecta automáticamente**: No se necesita pasarlo manualmente en cada llamada
- ✅ **OkHttp lo ejecuta**: Antes de cada request a Spotify API
- ✅ **Simplifica SpotifyApi**: Las interfaces Retrofit quedan limpias sin `@Header`
- ✅ **No necesitas implementar refresh manual**: Supabase maneja el ciclo de vida del token
- ⚠️ **Requiere sesión activa**: Si no hay sesión en Supabase, no hay token
- ⚠️ **Solo para Spotify API**: No se usa para llamadas a Supabase directas