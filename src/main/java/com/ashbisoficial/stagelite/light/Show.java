package com.ashbisoficial.stagelite.light;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Un show (SPEC.md 4.3.3): una lista de pasos que encadenan escenas con duracion y
 * transicion. Simplificacion deliberada de la Fase 4: siempre tiene exactamente
 * STEP_COUNT pasos (algunos pueden dejarse "vacios", ver ShowStep.isEmpty), para que el
 * editor en la consola sea una lista fija de filas en vez de un gestor dinamico.
 */
public class Show {

    public static final int STEP_COUNT = 4;

    public static final String TRANSITION_CORTE = "corte";
    public static final String TRANSITION_FUNDIDO = "fundido";

    private final List<ShowStep> steps = new ArrayList<>();
    private boolean loop;

    public Show() {
        this(false);
    }

    public Show(boolean loop) {
        this.loop = loop;
        for (int i = 0; i < STEP_COUNT; i++) {
            steps.add(new ShowStep("", 60, TRANSITION_CORTE));
        }
    }

    public List<ShowStep> getSteps() {
        return steps;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("Loop", loop);
        ListTag list = new ListTag();
        for (ShowStep step : steps) {
            list.add(step.save(new CompoundTag()));
        }
        tag.put("Steps", list);
        return tag;
    }

    public static Show load(CompoundTag tag) {
        Show show = new Show(tag.getBoolean("Loop"));
        ListTag list = tag.getList("Steps", Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(STEP_COUNT, list.size()); i++) {
            show.steps.set(i, ShowStep.load(list.getCompound(i)));
        }
        return show;
    }

    public static class ShowStep {
        private String sceneName;
        private int durationTicks;
        private String transition;

        public ShowStep(String sceneName, int durationTicks, String transition) {
            this.sceneName = sceneName == null ? "" : sceneName;
            this.durationTicks = Math.max(1, durationTicks);
            this.transition = transition;
        }

        public boolean isEmpty() {
            return sceneName.isBlank();
        }

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName == null ? "" : sceneName;
        }

        public int getDurationTicks() {
            return durationTicks;
        }

        public void setDurationTicks(int durationTicks) {
            this.durationTicks = Math.max(1, durationTicks);
        }

        public String getTransition() {
            return transition;
        }

        public void setTransition(String transition) {
            this.transition = transition;
        }

        public CompoundTag save(CompoundTag tag) {
            tag.putString("Scene", sceneName);
            tag.putInt("Duration", durationTicks);
            tag.putString("Transition", transition);
            return tag;
        }

        public static ShowStep load(CompoundTag tag) {
            return new ShowStep(
                    tag.getString("Scene"),
                    tag.contains("Duration") ? tag.getInt("Duration") : 60,
                    tag.contains("Transition") ? tag.getString("Transition") : TRANSITION_CORTE
            );
        }
    }
}
