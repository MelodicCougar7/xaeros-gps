package org.github.melodiccougar7.xaeros_gps;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.github.melodiccougar7.xaeros_gps.client.ClientEvents;
import org.github.melodiccougar7.xaeros_gps.client.tooltip.BatteryTooltipComponent;
import org.github.melodiccougar7.xaeros_gps.client.tooltip.ClientBatteryTooltipComponent;
import org.github.melodiccougar7.xaeros_gps.common.items.ItemGPS;
import org.github.melodiccougar7.xaeros_gps.common.network.GPSNetwork;
import org.github.melodiccougar7.xaeros_gps.common.registry.GPSItems;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file

@SuppressWarnings({"removal","unused"})
@Mod(XaerosGPS.MODID)
public class XaerosGPS {

	public static final String MODID = "xaeros_gps";
	private static final Logger LOGGER = LogUtils.getLogger();

	public XaerosGPS() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.register(ClientEvents.class);
		GPSItems.ITEMS.register(modEventBus);
		GPSItems.CREATIVE_TABS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(GPSNetwork::register);
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {

		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			event.enqueueWork(() -> {
				ItemProperties.register(GPSItems.GPS.get(),
						new ResourceLocation(XaerosGPS.MODID, ItemGPS.GPS_ON),
						(stack, level, entity, seed) -> {
							if (stack.getItem() instanceof ItemGPS gps) {
								return (gps.isGPSOn(stack) && !gps.isOutOfEnergy(stack)) ? 1.0f : 0.0f;
							}
							return 0.0f;
						});
			});
		}

		@SubscribeEvent
		public static void onRegisterTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
			event.register(BatteryTooltipComponent.class, ClientBatteryTooltipComponent::new);
		}

	}
}