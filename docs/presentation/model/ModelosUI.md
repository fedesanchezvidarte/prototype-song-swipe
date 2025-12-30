# Modelos UI y Mappers

## Descripción

Los modelos UI (`*Ui`) representan datos preparados específicamente para la capa de presentación. Incluyen propiedades dependientes de Android/Compose (como `Color`) que no pertenecen al dominio.

## Patrón de Separación

```
Domain Model          →    UI Model
(platform-agnostic)        (Compose-specific)

MusicCategory         →    MusicCategoryUi
  - id: String              - id: String
  - name: String            - name: String
                            - color: Color         ← Compose
                            - gradientColors: List<Color>?
```

## Ubicación

```
presentation/model/
└── MusicCategoryUi.kt
```

## MusicCategoryUi

### Modelo

```kotlin
data class MusicCategoryUi(
    val id: String,
    val name: String,
    val color: Color,
    val gradientColors: List<Color>? = null
)
```

### Mapper Functions

```kotlin
// Mapeo individual
fun MusicCategory.toUi(): MusicCategoryUi {
    val (color, gradient) = getCategoryColors(id)
    return MusicCategoryUi(
        id = id,
        name = name,
        color = color,
        gradientColors = gradient
    )
}

// Mapeo de lista
fun List<MusicCategory>.toUi(): List<MusicCategoryUi> = map { it.toUi() }
```

### Asignación de Colores

Los colores se asignan según el ID de la categoría usando constantes de `Color.kt`:

```kotlin
private fun getCategoryColors(categoryId: String): Pair<Color, List<Color>?> {
    return when (categoryId) {
        "1" -> NeonPink to null                              // Pop
        "2" -> NeonOrange to null                            // Rock
        "3" -> NeonCyan to listOf(NeonPurple, NeonCyan)      // Electronic
        "4" -> NeonPurple to null                            // Hip Hop
        "5" -> CategoryColors.Jazz to null                   // Jazz
        "6" -> CategoryColors.Classical to null              // Classical
        "7" -> NeonPink to listOf(NeonPink, NeonOrange)      // R&B
        "8" -> CategoryColors.Country to null                // Country
        "9" -> NeonOrange to listOf(NeonOrange, NeonPink)    // Latin
        "10" -> CategoryColors.Indie to null                 // Indie
        else -> NeonPink to null                             // Default
    }
}
```

### Colores Adicionales

```kotlin
object CategoryColors {
    val Jazz = Color(0xFF1DB954)      // Spotify green
    val Classical = Color(0xFF8B5CF6) // Purple
    val Country = Color(0xFFD97706)   // Amber
    val Indie = Color(0xFF6366F1)     // Indigo
}
```

## Uso en Composables

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.categoriesState.collectAsState()

    when (val currentState = state) {
        is UiState.Success -> {
            // Mapear domain → UI al renderizar
            val uiCategories = currentState.data.toUi()
            CategoryGrid(categories = uiCategories)
        }
        // ... otros estados
    }
}

@Composable
fun CategoryCard(category: MusicCategoryUi) {
    Card(
        colors = CardDefaults.cardColors(containerColor = category.color)
    ) {
        // Usar gradiente si está disponible
        val background = category.gradientColors?.let { colors ->
            Brush.linearGradient(colors)
        } ?: SolidColor(category.color)
        
        Box(modifier = Modifier.background(background)) {
            Text(text = category.name)
        }
    }
}
```

## Justificación del Patrón

### ¿Por qué separar Domain de UI?

1. **Domain puro**: `MusicCategory` no tiene dependencias de Android
2. **Testeable**: Los modelos de dominio se testean sin Robolectric
3. **Flexible**: Cambiar colores solo afecta la capa de presentación
4. **Serializable**: Los modelos de dominio pueden persistirse/transmitirse fácilmente

### ¿Cuándo crear un modelo UI?

- ✅ Cuando necesitas propiedades específicas de Compose (`Color`, `ImageVector`)
- ✅ Cuando necesitas formateo especial para mostrar
- ❌ Si el modelo de dominio ya tiene todo lo necesario

## Relación con Otros Componentes

```
ViewModel                  Composable
    │                          │
    │ expone                   │ observa
    ▼                          ▼
UiState<List<MusicCategory >>  state.data.toUi()
                                    │
                                    ▼
                            List<MusicCategoryUi>
```
