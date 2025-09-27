package com.bikerboys.simplekeypads.entity.client;

import com.bikerboys.simplekeypads.SimpleKeypads;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation KEYPAD_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(SimpleKeypads.MODID, "keypad"), "main");

}
