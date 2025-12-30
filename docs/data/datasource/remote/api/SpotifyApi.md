# SpotifyApi

Interfaz de Retrofit que define los endpoints de la API de Spotify para obtener datos de música y usuario.

## Responsabilidades
- Definir endpoints REST de Spotify API
- Mapear responses JSON a DTOs de Kotlin
- Usar suspend functions para llamadas asíncronas
- No manejar autenticación (delegada a SpotifyAuthInterceptor)

## Endpoints Implementados (MVP)

### Usuario
```kotlin
interface SpotifyApi {
    
    /**
     * Obtiene el perfil del usuario actual
     * https://api.spotify.com/v1/me
     */
    @GET("me")
    suspend fun getCurrentUser(): Response<SpotifyUserDto>
}
```

### Playlists
TODO

### Top Tracks
TODO

## Configuración en NetworkModule

```kotlin
@Provides
@Singleton
fun provideSpotifyApi(retrofit: Retrofit): SpotifyApi {
    return retrofit.create(SpotifyApi::class.java)
}
```

## Uso en Repository

```kotlin
class SpotifyRepositoryImpl(
    private val spotifyApi: SpotifyApi
) : SpotifyRepository {
    
    override suspend fun getCurrentUser(): NetworkResult<SpotifyUser> {
        return try {
            val response = spotifyApi.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val user = dto.toDomainModel() // Mapper
                NetworkResult.Success(user)
            } else {
                NetworkResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Unknown error")
        }
    }
}
```

## Autenticación

El **token NO se pasa manualmente**. El `SpotifyAuthInterceptor` lo inyecta automáticamente:

```kotlin
// ❌ NO hacer esto
@GET("me")
suspend fun getCurrentUser(@Header("Authorization") token: String): Response<SpotifyUserDto>

// ✅ Hacer esto (interceptor añade el header)
@GET("me")
suspend fun getCurrentUser(): Response<SpotifyUserDto>
```

## DTOs Asociados

Los endpoints retornan DTOs que luego se mapean a modelos de dominio:

```
SpotifyApi → SpotifyUserDto → (mapper) → User (domain)
SpotifyApi → PlaylistsResponseDto → (mapper) → List<Playlist> (domain)
SpotifyApi → TracksResponseDto → (mapper) → List<Track> (domain)
```

## Consideraciones

- ✅ Usar `Response<T>` de Retrofit para manejar errores manualmente
- ✅ Todas las funciones son `suspend` (coroutines)
- ✅ No incluir lógica de negocio en la interfaz
