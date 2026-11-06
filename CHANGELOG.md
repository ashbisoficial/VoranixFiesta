# Changelog

Formato libre, orden cronológico inverso (lo más reciente arriba).

## Fase 0 — Proyecto base

- Proyecto Forge 1.20.1 inicial (Gradle, `mods.toml`, `pack.mcmeta`, pestaña creativa vacía).
- `en_us.json` / `es_es.json` desde el principio.
- Workflow de CI en GitHub Actions (compila con JDK 17 en cada push/PR).
- `docs/DECISIONES.md`, `docs/TESTING.md`, `README.md`.
- Verificado en esta sesión: `./gradlew build` (`BUILD SUCCESSFUL`, Forge 1.20.1-47.4.28) y `./gradlew runServer` (el servidor dedicado arranca y carga StageLite sin errores). `runClient` no se pudo probar aquí por falta de pantalla; queda pendiente de prueba en un PC real.
- Advertencia conocida (no bloqueante): `FMLJavaModLoadingContext.get()` está marcado `@Deprecated(forRemoval = true)` en Forge 47.4.28, pero sigue siendo la API estable documentada para 1.20.1. Se revisará si aparece una alternativa clara antes de la Fase 6.
