package com.bikerboys.simplekeypads.networking;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import io.netty.buffer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import net.neoforged.neoforge.network.event.*;
import net.neoforged.neoforge.network.handling.*;
import net.neoforged.neoforge.network.registration.*;

import java.util.function.Supplier;

public record SetPasscodeC2S(BlockPos blockPos, Direction face, String code) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetPasscodeC2S> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("simplekeypads", "set_passcode"));

    public static final StreamCodec<ByteBuf, SetPasscodeC2S> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SetPasscodeC2S::blockPos,
            ByteBufCodecs.fromCodec(Direction.CODEC),
            SetPasscodeC2S::face,
            ByteBufCodecs.STRING_UTF8,
            SetPasscodeC2S::code,
            SetPasscodeC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final SetPasscodeC2S packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();


            if (distanceTo(sender.blockPosition(), packet.blockPos()) < 7) {
                KeypadEntity keypadOnBlock = SimpleKeypads.getKeypadOnBlock(sender.level(), packet.blockPos(), packet.face());
                if (keypadOnBlock == null) return;

                if (keypadOnBlock.getFirstPlaced()) {
                    keypadOnBlock.setKeycode(packet.code());
                    keypadOnBlock.setFirstplaced(true);
                    sender.sendSystemMessage(Component.literal("Set Code to " + packet.code() + "!"), true);
                } else {
                    sender.sendSystemMessage(Component.literal("Can't modify a keypad's code if the code has already been set!"), true);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.literal("Packet handling failed: " + e.getMessage()));
            return null;
        });
    }

    private static float distanceTo(BlockPos location1, BlockPos location2) {
        float f = (float) (location1.getX() - location2.getX());
        float f1 = (float) (location1.getY() - location2.getY());
        float f2 = (float) (location1.getZ() - location2.getZ());
        return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    // Register this payload
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1"); // version "1" as shown in docs
        registrar.playToServer(TYPE, STREAM_CODEC, SetPasscodeC2S::handle);
    }
}
