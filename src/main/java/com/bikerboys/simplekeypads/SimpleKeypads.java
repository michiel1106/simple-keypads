package com.bikerboys.simplekeypads;

import com.bikerboys.simplekeypads.entity.ModEntities;
import com.bikerboys.simplekeypads.entity.client.KeypadRenderer;
import com.bikerboys.simplekeypads.entity.custom.KeypadEntity;
import com.bikerboys.simplekeypads.item.KeypadItem;
import com.bikerboys.simplekeypads.networking.NetworkHandler;
import com.bikerboys.simplekeypads.networking.UpdateAllowedBlockPosS2C;
import com.bikerboys.simplekeypads.util.KeypadContext;
import com.mojang.authlib.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.*;
import net.minecraftforge.fml.loading.moddiscovery.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SimpleKeypads.MODID)
public class SimpleKeypads
{

    public static boolean lootrInstalled = false;

    public static List<KeypadContext> allowedplayercontext = new ArrayList<>();

    public static final String MODID = "simplekeypads";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);


    public static final RegistryObject<Item> KEYPAD_ITEM = ITEMS.register("keypad",
            () -> new KeypadItem(new Item.Properties()));

    public SimpleKeypads(FMLJavaModLoadingContext context)
    {

        IEventBus modEventBus = context.getModEventBus();
        ITEMS.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);

        ModFileInfo lootr = FMLLoader.getLoadingModList().getModFileById("lootr");

        if (lootr != null) {
            lootrInstalled = true;
        }


    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

        event.enqueueWork(() -> {

            NetworkHandler.register();


        });
    }


    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(KEYPAD_ITEM);
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


        ;
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent tickEvent) {



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
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.KEYPAD.get(), KeypadRenderer::new);
        }
    }
}
