# SPEC: Mod de iluminación de escenario simplificada (Forge 1.20.1)

> Documento para Claude Code. Léelo completo antes de escribir código. Trabaja por fases, haz commits pequeños y verifica que el proyecto compila y arranca después de cada fase.

## 1. Objetivo

Crear un mod para **Minecraft Forge 1.20.1** que permita montar escenarios, conciertos y shows de luces **sin necesidad de saber nada de iluminación profesional**. Está inspirado en la idea del mod *Theatrical* (equipos de eventos en vivo), pero con un enfoque radicalmente más simple:

- **Sin DMX, sin direcciones de canal, sin software externo** en la versión base.
- Todo se maneja dentro del juego con **bloques, pantallas con botones y redstone**.
- Pensado para un **servidor de supervivencia y roleplay**, para un proyecto propio.

Público objetivo: jugadores que nunca han tocado una mesa de luces.

## 2. Restricciones y reglas

1. **Código y assets 100 % originales.** No copies código, modelos ni texturas de Theatrical ni de ningún otro mod. Si necesitas inspiración, usa solo su descripción pública. Las texturas pueden ser placeholders simples (pixel art propio) que luego se mejoran.
2. **Stack:** Minecraft 1.20.1, Forge 47.x (usa la versión recomendada más reciente de 1.20.1), Java 17, ForgeGradle, mappings oficiales de Mojang (o Parchment si lo prefieres, indícalo).
3. **Solo Forge.** No añadas Fabric/NeoForge ni Architectury, salvo que se pida después.
4. **Sin dependencias externas obligatorias.** JEI como dependencia opcional (compatibilidad de recetas) está bien, pero el mod debe arrancar sin él.
5. **Servidor primero.** Toda la lógica de estado va en el servidor; el cliente solo renderiza. Sincroniza con `SimpleChannel` o con los paquetes de actualización de `BlockEntity`.
6. **Rendimiento:** muchos focos activos a la vez no deben hundir los FPS ni el TPS. Aplica las reglas de la sección 8.
7. **Idiomas:** incluye `en_us.json` y `es_es.json` desde el principio. Todo texto visible va por claves de traducción.
8. **Antes de decidir nombres, pregunta o usa estos valores por defecto** (cámbialos en un solo sitio para que sea fácil renombrar):
   - Nombre visible: `StageLite`
   - Mod ID: `stagelite`
   - Paquete base: `com.stagelite` (ajústalo al usuario/organización del repo)
   - Licencia: pregunta al equipo; si no hay respuesta, deja `All Rights Reserved` indicado en `mods.toml` y avísalo.

## 3. Filosofía de diseño: "cero jerga"

En lugar de conceptos técnicos, usa conceptos que cualquiera entiende:

| Concepto profesional | Concepto en este mod |
|---|---|
| Dirección/canal DMX | **Grupo** (nombre o color de etiqueta, ej. "Escenario", "Público") |
| Mesa de luces / consola | **Consola de luces** con pantalla y botones grandes |
| Cue / look | **Escena** (un ajuste guardado de color, intensidad y efecto) |
| Cue list / show | **Show** (una lista de escenas con tiempos) |
| Chase, strobe, etc. | **Efectos** con nombres claros y vista previa en la consola |

Reglas de UX:
- Cada pantalla debe tener **textos de ayuda (tooltips)** que expliquen qué hace cada botón en lenguaje llano.
- Valores razonables **por defecto**: un foco recién colocado debe funcionar y verse bien sin configurar nada.
- Incluye un **libro-guía en el juego** (objeto) que explique cómo montar un escenario básico en 5 pasos. Puede ser un `Written Book` entregado con un comando, o una pantalla propia.

## 4. Contenido del mod (visión completa)

### 4.1 Focos (fixtures)

1. **Foco simple (Par/Spot):** bloque que emite un haz de color. Se puede colocar en las 6 direcciones. Propiedades: color, intensidad (0–100 %), ángulo del haz, encendido/apagado.
2. **Panel LED / Wash:** luz suave de área, sin haz marcado, color configurable.
3. **Cabeza móvil (Moving Head):** como el foco, pero con **giro (pan) y inclinación (tilt)** animados y configurables. Puede apuntar a una posición fija o seguir un patrón (ej. barrido, círculo, 8).
4. **Estroboscopio:** parpadeo con frecuencia configurable.
5. **Láser decorativo (opcional, fase tardía):** haces finos con patrones simples.
6. **Bola de espejos (opcional, fase tardía):** refleja puntos de luz que giran.

### 4.2 Estructura

- **Truss (estructura metálica):** bloques conectables para colgar focos, con modelo que se conecta a vecinos. Los focos se pueden colocar "colgados" debajo.
- **Plataforma/escenario:** bloques simples de tarima (opcional, pueden usarse bloques vanilla).

### 4.3 Control

1. **Control por redstone (obligatorio en la versión base):**
   - Cada foco reacciona a la señal de redstone: encendido/apagado y, si se configura, intensidad según la potencia (1–15).
   - Un **Receptor de grupo** o la propia configuración del foco permite asignarlo a un **grupo**.
