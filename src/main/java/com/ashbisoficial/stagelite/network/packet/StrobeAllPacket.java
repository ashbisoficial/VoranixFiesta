package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.blockentity.FocoBlockEntity;
import com.ashbisoficial.stagelite.light.Effects;
import com.ashbisoficial.stagelite.light.GroupManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Boton rapido "Estrobo general": pone el efecto parpadeo en todos los focos conocidos,
 * sin tocarles el color ni la intensidad que ya tuvieran configurados.
 */
public class StrobeAllPacket {

    public static void encode(StrobeAllPacket packet, FriendlyByteBuf buf) {
    }

    public static StrobeAllPacket decode(FriendlyByteBuf buf) {
        return new StrobeAllPacket();
    }

    public static void handle(StrobeAllPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !(player.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            GroupManager groups = GroupManager.get(serverLevel);
            for (BlockPos pos : groups.getAllPositions()) {
                if (serverLevel.getBlockEntity(pos) instanceof FocoBlockEntity foco) {
                    foco.applyGroupLook(
                            foco.getLightState().getColor(),
                            foco.getLightState().getIntensity(),
                            Effects.PARPADEO,
                            1.0f
                    );
                }
            }
        });
        context.setPacketHandled(true);
    }
}
