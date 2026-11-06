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
