package com.ashbisoficial.stagelite.client.screen;

import com.ashbisoficial.stagelite.light.Effects;
import com.ashbisoficial.stagelite.light.LightState;
import com.ashbisoficial.stagelite.menu.ConsolaMenu;
import com.ashbisoficial.stagelite.network.StageLiteNetwork;
import com.ashbisoficial.stagelite.network.packet.ApplyGroupLookPacket;
import com.ashbisoficial.stagelite.network.packet.BlackoutPacket;
import com.ashbisoficial.stagelite.network.packet.StrobeAllPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

/**
 * Consola de luces: elige un grupo (creado al ponerle nombre a algun foco) y le aplica
 * un color/intensidad/efecto a todos sus focos a la vez. Blackout y Estrobo general
 * actuan sobre todo lo que haya, sin necesidad de elegir grupo.
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

    private String selectedGroup;
    private int selectedColor = LightState.DEFAULT_COLOR;
    private int selectedIntensity = LightState.DEFAULT_INTENSITY;
    private String selectedEffect = Effects.FIJO;
    private float selectedSpeed = 1.0f;

    public ConsolaScreen(ConsolaMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 236;
        this.imageHeight = 260;
        List<String> groups = menu.getGroupNames();
        this.selectedGroup = groups.isEmpty() ? null : groups.get(0);
    }

    @Override
    protected void init() {
        super.init();
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;

        List<String> groups = menu.getGroupNames();
        int groupButtonWidth = 60;
        for (int i = 0; i < groups.size(); i++) {
            String group = groups.get(i);
            int x = left + 8 + (i % 3) * (groupButtonWidth + 4);
            int y = top + 24 + (i / 3) * 20;
            Button groupButton = Button.builder(Component.literal(group), btn -> selectedGroup = group)
                    .bounds(x, y, groupButtonWidth, 18)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.group.tooltip")))
                    .build();
            addRenderableWidget(groupButton);
        }

        int rowsOfGroups = Math.max(1, (groups.size() + 2) / 3);
        int belowGroups = top + 24 + rowsOfGroups * 20 + 6;

        int swatchSize = 16;
        int startX = left + 8;
        for (int i = 0; i < PALETTE.length; i++) {
            int color = PALETTE[i];
            int x = startX + i * (swatchSize + 2);
            Button swatch = Button.builder(Component.empty(), btn -> selectedColor = color)
                    .bounds(x, belowGroups, swatchSize, swatchSize)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.foco.color." + PALETTE_KEYS[i])))
                    .build();
            addRenderableWidget(swatch);
        }

        IntensitySlider slider = new IntensitySlider(left + 8, belowGroups + swatchSize + 10, imageWidth - 16, 20, selectedIntensity);
        slider.setTooltip(Tooltip.create(Component.translatable("gui.stagelite.foco.intensity.tooltip")));
        addRenderableWidget(slider);

        int effectsY = belowGroups + swatchSize + 36;
        int effectWidth = (imageWidth - 16 - 8) / 3;
        for (int i = 0; i < EFFECTS.length; i++) {
            String effect = EFFECTS[i];
            int x = left + 8 + (i % 3) * (effectWidth + 4);
            int y = effectsY + (i / 3) * 20;
            Button effectButton = Button.builder(Component.translatable("gui.stagelite.effect." + effect), btn -> selectedEffect = effect)
                    .bounds(x, y, effectWidth, 18)
                    .build();
            addRenderableWidget(effectButton);
        }
        int effectRows = (EFFECTS.length + 2) / 3;

        SpeedSlider speedSlider = new SpeedSlider(left + 8, effectsY + effectRows * 20 + 6, imageWidth - 16, 20, selectedSpeed);
        speedSlider.setTooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.speed.tooltip")));
        addRenderableWidget(speedSlider);

        int applyY = effectsY + effectRows * 20 + 32;
        Button apply = Button.builder(Component.translatable("gui.stagelite.consola.apply"), btn -> applyToGroup())
                .bounds(left + 8, applyY, imageWidth - 16, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.apply.tooltip")))
                .build();
        addRenderableWidget(apply);

        int quickY = applyY + 26;
        Button blackout = Button.builder(Component.translatable("gui.stagelite.consola.blackout"), btn -> sendBlackout())
                .bounds(left + 8, quickY, (imageWidth - 16 - 6) / 2, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.blackout.tooltip")))
                .build();
        addRenderableWidget(blackout);

        Button strobe = Button.builder(Component.translatable("gui.stagelite.consola.strobe"), btn -> sendStrobe())
                .bounds(left + 8 + (imageWidth - 16 - 6) / 2 + 6, quickY, (imageWidth - 16 - 6) / 2, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.stagelite.consola.strobe.tooltip")))
                .build();
        addRenderableWidget(strobe);
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

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;
        guiGraphics.fill(left, top, left + imageWidth, top + imageHeight, 0xE0101018);
        guiGraphics.fill(left, top, left + imageWidth, top + 20, 0xE02A2A40);

        if (menu.getGroupNames().isEmpty()) {
            guiGraphics.drawString(font, Component.translatable("gui.stagelite.consola.no_groups"), left + 8, top + 26, 0xFFAAAAAA, false);
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
