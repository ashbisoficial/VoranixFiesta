# Changelog

Formato libre, orden cronológico inverso (lo más reciente arriba).

## Fase 3 — Más focos y efectos

- **Refactor**: se extrajo `AbstractLightBlockEntity` y `AbstractLightBlock` con la lógica común a todos los "focos" (LightState, redstone, blackout, grupo, sincronización, apertura de pantalla). Foco, Panel LED y Estroboscopio ya no duplican código entre sí; solo cambian su renderer.
- **Panel LED** (`panel`): igual que el foco (misma pantalla, mismo paquete de red), pero sin haz marcado — en vez de un cono dibuja un resplandor corto y ancho reutilizando el mismo método de vanilla. Simplificación deliberada: no es un quad propio, ver nota en `WashBlockEntityRenderer`.
- **Estroboscopio** (`estrobo`): igual que el foco, pero empieza con el efecto "parpadeo" activado por defecto.
- **Efectos de grupo con BPM**: arcoíris (cada foco del grupo gira de color, desfasado por su posición) y persecución (se encienden uno a uno siguiendo el orden del grupo). Ambos necesitan saber la posición del foco dentro de su grupo (`groupIndex`/`groupSize`), que la consola asigna al aplicar un look y que persiste en NBT. Un deslizador de velocidad en la consola controla el BPM de Parpadeo/Respiración/Arcoíris/Persecución.
- **Cabeza móvil** (`cabeza_movil`): como el foco, con un selector de patrón (Fijo/Barrido/Círculo) en su propia pantalla. Simplificación deliberada: el modelo del bloque no tiene partes que giren; lo que se mueve es la dirección del haz (dos rotaciones extra sobre el haz, deterministas por `gameTime`, igual que los demás efectos). Apuntar a una coordenada fija concreta queda fuera de alcance.
- **Truss** (`truss`): bloque metálico simple para la estructura. Simplificación deliberada: todavía no se conecta visualmente a los trusses vecinos (sin texturas conectadas tipo rejas de hierro) — es trabajo de arte, no de lógica, y queda para una pasada de pulido posterior. Colgar un foco debajo ya funciona solo: basta con orientarlo hacia abajo contra la cara inferior de cualquier bloque sólido, truss incluido.
- Recetas, loot tables, texturas placeholder y traducciones de los 4 bloques nuevos.
- Verificado en esta sesión: `./gradlew build` y `./gradlew runServer` sin errores.

## Fase 2 — Grupos y consola

- `GroupManager` (`SavedData`, persistente por dimensión): índice de qué focos pertenecen a qué grupo. No guarda el "look" del grupo, solo las posiciones — cada foco sigue siendo dueño de su propio `LightState`.
- El foco se registra/da de baja del grupo automáticamente al cambiar el nombre de grupo desde su pantalla (Fase 1), y al romperse (`FocoBlock.onRemove`).
- Bloque **Consola de luces** (4 direcciones horizontales) con su propia pantalla: lista los grupos existentes (botones), paleta de color, intensidad, selector de efecto (Fijo/Parpadeo/Respiración) y un botón "Aplicar a grupo" que cambia de golpe el look de todos los focos de ese grupo.
- Acciones rápidas de la consola: **Blackout** (alterna apagado de todos los focos conocidos, sin tocar su configuración ni la redstone) y **Estrobo general** (pone parpadeo en todos, conservando su color/intensidad).
- Efectos básicos (`light/Effects.java`): parpadeo y respiración, deterministas por `gameTime` — el cliente los anima sin recibir un paquete por tick; no afectan al nivel de luz real del bloque (solo al haz), para no forzar actualizaciones de blockstate innecesarias.
- Tres paquetes de red nuevos (`ApplyGroupLookPacket`, `BlackoutPacket`, `StrobeAllPacket`), todos resueltos en el hilo principal del servidor.
- Receta, loot table, texturas placeholder y traducciones de la consola.
- Verificado en esta sesión: `./gradlew build` y `./gradlew runServer` sin errores (bloques/items/block entities/menús/red cargan limpio).

**Simplificación deliberada:** "Blackout" necesitaba apagar un foco aunque tenga redstone (un apagón de verdad no depende del cableado), así que se añadió un override `blackedOut` en `FocoBlockEntity` independiente de la redstone, en vez de forzar la señal. Se persiste en NBT para que sobreviva a un reinicio del servidor.

**Pendiente de probar con cliente real** (no se pudo aquí, sin pantalla): el criterio de aceptación completo de la Fase 2 — diez focos en un mismo grupo, cambiando de color a la vez desde la consola, en servidor dedicado con dos jugadores. Ver `docs/TESTING.md`.

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
