package org.github.melodiccougar7.xaeros_gps.items;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nullable;
import java.util.List;

import static org.github.melodiccougar7.xaeros_gps.registry.ItemRegistry.BATTERY;

public class GPSItem extends Item implements ICurioItem {

    public GPSItem() {
        super(new Item.Properties().stacksTo(1).defaultDurability(0));
    }

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    // technically, I don't believe this is still needed
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.getItem().equals(newStack.getItem());
    }

    public static boolean isGPSOn(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("on")) {
            return stack.getTag().getBoolean("on");
        }
        // return false if no tag is present (create tag on item creation)
        return false;
    }
    public static boolean isGPSOutOfEnergy(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("energy")) {
            int energy = stack.getTag().getInt("energy");
            return energy == 0;
        }
        // return false if no energy tag is present (create tag on item creation)
        return false;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().getGameTime() % 20 == 0) {
            // failsafe to ensure that tag is created at some point
            if (!stack.hasTag()) {
                stack.getOrCreateTag().putInt("energy", 0);
                stack.getOrCreateTag().putBoolean("on", false);
            }
            boolean active = stack.getTag().getBoolean("on");
            if (active) {
                int energy = stack.getTag().getInt("energy");
                CompoundTag tag = stack.getOrCreateTag();
                // ensure that this doesn't drop below zero
                tag.putInt("energy", energy - 1);
            }
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack inc, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (inc.is(BATTERY.get())) {
            inc.shrink(1);
            CompoundTag tag = stack.getOrCreateTag();
            tag.putInt("energy", 1000);
            return true;
        }
        return false;
    }

    public static void toggleGPS(ServerPlayer player, boolean active) {
        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
        final ItemStack[] GPS = new ItemStack[1];
        curiosInventory.getStacksHandler("gps").ifPresent(slotInventory -> {
            //isGPSPresent.set(true); // won't ever set isGPSPresent to false, we'll see how this works out
            var stacks = slotInventory.getStacks();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack stack = stacks.getStackInSlot(i);
                if (stack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation("xaeros_gps:gps"))) {
                    GPS[0] = stack;
                }
            }
        });
// Deprecated to make things easier for the player, and also to show the "GPS out of battery message ever"
//        // cut off toggle if the GPS is out of power
//        if (GPS[0].getTag().getInt("energy") == 0) {
//            CompoundTag tag = GPS[0].getOrCreateTag();
//            tag.putBoolean("on", false);
//            return;
//        }

        if (!active) { // active technically means "activate" I suppose
            CompoundTag tag = GPS[0].getOrCreateTag();
            tag.putBoolean("on", false);
        }
        else {
            CompoundTag tag = GPS[0].getOrCreateTag();
            tag.putBoolean("on", true);
        }
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltipComponents,
            TooltipFlag isAdvanced
    ) {
        CompoundTag tag = stack.getTag();
        if(tag != null && tag.contains("energy")) {
            int energy = tag.getInt("energy");
            tooltipComponents.add(
                    Component.translatable("tooltip.xaeros_gps.charge")
                            .withStyle(ChatFormatting.YELLOW)
                            .append(energy + " / 1000")
            );

            if(Screen.hasShiftDown()) {
                tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.gps1")
                        .withStyle(ChatFormatting.WHITE)
                );
                tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.gps2")
                        .withStyle(ChatFormatting.WHITE)
                );
            } else {
                tooltipComponents.add(
                        Component.translatable("tooltip.xaeros_gps.shift")
                                .withStyle(ChatFormatting.GRAY)
                );
            }
        }
    }
}
