package com.ashbisoficial.stagelite.client.render;

import com.ashbisoficial.stagelite.block.AbstractLightBlock;
import com.ashbisoficial.stagelite.blockentity.MovingHeadBlockEntity;
import com.ashbisoficial.stagelite.light.Effects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cabeza movil: el bloque no tiene partes que giren (ver nota en MovingHeadBlockEntity),
 * pero el haz si se mueve: se le aplican dos rotaciones extra (pan/tilt) por encima de la
 * orientacion fija del bloque, calculadas de forma determinista a partir de gameTime, igual
 * que los demas efectos (SPEC.md seccion 5).
 */
public class MovingHeadBlockEntityRenderer implements BlockEntityRenderer<MovingHeadBlockEntity> {

    private static final int BEAM_LENGTH = 10;

    public MovingHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MovingHeadBlockEntity cabeza, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!cabeza.isOn()) {
            return;
        }

        BlockState state = cabeza.getBlockState();
        Direction facing = state.getValue(AbstractLightBlock.FACING);
        String effect = cabeza.getLightState().getEffect();
        int groupIndex = cabeza.getGroupIndex();
        int groupSize = cabeza.getGroupSize();
        long gameTime = cabeza.getLevel() != null ? cabeza.getLevel().getGameTime() : 0L;

        int color = Effects.colorOverride(effect, cabeza.getDisplayColor(gameTime, partialTick), gameTime, partialTick, cabeza.getLightState().getSpeed(), groupIndex, groupSize);
        float[] colorComponents = BeamRenderUtil.toRgb(color);

        float effectMultiplier = Effects.multiplier(effect, cabeza.getLightState().getSpeed(), gameTime, partialTick, groupIndex, groupSize);
        float alpha = Math.max(0.0f, (cabeza.getEffectiveIntensity(gameTime, partialTick) / 100.0f) * effectMultiplier);
        if (alpha <= 0.01f) {
            return;
        }
        alpha = Math.max(0.15f, alpha);

        float t = gameTime + partialTick;
        float pan = 0f;
        float tilt = 0f;
        switch (cabeza.getPattern()) {
            case MovingHeadBlockEntity.PATTERN_BARRIDO -> pan = (float) Math.sin(t * 0.05) * 40.0f;
            case MovingHeadBlockEntity.PATTERN_CIRCULO -> {
                pan = (float) Math.sin(t * 0.05) * 30.0f;
                tilt = (float) Math.cos(t * 0.05) * 20.0f;
            }
            default -> {
                // fijo: sin pan/tilt, el haz apunta siempre hacia FACING.
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        BeamRenderUtil.rotateToFacing(poseStack, facing);
        poseStack.mulPose(Axis.ZP.rotationDegrees(pan));
        poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
        poseStack.translate(0.0, -0.5, 0.0);

        BeaconRenderer.renderBeaconBeam(
                poseStack,
                bufferSource,
                BeaconRenderer.BEAM_LOCATION,
                partialTick,
                1.0f,
                gameTime,
                0,
                BEAM_LENGTH,
                colorComponents,
                alpha,
                0.12f
        );

        poseStack.popPose();
    }
}
