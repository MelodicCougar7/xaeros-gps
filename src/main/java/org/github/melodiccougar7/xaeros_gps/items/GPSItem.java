package org.github.melodiccougar7.xaeros_gps.items;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.github.melodiccougar7.xaeros_gps.registry.ItemRegistry;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import javax.annotation.Nullable;
import java.util.List;

import static org.github.melodiccougar7.xaeros_gps.registry.ItemRegistry.BATTERY;

public class GPSItem extends AbstractEnergyItem implements ICurioItem {

    public static final int MAX_ENERGY = 1000;
    public static final int MAX_TRANSFER = 100;

    public GPSItem() {
        super(new Properties().stacksTo(1), MAX_ENERGY, MAX_TRANSFER, true, false, 1000);
    }

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

//    @Override
//    public void curioTick(SlotContext slotContext, ItemStack stack) {
//        if (slotContext.entity().level().getGameTime() % 20 == 0) {
//            // failsafe to ensure that tag is created at some point
//            if (!stack.hasTag()) {
//                stack.getOrCreateTag().putInt("energy", 0);
//                stack.getOrCreateTag().putBoolean("on", false);
//            }
//            boolean active = stack.getTag().getBoolean("on");
//            if (active) {
//                int energy = stack.getTag().getInt("energy");
//                CompoundTag tag = stack.getOrCreateTag();
//                // ensure that this doesn't drop below zero
//                tag.putInt("energy", energy - 1);
//            }
//        }
//    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity().level().isClientSide())
            return;
        if (isGPSOn(stack)) {
            ItemStack battery = getInsertedBattery(stack);
            if (!battery.isEmpty() && battery.getItem() instanceof ItemBattery batteryItem) {
                if (slotContext.entity().tickCount % 20 == 0) {
                    int batteryEnergy = batteryItem.getEnergy(battery);
                    if (batteryEnergy > 0) {
                        batteryItem.setEnergy(battery, batteryEnergy - 1);
                        setInsertedBattery(stack, battery);
                    } else {
                        if (slotContext.entity() instanceof ServerPlayer player) {
                            setGPSOn(player, false);
                        }
                    }
                }
            } else {
                // Turn off when there's no power.
                if (slotContext.entity() instanceof ServerPlayer player) {
                    setGPSOn(player, false);
                }
            }
        }
    }

    //@Override
    // rework this entirely to consume batteries 100% if they aren't chargeable
    // or just to swap batteries out I guess
//    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack inc, Slot slot, ClickAction action, Player player, SlotAccess access) {
//        if (inc.is(BATTERY.get())) {
//            inc.shrink(1);
//            CompoundTag tag = stack.getOrCreateTag();
//            tag.putInt("energy", 1000);
//            return true;
//        }
//        return false;
//    }

    public static void setGPSOn(ServerPlayer player, boolean active) {
        ICuriosItemHandler curiosInventory = CuriosApi.getCuriosInventory(player).resolve().get();
        final ItemStack GPS = getGpsItem(player);
// Deprecated to make things easier for the player, and also to show the "GPS out of battery message ever"
//        // cut off toggle if the GPS is out of power
//        if (GPS.getTag().getInt("energy") == 0) {
//            CompoundTag tag = GPS.getOrCreateTag();
//            tag.putBoolean("on", false);
//            return;
//        }
            CompoundTag tag = GPS.getOrCreateTag();
            tag.putBoolean("on", active);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltipComponents,
            TooltipFlag isAdvanced
    ) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        boolean isShiftDown = false;
        if (level != null && level.isClientSide()) {
            isShiftDown = Screen.hasShiftDown();
        }
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("energy")) {
            if(isShiftDown) {
                tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.gps1").withStyle(ChatFormatting.WHITE));
                tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.gps2").withStyle(ChatFormatting.WHITE));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.shift").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static ItemStack getGpsItem(Player player) {
        if (player == null)
            return ItemStack.EMPTY;
        return CuriosApi.getCuriosInventory(player).resolve().map(curiosInventory -> {
            var optStacks = curiosInventory.getStacksHandler("gps_slot");
            if (optStacks.isPresent()) {
                var stacks = optStacks.get().getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getItem() == ItemRegistry.GPS.get()) {
                        return stack;
                    }
                }
            }
            return ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }
}
