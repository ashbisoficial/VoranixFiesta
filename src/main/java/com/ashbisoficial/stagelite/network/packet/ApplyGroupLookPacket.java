package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.blockentity.AbstractLightBlockEntity;
import com.ashbisoficial.stagelite.light.GroupManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Enviado por la consola al pulsar "Aplicar": cambia el look de todos los focos de un grupo a la vez. */
public class ApplyGroupLookPacket {

    private static final int MAX_GROUP_LENGTH = 32;
    private static final int MAX_EFFECT_LENGTH = 16;

    private final String group;
    private final int color;
    private final int intensity;
    private final String effect;
    private final float speed;

    public ApplyGroupLookPacket(String group, int color, int intensity, String effect, float speed) {
        this.group = group;
        this.color = color;
        this.intensity = intensity;
        this.effect = effect;
        this.speed = speed;
    }

    public static void encode(ApplyGroupLookPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.group, MAX_GROUP_LENGTH);
        buf.writeInt(packet.color);
        buf.writeInt(packet.intensity);
        buf.writeUtf(packet.effect, MAX_EFFECT_LENGTH);
        buf.writeFloat(packet.speed);
    }

    public static ApplyGroupLookPacket decode(FriendlyByteBuf buf) {
        return new ApplyGroupLookPacket(
                buf.readUtf(MAX_GROUP_LENGTH),
                buf.readInt(),
                buf.readInt(),
                buf.readUtf(MAX_EFFECT_LENGTH),
                buf.readFloat()
        );
    }

    public static void handle(ApplyGroupLookPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !(player.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            GroupManager groups = GroupManager.get(serverLevel);
            var positions = groups.getPositions(packet.group);
            int size = positions.size();
            int index = 0;
            for (BlockPos pos : positions) {
                if (serverLevel.getBlockEntity(pos) instanceof AbstractLightBlockEntity foco) {
                    foco.applyGroupLook(packet.color, packet.intensity, packet.effect, packet.speed, index, size);
                }
                index++;
            }
        });
        context.setPacketHandled(true);
    }
}
