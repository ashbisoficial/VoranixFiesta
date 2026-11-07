package com.ashbisoficial.stagelite.light;

import net.minecraft.nbt.CompoundTag;

/**
 * Una escena guardada (SPEC.md 4.3.2): el "look" de un grupo en un momento dado,
 * para poder volver a cargarlo despues con un boton. No es el grupo en si, solo
 * recuerda a que grupo se aplicaba cuando se guardo.
 */
public class Scene {

    private String group;
    private int color;
    private int intensity;
    private String effect;
    private float speed;

    public Scene() {
        this("", LightState.DEFAULT_COLOR, LightState.DEFAULT_INTENSITY, Effects.FIJO, 1.0f);
    }

    public Scene(String group, int color, int intensity, String effect, float speed) {
        this.group = group;
        this.color = color;
        this.intensity = intensity;
        this.effect = effect;
        this.speed = speed;
    }

    public String getGroup() {
        return group;
    }

    public int getColor() {
        return color;
    }

    public int getIntensity() {
        return intensity;
    }

    public String getEffect() {
        return effect;
    }

    public float getSpeed() {
        return speed;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putString("Group", group);
        tag.putInt("Color", color);
        tag.putInt("Intensity", intensity);
        tag.putString("Effect", effect);
        tag.putFloat("Speed", speed);
        return tag;
    }

    public static Scene load(CompoundTag tag) {
        return new Scene(
                tag.getString("Group"),
                tag.getInt("Color"),
                tag.getInt("Intensity"),
                tag.contains("Effect") ? tag.getString("Effect") : Effects.FIJO,
                tag.contains("Speed") ? tag.getFloat("Speed") : 1.0f
        );
    }
}
