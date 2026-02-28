package org.github.melodiccougar7.xaeros_gps.client.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

// TODO REFACTOR INTO ITEMSTACK ARRAY
public record BatteryTooltipComponent(ItemStack battery) implements TooltipComponent {
}