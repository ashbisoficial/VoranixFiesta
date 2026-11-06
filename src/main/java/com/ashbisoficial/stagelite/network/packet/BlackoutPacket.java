package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.blockentity.FocoBlockEntity;
import com.ashbisoficial.stagelite.light.GroupManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Boton rapido "Blackout" de la consola: alterna el apagado de todos los focos conocidos. */
public class BlackoutPacket {

    public static void encode(BlackoutPacket packet, FriendlyByteBuf buf) {
    }

    public static BlackoutPacket decode(FriendlyByteBuf buf) {
        return new BlackoutPacket();
    }

    public static void handle(BlackoutPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !(player.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            GroupManager groups = GroupManager.get(serverLevel);
            boolean anyOn = false;
            for (BlockPos pos : groups.getAllPositions()) {
                if (serverLevel.getBlockEntity(pos) instanceof FocoBlockEntity foco && !foco.isBlackedOut()) {
                    anyOn = true;
                    break;
                }
            }
            boolean newBlackout = anyOn;
            for (BlockPos pos : groups.getAllPositions()) {
                if (serverLevel.getBlockEntity(pos) instanceof FocoBlockEntity foco) {
                    foco.setBlackedOut(newBlackout);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
