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
 * Etapa B del plan de renderizado (SPEC.md seccion 6): un haz basico, de longitud fija.
 * Se reutiliza el metodo publico que usa el propio faro de Minecraft para dibujar su haz
 * (BeaconRenderer.renderBeaconBeam), solo que orientado hacia la cara del foco en vez de
 * siempre hacia arriba. La Etapa C (recortar el haz contra bloques solidos) queda para mas
 * adelante.
 *
 * Lo usan tanto el Foco como el Estroboscopio (Fase 3): a este ultimo solo le cambia el
 * efecto por defecto (parpadeo), no la forma en que se dibuja.
 */
public class FocoBlockEntityRenderer implements BlockEntityRenderer<AbstractLightBlockEntity> {

    private static final int BEAM_LENGTH = 10;

    public FocoBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(AbstractLightBlockEntity foco, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!foco.isOn()) {
            return;
        }

        BlockState state = foco.getBlockState();
        Direction facing = state.getValue(AbstractLightBlock.FACING);
        String effect = foco.getLightState().getEffect();
        int groupIndex = foco.getGroupIndex();
        int groupSize = foco.getGroupSize();
        long gameTime = foco.getLevel() != null ? foco.getLevel().getGameTime() : 0L;

        int color = Effects.colorOverride(effect, foco.getLightState().getColor(), gameTime, partialTick, foco.getLightState().getSpeed(), groupIndex, groupSize);
        float[] colorComponents = BeamRenderUtil.toRgb(color);

        float effectMultiplier = Effects.multiplier(effect, foco.getLightState().getSpeed(), gameTime, partialTick, groupIndex, groupSize);
        float alpha = Math.max(0.0f, (foco.getEffectiveIntensity() / 100.0f) * effectMultiplier);
        if (alpha <= 0.01f) {
            return;
        }
        alpha = Math.max(0.15f, alpha);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        BeamRenderUtil.rotateToFacing(poseStack, facing);
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
                0.15f
        );

        poseStack.popPose();
    }
}
