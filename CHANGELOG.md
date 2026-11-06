# Changelog

Formato libre, orden cronológico inverso (lo más reciente arriba).

## Fase 1 — Foco simple con redstone

- `LightState` (color, intensidad 0-100, grupo; angulo/efecto/velocidad reservados para fases posteriores).
- Bloque `foco` (6 direcciones vía `BlockStateProperties.FACING`, propiedad `lit`), con `BlockEntity` propio.
- Reacciona a redstone: `neighborChanged` lee `getBestNeighborSignal` (0-15) y escala la intensidad efectiva proporcionalmente; sin señal, apagado.
- Pantalla propia al hacer clic derecho (10 colores en paleta, deslizador de intensidad, nombre de grupo) — sin slots de inventario. Un paquete `SimpleChannel` (`SetFocoConfigPacket`) manda los cambios del cliente al servidor, que valida distancia antes de aplicar.
- Renderizado:
  - **Etapa A** (solo iluminación): `lightLevel()` dinámico según la propiedad `lit` (13 encendido / 0 apagado) + textura emisiva del lente.
  - **Etapa B** (haz básico): se reutiliza el método público `BeaconRenderer.renderBeaconBeam` de vanilla (el mismo que dibuja el haz del faro), orientado hacia la cara del foco mediante rotación del `PoseStack`. Longitud fija (10 bloques) — la Etapa C (recortar contra bloques sólidos) queda para una fase posterior.
- Receta de crafteo (hierro + cristal + redstone + polvo de glowstone) y loot table (se recupera al romperlo).
- Texturas placeholder generadas con PIL (metal gris + lente que cambia de apagado a cálido encendido).
- Verificado en esta sesión: `./gradlew build` y `./gradlew runServer` sin errores de carga (bloque/item/receta/loot table/red registrados correctamente). `runClient` sigue sin poderse probar aquí (sin pantalla).

**Simplificaciones deliberadas respecto al SPEC** (documentadas para no olvidarlas):
- No hay interruptor manual de encendido/apagado independiente de la redstone: el foco está apagado si no recibe señal, encendido si la recibe, con intensidad proporcional a la potencia (1-15). Esto coincide exactamente con el criterio de aceptación de la Fase 1 ("colocar un foco, ponerle redstone y verlo encenderse").
- El color se elige por paleta (10 colores), no por RGB libre: el selector "paleta + RGB" completo lo pide el SPEC para la **consola** (Fase 2), no para el foco individual.
- `GroupManager` (SavedData) todavía no existe: el campo "grupo" ya se guarda en cada foco, pero no hace nada hasta la Fase 2, cuando la consola lo use para agrupar.

## Fase 0 — Proyecto base

- Proyecto Forge 1.20.1 inicial (Gradle, `mods.toml`, `pack.mcmeta`, pestaña creativa vacía).
- `en_us.json` / `es_es.json` desde el principio.
- Workflow de CI en GitHub Actions (compila con JDK 17 en cada push/PR).
- `docs/DECISIONES.md`, `docs/TESTING.md`, `README.md`.
- Verificado en esta sesión: `./gradlew build` (`BUILD SUCCESSFUL`, Forge 1.20.1-47.4.28) y `./gradlew runServer` (el servidor dedicado arranca y carga StageLite sin errores). `runClient` no se pudo probar aquí por falta de pantalla; queda pendiente de prueba en un PC real.
- Advertencia conocida (no bloqueante): `FMLJavaModLoadingContext.get()` está marcado `@Deprecated(forRemoval = true)` en Forge 47.4.28, pero sigue siendo la API estable documentada para 1.20.1. Se revisará si aparece una alternativa clara antes de la Fase 6.
