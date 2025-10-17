package com.bikerboys.simplekeypads;

import com.bikerboys.simplekeypads.entity.ModEntities;
import com.bikerboys.simplekeypads.entity.client.KeypadRenderer;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import com.bikerboys.simplekeypads.item.KeypadItem;
import com.bikerboys.simplekeypads.networking.NetworkHandler;
import com.bikerboys.simplekeypads.networking.UpdateAllowedBlockPosS2C;
import com.bikerboys.simplekeypads.util.KeypadContext;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import net.neoforged.api.distmarker.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.*;
import net.neoforged.fml.common.*;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.fml.loading.*;
import net.neoforged.fml.loading.moddiscovery.*;
import net.neoforged.neoforge.common.*;
import net.neoforged.neoforge.event.*;
import net.neoforged.neoforge.event.server.*;
import net.neoforged.neoforge.event.tick.*;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SimpleKeypads.MODID)
public class SimpleKeypads
{

    public static boolean lootrInstalled = false;

    public static List<KeypadContext> allowedplayercontext = new ArrayList<>();

    public static final String MODID = "simplekeypads";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);


    public static final DeferredHolder<Item, KeypadItem> KEYPAD_ITEM = ITEMS.register("keypad",
            () -> new KeypadItem(new Item.Properties()));

    public SimpleKeypads(IEventBus modEventBus, ModContainer container)
    {


        ITEMS.register(modEventBus);
        ModEntities.register(modEventBus);
        modEventBus.addListener(NetworkHandler::register);
        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(this::addCreative);

        NeoForge.EVENT_BUS.register(this);

        ModFileInfo lootr = FMLLoader.getLoadingModList().getModFileById("lootr");





        if (lootr != null) {
            lootrInstalled = true;
        }


    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }


    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(KEYPAD_ITEM.get());
        }

    }


    public static KeypadEntity getKeypadOnBlock(Level level, BlockPos blockPos, Direction face) {
        // Make a tiny bounding box around the block center
        AABB box = new AABB(blockPos).inflate(0.05);
        for (KeypadEntity keypad : level.getEntitiesOfClass(KeypadEntity.class, box)) {
            // Check if this keypad is attached to this block
            if (keypad.isAttachedToBlock(blockPos)) {
                if (keypad.getAttachedFace().equals(face)) {
                    return keypad;
                }
            }
        }

        return null;
    }

    public static KeypadEntity getKeypadOnBlockFace(Level level, BlockPos pos, Direction face) {
        AABB box = new AABB(pos).inflate(0.1);
        for (KeypadEntity entity : level.getEntitiesOfClass(KeypadEntity.class, box)) {
            if (entity.isAttachedToBlockFace(pos, face)) {
                return entity;
            }
        }
        return null;
    }

    public static KeypadEntity getKeypadOnPos(Level level, BlockPos blockPos) {
        // Make a tiny bounding box around the block center
        AABB box = new AABB(blockPos).inflate(0.05);
        for (KeypadEntity keypad : level.getEntitiesOfClass(KeypadEntity.class, box)) {
            // Check if this keypad is attached to this block
            if (keypad.isAttachedToBlock(blockPos)) {
                return keypad;
            }
        }
        return null;
    }

    public static KeypadEntity getKeypadOnBlock(LevelAccessor level, BlockPos blockPos) {
        // Make a tiny bounding box around the block center
        AABB box = new AABB(blockPos).inflate(0.05);
        for (KeypadEntity keypad : level.getEntitiesOfClass(KeypadEntity.class, box)) {
            // Check if this keypad is attached to this block
            if (keypad.isAttachedToBlock(blockPos)) {
                return keypad;
            }
        }
        return null;
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent.Post tickEvent) {


        Iterator<KeypadContext> iterator = allowedplayercontext.iterator();

        while (iterator.hasNext()) {
            KeypadContext context = iterator.next();

            if (context.initialtime <= 0) {
                iterator.remove();

                BlockPos pos = context.pos;
                Player player = context.player;

                if (player instanceof ServerPlayer player1) {
                    NetworkHandler.sendToPlayer(new UpdateAllowedBlockPosS2C(pos, true, context.face), player1);
                }


                continue;
            }

            context.initialtime -= 1;




        }

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.KEYPAD.get(), KeypadRenderer::new);
        }
    }
}
