package org.github.melodiccougar7.xaeros_gps.items;

public class ItemBattery extends AbstractEnergyItem {

    public ItemBattery(Properties properties,
                       int capacity,
                       int transferRate,
                       int initialCapacity,
                       boolean allowRecharge) {
        super(properties, capacity, transferRate, allowRecharge, true, initialCapacity);
    }
}