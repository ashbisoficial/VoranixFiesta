package com.ashbisoficial.stagelite.light;

/**
 * Efectos basicos (Fase 2): solo calculan un multiplicador de intensidad (0-1) a partir del
 * tiempo de juego, para que el cliente los pueda animar sin recibir un paquete cada tick
 * (ver SPEC.md seccion 5, "Efectos deterministas por tiempo").
 */
public final class Effects {

    public static final String FIJO = "fijo";
    public static final String PARPADEO = "parpadeo";
    public static final String RESPIRACION = "respiracion";

    private Effects() {
    }

    public static float multiplier(String effect, float speed, long gameTime, float partialTick) {
        float t = (gameTime + partialTick) * Math.max(0.05f, speed);
        return switch (effect) {
            case PARPADEO -> (((long) (t / 5.0f)) % 2 == 0) ? 1.0f : 0.0f;
            case RESPIRACION -> (float) (0.5 + 0.5 * Math.sin(t * 0.1));
            default -> 1.0f;
        };
    }
}
