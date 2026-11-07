package com.ashbisoficial.stagelite.network.packet;

import com.ashbisoficial.stagelite.blockentity.AbstractLightBlockEntity;
import com.ashbisoficial.stagelite.blockentity.ConsolaBlockEntity;
import com.ashbisoficial.stagelite.light.GroupManager;
import com.ashbisoficial.stagelite.light.Scene;
import com.ashbisoficial.stagelite.light.SceneManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Enviado por la consola al pulsar "Cargar escena". Aplica la escena a todo su grupo. */
public class LoadScenePacket {

    private static final int MAX_NAME_LENGTH = 32;

    private final BlockPos consolaPos;
    private final String sceneName;

    public LoadScenePacket(BlockPos consolaPos, String sceneName) {
        this.consolaPos = consolaPos;
        this.sceneName = sceneName;
    }

    public static void encode(LoadScenePacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.consolaPos);
        buf.writeUtf(packet.sceneName, MAX_NAME_LENGTH);
    }

    public static LoadScenePacket decode(FriendlyByteBuf buf) {
        return new LoadScenePacket(buf.readBlockPos(), buf.readUtf(MAX_NAME_LENGTH));
    }

    public static void handle(LoadScenePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null || !(player.level() instanceof ServerLevel serverLevel)) {
                return;
            }
            Scene scene = SceneManager.get(serverLevel).get(packet.sceneName);
            if (scene == null) {
                return;
            }
            GroupManager groups = GroupManager.get(serverLevel);
            var positions = groups.getPositions(scene.getGroup());
            int size = positions.size();
            int index = 0;
            for (BlockPos pos : positions) {
                if (serverLevel.getBlockEntity(pos) instanceof AbstractLightBlockEntity foco) {
                    foco.applyGroupLook(scene.getColor(), scene.getIntensity(), scene.getEffect(), scene.getSpeed(), index, size);
                }
                index++;
            }
            if (serverLevel.getBlockEntity(packet.consolaPos) instanceof ConsolaBlockEntity consola) {
                consola.pulseFromScene(serverLevel);
            }
        });
        context.setPacketHandled(true);
    }
}
