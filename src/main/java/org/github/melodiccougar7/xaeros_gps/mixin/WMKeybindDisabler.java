package org.github.melodiccougar7.xaeros_gps.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.github.melodiccougar7.xaeros_gps.controls.ClientGPSState;
import org.github.melodiccougar7.xaeros_gps.items.GPSItem;
import org.github.melodiccougar7.xaeros_gps.network.GPSTogglePacket;
import org.github.melodiccougar7.xaeros_gps.network.XGPSNetwork;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.controls.ControlsHandler;
import xaero.map.controls.ControlsRegister;

@Mixin(value = ControlsHandler.class, remap = false)
public abstract class WMKeybindDisabler {

    @Inject (
        method = "keyDown(Lnet/minecraft/client/KeyMapping;ZZ)V",
        at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V",
        ordinal = 0),
        cancellable = true
    )
    private void injectBeforeSetScreen(KeyMapping kb, boolean tickEnd, boolean isRepeat, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        final ItemStack gps = GPSItem.getGpsItem(player);

        if (kb == ControlsRegister.keyOpenMap) {
            if (gps == null) {
                ci.cancel();
                if (player != null) {
                    player.displayClientMessage(Component.translatable("gui.xaero_no_worldmap_no_gps_message"), true);
                }
                return;
            }
// Deprecated in the interest of making things easier for players
//            if (!ClientGPSState.gpsEnabled) {
//                ci.cancel();
//                if (player != null) {
//                    player.displayClientMessage(Component.translatable("gui.xaero_no_worldmap_gps_off_message"), true);
//                }
//                return;
//            }
            // automatically turn on the GPS
            if (!ClientGPSState.gpsEnabled) {
                if (player != null) {
                    ClientGPSState.gpsEnabled = true;
                }
                XGPSNetwork.CHANNEL.sendToServer(
                        new GPSTogglePacket(ClientGPSState.gpsEnabled)
                );
                return;
            }
            if (GPSItem.isGPSOutOfEnergy(gps)) {
                ci.cancel();
                player.displayClientMessage(Component.translatable("gui.xaero_no_worldmap_gps_out_of_energy_message"), true);
            }
        }
    }
}
