package com.ashbisoficial.stagelite.network;

import com.ashbisoficial.stagelite.StageLite;
import com.ashbisoficial.stagelite.network.packet.SetFocoConfigPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class StageLiteNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(StageLite.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int nextId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                nextId++,
                SetFocoConfigPacket.class,
                SetFocoConfigPacket::encode,
                SetFocoConfigPacket::decode,
                SetFocoConfigPacket::handle
        );
    }
}
