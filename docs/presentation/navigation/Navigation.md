# Sistema de Navegación

## Descripción

Sistema de navegación basado en Navigation Compose con rutas type-safe usando sealed classes.

## Ubicación

```
presentation/navigation/
├── Screen.kt              # Definición de rutas
├── AppNavigation.kt       # NavHost configuration
├── BottomNavItem.kt       # Items del bottom navigation
└── BottomNavigationBar.kt # Composable del navigation bar
```

## Screen.kt - Rutas Type-Safe

Define todas las pantallas de la aplicación como una sealed class:

```kotlin
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object Playlists : Screen("playlists")
    data object Settings : Screen("settings")
    data object Swipe : Screen("swipe")

    companion object {
        val bottomNavScreens = listOf(Home, Playlists, Settings)
        
        fun fromRoute(route: String?): Screen? = when (route) {
            Login.route -> Login
            Home.route -> Home
            Playlists.route -> Playlists
            Settings.route -> Settings
            Swipe.route -> Swipe
            else -> null
        }
    }
}
```

### Ventajas
- **Type-safety**: No hay strings mágicos dispersos
- **Autocompletado**: IDE sugiere rutas disponibles
- **Refactoring seguro**: Cambiar una ruta actualiza todo el código

## AppNavigation.kt - NavHost

Configura el grafo de navegación con todas las pantallas:

```kotlin
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = viewModel,
                onCategoryClick = { /* navegación futura */ }
            )
        }
        
        composable(Screen.Swipe.route) {
            val viewModel: SwipeViewModel = hiltViewModel()
            SwipeScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ... otras pantallas
    }
}
```

### Inyección de ViewModels
- Usa `hiltViewModel()` dentro de cada `composable { }`
- Los ViewModels se crean con scope al NavBackStackEntry

## BottomNavItem.kt - Items de Navegación

Sealed class que define los items del bottom navigation:

```kotlin
sealed class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
) {
    data object Home : BottomNavItem(
        screen = Screen.Home,
        icon = Icons.Default.Home,
        label = "Home"
    )
    data object Playlists : BottomNavItem(
        screen = Screen.Playlists,
        icon = Icons.Default.QueueMusic,
        label = "Playlists"
    )
    data object Settings : BottomNavItem(
        screen = Screen.Settings,
        icon = Icons.Default.Settings,
        label = "Settings"
    )

    companion object {
        val items = listOf(Home, Playlists, Settings)
    }
}
```

## BottomNavigationBar.kt

Composable que renderiza el bottom navigation:

```kotlin
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = modifier) {
        BottomNavItem.items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
```

## AppScaffold.kt - Integración

El scaffold principal integra navegación y bottom bar:

```kotlin
@Composable
fun AppScaffold(
    settingsViewModel: SettingsViewModel,
    onNavigateToLogin: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.fromRoute(navBackStackEntry?.destination?.route)
    
    // Ocultar bottom bar en ciertas pantallas (ej: Swipe)
    val showBottomBar = currentScreen in Screen.bottomNavScreens

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
```

## Flujo de Navegación

```
┌─────────────┐
│   Login     │ ← Pantalla inicial si no autenticado
└──────┬──────┘
       │ auth success
       ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    Home     │ ←→  │  Playlists  │ ←→  │  Settings   │
└──────┬──────┘     └─────────────┘     └─────────────┘
       │                                       │
       │ category click                        │ sign out
       ▼                                       ▼
┌─────────────┐                         ┌─────────────┐
│    Swipe    │                         │   Login     │
└─────────────┘                         └─────────────┘
```

## Dependencias

```kotlin
// build.gradle.kts
implementation(libs.navigation.compose)       // Navigation Compose
implementation(libs.hilt.navigation.compose)  // hiltViewModel() support
```
