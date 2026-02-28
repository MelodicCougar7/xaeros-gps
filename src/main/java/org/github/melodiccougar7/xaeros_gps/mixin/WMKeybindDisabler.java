package org.github.melodiccougar7.xaeros_gps.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.github.melodiccougar7.xaeros_gps.common.items.ItemGPS;
import org.github.melodiccougar7.xaeros_gps.utils.GPSUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.controls.ControlsHandler;
import xaero.map.controls.ControlsRegister;

@Mixin(value = ControlsHandler.class, remap = false)
public abstract class WMKeybindDisabler {

	@Inject(method = "keyDown(Lnet/minecraft/client/KeyMapping;ZZ)V", at = @At(value = "INVOKE", target = "Lnet" +
            "/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", ordinal = 0),
            cancellable = true)
	private void injectBeforeSetScreen(KeyMapping kb, boolean tickEnd, boolean isRepeat, CallbackInfo ci) {
		if (kb == ControlsRegister.keyOpenMap) {
			Player player = Minecraft.getInstance().player;
			ItemStack gpsStack = GPSUtil.getGpsItem(player); // you don't need static methods, 1. get item
			if (gpsStack.isEmpty()) {
				ci.cancel();
				if (player != null) {
					player.displayClientMessage(Component.translatable("gui.xaero_no_worldmap_no_gps_message"), true);
				}
				return;
			}
			if (gpsStack.getItem() instanceof ItemGPS itemGPS) { // 2. cast it
				if (!itemGPS.isGPSOn(gpsStack)) {
					ci.cancel();
					if (player != null) {
						player.displayClientMessage(Component.translatable("gui.xaero_no_worldmap_gps_off_message"),
								true);
					}
					return;
				}
				if (itemGPS.isOutOfEnergy(gpsStack)) {
					ci.cancel();
					if (player != null) {
						player.displayClientMessage(Component.translatable(
								"gui.xaero_no_worldmap_gps_out_of_energy_message"), true);
					}
				}
			}
		}
	}
}