# Song Swipe

Aplicación Android de descubrimiento musical mediante swipes. Integra Spotify API para recomendaciones personalizadas y Supabase como backend.

## Stack Tecnológico

- **Lenguaje**: Kotlin
- **SDK Mínimo**: Android 8.0 (API 26)
- **Arquitectura**: Clean Architecture (Data, Domain, Presentation)
- **Inyección de Dependencias**: Koin
- **Base de Datos Local**: Room
- **Networking**: Retrofit + OkHttp
- **Backend**: Supabase
- **API Externa**: Spotify Web API

## Estructura del Proyecto

```
app/src/main/java/
├── core/           # Configuración, network, utilidades
├── data/           # DataSources, repositories, mappers
├── domain/         # Modelos, casos de uso, interfaces
├── presentation/   # UI, ViewModels, activities/fragments
└── di/             # Módulos de inyección de dependencias
```

## Setup Inicial

### Prerrequisitos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 17 o superior
- Cuenta de Spotify Developer
- Proyecto en Supabase

### Instalación

TODO