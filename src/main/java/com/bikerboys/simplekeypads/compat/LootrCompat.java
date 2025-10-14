package com.bikerboys.simplekeypads.compat;

import com.bikerboys.simplekeypads.*;
import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import noobanidus.mods.lootr.*;
import noobanidus.mods.lootr.block.*;

public class LootrCompat {



    public static boolean isLootrChest(BlockPos pos, Level world) {
        if (SimpleKeypads.lootrInstalled) {
            Block block = world.getBlockState(pos).getBlock();

            if (block instanceof LootrChestBlock || block instanceof LootrBarrelBlock || block instanceof LootrShulkerBlock || block instanceof LootrTrappedChestBlock) {
                return true;
            }
        }
        return false;
    }


}
