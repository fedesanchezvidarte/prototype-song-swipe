# DAOs (Data Access Objects)

Interfaz de acceso a la base de datos local Room, definiendo operaciones CRUD sobre las entidades. Abstraen las queries SQL y exponen métodos type-safe para manipular datos.

## Responsabilidades
- Definir operaciones de lectura, escritura, actualización y eliminación
- Exponer métodos con suspendidos para operaciones asíncronas*
- Proveer queries personalizadas con `@Query` para casos específicos
- Manejar relaciones entre entidades (one-to-many, many-to-many)
- Retornar `Flow<>` para observar cambios reactivos en tiempo real

## DAOs esperados
- **SongDao**: CRUD de canciones guardadas localmente
- **PlaylistDao**: CRUD de playlists del usuario
- **SwipeDao**: Registro de swipes (likes/dislikes) para sincronizar
- **UserDao**: Información del perfil de usuario

## Buenas prácticas
- *Usar `suspend` para operaciones que bloqueen (insert, update, delete) y usar `Flow<>` para queries que necesitan observarse reactivamente, así la base de datos no congela la interfaz aunque tarde en responder
- Nombrar métodos descriptivamente: `getSongsByPlaylistId`, `deleteSwipeById`
- **Evitar lógica de negocio en DAOs**, solo acceso a datos
- Combinar múltiples operaciones en transacciones cuando sea necesario