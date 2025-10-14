package com.bikerboys.simplekeypads.client;


import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SimpleKeypads.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class SimplekeypadsClient {


    @SubscribeEvent
    public static void interactEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide) {

            Entity target = event.getTarget();
            if (target instanceof KeypadEntity keypad) {


                Minecraft instance = Minecraft.getInstance();
                if (instance != null) {
                    System.out.println(keypad.getAttachedFace());
                    System.out.println(keypad.getOnPos());

                    instance.setScreen(new PasscodeScreen(Component.literal("Input passcode"), keypad.getOnPos(), keypad.getAttachedFace()));
                }


            }
        }

    }


    @SubscribeEvent
    public static void interactBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) {
            if (Minecraft.getInstance().player.getMainHandItem().is(SimpleKeypads.KEYPAD_ITEM.get())) {
                event.setUseBlock(Event.Result.DENY);
                Minecraft.getInstance().setScreen(new SetPasscodeScreen(Component.literal("Set passcode"), event.getPos(), event.getFace()));


            }

        }
    }







}
