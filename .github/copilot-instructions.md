# Song Swipe - Copilot Instructions

## Descripción del Proyecto

**Song Swipe** es una aplicación Android para descubrir música mediante swipes, integrada con Spotify. Utiliza Clean Architecture + MVVM con Jetpack Compose.

## Convenciones de Idioma

- **Código fuente**: Inglés (variables, funciones, clases, comentarios de código)
- **Documentación** (`docs/`): Español
- **Commits y PRs**: Español o Inglés (consistente por PR)

## Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Kotlin |
| UI | Jetpack Compose |
| Arquitectura | Clean Architecture + MVVM |
| DI | Hilt |
| Navegación | Navigation Compose |
| Backend | Supabase |
| API | Spotify Web API |

## Estructura del Proyecto

```
app/src/main/java/org/ilerna/song_swipe_frontend/
├── core/           # Infraestructura (config, network, state)
├── data/           # DataSources, Repositories, DTOs, Mappers
├── domain/         # Models, Repository interfaces, UseCases
├── presentation/   # Screens, ViewModels, Components, Navigation
├── di/             # Hilt modules
└── MainActivity.kt
```

## Documentación de Referencia

Consultar `docs/` para documentación detallada:

- `docs/architecture/` - Arquitectura y estructura
- `docs/core/state/UiState.md` - Manejo de estados UI
- `docs/presentation/navigation/` - Sistema de navegación
- `docs/presentation/model/` - Modelos UI y mappers
- `docs/di/` - Inyección de dependencias
- `docs/domain/model/` - Modelos de dominio

## Android Studio, Jetpack Compose y Kotlin

El proyecto se desarrolla usando Android Studio con soporte para Jetpack Compose y Kotlin. Las ejecuciones de la app y pruebas unitarias se realizan dentro del IDE, no desde línea de comandos o VS Code.

## Patrones Clave

### UiState<T>

Wrapper genérico para estados de UI:

```kotlin
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data class Loading(val message: String? = null) : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}
```

### Screen (Navegación)

```kotlin
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Swipe : Screen("swipe")
    // ...
}
```

### ViewModels con Hilt

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel()
```

## Convenciones de Código

### Nombrado

- **Classes**: PascalCase (`HomeViewModel`, `MusicCategory`)
- **Functions/Variables**: camelCase (`loadCategories`, `categoryState`)
- **Constants**: SCREAMING_SNAKE_CASE o PascalCase para objetos
- **Files**: Mismo nombre que la clase principal

### Estructura de Archivos

```kotlin
// 1. Package
package org.ilerna.song_swipe_frontend.presentation.screen.home

// 2. Imports (ordenados, sin wildcards)
import androidx.compose.runtime.*
import dagger.hilt.android.lifecycle.HiltViewModel

// 3. KDoc para clases públicas
/**
 * ViewModel for the Home screen.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    // ...
}
```

### Testing

- Unit tests en `app/src/test/`
- Usar MockK para mocking
- Patrón Gherkin Given/When/Then en tests

## Flujo de Trabajo

1. **Crear feature branch** desde `main`
2. **Implementar** siguiendo arquitectura Clean
3. **Documentar** cambios significativos en `docs/`
4. **Testear** con unit tests
5. **PR** con descripción clara en .github/issue_template/pull_request_template.md
6. **Revisar y mergear** tras aprobación

## Archivos Importantes

| Archivo | Propósito |
|---------|-----------|
| `core/state/UiState.kt` | Wrapper de estados |
| `presentation/navigation/Screen.kt` | Rutas de navegación |
| `presentation/navigation/AppNavigation.kt` | NavHost config |
| `di/RepositoryModule.kt` | Bindings de repos |
| `presentation/theme/Color.kt` | Paleta de colores |