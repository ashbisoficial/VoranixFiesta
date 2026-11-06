package com.ashbisoficial.stagelite.light;

import net.minecraft.nbt.CompoundTag;

/**
 * Fuente de verdad del "look" configurado de un foco: lo que el jugador elige en la pantalla.
 * El estado electrico (si hay redstone y con que potencia) vive aparte, en el BlockEntity,
 * porque no es algo que el jugador configure sino algo que el mundo le impone.
 */
public class LightState {

    public static final int DEFAULT_COLOR = 0xFFF4C2; // blanco calido
    public static final int DEFAULT_INTENSITY = 100;
    public static final int DEFAULT_ANGLE = 30;

    private int color = DEFAULT_COLOR;
    private int intensity = DEFAULT_INTENSITY; // 0-100
    private int angle = DEFAULT_ANGLE; // grados, reservado para fases posteriores
    private String effect = "fijo"; // reservado para la Fase 2+
    private float speed = 1.0f; // reservado para la Fase 2+ (BPM/velocidad de efecto)
    private String group = "";

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color & 0xFFFFFF;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = Math.max(0, Math.min(100, intensity));
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group == null ? "" : group;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putInt("Color", color);
        tag.putInt("Intensity", intensity);
        tag.putInt("Angle", angle);
        tag.putString("Effect", effect);
        tag.putFloat("Speed", speed);
        tag.putString("Group", group);
        return tag;
    }

    public void load(CompoundTag tag) {
        color = tag.contains("Color") ? tag.getInt("Color") & 0xFFFFFF : DEFAULT_COLOR;
        intensity = tag.contains("Intensity") ? Math.max(0, Math.min(100, tag.getInt("Intensity"))) : DEFAULT_INTENSITY;
        angle = tag.contains("Angle") ? tag.getInt("Angle") : DEFAULT_ANGLE;
        effect = tag.contains("Effect") ? tag.getString("Effect") : "fijo";
        speed = tag.contains("Speed") ? tag.getFloat("Speed") : 1.0f;
        group = tag.contains("Group") ? tag.getString("Group") : "";
    }
}
