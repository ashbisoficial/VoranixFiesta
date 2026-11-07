package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.light.Show;
import com.ashbisoficial.stagelite.light.ShowManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Enviado por la consola al pulsar "Guardar show". Siempre manda los Show.STEP_COUNT pasos. */
public class SaveShowPacket {

    private static final int MAX_NAME_LENGTH = 32;
    private static final int MAX_SCENE_LENGTH = 32;
    private static final int MAX_TRANSITION_LENGTH = 16;

    private final String name;
    private final boolean loop;
    private final Show show;

    public SaveShowPacket(String name, Show show) {
        this.name = name;
        this.loop = show.isLoop();
        this.show = show;
    }

    public static void encode(SaveShowPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.name, MAX_NAME_LENGTH);
        buf.writeBoolean(packet.loop);
        for (Show.ShowStep step : packet.show.getSteps()) {
            buf.writeUtf(step.getSceneName(), MAX_SCENE_LENGTH);
            buf.writeVarInt(step.getDurationTicks());
            buf.writeUtf(step.getTransition(), MAX_TRANSITION_LENGTH);
        }
    }

    public static SaveShowPacket decode(FriendlyByteBuf buf) {
        String name = buf.readUtf(MAX_NAME_LENGTH);
        boolean loop = buf.readBoolean();
        Show show = new Show(loop);
        for (int i = 0; i < Show.STEP_COUNT; i++) {
            String scene = buf.readUtf(MAX_SCENE_LENGTH);
            int duration = buf.readVarInt();
            String transition = buf.readUtf(MAX_TRANSITION_LENGTH);
            show.getSteps().set(i, new Show.ShowStep(scene, duration, transition));
        }
        return new SaveShowPacket(name, show);
    }

    public static void handle(SaveShowPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !(player.level() instanceof ServerLevel serverLevel) || packet.name.isBlank()) {
                return;
            }
            ShowManager.get(serverLevel).save(packet.name, packet.show);
        });
        context.setPacketHandled(true);
    }
}
