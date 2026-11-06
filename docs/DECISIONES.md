# Decisiones del proyecto

Este documento registra las decisiones tomadas siguiendo `SPEC.md` sección 2.8 y 10.2, cuando no hubo respuesta explícita del equipo antes de empezar la Fase 0.

## Identidad del mod

| Campo | Valor | Origen |
|---|---|---|
| Nombre visible | `StageLite` | Valor por defecto de `SPEC.md` §2.8 |
| Mod ID | `stagelite` | Valor por defecto de `SPEC.md` §2.8 |
| Paquete base | `com.ashbisoficial.stagelite` | `com.stagelite` ajustado al propietario del repositorio GitHub (`ashbisoficial`), como pide `SPEC.md` §2.8 |
| Licencia | `All Rights Reserved` (indicado así en `mods.toml`) | `SPEC.md` §2.8: "pregunta al equipo; si no hay respuesta, deja `All Rights Reserved`... y avísalo" |

**Pendiente:** confirmar con el equipo si `All Rights Reserved` es la licencia final, o si prefieren una licencia abierta (MIT, GPL, etc.). Es fácil de cambiar más adelante: solo afecta a `mod_license` en `gradle.properties` y al `LICENSE` del repositorio (todavía no se ha añadido ningún archivo `LICENSE`).

## Plataforma y stack

- Minecraft 1.20.1, Forge 47.x, Java 17 (`SPEC.md` §2.2, obligatorio, no negociado).
- Mappings: **oficiales de Mojang** (`mapping_channel=official`), por ser la opción por defecto más simple y sin dependencias adicionales. No se usó Parchment.
- Versión concreta de Forge fijada en `gradle.properties` (`forge_version`): se verificó/ajustó durante la Fase 0 al compilar por primera vez, consultando los metadatos reales de Forge en vez de inventar un número.

## Entorno de build en esta sesión

El contenedor de este agente solo permite por defecto gestores de paquetes comunes (Maven Central, apt, etc.). Los repositorios de Mojang y de Forge (`maven.minecraftforge.net`, `libraries.minecraft.net`, `piston-meta.mojang.com`, etc.) no estaban permitidos por defecto y se solicitó explícitamente su acceso, ya que `build.gradle`/`settings.gradle` los necesitan para compilar un mod de Forge. Ver el resumen de la Fase 0 para el detalle de qué se pudo verificar.
