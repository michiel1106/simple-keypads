package com.bikerboys.simplekeypads.networking;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.ModEntities;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlaceKeypadC2S(BlockPos clickedPos, int face, CompoundTag tag) implements CustomPacketPayload {

    public static final Type<PlaceKeypadC2S> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleKeypads.MODID, "place_keypad_c2s"));

    public static final StreamCodec<FriendlyByteBuf, PlaceKeypadC2S> STREAM_CODEC =
            StreamCodec.of(PlaceKeypadC2S::encode, PlaceKeypadC2S::decode);

    private static PlaceKeypadC2S decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int face = buf.readByte();
        CompoundTag tag = buf.readNbt();
        return new PlaceKeypadC2S(pos, face, tag);
    }

    private static void encode(FriendlyByteBuf buf, PlaceKeypadC2S msg) {
        buf.writeBlockPos(msg.clickedPos);
        buf.writeByte(msg.face);
        buf.writeNbt(msg.tag);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PlaceKeypadC2S msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player == null) return;

            ServerLevel level = player.serverLevel();
            Direction dir = Direction.from3DDataValue(msg.face);
            BlockPos placePos = msg.clickedPos.relative(dir);

            if (dir.getAxis().isVertical()) return;

            ItemStack held = player.getMainHandItem();
            if (held.isEmpty() || held.getItem() != SimpleKeypads.KEYPAD_ITEM.get()) return;

            KeypadEntity entity = new KeypadEntity(ModEntities.KEYPAD.get(), level, placePos, dir, dir);
            if (msg.tag != null) EntityType.updateCustomEntityTag(level, player, entity, CustomData.of(msg.tag));

            if (entity.survives()) {
                entity.playPlacementSound();
                level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
                held.shrink(1);
                level.addFreshEntity(entity);
            }
        });
    }
}
