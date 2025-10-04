package com.bikerboys.simplekeypads.item;


import com.bikerboys.simplekeypads.networking.NetworkHandler;
import com.bikerboys.simplekeypads.networking.PlaceKeypadC2S;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@SuppressWarnings("ConstantValue")
public class KeypadItem extends Item {


    public KeypadItem(Properties properties) {
        super(properties);
    }



    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        Direction face = context.getClickedFace();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide()) {
            CompoundTag tag = new CompoundTag();
            NetworkHandler.sendToServer(new PlaceKeypadC2S(clicked, face.get3DDataValue(), tag));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }



    protected boolean mayPlace(Player p_41326_, Direction p_41327_, ItemStack p_41328_, BlockPos p_41329_) {
        return !p_41327_.getAxis().isVertical() && p_41326_.mayUseItemAt(p_41329_, p_41327_, p_41328_);
    }
}
