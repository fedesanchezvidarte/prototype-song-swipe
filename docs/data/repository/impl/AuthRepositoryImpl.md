# SupabaseAuthRepository

Implementación de `AuthRepository` que gestiona la autenticación usando Supabase Auth con OAuth de Spotify como proveedor.

## Responsabilidades
- Iniciar flujo OAuth de Spotify a través de Supabase
- Manejar callback de OAuth y establecer sesión
- Obtener usuario autenticado actual desde sesión de Supabase
- Verificar estado de sesión activa
- Recuperar provider token de Spotify para llamadas a Spotify API
- Cerrar sesión y limpiar datos de autenticación

## Dependencias
- **SupabaseClient**: Cliente configurado con Auth y Postgrest plugins
- **AppConfig**: Para logging y configuración de la app
