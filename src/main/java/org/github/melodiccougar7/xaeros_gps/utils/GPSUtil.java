package org.github.melodiccougar7.xaeros_gps.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.github.melodiccougar7.xaeros_gps.common.registry.GPSItems;
import top.theillusivec4.curios.api.CuriosApi;

public class GPSUtil {

	public static final String GPS_SLOT = "gps_slot";

	// Pond this is nice easier for getting the item from Curios from a player.
	public static ItemStack getGpsItem(Player player) {
		if (player == null)
			return ItemStack.EMPTY;
		return CuriosApi.getCuriosInventory(player).resolve().map(curiosInventory -> {
			var optStacks = curiosInventory.getStacksHandler(GPS_SLOT);
			if (optStacks.isPresent()) {
				var stacks = optStacks.get().getStacks();
				for (int i = 0; i < stacks.getSlots(); i++) {
					ItemStack stack = stacks.getStackInSlot(i);
					if (!stack.isEmpty() && stack.getItem() == GPSItems.GPS.get()) {
						return stack;
					}
				}
			}
			return ItemStack.EMPTY;
		}).orElse(ItemStack.EMPTY);
	}
}