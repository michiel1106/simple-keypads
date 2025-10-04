package com.bikerboys.simplekeypads.events;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.client.KeypadModel;
import com.bikerboys.simplekeypads.entity.client.ModModelLayers;

import net.neoforged.api.distmarker.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.*;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid = SimpleKeypads.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @net.neoforged.bus.api.SubscribeEvent
    public static void registerLayer(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.KEYPAD_LAYER, KeypadModel::createBodyLayer);
    }
}
