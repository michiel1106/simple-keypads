package com.bikerboys.simplekeypads.networking;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import com.bikerboys.simplekeypads.util.KeypadContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.*;

import java.util.function.Supplier;

public record CheckPasscodeC2S(BlockPos blockPos, Direction face, String code) implements CustomPacketPayload {

    public static final Type<CheckPasscodeC2S> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleKeypads.MODID, "check_passcode_c2s"));

    public static final StreamCodec<FriendlyByteBuf, CheckPasscodeC2S> STREAM_CODEC =
            StreamCodec.of(CheckPasscodeC2S::encode, CheckPasscodeC2S::decode);

    private static CheckPasscodeC2S decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Direction face = buf.readEnum(Direction.class);
        String code = buf.readUtf();
        return new CheckPasscodeC2S(pos, face, code);
    }

    private static void encode(FriendlyByteBuf buf, CheckPasscodeC2S msg) {
        buf.writeBlockPos(msg.blockPos);
        buf.writeEnum(msg.face);
        buf.writeUtf(msg.code);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CheckPasscodeC2S msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();
            if (sender == null) return;

            if (distanceTo(sender.blockPosition(), msg.blockPos) < 7) {
                KeypadEntity keypadOnBlock = SimpleKeypads.getKeypadOnBlock(sender.level(), msg.blockPos, msg.face);
                if (keypadOnBlock == null) return;

                String keycode = keypadOnBlock.getKeycode();
                if (keycode.equals(msg.code)) {
                    SimpleKeypads.allowedplayercontext.add(new KeypadContext(sender, 200, msg.blockPos, msg.face));
                    sender.sendSystemMessage(Component.literal("Unlocked!"), true);
                    NetworkHandler.sendToPlayer(new UpdateAllowedBlockPosS2C(msg.blockPos, true, msg.face), sender);
                }
            }
        });
    }

    private static float distanceTo(BlockPos location1, BlockPos location2) {
        float f = (float) (location1.getX() - location2.getX());
        float f1 = (float) (location1.getY() - location2.getY());
        float f2 = (float) (location1.getZ() - location2.getZ());
        return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
    }
}
