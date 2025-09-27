package com.bikerboys.simplekeypads.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

public class KeypadContext {

    public Player player;
    public Integer initialtime;
    public BlockPos pos;
    public Direction face;

    public KeypadContext(Player player, int intitialtime, BlockPos pos, Direction face) {
        this.player = player;
        this.initialtime = intitialtime;
        this.pos = pos;
        this.face = face;
    }

}
