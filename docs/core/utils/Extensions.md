# Extensions

Kotlin permite "extender" clases con nuevas funciones. Son útiles para operaciones comunes que repites en toda la app.
`Extensions.kt` es tu "caja de herramientas" con funciones útiles de uso repetido en toda la aplicación.


## Ejemplos Comunes

```kotlin
// core/utils/Extensions.kt

// ============================================
// String Extensions
// ============================================

/** Valida si un email tiene formato correcto */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/** Capitaliza la primera letra de cada palabra */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { 
        it.replaceFirstChar { char -> char.uppercase() } 
    }
}

/** Acorta texto largo para UI */
fun String.truncate(maxLength: Int = 50): String {
    return if (length > maxLength) "${take(maxLength)}..." else this
}


// ============================================
// Int/Long Extensions (Duración de canciones)
// ============================================

/** Convierte milisegundos a formato MM:SS */
fun Int.toTimeFormat(): String {
    val minutes = this / 60000
    val seconds = (this % 60000) / 1000
    return String.format("%d:%02d", minutes, seconds)
}

// Uso: 30000.toTimeFormat() → "0:30"


// ============================================
// Context Extensions (Android)
// ============================================

/** Muestra un Toast corto */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/** Oculta el teclado */
fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}


// ============================================
// Flow Extensions (Manejo de estados)
// ============================================

/** Convierte Flow en StateFlow con valor inicial */
fun <T> Flow<T>.asState(
    scope: CoroutineScope,
    initialValue: T
): StateFlow<T> {
    return stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialValue
    )
}
```

## Uso de Extensiones

```kotlin
// ============================================
// Compose Extensions
// ============================================

/** Modificador para padding horizontal/vertical */
fun Modifier.paddingHorizontal(padding: Dp): Modifier {
    return this.padding(horizontal = padding)
}

fun Modifier.paddingVertical(padding: Dp): Modifier {
    return this.padding(vertical = padding)
}

// En LoginViewModel
fun validateEmail(email: String): Boolean {
    return email.isValidEmail()  // ← Extension de String
}

// En SwipeCard (Compose)
Text(
    text = song.title.capitalizeWords(),  // ← Extension de String
    style = MaterialTheme.typography...
)

Text(
    text = song.durationMs.toTimeFormat(),  // ← Extension de Int
    style = MaterialTheme.typography...
)

// En LoginScreen
context.showToast("Login exitoso")  // ← Extension de Context
context.hideKeyboard(view)
```