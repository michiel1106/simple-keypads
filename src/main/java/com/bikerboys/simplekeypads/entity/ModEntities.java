package com.bikerboys.simplekeypads.entity;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import net.minecraft.core.registries.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.*;
import net.neoforged.neoforge.registries.*;


public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SimpleKeypads.MODID);


    public static final DeferredHolder<EntityType<?>, EntityType<KeypadEntity>> KEYPAD =
            ENTITY_TYPES.register("keypad", () ->
                    EntityType.Builder.<KeypadEntity>of(KeypadEntity::new,
                                    MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build(SimpleKeypads.MODID + ":keypad"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


}
