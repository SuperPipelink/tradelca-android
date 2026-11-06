# Aplicaciones Android · Guía 8 (SENA ADSO)

Aprendiz: Felipe Flórez Quintero · Ficha 3336119

| Carpeta | Evidencia | Qué hace |
|---|---|---|
| `hola-tradelca/` | GA8-220501096-AA2-EV01 | Primera aplicación: muestra un saludo y responde a un botón. |
| `tradelca-ingresos/` | GA8-220501096-AA2-EV02 | Módulo móvil de Ingresos de Programación Tradelca: inicia sesión con segundo factor, lista las visitas del día, registra visitas nuevas y su hora de salida contra la API real. |

Las dos están en Java con el SDK de Android (sin librerías externas); minSdk 24, targetSdk 34.

## Cómo se obtiene el APK
El flujo `.github/workflows/apk.yml` corre las pruebas unitarias y compila los dos APK en GitHub Actions.
Al terminar, en la ejecución aparecen los artefactos `hola-tradelca-apk` y `tradelca-ingresos-apk` para descargar.

## Estructura de `tradelca-ingresos`
- `ui/`: actividades (presentación) y `Tarea` (trabajo en segundo plano).
- `datos/`: `ApiCliente` (HTTP, cookies y CSRF), `Respuesta`, `Visita`, `Persona`, `Ajustes` (dirección del servidor).
- `logica/`: `ValidadorVisita`, `Reloj`, `Mensajes` (Java puro, con pruebas JUnit en `app/src/test`).
