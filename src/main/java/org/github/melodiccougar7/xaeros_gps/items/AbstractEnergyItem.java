package org.github.melodiccougar7.xaeros_gps.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ForgeRegistries;
import org.github.melodiccougar7.xaeros_gps.client.tooltip.BatteryTooltipComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public abstract class AbstractEnergyItem extends Item {

    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;
    private final boolean allowRecharge;
    private final boolean allowExtract;

    //TODO MAKE BETTER CONSTRUCTORS THESE ARE NOT SO HOT.
    public AbstractEnergyItem(Properties properties,
                              int capacity,
                              int maxReceive,
                              int maxExtract,
                              boolean allowRecharge,
                              boolean allowExtract) {
        super(properties);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.allowRecharge = allowRecharge;
        this.allowExtract = allowExtract;
    }

    public AbstractEnergyItem(Properties properties,
                              int capacity,
                              int transferRate,
                              boolean allowRecharge,
                              boolean allowExtract,
                              int initialCapacity) {
        super(properties);
        this.capacity = capacity;
        this.maxReceive = transferRate;
        this.maxExtract = transferRate;
        this.allowRecharge = allowRecharge;
        this.allowExtract = allowExtract;
    }

    public int getEnergy(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt("energy") : 0;
    }

    public void setEnergy(ItemStack stack, int energy) {
        int clampedEnergy = Math.max(0, Math.min(energy, this.capacity));
        stack.getOrCreateTag().putInt("energy", clampedEnergy);
    }

    public boolean isOutOfEnergy(ItemStack stack) {
        return getEnergy(stack) <= 0;
    }

    public int getCapacity() {
        return this.capacity;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            private final LazyOptional<IEnergyStorage> energyCap =
                    LazyOptional.of(() -> new EnergyStorage(capacity, maxReceive, maxExtract) {

                        @Override
                        public int getEnergyStored() {
                            return getEnergy(stack);
                        }

                        @Override
                        public boolean canReceive() {
                            return allowRecharge && super.canReceive();
                        }

                        @Override
                        public boolean canExtract() {
                            return allowExtract && super.canExtract();
                        }

                        @Override
                        public int receiveEnergy(int maxReceiveAmount, boolean simulate) {
                            if (!canReceive())
                                return 0; // Respect the recharge flag
                            int current = getEnergyStored();
                            int received = Math.min(capacity - current, Math.min(maxReceive, maxReceiveAmount));
                            if (!simulate && received > 0) {
                                setEnergy(stack, current + received);
                            }
                            return received;
                        }

                        @Override
                        public int extractEnergy(int maxExtractAmount, boolean simulate) {
                            if (!canExtract())
                                return 0;
                            int current = getEnergyStored();
                            int extracted = Math.min(current, Math.min(maxExtract, maxExtractAmount));
                            if (!simulate && extracted > 0) {
                                setEnergy(stack, current - extracted);
                            }
                            return extracted;
                        }
                    });

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ENERGY) {
                    return energyCap.cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    // TODO MAKE THES MORE ROBUST WITH ItemStacks of multiple batteries
    public boolean hasBattery(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("BatteryStack");
    }

    public ItemStack getInsertedBattery(ItemStack stack) {
        if (!hasBattery(stack))
            return ItemStack.EMPTY;
        return ItemStack.of(stack.getTag().getCompound("BatteryStack"));
    }

    public void setInsertedBattery(ItemStack stack, ItemStack battery) {
        if (battery.isEmpty()) {
            stack.getOrCreateTag().remove("BatteryStack");
        } else {
            stack.getOrCreateTag().put("BatteryStack", battery.save(new CompoundTag()));
        }
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack thisStack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY)
            return false;
        ItemStack slotStack = slot.getItem();
        if (slotStack.getItem() instanceof ItemBattery) {
            if (!player.level().isClientSide()) {
                if (hasBattery(thisStack)) {
                    player.getInventory().placeItemBackInInventory(getInsertedBattery(thisStack));
                }
                ItemStack batteryToInsert = slotStack.split(1);
                setInsertedBattery(thisStack, batteryToInsert);
                thisStack.getOrCreateTag()
                        .putString("battery_type", ForgeRegistries.ITEMS.getKey(batteryToInsert.getItem()).toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack thisStack,
                                            ItemStack other,
                                            Slot slot,
                                            ClickAction action,
                                            Player player,
                                            SlotAccess access) {
        if (action != ClickAction.SECONDARY)
            return false; // right click
        if (other.getItem() instanceof ItemBattery battery) {
            if (!player.level().isClientSide()) {
                int space = getCapacity() - getEnergy(thisStack);
                int available = battery.getEnergy(other);
                int transfer = Math.min(space, available);
                if (transfer > 0) {
                    setEnergy(thisStack, getEnergy(thisStack) + transfer);
                    battery.setEnergy(other, available - transfer);
                }

            }
            thisStack.getOrCreateTag().putString("battery_type", ForgeRegistries.ITEMS.getKey(battery).toString());
            return true;
        }
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(Item.MAX_BAR_WIDTH * getEnergy(stack) / getCapacity());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float ratio = (float) getEnergy(stack) / getCapacity();
        int r = (int) (255 * (1.0f - ratio));
        int g = (int) (255 * ratio);
        int b = (int) (200 * ratio);
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                @Nullable Level level,
                                List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        int energy = getEnergy(stack);
        tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.charge")
                .withStyle(ChatFormatting.YELLOW)
                .append(energy + " / " + getCapacity()));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (!Screen.hasShiftDown())
            return Optional.empty();
        if (!stack.hasTag() || !stack.getTag().contains("battery_type"))
            return Optional.empty();
        String type = stack.getTag().getString("battery_type");
        if (type.isEmpty())
            return Optional.empty();
        ResourceLocation id = new ResourceLocation(type);
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null)
            return Optional.empty();
        return Optional.of(new BatteryTooltipComponent(new ItemStack(item)));
    }
}
