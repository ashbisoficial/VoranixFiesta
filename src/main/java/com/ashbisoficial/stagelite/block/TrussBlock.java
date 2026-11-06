package com.ashbisoficial.stagelite.block;

import net.minecraft.world.level.block.Block;

/**
 * Truss (SPEC.md 4.2): estructura metalica para colgar focos debajo (eso ya funciona solo:
 * cualquier foco orientado hacia abajo contra la cara inferior de un truss se "cuelga").
 * Simplificacion deliberada de la Fase 3: todavia no se conecta visualmente a los trusses
 * vecinos (sin texturas conectadas, como las rejas de hierro de vanilla) - es un bloque
 * metalico simple por ahora; las texturas conectadas son trabajo de arte, no de logica, y
 * quedan para una pasada de pulido posterior.
 */
public class TrussBlock extends Block {

    public TrussBlock(Properties properties) {
        super(properties);
    }
}
