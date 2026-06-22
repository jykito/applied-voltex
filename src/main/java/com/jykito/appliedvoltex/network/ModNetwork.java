package com.jykito.appliedvoltex.network;

import com.jykito.appliedvoltex.AppliedVoltex;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModNetwork {

    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(AppliedVoltex.MODID, "main"),
                () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

        CHANNEL.registerMessage(0, EncodePatternC2SPacket.class,
                EncodePatternC2SPacket::encode,
                EncodePatternC2SPacket::decode,
                EncodePatternC2SPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
