# App Móvil: Recolección de Datos Rurales
Este repositorio contiene el código fuente de la aplicación móvil Android para la Plataforma de Recolección de Datos de Medidores de Agua en Zonas Rurales.

Esta aplicación es la herramienta de trabajo para los técnicos en terreno. Su objetivo principal es permitir el registro de lecturas de medidores de agua en entornos con conectividad a internet limitada o nula.

## Características Principales
Autenticación Segura: Login de usuario (técnico) contra la API central.

Modo Offline (Offline-First): La característica más importante de la app.

Las lecturas se guardan primero en una base de datos local (Room) en el dispositivo.

Esto permite a la app funcionar al 100% sin conexión a internet.

Registro de Lecturas: Formulario para ingresar el valor numérico de la lectura, ID del medidor y observaciones.

Sincronización de Datos: Una pantalla dedicada permite al técnico enviar todas las lecturas pendientes (guardadas localmente) al servidor central cuando recupera la conexión a internet.

Base de Datos Local: Gestión de la base de datos Room para almacenar LecturasPendientes.

## Tecnologías Utilizadas
Lenguaje: Java

Arquitectura: Android Nativo

Base de Datos Local: Room

Networking (API): Retrofit

Manejo de JSON: Gson

Componentes UI: AndroidX, RecyclerView, Material Components.

## Puesta en Marcha
Clona este repositorio.

Abre el proyecto con Android Studio.

Configura la URL de la API (Modifica la variable BASE_URL dentro del archivo ApiClient.java).

### app/src/main/java/com/zebra/basicintent1/api/ApiClient.java

Sincroniza el proyecto con Gradle.

Ejecuta la aplicación en un emulador o un dispositivo físico.