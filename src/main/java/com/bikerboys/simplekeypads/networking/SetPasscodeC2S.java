package com.bikerboys.simplekeypads.networking;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetPasscodeC2S {

    private final BlockPos blockPos;
    private final String code;
    private final Direction face;



    public SetPasscodeC2S(BlockPos blockPos, Direction face, String code) {
        this.code = code;
        this.blockPos = blockPos;
        this.face = face;
    }

    public SetPasscodeC2S(FriendlyByteBuf buf) {

        this(buf.readBlockPos(), buf.readEnum(Direction.class), buf.readUtf());

    }

    public void encode(FriendlyByteBuf buf) {

        buf.writeBlockPos(this.blockPos);
        buf.writeEnum(this.face);
        buf.writeUtf(this.code);

    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();

        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) return;

            if (distanceTo(sender.blockPosition(), this.blockPos) < 7) {
                KeypadEntity keypadOnBlock = SimpleKeypads.getKeypadOnBlock(sender.level(), this.blockPos, this.face);
                if (keypadOnBlock == null) return;


                    if (keypadOnBlock.getFirstPlaced()) {
                        keypadOnBlock.setKeycode(code);
                        keypadOnBlock.setFirstplaced(true);
                        sender.sendSystemMessage(Component.literal("Set Code to " + code + "!"), true);
                    } else {
                        sender.sendSystemMessage(Component.literal("Cant modify a keypad's code if the code has already been set!"), true);
                    }

            }
        });

        context.setPacketHandled(true);
    }

    public float distanceTo(BlockPos location1, BlockPos location2) {
        float f = (float)(location1.getX() - location2.getX());
        float f1 = (float)(location1.getY() - location2.getY());
        float f2 = (float)(location1.getZ() - location2.getZ());
        return Mth.sqrt(f * f + f1 * f1 + f2 * f2);
    }
}
