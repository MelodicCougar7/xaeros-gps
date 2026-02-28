package org.github.melodiccougar7.xaeros_gps.registry;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.github.melodiccougar7.xaeros_gps.items.GPSItem;

import static org.github.melodiccougar7.xaeros_gps.XaerosGPS.MODID;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> GPS = ITEMS.register("gps", () -> new GPSItem());
    public static final RegistryObject<Item> BATTERY = ITEMS.register("battery", () -> new Item(new Item.Properties().stacksTo(64).defaultDurability(0)));
}
