# Pruebas manuales

Lista de comprobaciones manuales que no se pueden automatizar fácilmente con JUnit (dependen del juego). Se amplía en cada fase.

## Fase 0 — Proyecto base

- [x] `./gradlew build` termina sin errores. Verificado en el entorno de desarrollo (sesión en la nube, sin pantalla): `BUILD SUCCESSFUL in 5m 20s` con Forge 1.20.1-47.4.28.
- [ ] `./gradlew runClient` arranca el juego y StageLite aparece en la lista de mods. **No verificado aquí**: este entorno no tiene pantalla, así que no puede arrancar el cliente de Minecraft (necesita OpenGL/una ventana). Pendiente de que alguien lo pruebe en su propio PC.
- [ ] La pestaña creativa de StageLite aparece en el inventario creativo (vacía por ahora). Depende del punto anterior (necesita el cliente).
- [x] `./gradlew runServer` arranca un servidor dedicado sin errores relacionados con el mod. Verificado: el servidor llega a `Done (29.012s)! For help, type "help"` y el log confirma que FML crea el contenedor del mod (`Creating FMLModContainer instance for com.ashbisoficial.stagelite.StageLite`) sin excepciones.

## Fase 1 — Foco simple con redstone

- [x] `./gradlew build` sin errores (solo warnings de deprecación esperados, ver CHANGELOG).
- [x] `./gradlew runServer` carga el bloque, el item, la receta y la loot table sin errores ni excepciones. (El log muestra `RecipeManager: Loaded 7 recipes` en vez de las ~300 recetas vanilla habituales; es un detalle ya presente en la Fase 0 **antes** de añadir ningún contenido propio, así que es una limitación del entorno de pruebas en la nube, no un fallo introducido por `foco.json`. No se vio ningún error de parseo de la receta ni de la loot table.)
- [ ] **Pendiente de probar con cliente real (necesita pantalla):**
  - [ ] Craftear un foco con hierro + cristal + redstone + polvo de glowstone (receta en forma de "I" con la G/R/D en el centro de cada fila).
  - [ ] Colocarlo contra una pared/suelo/techo en las 6 orientaciones y comprobar que el lente mira hacia donde se colocó.
  - [ ] Sin redstone: debe estar apagado (sin luz, sin haz).
  - [ ] Conectarle una palanca/bloque de redstone: debe encenderse (lente brillante, nivel de luz, haz de color visible).
  - [ ] Conectarle un comparador/repetidor con distintas potencias (1-15): el haz y la luz deben verse más tenues con menos potencia.
  - [ ] Clic derecho: debe abrir la pantalla (10 colores, deslizador de intensidad, nombre de grupo), con tooltips al pasar el ratón.
  - [ ] Cambiar el color/intensidad/grupo y pulsar "Guardar": el cambio debe verse en el bloque sin tener que volver a entrar al chunk.
  - [ ] Romper el foco: debe soltar el item (loot table).
  - [ ] Probar en servidor dedicado con dos jugadores (uno coloca/configura, el otro debe ver el mismo resultado).

## Fase 2 — Grupos y consola

- [x] `./gradlew build` sin errores.
- [x] `./gradlew runServer` carga el bloque/item/block entity/menú de la consola y los paquetes de red sin errores.
- [ ] **Pendiente de probar con cliente real (necesita pantalla) — criterio de aceptación de la Fase 2:**
  - [ ] Colocar 10 focos, darles redstone y ponerles a todos el mismo nombre de grupo (p. ej. "Escenario") desde su pantalla individual.
  - [ ] Craftear y colocar una consola (hierro + redstone + antorcha de redstone).
  - [ ] Abrir la consola: el grupo "Escenario" debe aparecer como botón.
  - [ ] Elegir el grupo, un color y una intensidad, pulsar "Aplicar a grupo": los 10 focos deben cambiar de color a la vez.
  - [ ] Probar los tres efectos (Fijo/Parpadeo/Respiración) aplicados a un grupo.
  - [ ] Pulsar "Blackout": todos los focos conocidos se apagan (aunque tengan redstone); pulsarlo otra vez los vuelve a encender con su configuración previa.
  - [ ] Pulsar "Estrobo general": todos los focos conocidos parpadean, conservando su color/intensidad.
  - [ ] **En servidor dedicado con dos jugadores:** un jugador aplica un cambio desde la consola; el otro, ya colocado mirando los focos, debe ver el cambio sin reconectarse.
  - [ ] Romper un foco del grupo y comprobar (con otro comando o reabriendo la consola) que ya no aparece afectado por "Aplicar a grupo" (se dio de baja correctamente).

## Fase 3 — Más focos y efectos

- [x] `./gradlew build` sin errores.
- [x] `./gradlew runServer` carga los 4 bloques nuevos (panel, estrobo, cabeza_movil, truss), sus items, block entities, menús y el paquete de red de la cabeza móvil, sin errores.
- [ ] **Pendiente de probar con cliente real (necesita pantalla):**
  - [ ] Panel LED: craftear, colocar, dar redstone y comprobar que brilla como un resplandor de area (no como un cono como el foco).
  - [ ] Estroboscopio: craftear, colocar, dar redstone y comprobar que parpadea solo, sin tener que elegir el efecto manualmente.
  - [ ] Cabeza móvil: craftear, colocar, dar redstone, abrir su pantalla y probar los 3 patrones (Fijo = quieto, Barrido = va de lado a lado, Círculo = combinado) — el haz debe moverse solo sin que el bloque cambie de forma.
  - [ ] Truss: craftear (da 4), colocar varios en línea (no se conectan visualmente todavía, es la limitación conocida) y colgar un foco apuntando hacia abajo desde su cara inferior.
  - [ ] Agrupar 5-10 focos de distinto tipo (foco + panel + estrobo + cabeza móvil) en un mismo grupo y, desde la consola, probar:
    - [ ] Efecto Arcoíris: cada uno debe mostrar un color distinto que rota con el tiempo.
    - [ ] Efecto Persecución: deben encenderse uno a uno, en orden, en bucle.
    - [ ] El deslizador de Velocidad de la consola debe acelerar/ralentizar Parpadeo, Respiración, Arcoíris y Persecución.
  - [ ] Comprobar que un foco roto y vuelto a colocar en el mismo grupo recibe un `groupIndex` nuevo correcto (no debe quedarse con el del foco anterior).
