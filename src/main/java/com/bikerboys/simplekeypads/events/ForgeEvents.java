package com.bikerboys.simplekeypads.events;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import com.bikerboys.simplekeypads.util.KeypadContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.level.PistonEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SimpleKeypads.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {


    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;

        BlockPos pos = event.getPos();

        BlockState blockState = event.getLevel().getBlockState(pos);

        if (blockState.getBlock() instanceof DoorBlock) {
            if (blockState.getValue(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER)) {


                KeypadEntity keypadOnBlock = SimpleKeypads.getKeypadOnBlock(event.getLevel(), pos.below());

                if (keypadOnBlock != null) {
                    boolean allowed = false;

                    for (KeypadContext context : SimpleKeypads.allowedplayercontext) {
                        if (context.player.getUUID().equals(event.getEntity().getUUID())
                                && context.pos.equals(pos.below())) {
                            allowed = true;
                            break;
                        }
                    }
                    if (event.getEntity() instanceof ServerPlayer player) {
                        if (player.hasPermissions(2)) {
                            allowed = true;
                        }

                    }

                    if (!allowed) {
                        event.setCanceled(true);
                    }
                }


            } else if (blockState.getValue(DoorBlock.HALF).equals(DoubleBlockHalf.LOWER)) {
                KeypadEntity keypadOnBlock = SimpleKeypads.getKeypadOnBlock(event.getLevel(), pos.above());

                if (keypadOnBlock != null) {
                    boolean allowed = false;

                    for (KeypadContext context : SimpleKeypads.allowedplayercontext) {
                        if (context.player.getUUID().equals(event.getEntity().getUUID())
                                && context.pos.equals(pos.above())) {
                            allowed = true;
                            break;
                        }
                    }

                    if (event.getEntity() instanceof ServerPlayer player) {
                        if (player.hasPermissions(2)) {
                            allowed = true;
                        }

                    }

                    if (!allowed) {
                        event.setCanceled(true);
                    }
                }

            }


        }


        KeypadEntity keypadOnBlock = SimpleKeypads.getKeypadOnBlock(event.getLevel(), pos);

        if (keypadOnBlock != null) {
            boolean allowed = false;

            for (KeypadContext context : SimpleKeypads.allowedplayercontext) {
                if (context.player.getUUID().equals(event.getEntity().getUUID())
                        && context.pos.equals(pos)) {
                    allowed = true;
                    break;
                }
            }
            if (event.getEntity() instanceof ServerPlayer player) {
                if (player.hasPermissions(2)) {
                    allowed = true;
                }

            }

            if (!allowed) {

                event.setCanceled(true);
            }
        }
    }






}
