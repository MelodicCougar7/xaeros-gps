package org.github.melodiccougar7.xaeros_gps.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.github.melodiccougar7.xaeros_gps.client.controls.GPSKeybinds;
import org.github.melodiccougar7.xaeros_gps.common.items.ItemGPS;
import org.github.melodiccougar7.xaeros_gps.common.network.GPSNetwork;
import org.github.melodiccougar7.xaeros_gps.common.network.GPSTogglePacket;
import org.github.melodiccougar7.xaeros_gps.utils.GPSUtil;
import xaero.map.gui.GuiMap;

public class ClientEvents {

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		Minecraft mc = Minecraft.getInstance();
		while (GPSKeybinds.TOGGLE_GPS.consumeClick()) {
			ItemStack gps = GPSUtil.getGpsItem(mc.player);
			if (!gps.isEmpty() && gps.getItem() instanceof ItemGPS itemGPS) {
				boolean newState = !itemGPS.isGPSOn(gps);
				GPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(newState));
			}
		}
	}

	@SubscribeEvent
	public static void onScreenClosing(ScreenEvent.Closing event) {
		Screen screen = event.getScreen();
		if (GuiMap.class.isInstance(screen)) { // just because Xaero's lib is bundled we can't just do instanceof
			GPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(false));
		}
	}


}