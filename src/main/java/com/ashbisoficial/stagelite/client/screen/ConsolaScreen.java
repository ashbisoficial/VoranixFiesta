package com.ashbisoficial.stagelite.client.screen;

import com.ashbisoficial.stagelite.light.Effects;
import com.ashbisoficial.stagelite.light.LightState;
import com.ashbisoficial.stagelite.light.Show;
import com.ashbisoficial.stagelite.menu.ConsolaMenu;
import com.ashbisoficial.stagelite.network.StageLiteNetwork;
import com.ashbisoficial.stagelite.network.packet.ApplyGroupLookPacket;
import com.ashbisoficial.stagelite.network.packet.BlackoutPacket;
import com.ashbisoficial.stagelite.network.packet.LoadScenePacket;
import com.ashbisoficial.stagelite.network.packet.PlayShowPacket;
import com.ashbisoficial.stagelite.network.packet.SaveScenePacket;
import com.ashbisoficial.stagelite.network.packet.SaveShowPacket;
import com.ashbisoficial.stagelite.network.packet.StopShowPacket;
import com.ashbisoficial.stagelite.network.packet.StrobeAllPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Consola de luces. Dos pestanas en la misma pantalla (sin volver a abrir nada):
 * "Grupos" (como en la Fase 2: color/intensidad/efecto aplicados a un grupo, Blackout,
 * Estrobo general) y "Shows" (Fase 4: guardar/cargar escenas, armar un show de hasta
 * 4 pasos con duracion y transicion, reproducir/parar).
 */
public class ConsolaScreen extends AbstractContainerScreen<ConsolaMenu> {

    private static final int[] PALETTE = {
            0xFFFFFF, 0xFFF4C2, 0xFF2B2B, 0xFF8A2B, 0xFFE12B, 0x2BFF4D, 0x2BF4FF, 0x2B6CFF, 0xB22BFF, 0xFF2BD1
    };
    private static final String[] PALETTE_KEYS = {
            "white", "warm", "red", "orange", "yellow", "green", "cyan", "blue", "purple", "pink"
    };
    private static final String[] EFFECTS = {
            Effects.FIJO, Effects.PARPADEO, Effects.RESPIRACION, Effects.ARCOIRIS, Effects.PERSECUCION
    };
    private static final int TAB_BAR_Y_OFFSET = 24;

    private enum Tab { GRUPOS, SHOWS }

    private Tab tab = Tab.GRUPOS;

    // Compartido entre pestanas: el "look" que se esta editando.
    private String selectedGroup;
    private int selectedColor = LightState.DEFAULT_COLOR;
    private int selectedIntensity = LightState.DEFAULT_INTENSITY;
    private String selectedEffect = Effects.FIJO;
    private float selectedSpeed = 1.0f;

    // Estado del editor de shows.
    private final String[] stepScene = new String[Show.STEP_COUNT];
    private final int[] stepDurationTicks = new int[Show.STEP_COUNT];
    private final String[] stepTransition = new String[Show.STEP_COUNT];
    private boolean showLoop = false;
    private String showToPlay;
    private EditBox sceneNameBox;
    private EditBox showNameBox;

    public ConsolaScreen(ConsolaMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 236;
        this.imageHeight = 280;
        List<String> groups = menu.getGroupNames();
        this.selectedGroup = groups.isEmpty() ? null : groups.get(0);
        List<String> shows = menu.getShowNames();
        this.showToPlay = shows.isEmpty() ? null : shows.get(0);
        for (int i = 0; i < Show.STEP_COUNT; i++) {
            stepScene[i] = "";
            stepDurationTicks[i] = 60;
            stepTransition[i] = Show.TRANSITION_CORTE;
        }
    }

    @Override
    protected void init() {
        super.init();
        rebuildConsolaWidgets();
    }

    private void rebuildConsolaWidgets() {
        clearWidgets();
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;

        int tabWidth = (imageWidth - 16 - 4) / 2;
        Button groupsTabButton = Button.builder(Component.translatable("gui.stagelite.consola.tab.groups"), btn -> switchTab(Tab.GRUPOS))
                .bounds(left + 8, top + TAB_BAR_Y_OFFSET, tabWidth, 18)
                .build();
        addRenderableWidget(groupsTabButton);
        Button showsTabButton = Button.builder(Component.translatable("gui.stagelite.consola.tab.shows"), btn -> switchTab(Tab.SHOWS))
                .bounds(left + 8 + tabWidth + 4, top + TAB_BAR_Y_OFFSET, tabWidth, 18)
                .build();
        addRenderableWidget(showsTabButton);

        int contentTop = top + TAB_BAR_Y_OFFSET + 22;
        if (tab == Tab.GRUPOS) {
            initGroupsTab(left, contentTop);
        } else {
            initShowsTab(left, contentTop);
        }
    }

