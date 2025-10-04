package com.bikerboys.simplekeypads.client;


import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.*;
import net.neoforged.neoforge.event.entity.player.*;


@EventBusSubscriber(modid = SimpleKeypads.MODID, bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
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
                Minecraft.getInstance().setScreen(new SetPasscodeScreen(Component.literal("Set passcode"), event.getPos(), event.getFace()));
            }

        }
    }







}
