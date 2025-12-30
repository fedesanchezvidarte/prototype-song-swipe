# SupabaseConfig

Centraliza la configuración de Supabase, inicializa el cliente y gestiona autenticación OAuth con Spotify.

## Responsabilidades
- Almacenar credenciales del proyecto Supabase (URL y anon key)
- Inicializar cliente de Supabase con lazy initialization
- Configurar plugin de Auth para OAuth con Spotify
- Configurar plugin de Postgrest para operaciones de base de datos (futura implementación)
- Definir esquema de deep link para callbacks de OAuth
- Proveer instancia singleton del cliente

## Información del Proyecto

- **Project Name**: song-swipe
- **Project ID**: keogusadivocspsdysez
- **URL**: https://keogusadivocspsdysez.supabase.co
- **Deep Link**: songswipe://callback

## Implementación

```kotlin
object SupabaseConfig {
    
    const val SUPABASE_URL = "https://keogusadivocspsdysez.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth) {
                scheme = "songswipe"
                host = "callback"
            }
            
            install(Postgrest)
        }
    }
}
```

## Plugins Instalados

### Auth
Plugin de autenticación que gestiona OAuth y sesiones.

```kotlin
install(Auth) {
    scheme = "songswipe"     // Deep link scheme
    host = "callback"         // Deep link host
}
```

**Características**:
- OAuth con Spotify como proveedor
- Gestión automática de sesiones
- Auto-refresh de tokens
- Almacenamiento seguro de credenciales
- Recuperación de provider tokens

### Postgrest
Plugin para operaciones de base de datos. (futura implementación)

```kotlin
install(Postgrest)
```

**Uso futuro**:
- Queries a tablas custom de Supabase
- CRUD de playlists, swipes, etc.
- Row Level Security (RLS)

## Uso del Cliente

### Acceso desde Repositorios
```kotlin
class SupabaseAuthRepository : AuthRepository {
    private val supabase = SupabaseConfig.client
    
    override suspend fun initiateSpotifyLogin() {
        supabase.auth.signInWith(Spotify)
    }
}
```

### Operaciones Comunes
```kotlin
// Obtener sesión actual
val session = supabase.auth.currentSessionOrNull()

// Obtener usuario
val user = supabase.auth.currentUserOrNull()

// Obtener provider token (Spotify access token)
val spotifyToken = session?.providerToken

// Sign out
supabase.auth.signOut()
```

## Deep Link Configuration

El deep link debe estar configurado en `AndroidManifest.xml`:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    
    <data
        android:scheme="songswipe"
        android:host="callback" />
</intent-filter>
```

Este deep link debe estar en la whitelist de Supabase Dashboard:
- Dashboard → Authentication → URL Configuration
- Añadir: `songswipe://callback`

## Consideraciones de Seguridad

- `SUPABASE_ANON_KEY` es pública y segura para uso client-side
- Protegida por Row Level Security (RLS) en Supabase
- En un futuro, considerar cargar desde `local.properties` o variables de entorno

## Inicialización Lazy

El cliente se inicializa mediante `lazy` delegation:
- Solo se crea cuando se accede por primera vez
- Garantiza instancia singleton
- Thread-safe por defecto en Kotlin
- Debe estar en whitelist de Supabase Dashboard

### Session Management
Supabase maneja automáticamente:
- Persistencia de sesión (sobrevive reinicios)
- Refresh de tokens expirados
- Storage seguro en SharedPreferences

## Uso en la App

```kotlin
// Obtener sesión actual
val session = SupabaseConfig.client.auth.currentSessionOrNull()

// Obtener usuario
val user = SupabaseConfig.client.auth.currentUserOrNull()

// Obtener provider token (para Spotify API)
val spotifyToken = session?.providerToken

// Sign out
SupabaseConfig.client.auth.signOut()
```