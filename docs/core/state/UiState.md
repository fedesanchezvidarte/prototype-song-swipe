# UiState<T> - Wrapper Genérico de Estados

## Descripción

`UiState<T>` es una sealed class que estandariza el manejo de estados en toda la aplicación. Proporciona un patrón consistente para representar los estados de carga, éxito y error en la UI.

## Ubicación

```
core/state/UiState.kt
```

## Estados

| Estado | Descripción | Datos |
|--------|-------------|-------|
| `Idle` | Estado inicial antes de cualquier acción | Ninguno |
| `Loading` | Cargando datos | `message: String?` (opcional) |
| `Success<T>` | Operación exitosa | `data: T` |
| `Error` | Error en la operación | `message: String`, `throwable: Throwable?` |

## Definición

```kotlin
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data class Loading(val message: String? = null) : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UiState<Nothing>()
}
```

## Propiedades Helper

```kotlin
val isIdle: Boolean
val isLoading: Boolean
val isSuccess: Boolean
val isError: Boolean
```

## Funciones Útiles

### getOrNull / errorOrNull
```kotlin
fun getOrNull(): T?        // Retorna data si Success, null si no
fun errorOrNull(): String? // Retorna message si Error, null si no
```

### map - Transformación de datos
```kotlin
fun <R> map(transform: (T) -> R): UiState<R>

// Ejemplo
val categoriesState: UiState<List<MusicCategory>> = ...
val uiState = categoriesState.map { categories -> categories.toUi() }
```

### Callbacks condicionales
```kotlin
fun onSuccess(action: (T) -> Unit): UiState<T>
fun onError(action: (String, Throwable?) -> Unit): UiState<T>
fun onLoading(action: (String?) -> Unit): UiState<T>
```

### Extensión Result → UiState
```kotlin
fun <T> Result<T>.toUiState(): UiState<T>

// Ejemplo
val result = repository.getCategories()
_state.value = result.toUiState()
```

## Uso en ViewModel

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<MusicCategory>>>(UiState.Idle)
    val categoriesState: StateFlow<UiState<List<MusicCategory>>> = _categoriesState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading()
            _categoriesState.value = categoryRepository.getCategories().toUiState()
        }
    }
}
```

## Uso en Composables

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.categoriesState.collectAsState()

    when (val currentState = state) {
        is UiState.Idle -> { /* No mostrar nada */ }
        is UiState.Loading -> LoadingIndicator(message = currentState.message)
        is UiState.Success -> CategoryGrid(categories = currentState.data.toUi())
        is UiState.Error -> ErrorMessage(
            message = currentState.message,
            onRetry = viewModel::loadCategories
        )
    }
}
```

## Ventajas

- **Type-safe**: El compilador garantiza manejo exhaustivo de estados
- **Consistente**: Mismo patrón en toda la app
- **Testeable**: Fácil de verificar estados en unit tests
- **Componible**: Las funciones `map`, `onSuccess`, etc. permiten encadenar operaciones

## Relación con Otros Componentes

- **ViewModels**: Exponen `StateFlow<UiState<T>>`
- **Repositories**: Retornan `Result<T>` que se convierte a `UiState<T>`
- **Screens**: Observan con `collectAsState()` y renderizan según estado