2. **Consola de luces (bloque con GUI):**
   - Lista los grupos existentes en el área (por radio configurable) o los grupos que el jugador creó.
   - Por grupo: selector de **color** (paleta + RGB), **intensidad**, **efecto**, **velocidad**.
   - Botones **Guardar escena** y **Cargar escena**.
   - Botón **Blackout** (todo apagado) y **Estrobo general** como acciones rápidas.
   - Salida de **redstone** configurable: la consola puede emitir señal cuando se activa una escena (para integrarla con puertas, pirotecnia, etc.).
3. **Secuenciador de shows (fase posterior):** línea de tiempo simple donde se encadenan escenas con duración y transición (corte o fundido). Reproducción con botón o redstone, con opción de bucle.
4. **Efectos integrados:** fijo, parpadeo, respiración (fade in/out), arcoíris, persecución (chase) entre focos de un grupo, sincronizado a un **BPM** configurable.

### 4.4 Funciones opcionales / futuras

- **Sincronización con música** (discos, jukebox cercano): fase muy tardía, solo si el resto está estable.
- **Pirotecnia/efectos de humo:** fase muy tardía.
- **Puente DMX/Art-Net para usuarios avanzados:** al final y desactivado por defecto. No es prioridad ni se necesita para el resto.

### 4.5 Supervivencia y roleplay

- **Recetas de crafteo** equilibradas y que usen materiales vanilla (hierro, cristal, redstone, polvo de glowstone, etc.). Deben ser configurables o fáciles de cambiar por datapack.
- **Permisos y bloqueo:** el dueño que coloca la consola puede bloquear su uso (solo él o jugadores de una lista). Los OPs pueden saltarse el bloqueo. Evita que cualquiera pueda cambiar luces ajenas.
- **Comandos** (`/stagelite ...`) para administradores: listar grupos, apagar todo en un radio, resetear un foco, recargar configuración.
- **Configuración** (`stagelite-common.toml` y `-client.toml`): límite de focos activos por chunk, distancia de renderizado del haz, calidad del haz, activar/desactivar efectos pesados.

## 5. Arquitectura sugerida

```
src/main/java/<paquete>/
  StageLite.java                 // @Mod, registro de eventos
  registry/                      // DeferredRegister: bloques, items, block entities, menús, sonidos, tabs
  block/                         // Bloques (Foco, MovingHead, Truss, Consola, ...)
  blockentity/                   // BlockEntities con estado de luz y sincronización
  light/
    LightState.java              // color, intensidad, ángulo, efecto, velocidad (serializable a NBT)
    Group.java / GroupManager.java   // grupos por nivel (SavedData)
    Effect.java + implementaciones   // interfaz de efectos, calculan estado en función del tiempo
    Scene.java / Show.java       // escenas y shows (serializables)
  menu/ + screen/                // Menús y pantallas de la consola
  network/                       // Paquetes cliente<->servidor
  client/render/                 // Renderers de haz y de movimiento
  config/                        // ForgeConfigSpec
  command/                       // Comandos
src/main/resources/
  assets/<modid>/                // blockstates, models, textures, lang, shaders (si hacen falta)
  data/<modid>/                  // recetas, loot tables, tags, advancements
```

Principios:
- **`LightState` es la única fuente de verdad** para un foco. Los efectos producen un `LightState` calculado en cada tick de servidor (o por cliente si el efecto es puramente visual y determinista a partir de `gameTime`, lo que reduce tráfico de red).
- **Efectos deterministas por tiempo:** calcula animaciones a partir de `level.getGameTime()` + parámetros, para que el cliente pueda renderizarlas sin recibir un paquete cada tick.
- **Grupos persistentes** con `SavedData` por dimensión.
- **Un solo paquete de sincronización por cambio**, nunca por tick.
- Código limpio, comentado en los puntos no obvios, con nombres claros.

## 6. Renderizado del haz de luz (la parte difícil)

Plan por etapas, cada una funcional por sí sola:

1. **Etapa A, solo iluminación:** el foco emite luz de bloque (propiedad `LIGHT_LEVEL` o bloque auxiliar de luz) y cambia de textura/emisivo según el color. Sin haz.
2. **Etapa B, haz básico:** `BlockEntityRenderer` que dibuja un **cono o prisma translúcido** desde el foco hacia la dirección, con color y alfa, usando un `RenderType` translúcido/aditivo propio (tómalo de ejemplo de cómo vanilla dibuja el haz del faro/beacon). Longitud fija configurable.
3. **Etapa C, haz con longitud real:** hace *raycast* (en cliente, con caché y recalculando solo cuando cambie algo) para cortar el haz contra bloques sólidos.
4. **Etapa D, calidad:** desvanecido en los bordes, ligera variación en el tiempo, y opción de calidad baja/alta en la config cliente.

Notas:
- El haz **no debe iluminar el mundo dinámicamente** (Minecraft no tiene luces dinámicas): es un efecto visual. La iluminación real viene de bloques con nivel de luz.
- Asegúrate de que el renderer tenga `getRenderBoundingBox` adecuado para que el haz no desaparezca al apartar la vista del bloque.
- Prueba con y sin shaders (Oculus/Embeddium) y documenta lo que ocurra.

