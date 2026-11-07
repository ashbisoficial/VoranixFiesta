package com.ashbisoficial.stagelite.client.render;

import com.ashbisoficial.stagelite.block.AbstractLightBlock;
import com.ashbisoficial.stagelite.blockentity.AbstractLightBlockEntity;
import com.ashbisoficial.stagelite.light.Effects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Panel LED: no tiene un haz marcado como el foco, solo una luz de area suave
 * (SPEC.md 4.1.2). Simplificacion deliberada: en vez de dibujar un quad propio,
 * se reutiliza el mismo metodo del haz del faro pero muy corto y ancho, para que
 * se vea como un resplandor difuso en vez de un cono. Si el resultado no convence
 * visualmente, es el primer sitio a cambiar por un quad dedicado.
 */
public class WashBlockEntityRenderer implements BlockEntityRenderer<AbstractLightBlockEntity> {

    private static final int GLOW_LENGTH = 2;
    private static final float GLOW_WIDTH = 0.6f;

    public WashBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AbstractLightBlockEntity panel, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!panel.isOn()) {
            return;
        }

        BlockState state = panel.getBlockState();
        Direction facing = state.getValue(AbstractLightBlock.FACING);
        String effect = panel.getLightState().getEffect();
        int groupIndex = panel.getGroupIndex();
        int groupSize = panel.getGroupSize();
        long gameTime = panel.getLevel() != null ? panel.getLevel().getGameTime() : 0L;

        int color = Effects.colorOverride(effect, panel.getDisplayColor(gameTime, partialTick), gameTime, partialTick, panel.getLightState().getSpeed(), groupIndex, groupSize);
        float[] colorComponents = BeamRenderUtil.toRgb(color);

        float effectMultiplier = Effects.multiplier(effect, panel.getLightState().getSpeed(), gameTime, partialTick, groupIndex, groupSize);
        float alpha = Math.max(0.0f, (panel.getEffectiveIntensity(gameTime, partialTick) / 100.0f) * effectMultiplier);
        if (alpha <= 0.01f) {
            return;
        }
        alpha = Math.max(0.2f, alpha * 0.8f);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        BeamRenderUtil.rotateToFacing(poseStack, facing);
        poseStack.translate(0.0, -0.4, 0.0);

        BeaconRenderer.renderBeaconBeam(
                poseStack,
                bufferSource,
                BeaconRenderer.BEAM_LOCATION,
                partialTick,
                1.0f,
                gameTime,
                0,
                GLOW_LENGTH,
                colorComponents,
                alpha,
                GLOW_WIDTH
        );

        poseStack.popPose();
    }
}
