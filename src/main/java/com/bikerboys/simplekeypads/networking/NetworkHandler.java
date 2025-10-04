package com.bikerboys.simplekeypads.networking;

import net.minecraft.network.protocol.common.custom.*;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.*;
import net.neoforged.neoforge.network.event.*;
import net.neoforged.neoforge.network.registration.*;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    /**
     * Called automatically from your mod's main class
     * when subscribing to RegisterPayloadHandlersEvent.
     */
    public static void register(final RegisterPayloadHandlersEvent event) {
        // The "1" here is your network protocol version, matching PROTOCOL_VERSION
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        // === Register all payloads ===
        // C2S
        registrar.playToServer(SetPasscodeC2S.TYPE, SetPasscodeC2S.STREAM_CODEC, SetPasscodeC2S::handle);
        registrar.playToServer(CheckPasscodeC2S.TYPE, CheckPasscodeC2S.STREAM_CODEC, CheckPasscodeC2S::handle);
        registrar.playToServer(PlaceKeypadC2S.TYPE, PlaceKeypadC2S.STREAM_CODEC, PlaceKeypadC2S::handle);

        // S2C
        registrar.playToClient(UpdateAllowedBlockPosS2C.TYPE, UpdateAllowedBlockPosS2C.STREAM_CODEC, UpdateAllowedBlockPosS2C::handle);
    }

    // === Sending helpers ===

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.sendToServer(msg);
    }

    public static void sendToPlayer(CustomPacketPayload msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }
}