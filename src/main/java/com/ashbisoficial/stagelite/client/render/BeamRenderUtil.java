package com.ashbisoficial.stagelite.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;

/** Utilidades compartidas por los renderers de haz/glow de StageLite. */
final class BeamRenderUtil {

    private BeamRenderUtil() {
    }

    /** Rota el eje local +Y (direccion del haz del faro) hacia la cara en la que mira el foco. */
    static void rotateToFacing(PoseStack poseStack, Direction facing) {
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

    static float[] toRgb(int color) {
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        };
    }
}
