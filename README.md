# StageLite

Mod de **Minecraft Forge 1.20.1** para montar escenarios, conciertos y shows de luces sin necesitar conocimientos de iluminación profesional. Pensado para un servidor de supervivencia y roleplay propio.

El plan completo de desarrollo vive en [`SPEC.md`](SPEC.md). Este README se limita al estado actual y a cómo compilar/probar.

## Estado

**Fase 0 — Proyecto base.** Ver [`docs/DECISIONES.md`](docs/DECISIONES.md) para las decisiones tomadas (nombre, mod ID, paquete, licencia) y [`CHANGELOG.md`](CHANGELOG.md) para el detalle de lo entregado en cada fase.

## Requisitos

- JDK 17
- Conexión a internet la primera vez (Gradle descarga Forge, Minecraft y las dependencias)

## Compilar

```bash
./gradlew build
```

El `.jar` resultante queda en `build/libs/`.

## Probar en el juego

```bash
./gradlew runClient
```

```bash
./gradlew runServer
```

## Estructura del proyecto

Ver la sección 5 de [`SPEC.md`](SPEC.md) para la arquitectura completa prevista.

## Licencia

Pendiente de decisión del equipo — ver [`docs/DECISIONES.md`](docs/DECISIONES.md).
