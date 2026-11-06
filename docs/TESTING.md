# Pruebas manuales

Lista de comprobaciones manuales que no se pueden automatizar fácilmente con JUnit (dependen del juego). Se amplía en cada fase.

## Fase 0 — Proyecto base

- [x] `./gradlew build` termina sin errores. Verificado en el entorno de desarrollo (sesión en la nube, sin pantalla): `BUILD SUCCESSFUL in 5m 20s` con Forge 1.20.1-47.4.28.
- [ ] `./gradlew runClient` arranca el juego y StageLite aparece en la lista de mods. **No verificado aquí**: este entorno no tiene pantalla, así que no puede arrancar el cliente de Minecraft (necesita OpenGL/una ventana). Pendiente de que alguien lo pruebe en su propio PC.
- [ ] La pestaña creativa de StageLite aparece en el inventario creativo (vacía por ahora). Depende del punto anterior (necesita el cliente).
- [x] `./gradlew runServer` arranca un servidor dedicado sin errores relacionados con el mod. Verificado: el servidor llega a `Done (29.012s)! For help, type "help"` y el log confirma que FML crea el contenedor del mod (`Creating FMLModContainer instance for com.ashbisoficial.stagelite.StageLite`) sin excepciones.
