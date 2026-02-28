package org.github.melodiccougar7.xaeros_gps.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.github.melodiccougar7.xaeros_gps.XaerosGPS;
import org.github.melodiccougar7.xaeros_gps.common.items.ItemBattery;
import org.github.melodiccougar7.xaeros_gps.common.items.ItemGPS;

import static org.github.melodiccougar7.xaeros_gps.XaerosGPS.MODID;

public class GPSItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final RegistryObject<Item> GPS = ITEMS.register("gps", () -> new ItemGPS());
	public static final RegistryObject<Item> BATTERY =
			ITEMS.register("battery", () -> new ItemBattery(new Item.Properties().stacksTo(64), 1000, 100, 1000,
                    true));

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, XaerosGPS.MODID);

	public static final RegistryObject<CreativeModeTab> GPS_TAB = CREATIVE_TABS.register("gps_tab",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.xaeros_gps"))
					.icon(() -> new ItemStack(GPSItems.GPS.get()))
					.displayItems((parameters, output) -> {
						output.accept(GPSItems.GPS.get());
						ItemStack fullBattery = new ItemStack(GPSItems.BATTERY.get());
						ItemBattery batteryItem = (ItemBattery) fullBattery.getItem();
						batteryItem.setEnergy(fullBattery, batteryItem.getCapacity());
						output.accept(fullBattery);
						output.accept(GPSItems.BATTERY.get());
					})
					.build());
}
