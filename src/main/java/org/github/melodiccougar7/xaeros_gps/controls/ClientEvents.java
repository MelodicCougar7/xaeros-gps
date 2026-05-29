package org.github.melodiccougar7.xaeros_gps.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.github.melodiccougar7.xaeros_gps.XaerosGPS;
import org.github.melodiccougar7.xaeros_gps.client.tooltip.BatteryTooltipComponent;
import org.github.melodiccougar7.xaeros_gps.client.tooltip.ClientBatteryTooltipComponent;
import org.github.melodiccougar7.xaeros_gps.network.GPSTogglePacket;
import org.github.melodiccougar7.xaeros_gps.network.XGPSNetwork;
import xaero.map.gui.GuiMap;

public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (XGPSKeybinds.TOGGLE_GPS.consumeClick()) {
// Deprecated to make things simpler for the player
//                if (Minecraft.getInstance().screen == null) {
//                    XGPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(true));
//                } else {
//                    XGPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(false));
//                }
                ClientGPSState.gpsEnabled = !ClientGPSState.gpsEnabled;
                XGPSNetwork.CHANNEL.sendToServer(
                        new GPSTogglePacket(ClientGPSState.gpsEnabled)
                );

                ClientLevel level = Minecraft.getInstance().level;
                Player player = Minecraft.getInstance().player;
                level.playLocalSound(
                        new BlockPos(
                        (int) player.getX(),
                        (int) player.getY(),
                        (int) player.getZ()
                        ),
                        SoundEvents.UI_BUTTON_CLICK.get(),
                        SoundSource.PLAYERS,
                        0.35f,
                        !ClientGPSState.gpsEnabled ? 1.2f : 0.8f,
                        false
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
    public static void onScreenClosing(ScreenEvent.Closing event) {
        Screen screen = event.getScreen();
        if (GuiMap.class.isInstance(screen)) {
            XGPSNetwork.CHANNEL.sendToServer(new GPSTogglePacket(false));
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("client.xaeros_gps.gps_off"), true);
        }
    }
}