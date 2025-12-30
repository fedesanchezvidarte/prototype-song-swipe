# Autenticación con Supabase y Spotify OAuth

Flujo de autenticación de SongSwipe, utiliza **Supabase Auth** como intermediario para gestionar el OAuth de Spotify. Esta arquitectura centraliza el manejo de tokens, sesiones y refresh automático sin requerir lógica compleja en el cliente.

## Por qué Supabase + Spotify?

En lugar de implementar OAuth directamente con el SDK de Spotify, utilizamos Supabase como capa de autenticación porque:

- **Gestión automática de tokens**: Supabase refresca los tokens automáticamente sin intervención manual
- **Sesión persistente**: El usuario permanece autenticado entre reinicios de la app
- **Provider token disponible**: Acceso directo al access token de Spotify para llamadas API
- **Seguridad mejorada**: Tokens manejados por Supabase, no almacenados localmente en la app
- **User metadata**: Información del perfil de Spotify sincronizada automáticamente

---

## Flujo de Autenticación

### 1. Usuario inicia sesión
```
Usuario presiona "Login con Spotify" → App llama a Supabase Auth
```

**Código:**
```kotlin
// SupabaseAuthRepository.kt
supabase.auth.signInWith(Spotify)
```

Supabase abre automáticamente el navegador con la URL de autorización de Spotify.

### 2. Usuario autoriza en Spotify
```
Navegador → Spotify OAuth → Usuario acepta permisos
```

Spotify muestra la pantalla de consentimiento solicitando acceso a la cuenta del usuario.

### 3. Spotify redirige a Supabase
```
Spotify → https://keogusadivocspsdysez.supabase.co/auth/v1/callback?code=...
```

Spotify redirige al **Callback URL de Supabase** (configurado en Spotify Dashboard) con un código de autorización.

Supabase backend intercepta esta petición, intercambia el código por tokens con Spotify API y crea la sesión del usuario en su base de datos.

### 4. Supabase redirige a la app
```
Supabase → songswipe://callback#access_token=...&refresh_token=...
```

Supabase redirige al **deep link de la app** (definido en `SupabaseConfig` con `scheme` y `host`) pasando los tokens en el fragment.

Este deep link debe estar en la **whitelist de Redirect URLs** en Supabase Dashboard.

### 5. App importa la sesión
```kotlin
// SupabaseAuthRepository.kt
supabase.auth.importAuthToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
    retrieveUser = true,
    autoRefresh = true
)
```

La sesión queda almacenada y Supabase comienza a refrescar tokens automáticamente.

---

## Configuración del Proyecto

### Spotify Developer Dashboard

