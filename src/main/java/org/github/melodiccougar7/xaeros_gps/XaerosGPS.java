package org.github.melodiccougar7.xaeros_gps;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.github.melodiccougar7.xaeros_gps.controls.ClientEvents;
import org.github.melodiccougar7.xaeros_gps.network.XGPSNetwork;
import org.github.melodiccougar7.xaeros_gps.registry.ItemRegistry;
import org.slf4j.Logger;

import static org.github.melodiccougar7.xaeros_gps.registry.ItemRegistry.GPS;

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
        ItemRegistry.ITEMS.register(modEventBus);
        ItemRegistry.CREATIVE_TABS.register(modEventBus);
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