# Preferences (SharedPreferences / DataStore)

Almacena datos simples y persistentes en formato clave-valor (key-value) en el dispositivo. Es más ligero que una base de datos y se usa para configuraciones, preferencias de usuario y datos pequeños.

## Diferencia con Room Database
- **Room**: Para datos estructurados, tablas, relaciones complejas (canciones, playlists)
- **Preferences**: Para datos simples (booleanos, strings, números)

## Casos de uso
- Preferencias de la app (modo oscuro, notificaciones activadas)
- Estado de onboarding (si el usuario ya vio el tutorial)
- Última fecha de sincronización con Supabase
- Configuraciones de UI (darkmode, volumen, calidad de audio)
- Flags de features (activar/desactivar funcionalidades experimentales)

**Nota importante:** Los tokens de autenticación (Supabase y Spotify) son gestionados automáticamente por el SDK de Supabase, no se guardan manualmente en Preferences.

## SharedPreferences vs DataStore

**SharedPreferences** (antiguo):
- API síncrona (puede bloquear UI)
- Formato XML
- Menos seguro para datos sensibles

**DataStore** (moderno, recomendado):
- API asíncrona con Flow y coroutinas
- Más seguro y robusto
- Dos tipos: Preferences DataStore (key-value) y Proto DataStore (objetos tipados)

## Ejemplo de lo que se guardaría
```
"dark_mode_enabled" → true
"onboarding_completed" → false
"last_sync_timestamp" → 1732550400000
"notifications_enabled" → true
"preferred_language" → "es"
"audio_quality" → "high"
```

## Implementación esperada
Crear una clase wrapper que encapsule el acceso:
- `UserPreferencesManager` o `AppPreferences`
- Métodos: `isDarkModeEnabled()`, `setDarkMode()`, `isOnboardingCompleted()`, `clearPreferences()`, etc.
- Usar DataStore preferiblemente sobre SharedPreferences
- Exponer `Flow<>` para observar cambios reactivamente

## Consideraciones
- **No guardar tokens o contraseñas** (Supabase los gestiona)
- Limpiar preferences no críticas al hacer logout (conservar preferencias de UI)
- DataStore es thread-safe y maneja errores automáticamente
- Usar tipos primitivos simples (Boolean, Int, String, Long, Float)