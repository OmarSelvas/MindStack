# MindStack 

MindStack es una aplicación movil desarrollada en Kotlin y Jetpack Compose. 
tiene como proposito ser un gestor de energía,

## Características Principales

- **Seguimiento del Estado de Ánimo (Mood Tracking):** Interfaz intuitiva y 
- circular para registrar cómo te sientes diariamente (Exhausto, Triste, Neutral, Feliz, Excelente).
- **Entrenamiento Cognitivo:** Diversos mini-juegos diseñados para estimular el cerebro:
  - **NeuroReflejo:** Analisis de tu concentracion.
  - **Memory Game:** Desafía tu capacidad de retención.
- **Historial de Progreso:** Visualiza tu evolución y registros pasados para identificar patrones en tu bienestar.
- **Gestión de Perfil:** Sistema de autenticación (Registro/Login) y personalización de ajustes.

## Tecnologías Utilizadas

- **Kotlin:** Lenguaje de programación principal.
- **Jetpack Compose:** Toolkit moderno para la construcción de interfaces nativas.
- **MVVM (Model-View-ViewModel):** Arquitectura limpia y escalable.
- **Room Database:** Persistencia de datos local para estados de ánimo y puntajes.
- **Retrofit & OkHttp:** Comunicación con APIs externas (si aplica).
- **Navigation Compose:** Gestión fluida de la navegación entre pantallas.

## Estructura del Proyecto

- `ui/`: Temas y estilos globales.
- `viewmodels/`: Lógica de negocio y gestión de estado de la UI.
- `views/`: Pantallas de la aplicación (Main, Mood, Games, History, etc.).
- `data/`: Modelos de datos, DAOs y configuración de la base de datos Room.
- `navigation/`: Configuración del `NavHost` y la barra de navegación personalizada.
- `components/`: Componentes de Compose reutilizables.
