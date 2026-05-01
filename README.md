# Gyncana AstroBot

Una aplicación de geocaching temática ambientada en el universo de Astro Bot, donde los usuarios deben completar misiones informáticas en ciudades andaluzas.

## Descripción

Gyncana AstroBot es una aplicación Android que combina mapas interactivos con misiones educativas sobre conceptos de informática. Los usuarios visitan ubicaciones reales en Andalucía, cada una asociada a una misión con un código de completación.

## Características

- **Mapa Interactivo**: Integración con Google Maps para mostrar ubicaciones de misiones.
- **Misiones Temáticas**: 16 misiones inspiradas en conceptos de informática (CPU, memoria RAM, redes, etc.).
- **Ubicación del Usuario**: Opción para mostrar la ubicación actual en el mapa.
- **Marcadores Dinámicos**: Los marcadores cambian de color (naranja a verde) al completar misiones.
- **Diálogos Interactivos**: Interfaz para introducir códigos de misión y validar completación.

## Tecnologías Utilizadas

- **Lenguaje**: Kotlin
- **Framework**: Android SDK
- **Mapas**: Google Maps API
- **Ubicación**: Fused Location Provider
- **UI**: ViewBinding
- **Build**: Gradle con Kotlin DSL

## Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/gyncana-astrobot.git
   cd gyncana-astrobot
   ```

2. Configura las claves API:
   - Crea un archivo `secrets.properties` en la raíz del proyecto.
   - Añade tu clave de Google Maps API:
     ```
     MAPS_API_KEY=tu_clave_api_aqui
     ```

3. Construye la aplicación:
   ```bash
   ./gradlew assembleDebug
   ```

4. Instala en dispositivo/emulador:
   ```bash
   ./gradlew installDebug
   ```

## Uso

1. Abre la aplicación (pantalla de splash de 2 segundos).
2. Concede permisos de ubicación si se solicitan.
3. Explora el mapa con marcadores en ciudades andaluzas.
4. Haz clic en un marcador para hacer zoom, luego clic otra vez para ver la misión.
5. Introduce el código correcto en el diálogo para completar la misión.
6. El marcador cambiará a verde al completar.

## Estructura del Proyecto

- `app/src/main/java/dam/pmdm/tarea6_gutierrezruiz_francisco/`: Código fuente principal
  - `MainActivity.kt`: Actividad principal con mapa y lógica de misiones
  - `SplashActivity.kt`: Pantalla de bienvenida
  - `datos/`: Modelos de datos (Localizacion, Mision, DatosMapa, DatosMision)
- `app/src/main/res/`: Recursos (layouts, estilos, mapa_style.json)
- `AndroidManifest.xml`: Permisos y configuración

## Contribución

1. Haz fork del proyecto.
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`).
3. Commit tus cambios (`git commit -am 'Añade nueva funcionalidad'`).
4. Push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Abre un Pull Request.

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Autor

Francisco Gutiérrez Ruiz - Proyecto para la asignatura PMDM (Programación Multimedia y Dispositivos Móviles)</content>
<parameter name="filePath">D:\AndroidStudioProjects\Tarea6_GutierrezRuiz_Francisco\README.md
