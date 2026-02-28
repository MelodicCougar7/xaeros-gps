package org.github.melodiccougar7.xaeros_gps.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.github.melodiccougar7.xaeros_gps.XaerosGPS;

//TODO ADD UR CONFIGS
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = XaerosGPS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GPSConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	static final ForgeConfigSpec SPEC = BUILDER.build();

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
	}
}
