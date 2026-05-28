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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import xaero.map.controls.ControlsHandler;
import xaero.map.controls.ControlsRegister;

import static org.github.melodiccougar7.xaeros_gps.registry.ItemRegistry.GPS;

@Mixin(value = ControlsHandler.class, remap = false) // the class that contains keyDown
public abstract class WMKeybindDisabler {

    @Inject(
            method = "keyDown(Lnet/minecraft/client/KeyMapping;ZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;m_91152_(Lnet/minecraft/client/gui/screens/Screen;)V",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void injectBeforeSetScreen(KeyMapping kb, boolean tickEnd, boolean isRepeat, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
//        AtomicBoolean isGPSPresent = new AtomicBoolean(false); //
//        isGPSPresent.set(player.getInventory().items.contains("xaeros_gps:gps"));
        final ItemStack[] gps = new ItemStack[1];

        curiosInventory.getStacksHandler("gps").ifPresent(slotInventory -> {
            //isGPSPresent.set(true); // won't ever set isGPSPresent to false, we'll see how this works out
            var stacks = slotInventory.getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack stack = stacks.getStackInSlot(i);
                if (stack.getItem() == GPS.get()) {
                    gps[0] = stack;
                }
            }
        });
        if (kb == ControlsRegister.keyOpenMap) {
            if (gps[0] == null || gps[0].isEmpty()) {
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
            if (GPSItem.isGPSOutOfEnergy(gps[0])) {
                ci.cancel();
                player.displayClientMessage(Component.translatable("gui.xaero_no_worldmap_gps_out_of_energy_message"), true);
            }
        }
    }
}
