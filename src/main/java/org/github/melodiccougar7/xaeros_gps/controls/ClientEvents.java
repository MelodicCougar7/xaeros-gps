package org.github.melodiccougar7.xaeros_gps.controls;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.github.melodiccougar7.xaeros_gps.network.GPSTogglePacket;
import org.github.melodiccougar7.xaeros_gps.network.XGPSNetwork;
import org.slf4j.Logger;
import xaero.map.gui.GuiMap;

public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (XGPSKeybinds.TOGGLE_GPS.consumeClick()) {
//                if (Minecraft.getInstance().screen == null) {
//                    XGPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(true));
//                } else {
//                    XGPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(false));
//                }
                ClientGPSState.gpsEnabled = !ClientGPSState.gpsEnabled;
                XGPSNetwork.CHANNEL.sendToServer(
                        new GPSTogglePacket(ClientGPSState.gpsEnabled)
                );

            }
        }
    }

    @SubscribeEvent
    public static void onScreenClosing(net.minecraftforge.client.event.ScreenEvent.Closing event) {
        Screen screen = event.getScreen();

        if (screen.getClass().getName().equals("xaero.map.gui.GuiMap")) {
            XGPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(false));
        }
    }


}
