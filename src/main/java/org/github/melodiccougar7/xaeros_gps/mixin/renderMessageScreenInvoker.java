package org.github.melodiccougar7.xaeros_gps.mixin;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import xaero.map.gui.GuiMap;

@Mixin(value = GuiMap.class, remap = false)
public interface renderMessageScreenInvoker {

	@Invoker("renderMessageScreen")
	void invokeRenderMessageScreen(GuiGraphics guiGraphics, String message);
	//@Invoker("renderMessageScreen")
	//void renderMessageScreen(GuiGraphics guiGraphics, String message, String message2);

}
