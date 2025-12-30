# Estructura de la Arquitectura

## Visión General

Aplicación Android con toda la lógica en el frontend, sin backend propio.

**Justificación:**
- Spotify API + Supabase proveen toda la funcionalidad necesaria
- Desarrollo más rápido con un solo lenguaje (Kotlin)
- Sin costos de infraestructura backend
- Clean Architecture para buenas prácticas y testabilidad

## Stack Tecnológico

| Componente | Tecnología | Versión |
|------------|------------|---------|
| UI | Jetpack Compose | Latest |
| Arquitectura | Clean Architecture + MVVM | - |
| DI | Hilt | 2.57.2 |
| Navegación | Navigation Compose | 2.8.9 |
| Backend | Supabase | 3.1.4 |
| API Externa | Spotify Web API | - |
| Base de datos | Room | - |

## Estructura de Carpetas (Implementada)

```
app/src/main/java/org/ilerna/song_swipe_frontend/
│
├── core/                                    # Infraestructura transversal
│   ├── auth/                                # Gestión de tokens Spotify
│   │   └── SpotifyTokenHolder.kt            # Singleton para access token
│   ├── config/                              # Configuraciones
│   │   ├── AppConfig.kt                     # Configuración general
│   │   └── SupabaseConfig.kt                # Credenciales Supabase
│   ├── network/                             # Manejo de red
│   │   ├── ApiResponse.kt                   # Wrapper para respuestas
│   │   ├── NetworkResult.kt                 # Estados de red
│   │   └── interceptors/
│   │       ├── SpotifyAuthInterceptor.kt    # Token injection + refresh
│   │       └── ErrorInterceptor.kt          # Manejo de errores HTTP
│   └── state/                               # Gestión de estados UI
│       └── UiState.kt                       # Wrapper genérico (Idle/Loading/Success/Error)
│
├── data/                                    # Capa de datos
│   ├── datasource/
│   │   ├── local/
│   │   │   ├── dao/                         # Room DAOs
│   │   │   ├── database/                    # Room Database
│   │   │   └── preferences/                 # DataStore
│   │   │       ├── SettingsDataStore.kt
│   │   │       └── SpotifyTokenDataStore.kt
│   │   └── remote/
│   │       ├── api/
│   │       │   └── SpotifyApi.kt            # Retrofit interface
│   │       ├── dto/                         # Data Transfer Objects
│   │       └── impl/
│   │           ├── SpotifyDataSourceImpl.kt
│   │           └── SupabaseDataSourceImpl.kt
│   └── repository/
│       ├── impl/
│       │   ├── AuthRepositoryImpl.kt        # ⚠️ Pendiente renombrar
│       │   ├── SupabaseAuthRepository.kt    # Autenticación Supabase
│       │   ├── SpotifyRepositoryImpl.kt     # Datos de Spotify
│       │   ├── PlaylistRepositoryImpl.kt    # Playlists
│       │   └── CategoryRepositoryImpl.kt    # Categorías musicales
│       └── mapper/
│           ├── SongMapper.kt
│           ├── PlaylistMapper.kt
│           └── UserMapper.kt
│
├── domain/                                  # Lógica de negocio pura
│   ├── model/                               # Entidades de dominio
│   │   ├── Track.kt
│   │   ├── Album.kt
│   │   ├── Artist.kt
│   │   ├── Playlist.kt
│   │   ├── User.kt
│   │   ├── AuthState.kt                     # Estado de autenticación
│   │   ├── UserProfileState.kt              # Estado del perfil
│   │   └── MusicCategory.kt                 # Categoría musical
│   ├── repository/                          # Interfaces (contratos)
│   │   ├── AuthRepository.kt
│   │   ├── SpotifyRepository.kt
│   │   ├── PlaylistRepository.kt
│   │   └── CategoryRepository.kt            
│   └── usecase/
│       ├── auth/
│       │   ├── LoginUseCase.kt
│       │   ├── LogoutUseCase.kt
│       │   └── GetCurrentUserUseCase.kt
│       ├── playlist/
│       │   └── GetPlaylistTracksUseCase.kt
│       └── user/
│           └── GetSpotifyUserProfileUseCase.kt
│
├── presentation/                            # Capa de presentación
│   ├── components/                          # Componentes reutilizables
│   │   ├── LoadingIndicator.kt
│   │   ├── PrimaryButton.kt
│   │   └── SecundaryButton.kt
│   ├── model/                               # Modelos UI
│   │   └── MusicCategoryUi.kt               # Mapper domain → UI con colores
│   ├── navigation/                          # Sistema de navegación
│   │   ├── Screen.kt                        # Sealed class con rutas
│   │   ├── AppNavigation.kt                 # NavHost configuration
│   │   ├── BottomNavItem.kt                 # Items del bottom nav
│   │   └── BottomNavigationBar.kt           # Composable del nav bar
│   ├── screen/                              # Pantallas por feature
│   │   ├── login/
│   │   │   ├── LoginScreen.kt
│   │   │   ├── LoginErrorScreen.kt
│   │   │   └── LoginViewModel.kt
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt             # Usa UiState<T>
│   │   ├── swipe/
│   │   │   ├── SwipeScreen.kt               # Pantalla de descubrimiento
│   │   │   ├── SwipeViewModel.kt
│   │   │   └── SwipeState.kt
│   │   ├── playlists/
│   │   │   └── PlaylistsScreen.kt
│   │   ├── settings/
│   │   │   ├── SettingsScreen.kt
│   │   │   └── SettingsViewModel.kt
│   │   └── main/
│   │       └── AppScaffold.kt               # Scaffold con bottom nav
│   └── theme/
│       ├── Color.kt                         # Paleta de colores (Neon theme)
│       ├── Theme.kt                         # Material 3 theme
│       ├── Type.kt                          # Tipografía
│       └── Dimensions.kt                    # Spacing constants
│
├── di/                                      # Inyección de dependencias (Hilt)
│   ├── AppModule.kt                         # Dependencias generales
│   ├── NetworkModule.kt                     # Retrofit, OkHttp
│   └── RepositoryModule.kt                  # Bindings de repositorios
│
├── MainActivity.kt                          # @AndroidEntryPoint
└── SongSwipeApplication.kt                  # @HiltAndroidApp
```