    private void switchTab(Tab newTab) {
        this.tab = newTab;
        rebuildConsolaWidgets();
    }

    private void initGroupsTab(int left, int y) {
        List<String> groups = menu.getGroupNames();
        int groupButtonWidth = 60;
        for (int i = 0; i < groups.size(); i++) {
            String group = groups.get(i);
            int x = left + 8 + (i % 3) * (groupButtonWidth + 4);
            int by = y + (i / 3) * 20;
            Button groupButton = Button.builder(Component.literal(group), btn -> selectedGroup = group)
                    .bounds(x, by, groupButtonWidth, 18)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.group.tooltip")))
                    .build();
            addRenderableWidget(groupButton);
        }

        int rowsOfGroups = Math.max(1, (groups.size() + 2) / 3);
        y += rowsOfGroups * 20 + 6;

        int swatchSize = 16;
        for (int i = 0; i < PALETTE.length; i++) {
            int color = PALETTE[i];
            int x = left + 8 + i * (swatchSize + 2);
            Button swatch = Button.builder(Component.empty(), btn -> selectedColor = color)
                    .bounds(x, y, swatchSize, swatchSize)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.foco.color." + PALETTE_KEYS[i])))
                    .build();
            addRenderableWidget(swatch);
        }
        y += swatchSize + 10;

        IntensitySlider slider = new IntensitySlider(left + 8, y, imageWidth - 16, 20, selectedIntensity);
        slider.setTooltip(Tooltip.create(Component.translatable("gui.stagelite.foco.intensity.tooltip")));
        addRenderableWidget(slider);
        y += 26;

        int effectWidth = (imageWidth - 16 - 8) / 3;
        for (int i = 0; i < EFFECTS.length; i++) {
            String effect = EFFECTS[i];
            int x = left + 8 + (i % 3) * (effectWidth + 4);
            int by = y + (i / 3) * 20;
            Button effectButton = Button.builder(Component.translatable("gui.stagelite.effect." + effect), btn -> selectedEffect = effect)
                    .bounds(x, by, effectWidth, 18)
                    .build();
            addRenderableWidget(effectButton);
        }
        int effectRows = (EFFECTS.length + 2) / 3;
        y += effectRows * 20 + 6;

        SpeedSlider speedSlider = new SpeedSlider(left + 8, y, imageWidth - 16, 20, selectedSpeed);
        speedSlider.setTooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.speed.tooltip")));
        addRenderableWidget(speedSlider);
        y += 26;

        Button apply = Button.builder(Component.translatable("gui.stagelite.consola.apply"), btn -> applyToGroup())
                .bounds(left + 8, y, imageWidth - 16, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.apply.tooltip")))
                .build();
        addRenderableWidget(apply);
        y += 26;

        int halfWidth = (imageWidth - 16 - 6) / 2;
        Button blackout = Button.builder(Component.translatable("gui.stagelite.consola.blackout"), btn -> sendBlackout())
                .bounds(left + 8, y, halfWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.blackout.tooltip")))
                .build();
        addRenderableWidget(blackout);

        Button strobe = Button.builder(Component.translatable("gui.stagelite.consola.strobe"), btn -> sendStrobe())
                .bounds(left + 8 + halfWidth + 6, y, halfWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.strobe.tooltip")))
                .build();
        addRenderableWidget(strobe);
    }

    private void initShowsTab(int left, int y) {
        sceneNameBox = new EditBox(font, left + 8, y, 140, 18, Component.translatable("gui.stagelite.consola.scene.name"));
        sceneNameBox.setMaxLength(32);
        addRenderableWidget(sceneNameBox);

        Button saveScene = Button.builder(Component.translatable("gui.stagelite.consola.scene.save"), btn -> saveScene())
                .bounds(left + 8 + 144, y, imageWidth - 16 - 144, 18)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.scene.save.tooltip")))
                .build();
        addRenderableWidget(saveScene);
        y += 24;

        List<String> scenes = menu.getSceneNames();
        int sceneButtonWidth = (imageWidth - 16 - 8) / 3;
        for (int i = 0; i < scenes.size(); i++) {
            String scene = scenes.get(i);
            int x = left + 8 + (i % 3) * (sceneButtonWidth + 4);
            int by = y + (i / 3) * 20;
            Button sceneButton = Button.builder(Component.literal(scene), btn -> loadScene(scene))
                    .bounds(x, by, sceneButtonWidth, 18)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.scene.load.tooltip")))
                    .build();
            addRenderableWidget(sceneButton);
        }
        int sceneRows = Math.max(1, (scenes.size() + 2) / 3);
        y += sceneRows * 20 + 8;

        int stepRowHeight = 20;
        List<String> sceneOptions = new ArrayList<>();
        sceneOptions.add("");
        sceneOptions.addAll(scenes);
        for (int i = 0; i < Show.STEP_COUNT; i++) {
            int stepIndex = i;
            int by = y + i * stepRowHeight;

            Button sceneCycle = Button.builder(stepSceneLabel(stepScene[i]), btn -> {
                String next = nextOption(sceneOptions, stepScene[stepIndex]);
                stepScene[stepIndex] = next;
                btn.setMessage(stepSceneLabel(next));
            }).bounds(left + 8, by, 100, 18)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.show.step.scene.tooltip")))
                    .build();
            addRenderableWidget(sceneCycle);

            Button durationCycle = Button.builder(durationLabel(stepDurationTicks[i]), btn -> {
                stepDurationTicks[stepIndex] = nextDuration(stepDurationTicks[stepIndex]);
                btn.setMessage(durationLabel(stepDurationTicks[stepIndex]));
            }).bounds(left + 8 + 104, by, 64, 18)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.show.step.duration.tooltip")))
                    .build();
            addRenderableWidget(durationCycle);

            Button transitionToggle = Button.builder(transitionLabel(stepTransition[i]), btn -> {
                stepTransition[stepIndex] = Show.TRANSITION_CORTE.equals(stepTransition[stepIndex]) ? Show.TRANSITION_FUNDIDO : Show.TRANSITION_CORTE;
                btn.setMessage(transitionLabel(stepTransition[stepIndex]));
            }).bounds(left + 8 + 104 + 68, by, imageWidth - 16 - 104 - 68, 18)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.show.step.transition.tooltip")))
                    .build();
            addRenderableWidget(transitionToggle);
        }
        y += Show.STEP_COUNT * stepRowHeight + 6;

        showNameBox = new EditBox(font, left + 8, y, 100, 18, Component.translatable("gui.stagelite.consola.show.name"));
        showNameBox.setMaxLength(32);
        addRenderableWidget(showNameBox);

        Button loopToggle = Button.builder(loopLabel(showLoop), btn -> {
            showLoop = !showLoop;
            btn.setMessage(loopLabel(showLoop));
        }).bounds(left + 8 + 104, y, 60, 18).build();
        addRenderableWidget(loopToggle);

        Button saveShow = Button.builder(Component.translatable("gui.stagelite.consola.show.save"), btn -> saveShow())
                .bounds(left + 8 + 168, y, imageWidth - 16 - 168, 18)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.show.save.tooltip")))
                .build();
        addRenderableWidget(saveShow);
        y += 24;

        List<String> shows = menu.getShowNames();
        int showButtonWidth = (imageWidth - 16 - 8) / 3;
        for (int i = 0; i < shows.size(); i++) {
            String show = shows.get(i);
            int x = left + 8 + (i % 3) * (showButtonWidth + 4);
            int by = y + (i / 3) * 20;
            Button showButton = Button.builder(Component.literal(show), btn -> showToPlay = show)
                    .bounds(x, by, showButtonWidth, 18)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.show.pick.tooltip")))
                    .build();
            addRenderableWidget(showButton);
        }
        int showRows = Math.max(1, (shows.size() + 2) / 3);
        y += showRows * 20 + 6;

        int halfWidth = (imageWidth - 16 - 6) / 2;
        Button play = Button.builder(Component.translatable("gui.stagelite.consola.show.play"), btn -> playShow())
                .bounds(left + 8, y, halfWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.show.play.tooltip")))
                .build();
        addRenderableWidget(play);

        Button stop = Button.builder(Component.translatable("gui.stagelite.consola.show.stop"), btn -> stopShow())
                .bounds(left + 8 + halfWidth + 6, y, halfWidth, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.show.stop.tooltip")))
                .build();
        addRenderableWidget(stop);
    }

    private static String nextOption(List<String> options, String current) {
        int index = options.indexOf(current);
        return options.get((index + 1) % options.size());
    }

    private static int nextDuration(int currentTicks) {
        int[] presets = {20, 40, 60, 100, 200, 400, 600};
        for (int preset : presets) {
            if (preset > currentTicks) {
                return preset;
            }
        }
        return presets[0];
    }

    private Component stepSceneLabel(String scene) {
        return scene.isBlank() ? Component.translatable("gui.stagelite.consola.show.step.empty") : Component.literal(scene);
    }

    private Component durationLabel(int ticks) {
        return Component.translatable("gui.stagelite.consola.show.step.duration", String.format("%.1f", ticks / 20.0f));
    }

    private Component transitionLabel(String transition) {
        return Component.translatable("gui.stagelite.effect.transition." + transition);
    }

    private Component loopLabel(boolean loop) {
        return Component.translatable(loop ? "gui.stagelite.consola.show.loop.on" : "gui.stagelite.consola.show.loop.off");
    }

    private void applyToGroup() {
        if (selectedGroup == null) {
            return;
        }
        StageLiteNetwork.CHANNEL.sendToServer(new ApplyGroupLookPacket(selectedGroup, selectedColor, selectedIntensity, selectedEffect, selectedSpeed));
    }

    private void sendBlackout() {
        StageLiteNetwork.CHANNEL.sendToServer(new BlackoutPacket());
    }

    private void sendStrobe() {
        StageLiteNetwork.CHANNEL.sendToServer(new StrobeAllPacket());
    }

    private void saveScene() {
        if (selectedGroup == null || sceneNameBox == null || sceneNameBox.getValue().isBlank()) {
            return;
        }
        StageLiteNetwork.CHANNEL.sendToServer(new SaveScenePacket(sceneNameBox.getValue(), selectedGroup, selectedColor, selectedIntensity, selectedEffect, selectedSpeed));
    }

    private void loadScene(String sceneName) {
        StageLiteNetwork.CHANNEL.sendToServer(new LoadScenePacket(menu.getPos(), sceneName));
    }

    private void saveShow() {
        if (showNameBox == null || showNameBox.getValue().isBlank()) {
            return;
        }
        Show show = new Show(showLoop);
        for (int i = 0; i < Show.STEP_COUNT; i++) {
            show.getSteps().set(i, new Show.ShowStep(stepScene[i], stepDurationTicks[i], stepTransition[i]));
        }
        StageLiteNetwork.CHANNEL.sendToServer(new SaveShowPacket(showNameBox.getValue(), show));
    }

    private void playShow() {
        if (showToPlay == null) {
            return;
        }
        StageLiteNetwork.CHANNEL.sendToServer(new PlayShowPacket(menu.getPos(), showToPlay));
    }

    private void stopShow() {
        StageLiteNetwork.CHANNEL.sendToServer(new StopShowPacket(menu.getPos()));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;
        guiGraphics.fill(left, top, left + imageWidth, top + imageHeight, 0xE0101018);
        guiGraphics.fill(left, top, left + imageWidth, top + 20, 0xE02A2A40);

        if (tab == Tab.GRUPOS && menu.getGroupNames().isEmpty()) {
            guiGraphics.drawString(font, Component.translatable("gui.stagelite.consola.no_groups"), left + 8, top + TAB_BAR_Y_OFFSET + 24, 0xFFAAAAAA, false);
        }
        if (tab == Tab.SHOWS && menu.getSceneNames().isEmpty()) {
            guiGraphics.drawString(font, Component.translatable("gui.stagelite.consola.no_scenes"), left + 8, top + TAB_BAR_Y_OFFSET + 46, 0xFFAAAAAA, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, 8, 6, 0xFFFFFF, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private class IntensitySlider extends AbstractSliderButton {

        IntensitySlider(int x, int y, int width, int height, int initialIntensity) {
            super(x, y, width, height, Component.empty(), initialIntensity / 100.0);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("gui.stagelite.foco.intensity", (int) Math.round(value * 100)));
        }

        @Override
        protected void applyValue() {
            selectedIntensity = (int) Math.round(value * 100);
        }
    }

    /** Velocidad/BPM del efecto: 0.2x (lento) a 3.0x (rapido), 1.0x por defecto. */
    private class SpeedSlider extends AbstractSliderButton {

        SpeedSlider(int x, int y, int width, int height, float initialSpeed) {
            super(x, y, width, height, Component.empty(), (initialSpeed - 0.2) / 2.8);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.translatable("gui.stagelite.consola.speed", String.format("%.1f", speedValue())));
        }

        @Override
        protected void applyValue() {
            selectedSpeed = speedValue();
        }

        private float speedValue() {
            return (float) (0.2 + value * 2.8);
        }
    }
}
