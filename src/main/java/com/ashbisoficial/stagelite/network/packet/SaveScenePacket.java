package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.light.Scene;
import com.ashbisoficial.stagelite.light.SceneManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Enviado por la consola al pulsar "Guardar escena". */
public class SaveScenePacket {

    private static final int MAX_NAME_LENGTH = 32;
    private static final int MAX_GROUP_LENGTH = 32;
    private static final int MAX_EFFECT_LENGTH = 16;

    private final String name;
    private final String group;
    private final int color;
    private final int intensity;
    private final String effect;
    private final float speed;

    public SaveScenePacket(String name, String group, int color, int intensity, String effect, float speed) {
        this.name = name;
        this.group = group;
        this.color = color;
        this.intensity = intensity;
        this.effect = effect;
        this.speed = speed;
    }

    public static void encode(SaveScenePacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.name, MAX_NAME_LENGTH);
        buf.writeUtf(packet.group, MAX_GROUP_LENGTH);
        buf.writeInt(packet.color);
        buf.writeInt(packet.intensity);
        buf.writeUtf(packet.effect, MAX_EFFECT_LENGTH);
        buf.writeFloat(packet.speed);
    }

    public static SaveScenePacket decode(FriendlyByteBuf buf) {
        return new SaveScenePacket(
                buf.readUtf(MAX_NAME_LENGTH), buf.readUtf(MAX_GROUP_LENGTH), buf.readInt(), buf.readInt(),
                buf.readUtf(MAX_EFFECT_LENGTH), buf.readFloat()
        );
    }

    public static void handle(SaveScenePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !(player.level() instanceof ServerLevel serverLevel) || packet.name.isBlank() || packet.group.isBlank()) {
                return;
            }
            SceneManager.get(serverLevel).save(packet.name, new Scene(packet.group, packet.color, packet.intensity, packet.effect, packet.speed));
        });
        context.setPacketHandled(true);
    }
}
