package org.github.melodiccougar7.xaeros_gps;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.github.melodiccougar7.xaeros_gps.config.Config;
import org.github.melodiccougar7.xaeros_gps.controls.ClientEvents;
import org.github.melodiccougar7.xaeros_gps.items.GPSItem;
import org.github.melodiccougar7.xaeros_gps.network.XGPSNetwork;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.SlotContext;

import static org.github.melodiccougar7.xaeros_gps.registry.ItemRegistry.GPS;
import static org.github.melodiccougar7.xaeros_gps.registry.ItemRegistry.ITEMS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(XaerosGPS.MODID)
public class XaerosGPS {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "xaeros_gps";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    public XaerosGPS() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(XGPSNetwork::register);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // credit to ActiveRadar
            ItemProperties.register(GPS.get(),
                    new ResourceLocation(XaerosGPS.MODID, "on"),
                    (stack, level, entity, seed) -> {
                        if (entity == null) {
                            return 0.0f;
                        }
                        if (stack.hasTag()) {
                            return stack.getTag().getBoolean("on") ? 1.0f : 0.0f;
                        }
                        return 0.0f;
                    });
        }
    }
}