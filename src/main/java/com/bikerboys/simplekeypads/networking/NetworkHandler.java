package com.bikerboys.simplekeypads.networking;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.mojang.serialization.Decoder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    public static int id = 0;
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(SimpleKeypads.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {

        INSTANCE.messageBuilder(SetPasscodeC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SetPasscodeC2S::encode)
                .decoder(SetPasscodeC2S::new)
                .consumerMainThread(SetPasscodeC2S::handle)
                .add();

        INSTANCE.messageBuilder(CheckPasscodeC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CheckPasscodeC2S::encode)
                .decoder(CheckPasscodeC2S::new)
                .consumerMainThread(CheckPasscodeC2S::handle)
                .add();



        INSTANCE.messageBuilder(UpdateAllowedBlockPosS2C.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UpdateAllowedBlockPosS2C::encode)
                .decoder(UpdateAllowedBlockPosS2C::new)
                .consumerMainThread(UpdateAllowedBlockPosS2C::handle)
                .add();

        INSTANCE.messageBuilder(PlaceKeypadC2S.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(PlaceKeypadC2S::encode)
                .decoder(PlaceKeypadC2S::new)
                .consumerMainThread(PlaceKeypadC2S::handle)
                .add();

        ;

    }



    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}
