package org.github.melodiccougar7.xaeros_gps.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.github.melodiccougar7.xaeros_gps.XaerosGPS;
import org.github.melodiccougar7.xaeros_gps.items.GPSItem;
import org.github.melodiccougar7.xaeros_gps.items.ItemBattery;

import static org.github.melodiccougar7.xaeros_gps.XaerosGPS.MODID;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> GPS = ITEMS.register("gps", () -> new GPSItem());
    public static final RegistryObject<Item> BATTERY = ITEMS.register("battery", () -> new ItemBattery(new Item.Properties().stacksTo(64), 1000, 100, 1000, false));
    public static final RegistryObject<Item> RECHARGEABLE_BATTERY = ITEMS.register("rechargeable_battery", () -> new ItemBattery(new Item.Properties().stacksTo(64), 1000, 100, 1000, true));
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, XaerosGPS.MODID);
    public static final RegistryObject<CreativeModeTab> GPS_TAB = CREATIVE_TABS.register("gps_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.xaeros_gps"))
                    .icon(() -> new ItemStack(ItemRegistry.GPS.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ItemRegistry.GPS.get());
                        ItemStack fullBattery = new ItemStack(ItemRegistry.RECHARGEABLE_BATTERY.get());
                        ItemBattery batteryItem = (ItemBattery) fullBattery.getItem();
                        batteryItem.setEnergy(fullBattery, batteryItem.getCapacity());
                        output.accept(fullBattery);
                        output.accept(ItemRegistry.BATTERY.get());
                    })
                    .build());
}
