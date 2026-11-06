package com.ashbisoficial.stagelite.client.render;

import com.ashbisoficial.stagelite.block.FocoBlock;
import com.ashbisoficial.stagelite.blockentity.FocoBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
 */
public class FocoBlockEntityRenderer implements BlockEntityRenderer<FocoBlockEntity> {

    private static final int BEAM_LENGTH = 10;

    public FocoBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FocoBlockEntity foco, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!foco.isOn()) {
            return;
        }

        BlockState state = foco.getBlockState();
        Direction facing = state.getValue(FocoBlock.FACING);
        int color = foco.getLightState().getColor();
        float[] colorComponents = new float[]{
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        };
        float alpha = Math.max(0.15f, foco.getEffectiveIntensity() / 100.0f);

        long gameTime = foco.getLevel() != null ? foco.getLevel().getGameTime() : 0L;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        rotateToFacing(poseStack, facing);
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

    /** Rota el eje local +Y (direccion del haz del faro) hacia la cara en la que mira el foco. */
    private static void rotateToFacing(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case UP -> {
            }
            case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90));
            case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        }
    }
}
