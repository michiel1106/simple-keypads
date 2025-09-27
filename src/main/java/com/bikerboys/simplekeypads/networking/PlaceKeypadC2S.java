package com.bikerboys.simplekeypads.networking;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.ModEntities;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlaceKeypadC2S {

    private final BlockPos clickedPos;
    private final int face; // Direction 3D data value
    private final CompoundTag tag; // optional item NBT

    public PlaceKeypadC2S(BlockPos clickedPos, int face, CompoundTag tag) {
        this.clickedPos = clickedPos;
        this.face = face;
        this.tag = tag;
    }

    public PlaceKeypadC2S(FriendlyByteBuf buf) {
        this.clickedPos = buf.readBlockPos();
        this.face = buf.readByte();
        this.tag = buf.readNbt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(clickedPos);
        buf.writeByte(face);
        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            ServerLevel level = player.serverLevel();

            var dir = net.minecraft.core.Direction.from3DDataValue(face);
            BlockPos placePos = clickedPos.relative(dir);

            if (dir.getAxis().isVertical()) return;

            ItemStack held = player.getMainHandItem();
            if (held.isEmpty() || held.getItem() != SimpleKeypads.KEYPAD_ITEM.get()) return;

            KeypadEntity entity = new KeypadEntity(ModEntities.KEYPAD.get(), level, placePos, dir, dir);
            if (tag != null) EntityType.updateCustomEntityTag(level, player, entity, tag);

            if (entity.survives()) {
                entity.playPlacementSound();
                level.gameEvent(player, GameEvent.ENTITY_PLACE, entity.position());
                held.shrink(1);
                level.addFreshEntity(entity);
            }
        });
        context.setPacketHandled(true);
    }
}
