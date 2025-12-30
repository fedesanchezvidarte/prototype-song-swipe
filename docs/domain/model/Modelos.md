# Modelos de Dominio

## Descripción

Los modelos de dominio representan las entidades de negocio de la aplicación. Son clases puras de Kotlin sin dependencias de Android framework.

## Ubicación

```
domain/model/
├── Track.kt           # Canción
├── Album.kt           # Álbum
├── Artist.kt          # Artista
├── Playlist.kt        # Lista de reproducción
├── User.kt            # Usuario
├── MusicCategory.kt   # Categoría/género musical
├── AuthState.kt       # Estado de autenticación
└── UserProfileState.kt # Estado del perfil de usuario
```

## Modelos Principales

### Track

```kotlin
data class Track(
    val id: String,
    val name: String,
    val artists: List<Artist>,
    val album: Album,
    val durationMs: Int,
    val previewUrl: String?,
    val externalUrl: String,
    val popularity: Int
)
```

### Album

```kotlin
data class Album(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val releaseDate: String?
)
```

### Artist

```kotlin
data class Artist(
    val id: String,
    val name: String
)
```

### Playlist

```kotlin
data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val trackCount: Int,
    val owner: String
)
```

### User

```kotlin
data class User(
    val id: String,
    val email: String?,
    val displayName: String,
    val profileImageUrl: String?,
    val spotifyId: String?
)
```

### MusicCategory

Representa una categoría o género musical:

```kotlin
data class MusicCategory(
    val id: String,
    val name: String
)
```

**Nota**: Los colores se mapean en la capa de presentación (`MusicCategoryUi`) basándose en el `id`, manteniendo este modelo libre de dependencias de Android.

## Estados (Sealed Classes)

### AuthState

Estado de autenticación del usuario:

```kotlin
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

### UserProfileState

Estado del perfil del usuario:

```kotlin
sealed class UserProfileState {
    data object Loading : UserProfileState()
    data class Success(val user: User) : UserProfileState()
    data class Error(val message: String) : UserProfileState()
}
```

## Principios de Diseño

### 1. Sin dependencias de Android

```kotlin
// ❌ Malo - depende de Android
data class Category(
    val id: String,
    val name: String,
    val color: Color  // androidx.compose.ui.graphics.Color
)

// ✅ Bueno - puro Kotlin
data class MusicCategory(
    val id: String,
    val name: String
)
```

### 2. Inmutabilidad

Todos los modelos usan `data class` con propiedades `val`:

```kotlin
data class Track(
    val id: String,      // Inmutable
    val name: String     // Inmutable
)
```

### 3. Nullable explícito

Propiedades opcionales se marcan explícitamente:

```kotlin
data class Album(
    val id: String,           // Requerido
    val name: String,         // Requerido
    val imageUrl: String?,    // Opcional
    val releaseDate: String?  // Opcional
)
```

## Relación con Otras Capas

```
Data Layer                    Domain Layer              Presentation Layer
────────────                  ────────────              ──────────────────
SpotifyTrackDto     →         Track            →        (usa directamente)
SpotifyAlbumDto     →         Album            →        (usa directamente)
SpotifyArtistDto    →         Artist           →        (usa directamente)
                              MusicCategory    →        MusicCategoryUi
```

## Mappers

Los mappers en `data/repository/mapper/` convierten DTOs a modelos de dominio:

```kotlin
// SongMapper.kt
fun SpotifyTrackDto.toDomain(): Track = Track(
    id = id,
    name = name,
    artists = artists.map { it.toDomain() },
    album = album.toDomain(),
    durationMs = durationMs,
    previewUrl = previewUrl,
    externalUrl = externalUrls.spotify,
    popularity = popularity
)
```
