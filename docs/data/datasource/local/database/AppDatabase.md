# AppDatabase

Clase abstracta que define la configuración de Room Database, actúa como punto de acceso principal a la base de datos local y expone los DAOs.

## Responsabilidades
- Definir entidades (tablas) que componen la base de datos
- Proveer instancia singleton de la base de datos
- Exponer métodos abstractos para obtener DAOs
- Configurar estrategias de migración entre versiones

## Configuración básica
- Anotar con `@Database` indicando entidades y versión
- Heredar de `RoomDatabase`
- Implementar pattern Singleton para evitar múltiples instancias
- Definir métodos abstractos para cada DAO (SongDao, PlaylistDao, etc.)

## Consideraciones
- **Versión**: Incrementar cuando cambien las entidades
- **Migraciones**: Definir estrategias para preservar datos al actualizar schema
- **exportSchema**: Mantener en `true` para trackear cambios de BD
- **Callback**: Útil para pre-poblar datos o ejecutar código al crear/abrir BD
- La instancia se provee vía Hilt en `DatabaseModule`
