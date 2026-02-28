package org.github.melodiccougar7.xaeros_gps.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.github.melodiccougar7.xaeros_gps.utils.GPSUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.gui.GuiMap;

@Mixin(value = GuiMap.class, remap = false)
public abstract class WorldMapDisabler {

	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"), cancellable = true)
	private void onRender(GuiGraphics guiGraphics,
			int scaledMouseX,
			int scaledMouseY,
			float partialTicks,
			CallbackInfo ci) {
		Player player = Minecraft.getInstance().player;
		ItemStack gpsStack = GPSUtil.getGpsItem(player);
		if (gpsStack.isEmpty()) {
			ci.cancel();
			renderMessageScreenInvoker invoker = (renderMessageScreenInvoker) this;
			invoker.invokeRenderMessageScreen(guiGraphics, I18n.get("gui.xaero_no_worldmap_no_gps_message"));
		}
	}
}