## 7. Plan por fases (entrégalas en este orden)

**Fase 0: Proyecto base**
- Crear el proyecto Forge 1.20.1 con Gradle, `mods.toml`, `pack.mcmeta`, pestaña creativa propia, `.gitignore`, README básico.
- Comprobar que `./gradlew build` genera el `.jar` y que `./gradlew runClient` arranca.
- Configurar CI en GitHub Actions que compile en cada push/PR (JDK 17).
- *Criterio de aceptación:* el mod aparece en la lista de mods del juego con su pestaña creativa vacía.

**Fase 1: Foco simple con redstone**
- Bloque Foco con orientación, color, intensidad y encendido/apagado.
- Reacción a redstone (on/off y por potencia).
- Pantalla de configuración al hacer clic derecho (color, intensidad, grupo).
- Etapa A del renderizado (solo iluminación) y luego Etapa B (haz básico).
- Receta de crafteo.
- *Criterio:* colocar un foco, ponerle redstone y verlo encenderse con haz del color elegido.

**Fase 2: Grupos y consola**
- `GroupManager` (SavedData), asignación de focos a grupos.
- Bloque Consola con GUI: color, intensidad, efectos básicos (fijo, parpadeo, respiración), blackout.
- Sincronización cliente-servidor correcta y probada en servidor dedicado.
- *Criterio:* cambiar el color de un grupo de 10 focos desde la consola, en servidor dedicado, con dos jugadores.

**Fase 3: Más focos y efectos**
- Panel LED, estroboscopio, cabeza móvil (pan/tilt con animación suave y patrones).
- Efectos arcoíris y persecución con BPM.
- Truss conectable.

**Fase 4: Escenas y shows**
- Guardar/cargar escenas; secuenciador de shows con transiciones; salida de redstone.

**Fase 5: Roleplay y administración**
- Bloqueo/permisos, comandos, configuración completa, libro-guía en el juego.

**Fase 6: Pulido**
- Optimización (ver sección 8), mejora de texturas/modelos, tests adicionales, README completo con capturas, empaquetado de releases en GitHub.

**Fase 7 (opcional):** láser, bola de espejos, sincronización con música, puente DMX/Art-Net avanzado.

## 8. Rendimiento

- Renderiza solo los haces dentro de una **distancia máxima configurable** (por defecto 64 bloques) y descarta los que están fuera de la vista.
- Cachea el resultado del raycast del haz; recalcula solo cuando el foco o los bloques cercanos cambien.
- Evita asignar objetos nuevos en el renderer cada frame (reutiliza `Vector3f`, `PoseStack`, etc.).
- En servidor, **no hagas ticks innecesarios**: un foco apagado y sin efecto no debe tickear.
- Límite configurable de focos activos por chunk y por jugador.

## 9. Calidad, pruebas y flujo de trabajo en Git

- Una rama por fase (`feature/fase-1-foco`), PRs pequeños hacia `main`, con descripción de qué cambia y cómo probarlo.
- Mensajes de commit claros, en español o inglés pero consistentes.
- Tras cada fase: `./gradlew build` sin errores ni warnings graves, y prueba manual en `runClient` **y** en `runServer` con un cliente conectado.
- Añade **tests unitarios** (JUnit) para lógica pura: efectos, interpolación de colores, serialización de escenas/shows. Para lo que dependa del juego, deja una lista de pruebas manuales en `docs/TESTING.md`.
- Mantén un `CHANGELOG.md` y un `docs/` con la arquitectura y las decisiones importantes.
- **Nunca** subas claves, tokens ni archivos de `run/` ni de compilaciones locales.

## 10. Cómo quiero que trabajes conmigo

1. Empieza leyendo este documento y el estado actual del repositorio. Si ya hay código, **respétalo y no lo reescribas** sin avisar.
2. Antes de la Fase 0, confirma conmigo el **nombre, mod ID, paquete y licencia** (sección 2.8). Si no hay respuesta, usa los valores por defecto y déjalo anotado.
3. Implementa **una fase cada vez** y no pases a la siguiente sin cumplir su criterio de aceptación.
4. Si una decisión técnica tiene alternativas con compromisos reales (por ejemplo, cómo renderizar el haz), **explícalas en pocas líneas y recomienda una** antes de implementar.
5. Si algo del plan no es viable en Forge 1.20.1 o no puedes verificarlo, **dímelo claramente** en lugar de inventar APIs. Comprueba las clases y métodos reales en el código fuente de Forge/Minecraft del proyecto.
6. Al terminar cada fase, resume: qué se hizo, cómo probarlo, qué queda pendiente y qué riesgos ves.

## 11. Fuera de alcance (por ahora)

- Soporte para otras versiones de Minecraft o para Fabric/NeoForge.
- Control externo por DMX/Art-Net (solo como fase opcional final).
- Audio/altavoces propios, rigging avanzado y cualquier cosa no listada arriba.
