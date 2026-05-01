# Gyncana AstroBot - AI Agent Guidelines

## Architecture Overview
- **App Structure**: Single-activity app with SplashActivity (2s delay) launching MainActivity featuring Google Maps integration
- **Data Model**: 
  - `Localizacion` (lat/lng + title + mission)
  - `Mision` (control point, activity, statement, completion code)
  - Static data in `DatosMapa` and `DatosMision` objects
- **Key Components**: Map markers change from orange to green on mission completion; user location toggle via switch

## Development Workflow
- **Build Commands**: Use `./gradlew assembleDebug` for APK; `./gradlew installDebug` for device deployment
- **API Keys**: Store Google Maps key in `secrets.properties`; defaults in `local.defaults.properties`
- **Permissions**: Location access required; handle in `onRequestPermissionsResult` with code 100
- **Map Styling**: Custom style loaded from `res/raw/mapa_style.json`

## Code Patterns
- **ViewBinding**: Enabled in `build.gradle.kts`; use `ActivityMainBinding` for UI access
- **Marker Interaction**: First click zooms to marker; second click shows mission dialog
- **Mission Completion**: Validate code in dialog; update `mision.completada` and marker color
- **Location Services**: Use `FusedLocationProviderClient` with permission checks
- **Dialog Positioning**: Gravity.TOP with MATCH_PARENT width for mission dialogs

## Key Files
- `MainActivity.kt`: Core logic for map, markers, permissions, dialogs
- `datos/DatosMapa.kt`: Andalusian city locations with missions
- `datos/DatosMision.kt`: 16 computer-themed missions with codes
- `AndroidManifest.xml`: Location permissions and Maps API key placeholder</content>
<parameter name="filePath">D:\AndroidStudioProjects\Tarea6_GutierrezRuiz_Francisco\AGENTS.md
