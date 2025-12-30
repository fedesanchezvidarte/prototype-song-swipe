# LoginUseCase

Caso de uso que orquesta el flujo de autenticación con Spotify a través de Supabase. Coordina las operaciones del repositorio de autenticación y expone una interfaz simplificada para la capa de presentación.

## Responsabilidad
Encapsular la lógica de negocio relacionada con el inicio de sesión, verificación de sesión existente y obtención de usuario actual.

## Dependencias
- **AuthRepository**: Interface del repositorio de autenticación

## Operaciones

### `initiateLogin()`
Inicia el flujo de autenticación con Spotify vía Supabase.

```kotlin
suspend fun initiateLogin() {
    authRepository.initiateSpotifyLogin()
}
```

Supabase abre automáticamente el navegador con la URL de OAuth.

### `handleAuthResponse(url: String)`
Procesa el callback de autenticación desde el deep link.

```kotlin
suspend fun handleAuthResponse(url: String): AuthState {
    return authRepository.handleAuthCallback(url)
}
```

Retorna `AuthState` indicando éxito o error en la autenticación.

### `getCurrentUser()`
Obtiene el usuario autenticado actual.

```kotlin
suspend fun getCurrentUser() = authRepository.getCurrentUser()
```

Retorna `User` si existe sesión activa, `null` en caso contrario.

### `hasActiveSession()`
Verifica si existe una sesión activa.

```kotlin
suspend fun hasActiveSession() = authRepository.hasActiveSession()
```

Retorna `true` si hay sesión válida, `false` si no.

### `awaitInitialization()`
Espera a que Supabase Auth complete su inicialización y cargue la sesión desde almacenamiento local.

```kotlin
suspend fun awaitInitialization() {
    kotlinx.coroutines.delay(200)
}
```

Agrega un delay de 200ms para asegurar que Supabase ha cargado completamente antes de verificar sesión.

### `signOut()`
Cierra la sesión del usuario actual.

```kotlin
suspend fun signOut() = authRepository.signOut()
```

## Notas
- No contiene lógica de UI, solo coordinación entre capas
- Todas las operaciones son suspendidas para ejecución asíncrona
- El UseCase no conoce detalles de Supabase, solo interactúa con la interfaz del repositorio
