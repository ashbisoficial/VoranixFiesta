package com.ashbisoficial.stagelite.client.screen;

import com.ashbisoficial.stagelite.blockentity.MovingHeadBlockEntity;
import com.ashbisoficial.stagelite.light.LightState;
import com.ashbisoficial.stagelite.menu.MovingHeadMenu;
import com.ashbisoficial.stagelite.network.StageLiteNetwork;
import com.ashbisoficial.stagelite.network.packet.SetMovingHeadConfigPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Como FocoScreen, pero con un selector de patron de movimiento (fijo/barrido/circulo). */
public class MovingHeadScreen extends AbstractContainerScreen<MovingHeadMenu> {

    private static final int[] PALETTE = {
            0xFFFFFF, 0xFFF4C2, 0xFF2B2B, 0xFF8A2B, 0xFFE12B, 0x2BFF4D, 0x2BF4FF, 0x2B6CFF, 0xB22BFF, 0xFF2BD1
    };
    private static final String[] PALETTE_KEYS = {
            "white", "warm", "red", "orange", "yellow", "green", "cyan", "blue", "purple", "pink"
    };
    private static final String[] PATTERNS = {
            MovingHeadBlockEntity.PATTERN_FIJO, MovingHeadBlockEntity.PATTERN_BARRIDO, MovingHeadBlockEntity.PATTERN_CIRCULO
    };

    private int selectedColor;
    private int selectedIntensity;
    private String selectedPattern;
    private EditBox groupBox;

    public MovingHeadScreen(MovingHeadMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 216;
        this.imageHeight = 216;
        MovingHeadBlockEntity blockEntity = getBlockEntity();
        if (blockEntity != null) {
            this.selectedColor = blockEntity.getLightState().getColor();
            this.selectedIntensity = blockEntity.getLightState().getIntensity();
            this.selectedPattern = blockEntity.getPattern();
        } else {
            this.selectedColor = LightState.DEFAULT_COLOR;
            this.selectedIntensity = LightState.DEFAULT_INTENSITY;
            this.selectedPattern = MovingHeadBlockEntity.PATTERN_BARRIDO;
        }
    }

    private MovingHeadBlockEntity getBlockEntity() {
        if (Minecraft.getInstance().level == null) {
            return null;
        }
        if (Minecraft.getInstance().level.getBlockEntity(menu.getPos()) instanceof MovingHeadBlockEntity cabeza) {
            return cabeza;
        }
        return null;
    }

    @Override
    protected void init() {
        super.init();
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;

        int swatchSize = 18;
        int startX = left + 8;
        int colorsY = top + 28;
        for (int i = 0; i < PALETTE.length; i++) {
            int color = PALETTE[i];
            int x = startX + i * (swatchSize + 2);
            Button swatch = Button.builder(Component.empty(), btn -> selectedColor = color)
                    .bounds(x, colorsY, swatchSize, swatchSize)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.foco.color." + PALETTE_KEYS[i])))
                    .build();
            addRenderableWidget(swatch);
        }

        IntensitySlider slider = new IntensitySlider(left + 8, top + 60, imageWidth - 16, 20, selectedIntensity);
        slider.setTooltip(Tooltip.create(Component.translatable("gui.stagelite.foco.intensity.tooltip")));
        addRenderableWidget(slider);

        int patternWidth = (imageWidth - 16 - 8) / 3;
        int patternY = top + 88;
        for (int i = 0; i < PATTERNS.length; i++) {
            String pattern = PATTERNS[i];
            int x = left + 8 + i * (patternWidth + 4);
            Button patternButton = Button.builder(Component.translatable("gui.stagelite.cabeza_movil.pattern." + pattern), btn -> selectedPattern = pattern)
                    .bounds(x, patternY, patternWidth, 18)
                    .tooltip(Tooltip.create(Component.translatable("gui.stagelite.cabeza_movil.pattern.tooltip")))
                    .build();
            addRenderableWidget(patternButton);
        }

        String initialGroup = getBlockEntity() != null ? getBlockEntity().getLightState().getGroup() : "";
        groupBox = new EditBox(font, left + 8, top + 114, imageWidth - 16, 18, Component.translatable("gui.stagelite.foco.group"));
        groupBox.setMaxLength(32);
        groupBox.setValue(initialGroup);
        groupBox.setTooltip(Tooltip.create(Component.translatable("gui.stagelite.foco.group.tooltip")));
        addRenderableWidget(groupBox);

        Button save = Button.builder(Component.translatable("gui.stagelite.save"), btn -> saveAndClose())
                .bounds(left + 8, top + imageHeight - 50, 95, 20)
                .build();
        addRenderableWidget(save);

        Button cancel = Button.builder(Component.translatable("gui.stagelite.cancel"), btn -> onClose())
                .bounds(left + imageWidth - 8 - 95, top + imageHeight - 50, 95, 20)
                .build();
        addRenderableWidget(cancel);
    }

    private void saveAndClose() {
        StageLiteNetwork.CHANNEL.sendToServer(new SetMovingHeadConfigPacket(menu.getPos(), selectedColor, selectedIntensity, groupBox.getValue(), selectedPattern));
        onClose();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;
        guiGraphics.fill(left, top, left + imageWidth, top + imageHeight, 0xE0101010);
        guiGraphics.fill(left, top, left + imageWidth, top + 20, 0xE0303030);

        int previewSize = 18;
        int previewX = left + imageWidth - 8 - previewSize;
        int previewY = top + 28;
        guiGraphics.fill(previewX - 2, previewY - 2, previewX + previewSize + 2, previewY + previewSize + 2, 0xFFFFFFFF);
        guiGraphics.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, 0xFF000000 | selectedColor);
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
}
