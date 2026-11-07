package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.blockentity.ConsolaBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Enviado por la consola al pulsar "Reproducir". Tambien arma el show para la redstone. */
public class PlayShowPacket {

    private static final double MAX_DISTANCE_SQ = 8.0 * 8.0;
    private static final int MAX_NAME_LENGTH = 32;

    private final BlockPos consolaPos;
    private final String showName;

    public PlayShowPacket(BlockPos consolaPos, String showName) {
        this.consolaPos = consolaPos;
        this.showName = showName;
    }

    public static void encode(PlayShowPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.consolaPos);
        buf.writeUtf(packet.showName, MAX_NAME_LENGTH);
    }

    public static PlayShowPacket decode(FriendlyByteBuf buf) {
        return new PlayShowPacket(buf.readBlockPos(), buf.readUtf(MAX_NAME_LENGTH));
    }

    public static void handle(PlayShowPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
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
                consola.startShow(packet.showName);
            }
        });
        context.setPacketHandled(true);
    }
}