## Flujo de Dependencias

```
┌─────────────────┐
│  Presentation   │  ViewModels (@HiltViewModel), Screens, Navigation
└────────┬────────┘
         │ depende de ↓
┌────────▼────────┐
│     Domain      │  UseCases, Models, Repository Interfaces
└────────┬────────┘
         │ implementado por ↓
┌────────▼────────┐
│      Data       │  Repository Implementations, DataSources, DTOs
└─────────────────┘
```

## Patrones Clave

### UiState<T> - Manejo de Estados

```kotlin
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data class Loading(val message: String? = null) : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}
```

**Uso en ViewModels:**
```kotlin
private val _categoriesState = MutableStateFlow<UiState<List<MusicCategory>>>(UiState.Idle)
val categoriesState: StateFlow<UiState<List<MusicCategory>>> = _categoriesState.asStateFlow()
```

### Screen - Navegación Type-Safe

```kotlin
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Playlists : Screen("playlists")
    data object Settings : Screen("settings")
    data object Swipe : Screen("swipe")
}
```

### Hilt - Inyección de Dependencias

```kotlin
// Application
@HiltAndroidApp
class SongSwipeApplication : Application()

// Activity
@AndroidEntryPoint
class MainActivity : ComponentActivity()

// ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel()
```

## Próximas Mejoras

1. **Implementar Room Database** - Persistencia local de likes/playlists
2. **Crear UseCaseModule** - Centralizar provisión de casos de uso
3. **Añadir Testing** - Unit tests para ViewModels y UseCases
4. **Offline Support** - Cacheo de datos con Room
