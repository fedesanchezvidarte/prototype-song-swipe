# DatabaseConstants

Centraliza nombres de tablas, columnas y queries relacionadas con la base de datos local, evitando hardcodear strings en múltiples lugares.

## Responsabilidades
- Definir nombres de tablas de Room
- Definir nombres de columnas para cada entidad
- Almacenar queries SQL complejas reutilizables
- Definir índices y claves foráneas como constantes
- Versión y nombre de la base de datos

## Organización recomendada
Agrupar constantes por tabla usando objects anidados:
- `DATABASE_NAME` y `DATABASE_VERSION`
- `SongTable`: Nombres de tabla y columnas de canciones
- `PlaylistTable`: Nombres de tabla y columnas de playlists
- `SwipeTable`: Nombres de tabla y columnas de swipes
- `UserTable`: Nombres de tabla y columnas de usuario

## Consideraciones
- Usar UPPER_SNAKE_CASE para nombres de constantes
- Mantener sincronizadas con las anotaciones `@ColumnInfo` en entidades
- Facilita refactoring: cambiar nombre en un solo lugar
- Útil para testing y queries personalizadas