1. **Crear aplicación** en [Spotify Dashboard](https://developer.spotify.com/dashboard)
2. **Obtener credenciales:**
   - Client ID: `6e0eabd770ec417e9e531631ac85af6a`
   - Client Secret: `3424e9ef74bb4430aaa6adaa7f37f1a6`
3. **Configurar Redirect URI:**
   - Este es el **Callback URL de Supabase** (donde Spotify redirige después de la autorización)
   - Añadir: `https://keogusadivocspsdysez.supabase.co/auth/v1/callback`

### Supabase Project

1. **Información del proyecto:**
   - URL: `https://keogusadivocspsdysez.supabase.co`
   - Anon Key: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

2. **Habilitar Spotify OAuth:**
   - Dashboard → Authentication → Providers → Spotify
   - Ingresar Client ID y Secret de Spotify (obtenidos del paso anterior)

3. **Whitelist de Redirect URLs:**
   - Dashboard → Authentication → URL Configuration
   - Este es el **deep link de tu app** (donde Supabase redirige después de procesar la autenticación)
   - Añadir: `songswipe://callback`

### AndroidManifest.xml

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

Permite que el sistema Android capture el deep link y abra la app.

---

## Arquitectura de Código

### SupabaseConfig

Inicializa el cliente de Supabase con plugins necesarios:

```kotlin
val client = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_ANON_KEY
) {
    install(Auth) {
        scheme = "songswipe"
        host = "callback"
    }
    install(Postgrest)  // Para operaciones de base de datos futuras
}
```

### AuthRepository Interface

Define el contrato para operaciones de autenticación:

```kotlin
interface AuthRepository {
    suspend fun initiateSpotifyLogin()
    suspend fun handleAuthCallback(url: String): AuthState
    suspend fun getCurrentUser(): User?
    suspend fun getSpotifyAccessToken(): String?  // ← Provider token
    suspend fun signOut()
    suspend fun hasActiveSession(): Boolean
}
```

### SupabaseAuthRepository

Implementación concreta usando Supabase:

**Iniciar login:**
```kotlin
override suspend fun initiateSpotifyLogin() {
    supabase.auth.signInWith(Spotify)  // Abre navegador automáticamente
}
```

**Manejar callback:**
```kotlin
override suspend fun handleAuthCallback(url: String): AuthState {
    // Extraer tokens desde URL fragment
    val fragment = url.substringAfter("#")
    val params = fragment.split("&").associate {
        val (key, value) = it.split("=")
        key to value
    }

    val accessToken = params["access_token"]
    val refreshToken = params["refresh_token"]

    if (accessToken != null && refreshToken != null) {
        supabase.auth.importAuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken,
            retrieveUser = true,
            autoRefresh = true
        )

        // Polling para esperar carga de sesión
        var session = supabase.auth.currentSessionOrNull()
        var attempts = 0
        val maxAttempts = 20 // 2 segundos max

        while (session == null && attempts < maxAttempts) {
            delay(100)
            session = supabase.auth.currentSessionOrNull()
            attempts++
        }
}
```

**Nota sobre el polling:** `importAuthToken()` retorna inmediatamente pero la recuperación del usuario ocurre de forma asíncrona. El polling espera hasta 2 segundos para que la sesión se complete antes de retornar el estado.

**Obtener provider token (para Spotify API):**
```kotlin
override suspend fun getSpotifyAccessToken(): String? {
    return supabase.auth.currentSessionOrNull()?.providerToken
}
```

### MainActivity - Deep Link Handler

```kotlin
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    val uri = intent?.data
    if (uri != null) {
        lifecycleScope.launch {
            viewModel.handleAuthCallback(uri.toString())
        }
    }
}
```

Captura el deep link cuando Supabase redirige a la app y procesa los tokens.

---

## Uso del Provider Token

El `provider_token` es el **access token de Spotify** almacenado por Supabase. Se usa para llamadas a la API de Spotify.

### Obtención del Token

```kotlin
val spotifyToken = authRepository.getSpotifyAccessToken()
```

### TODO: Inyección en Requests (SpotifyAuthInterceptor)

El token se inyecta automáticamente en todas las peticiones a Spotify API:

```kotlin
class SpotifyAuthInterceptor(
    private val authRepository: AuthRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { authRepository.getSpotifyAccessToken() }
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

**Configuración en Retrofit:**
```kotlin
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(spotifyAuthInterceptor)
    .build()

val spotifyApi = Retrofit.Builder()
    .baseUrl("https://api.spotify.com/v1/")
    .client(okHttpClient)
    .build()
```

Todas las peticiones a Spotify incluyen el token automáticamente.

---

## Persistencia y Sesión

### Dónde se almacenan los datos?

- **Tokens**: Almacenados por Supabase en `SharedPreferences` / `DataStore` internamente (cifrados)
- **Sesión**: Gestionada por Supabase Auth internamente
- **User metadata**: Disponible en `supabase.auth.currentUserOrNull()?.userMetadata`

### Verificar sesión existente

Al iniciar la app, el `LoginViewModel` verifica si hay una sesión activa:

```kotlin
init {
    checkExistingSession()
}

private fun checkExistingSession() {
    viewModelScope.launch {
        try {
            // Esperar inicialización de Supabase
            loginUseCase.awaitInitialization()

            if (loginUseCase.hasActiveSession()) {
                val user = loginUseCase.getCurrentUser()
                if (user != null) {
                    _authState.value = AuthState.Success(user.id)
                } else {
                    _authState.value = AuthState.Idle
                }
            } else {
                _authState.value = AuthState.Idle
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Idle
        }
    }
}

**Nota:** El delay de inicialización (200ms) asegura que Supabase haya cargado completamente la sesión desde el almacenamiento local antes de verificar si existe.
```

Si existe sesión válida, el usuario no necesita volver a autenticarse.

---

## Refresh Automático de Tokens

Supabase maneja el refresh de tokens automáticamente gracias al parámetro `autoRefresh = true` en `importAuthToken()`.

**Cómo funciona?**
1. Supabase detecta que el `access_token` está por expirar
2. Usa el `refresh_token` para obtener un nuevo `access_token`
3. Actualiza la sesión sin intervención de la app

**No se requiere código adicional** para manejar expiración de tokens.

---

## Modelo de Datos

### User

```kotlin
data class User(
    val id: String,                     // Supabase user ID
    val email: String,
    val displayName: String,
    val profileImageUrl: String? = null,
    val spotifyId: String? = null       // Spotify provider ID
)
```

**Fuente de datos (MVP):**
```kotlin
val supabaseUser = supabase.auth.currentUserOrNull()
val user = User(
    id = supabaseUser.id,
    email = supabaseUser.email ?: "",
    displayName = supabaseUser.userMetadata?.get("name") as? String ?: "",
    profileImageUrl = supabaseUser.userMetadata?.get("avatar_url") as? String,
    spotifyId = supabaseUser.userMetadata?.get("provider_id") as? String
)
```

> **Nota MVP:** No se crea tabla `public.users` personalizada. Se usa únicamente `auth.users` (tabla interna de Supabase) accedida vía `currentUserOrNull()?.userMetadata`.
> 
> **Post-MVP (v2):** Se creará tabla custom para datos adicionales (preferencias, bio, etc.).

### AuthState

```kotlin
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

Representa el estado actual de la autenticación en la UI.

---

## TODO: Sign Out


Limpia la sesión local y tokens almacenados. El usuario deberá volver a autenticarse.

---

## Diagrama de Flujo Completo

```
[Usuario]
   ↓ 
[Presiona "Login"]
   ↓
[App] → supabase.auth.signInWith(Spotify)
   ↓
[Navegador] → Se abre con URL de autorización de Spotify
   ↓
[Usuario autoriza en Spotify]
   ↓
[Spotify] → Redirige a: https://keogusadivocspsdysez.supabase.co/auth/v1/callback?code=ABC123
            (Callback URL configurado en Spotify Dashboard)
   ↓
[Supabase Backend] → Intercepta la petición
                   → Intercambia code por access_token y refresh_token
                   → Crea sesión en Supabase Auth
   ↓
[Supabase] → Redirige a: songswipe://callback#access_token=XYZ&refresh_token=...
            (Deep link configurado en Supabase whitelist)
   ↓
[Android System] → Captura deep link "songswipe://callback"
                 → Abre MainActivity
   ↓
[MainActivity] → onNewIntent() recibe el URI
               → Llama a viewModel.handleAuthCallback(uri.toString())
   ↓
[SupabaseAuthRepository] → Extrae tokens del fragment (#access_token=...&refresh_token=...)
                         → supabase.auth.importAuthToken(...)
   ↓
[Sesión establecida] → Usuario autenticado en Supabase
   ↓
[App] → Puede acceder a provider_token para llamadas a Spotify API
```

---

## Recursos

- [Supabase Spotify OAuth Guide](https://supabase.com/docs/guides/auth/social-login/auth-spotify?queryGroups=language&language=kotlin)
- [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
- [Supabase Auth Kotlin SDK](https://github.com/supabase-community/supabase-kt)

## Conceptos Clave

- **Supabase Auth**: Servicio de autenticación que maneja sesiones, tokens y OAuth
- **OAuth**: Protocolo de autorización que permite a las apps acceder a recursos en nombre del usuario
- **Tokens**: Credenciales que permiten acceso a recursos protegidos
- **Access Token**: Token temporal que permite acceso a la API de Spotify
- **Refresh Token**: Token persistente usado para obtener nuevos access tokens
- **Provider Token**: Access token específico del proveedor (Spotify) para llamadas API
- **Deep Link**: URL personalizada que abre la app desde el navegador o sistema operativo
- **Scheme y Host**: Componentes de un deep link que definen cómo se abre la app
- **Callback URL**: URL a la que el proveedor redirige después de la autorización OAuth
- **Metadata de Usuario**: Información adicional del usuario obtenida del proveedor OAuth
- **State de Autenticación**: Representa el estado actual del proceso de login en la UI
- **Sealed Class**: Clase en Kotlin que representa un conjunto cerrado de subclases, útil para estados y eventos
- **Fragments**: Parte de una URL después del símbolo `#`, usado para pasar tokens de forma segura
- **Coroutines**: Biblioteca de Kotlin para manejar operaciones asíncronas de manera sencilla, parecida a los hilos pero más ligera
- **ViewModelScope**: Alcance de coroutine ligado al ciclo de vida del ViewModel
- **ViewModel**: Componente de arquitectura Android que maneja la lógica de UI y sobrevive a cambios de configuración
- **Interceptor**: Componente que intercepta y modifica peticiones HTTP, usado para inyectar tokens de autorización
