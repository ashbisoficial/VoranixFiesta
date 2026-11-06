package com.ashbisoficial.stagelite.light;

/**
 * Efectos (Fase 2 basicos + Fase 3 de grupo): solo calculan, a partir del tiempo de juego,
 * un multiplicador de intensidad (0-1) y, para el arcoiris, un color propio. Deterministas
 * por gameTime (ver SPEC.md seccion 5): el cliente los anima sin recibir un paquete cada tick.
 *
 * Arcoiris y persecucion necesitan saber la posicion del foco dentro de su grupo
 * (groupIndex/groupSize) para desfasarse unos de otros; el resto de efectos los ignoran.
 */
public final class Effects {

    public static final String FIJO = "fijo";
    public static final String PARPADEO = "parpadeo";
    public static final String RESPIRACION = "respiracion";
    public static final String ARCOIRIS = "arcoiris";
    public static final String PERSECUCION = "persecucion";

    private Effects() {
    }

    public static float multiplier(String effect, float speed, long gameTime, float partialTick, int groupIndex, int groupSize) {
        float t = (gameTime + partialTick) * Math.max(0.05f, speed);
        return switch (effect) {
            case PARPADEO -> (((long) (t / 5.0f)) % 2 == 0) ? 1.0f : 0.0f;
            case RESPIRACION -> (float) (0.5 + 0.5 * Math.sin(t * 0.1));
            case PERSECUCION -> {
                int size = Math.max(1, groupSize);
                long activeSlot = ((long) (t / 8.0f)) % size;
                yield activeSlot == groupIndex ? 1.0f : 0.08f;
            }
            default -> 1.0f; // fijo, arcoiris: la intensidad no varia, solo (en arcoiris) el color
        };
    }

    /** Para "arcoiris": sustituye el color guardado por uno que rota con el tiempo, desfasado por grupo. */
    public static int colorOverride(String effect, int baseColor, long gameTime, float partialTick, float speed, int groupIndex, int groupSize) {
        if (!ARCOIRIS.equals(effect)) {
            return baseColor;
        }
        float t = (gameTime + partialTick) * Math.max(0.02f, speed) * 0.01f;
        float offset = groupSize > 0 ? (float) groupIndex / groupSize : 0f;
        float hue = (t + offset) % 1.0f;
        return hsbToRgb(hue, 1.0f, 1.0f);
    }

    /** Conversion HSB -> RGB manual, para no depender de java.awt.Color en codigo de cliente. */
    private static int hsbToRgb(float hue, float saturation, float brightness) {
        float h = (float) (hue - Math.floor(hue)) * 6.0f;
        float f = h - (float) Math.floor(h);
        float p = brightness * (1.0f - saturation);
        float q = brightness * (1.0f - saturation * f);
        float t = brightness * (1.0f - saturation * (1.0f - f));
        float r, g, b;
        switch ((int) h) {
            case 0 -> { r = brightness; g = t; b = p; }
            case 1 -> { r = q; g = brightness; b = p; }
            case 2 -> { r = p; g = brightness; b = t; }
            case 3 -> { r = p; g = q; b = brightness; }
            case 4 -> { r = t; g = p; b = brightness; }
            default -> { r = brightness; g = p; b = q; }
        }
        int ri = Math.round(r * 255.0f);
        int gi = Math.round(g * 255.0f);
        int bi = Math.round(b * 255.0f);
        return (ri << 16) | (gi << 8) | bi;
    }
}
