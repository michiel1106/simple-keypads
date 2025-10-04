package com.bikerboys.simplekeypads.networking;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.client.ClientPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@OnlyIn(Dist.CLIENT)
public record UpdateAllowedBlockPosS2C(BlockPos blockPos, boolean allowed, Direction face) implements CustomPacketPayload {

    public static final Type<UpdateAllowedBlockPosS2C> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleKeypads.MODID, "update_allowed_blockpos_s2c"));

    public static final StreamCodec<FriendlyByteBuf, UpdateAllowedBlockPosS2C> STREAM_CODEC =
            StreamCodec.of(UpdateAllowedBlockPosS2C::encode, UpdateAllowedBlockPosS2C::decode);

    private static UpdateAllowedBlockPosS2C decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        boolean allowed = buf.readBoolean();
        Direction face = buf.readEnum(Direction.class);
        return new UpdateAllowedBlockPosS2C(pos, allowed, face);
    }

    private static void encode(FriendlyByteBuf buf, UpdateAllowedBlockPosS2C msg) {
        buf.writeBlockPos(msg.blockPos);
        buf.writeBoolean(msg.allowed);
        buf.writeEnum(msg.face);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateAllowedBlockPosS2C msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Make sure this only runs on the client
            if (context.player().level().isClientSide) {
                if (msg.allowed) {
                    if (!ClientPacketHandler.allowedblockpos.containsKey(msg.blockPos)) {
                        ClientPacketHandler.allowedblockpos.put(msg.blockPos, msg.face);
                    }
                } else {
                    ClientPacketHandler.allowedblockpos.remove(msg.blockPos);
                }
            }
        });
    }
}
