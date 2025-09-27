package com.bikerboys.simplekeypads.entity;

import com.bikerboys.simplekeypads.SimpleKeypads;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SimpleKeypads.MODID);


    public static final RegistryObject<EntityType<KeypadEntity>> KEYPAD =
            ENTITY_TYPES.register("keypad", () ->
                    EntityType.Builder.<KeypadEntity>of(KeypadEntity::new,
                                    MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build(SimpleKeypads.MODID + ":keypad"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


}
