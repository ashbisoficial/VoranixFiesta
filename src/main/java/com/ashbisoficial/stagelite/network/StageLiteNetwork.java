package com.ashbisoficial.stagelite.network;

import com.ashbisoficial.stagelite.StageLite;
import com.ashbisoficial.stagelite.network.packet.ApplyGroupLookPacket;
import com.ashbisoficial.stagelite.network.packet.BlackoutPacket;
import com.ashbisoficial.stagelite.network.packet.SetFocoConfigPacket;
import com.ashbisoficial.stagelite.network.packet.SetMovingHeadConfigPacket;
import com.ashbisoficial.stagelite.network.packet.StrobeAllPacket;
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
        CHANNEL.registerMessage(
                nextId++,
                ApplyGroupLookPacket.class,
                ApplyGroupLookPacket::encode,
                ApplyGroupLookPacket::decode,
                ApplyGroupLookPacket::handle
        );
        CHANNEL.registerMessage(
                nextId++,
                BlackoutPacket.class,
                BlackoutPacket::encode,
                BlackoutPacket::decode,
                BlackoutPacket::handle
        );
        CHANNEL.registerMessage(
                nextId++,
                StrobeAllPacket.class,
                StrobeAllPacket::encode,
                StrobeAllPacket::decode,
                StrobeAllPacket::handle
        );
        CHANNEL.registerMessage(
                nextId++,
                SetMovingHeadConfigPacket.class,
                SetMovingHeadConfigPacket::encode,
                SetMovingHeadConfigPacket::decode,
                SetMovingHeadConfigPacket::handle
        );
    }
}
