package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.blockentity.MovingHeadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Enviado por el cliente al guardar la pantalla de la cabeza movil. */
public class SetMovingHeadConfigPacket {

    private static final double MAX_DISTANCE_SQ = 8.0 * 8.0;
    private static final int MAX_GROUP_LENGTH = 32;
    private static final int MAX_PATTERN_LENGTH = 16;

    private final BlockPos pos;
    private final int color;
    private final int intensity;
    private final String group;
    private final String pattern;

    public SetMovingHeadConfigPacket(BlockPos pos, int color, int intensity, String group, String pattern) {
        this.pos = pos;
        this.color = color;
        this.intensity = intensity;
        this.group = group;
        this.pattern = pattern;
    }

    public static void encode(SetMovingHeadConfigPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.pos);
        buf.writeInt(packet.color);
        buf.writeInt(packet.intensity);
        buf.writeUtf(packet.group, MAX_GROUP_LENGTH);
        buf.writeUtf(packet.pattern, MAX_PATTERN_LENGTH);
    }

    public static SetMovingHeadConfigPacket decode(FriendlyByteBuf buf) {
        return new SetMovingHeadConfigPacket(
                buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readUtf(MAX_GROUP_LENGTH), buf.readUtf(MAX_PATTERN_LENGTH)
        );
    }

    public static void handle(SetMovingHeadConfigPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null) {
                return;
            }
            Level level = player.level();
            if (!level.isLoaded(packet.pos) || player.distanceToSqr(packet.pos.getX() + 0.5, packet.pos.getY() + 0.5, packet.pos.getZ() + 0.5) > MAX_DISTANCE_SQ) {
                return;
            }
            if (level.getBlockEntity(packet.pos) instanceof MovingHeadBlockEntity cabeza) {
                cabeza.applyConfigFromClient(packet.color, packet.intensity, packet.group, packet.pattern);
            }
        });
        context.setPacketHandled(true);
    }
}
