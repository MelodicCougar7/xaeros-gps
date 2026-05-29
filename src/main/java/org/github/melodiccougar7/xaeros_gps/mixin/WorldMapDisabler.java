package org.github.melodiccougar7.xaeros_gps.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import xaero.map.gui.GuiMap;

@Mixin(value = GuiMap.class, remap = false)
public abstract class WorldMapDisabler {
    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
        final ItemStack[] GPS = new ItemStack[1];
        curiosInventory.getStacksHandler("gps").ifPresent(slotInventory -> {
            var stacks = slotInventory.getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack stack = stacks.getStackInSlot(i);
                if (stack.getItem() == ForgeRegistries.ITEMS.getValue(ResourceLocation.parse("xaeros_gps:gps"))) {
                    GPS[0] = stack;
                }
            }
        });

        if (GPS[0] == null || GPS[0].isEmpty()) {
            ci.cancel();
            renderMessageScreenInvoker invoker = (renderMessageScreenInvoker)(Object)this;
            invoker.invokeRenderMessageScreen(guiGraphics, I18n.get("gui.xaero_no_worldmap_no_gps_message", new Object[0]));
        }
    }
}
