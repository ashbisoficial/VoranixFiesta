package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.blockentity.ConsolaBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Enviado por la consola al pulsar "Parar". */
public class StopShowPacket {

    private static final double MAX_DISTANCE_SQ = 8.0 * 8.0;

    private final BlockPos consolaPos;

    public StopShowPacket(BlockPos consolaPos) {
        this.consolaPos = consolaPos;
    }

    public static void encode(StopShowPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.consolaPos);
    }

    public static StopShowPacket decode(FriendlyByteBuf buf) {
        return new StopShowPacket(buf.readBlockPos());
    }

    public static void handle(StopShowPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null) {
                return;
            }
            Level level = player.level();
            if (!level.isLoaded(packet.consolaPos) || player.distanceToSqr(packet.consolaPos.getX() + 0.5, packet.consolaPos.getY() + 0.5, packet.consolaPos.getZ() + 0.5) > MAX_DISTANCE_SQ) {
                return;
            }
            if (level.getBlockEntity(packet.consolaPos) instanceof ConsolaBlockEntity consola) {
                consola.stopShow();
            }
        });
        context.setPacketHandled(true);
    }
}
