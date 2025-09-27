package com.bikerboys.simplekeypads.networking;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.client.ClientPacketHandler;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateAllowedBlockPosS2C {

    private final BlockPos blockPos;
    private final boolean allowed;
    private final Direction face;



    public UpdateAllowedBlockPosS2C(BlockPos blockPos, Boolean allowed, Direction face) {
        this.allowed = allowed;
        this.blockPos = blockPos;
        this.face = face;
    }

    public UpdateAllowedBlockPosS2C(FriendlyByteBuf buf) {

        this(buf.readBlockPos(), buf.readBoolean(), buf.readEnum(Direction.class));

    }

    public void encode(FriendlyByteBuf buf) {

        buf.writeBlockPos(this.blockPos);
        buf.writeBoolean(this.allowed);
        buf.writeEnum(this.face);

    }


    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {

        contextSupplier.get().enqueueWork(() -> {

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {

                if (this.allowed) {
                    if (!ClientPacketHandler.allowedblockpos.containsKey(blockPos)) {
                        ClientPacketHandler.allowedblockpos.put(blockPos, face);
                    }
                }

                if (!this.allowed) {
                    ClientPacketHandler.allowedblockpos.remove(blockPos);
                }



            });
        });


    }
}
