# AppConfig

Concentra la configuración general de la aplicación en un único punto, facilitando cambios entre entornos (desarrollo, producción) y evitando hardcodear valores en múltiples lugares.

## Responsabilidades
- Almacenar URLs base de APIs externas (Spotify, Supabase)
- Definir timeouts de red y configuraciones de cliente HTTP
- Gestionar flags de feature toggles (activar/desactivar funcionalidades)
- Configurar logs y modo debug
- Exponer constantes de configuración de forma segura