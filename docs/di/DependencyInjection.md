# Inyección de Dependencias con Hilt

## Descripción

El proyecto utiliza **Hilt** para inyección de dependencias, facilitando el testing y desacoplando las capas de la arquitectura.

## Ubicación

```
di/
├── AppModule.kt        # Dependencias generales de la aplicación
├── NetworkModule.kt    # Retrofit, OkHttp, interceptores
└── RepositoryModule.kt # Bindings de repositorios
```

## Configuración Base

### SongSwipeApplication.kt

```kotlin
@HiltAndroidApp
class SongSwipeApplication : Application()
```

- Punto de entrada de Hilt
- Genera el componente raíz de la aplicación

### MainActivity.kt

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var spotifyTokenDataStore: ISpotifyTokenDataStore
}
```

- Permite inyección de campo en Activities
- Habilita `hiltViewModel()` en Composables

## Módulos

### AppModule.kt

Provee dependencias generales de la aplicación:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): SettingsDataStore = SettingsDataStore(context)
    
    @Provides
    @Singleton
    fun provideSpotifyTokenDataStore(
        @ApplicationContext context: Context
    ): ISpotifyTokenDataStore = SpotifyTokenDataStore(context)
}
```

### NetworkModule.kt

Provee configuración de red:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideSpotifyAuthInterceptor(
        authRepository: AuthRepository
    ): SpotifyAuthInterceptor = SpotifyAuthInterceptor(authRepository)
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        spotifyAuthInterceptor: SpotifyAuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(spotifyAuthInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    
    @Provides
    @Singleton
    fun provideSpotifyApi(retrofit: Retrofit): SpotifyApi =
        retrofit.create(SpotifyApi::class.java)
}
```

### RepositoryModule.kt

Vincula interfaces con implementaciones:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: SupabaseAuthRepository
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindSpotifyRepository(
        impl: SpotifyRepositoryImpl
    ): SpotifyRepository
    
    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(
        impl: PlaylistRepositoryImpl
    ): PlaylistRepository
    
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository
}
```

## ViewModels con Hilt

### Definición

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    // ...
}
```

### Uso en Composables

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.categoriesState.collectAsState()
    // ...
}
```

### En Navigation

```kotlin
composable(Screen.Home.route) {
    val viewModel: HomeViewModel = hiltViewModel()
    HomeScreen(viewModel = viewModel)
}
```

## Dependencias en build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
}
```

## Tree de Dependencias

```
SingletonComponent
       │
       ├── AppModule
       │   ├── SettingsDataStore
       │   └── SpotifyTokenDataStore
       │
       ├── NetworkModule
       │   ├── SpotifyAuthInterceptor
       │   ├── OkHttpClient
       │   ├── Retrofit
       │   └── SpotifyApi
       │
       └── RepositoryModule
           ├── AuthRepository ← SupabaseAuthRepository
           ├── SpotifyRepository ← SpotifyRepositoryImpl
           ├── PlaylistRepository ← PlaylistRepositoryImpl
           └── CategoryRepository ← CategoryRepositoryImpl
```

## Mejores Prácticas

1. **Usar `@Singleton`** para dependencias costosas (Retrofit, Database)
2. **Usar `@Binds`** para interfaces con una sola implementación
3. **Usar `@Provides`** cuando se necesita lógica de construcción
4. **No inyectar Context directamente** - usar `@ApplicationContext`
5. **ViewModels siempre con `@HiltViewModel`**

## TODO

- [ ] Crear `DatabaseModule` para Room Database y DAOs
- [ ] Crear `UseCaseModule` para casos de uso (opcional, pueden usar constructor injection)
