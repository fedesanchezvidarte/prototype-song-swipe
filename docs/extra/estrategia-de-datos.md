# Estrategia de Datos MVP: Online-First (Simplificado)

## **MVP: Solo Supabase Auth + Spotify API**
```kotlin
// Datos de usuario desde Supabase Auth
supabase.auth.currentUserOrNull()?.userMetadata:
├── id (Supabase user ID)
├── email
├── display_name (de Spotify)
├── avatar_url (de Spotify)
└── provider_id (Spotify ID)

// Datos de música desde Spotify API (vía Retrofit)
Spotify API:
├── Playlists del usuario
├── Canciones y tracks
├── Información de artistas
└── Top tracks del usuario
```

## **Decisiones de Arquitectura MVP:**

✅ **Implementado:**
- Supabase Auth para autenticación y sesión
- Retrofit para consumir Spotify API
- Provider token de Supabase para autorizar llamadas a Spotify
- Datos de usuario desde `auth.users` (tabla interna de Supabase)

❌ **NO en MVP:**
- Room database (no hay persistencia local)
- Tabla `public.users` en Supabase (solo `auth.users`)
- Modo offline (requiere conexión siempre)
- Sincronización de datos entre local y remoto

## **Flujo de Datos Simplificado:**
```
1. Usuario autenticado → Session en Supabase
2. Obtener provider_token → Token de Spotify
3. Llamar Spotify API con Retrofit → Datos en tiempo real
4. No hay caché local → Siempre datos frescos
5. Sin conexión → App no funciona
```