package org.github.melodiccougar7.xaeros_gps.controls;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.checkerframework.checker.units.qual.C;
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
                if (ClientGPSState.gpsEnabled) {
                    Minecraft.getInstance().player.displayClientMessage(Component.translatable("client.xaeros_gps.gps_on"), true);
                } else {
                    Minecraft.getInstance().player.displayClientMessage(Component.translatable("client.xaeros_gps.gps_off"), true);
                }

            }
        }
    }

    @SubscribeEvent
    public static void onScreenClosing(net.minecraftforge.client.event.ScreenEvent.Closing event) {
        Screen screen = event.getScreen();
        if (screen.getClass().getName().equals("xaero.map.gui.GuiMap")) {
            XGPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(false));
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("client.xaeros_gps.gps_off"), false);
        }
    }
}