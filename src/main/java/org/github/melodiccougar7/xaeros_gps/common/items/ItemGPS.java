package org.github.melodiccougar7.xaeros_gps.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class ItemGPS extends AbstractEnergyItem implements ICurioItem {

	public static final int MAX_ENERGY = 1000;
	public static final int MAX_TRANSFER = 100;
	public static final String GPS_ON = "on";

	public ItemGPS() {
		super(new Properties().stacksTo(1), MAX_ENERGY, MAX_TRANSFER, MAX_TRANSFER, true, false);
	}

	public boolean isGPSOn(ItemStack stack) {
		return stack.hasTag() && stack.getTag().getBoolean(GPS_ON);
	}

	public void setGPSState(ItemStack stack, boolean active) {
		CompoundTag tag = stack.getOrCreateTag();
		if (active && isOutOfEnergy(stack)) {
			tag.putBoolean(GPS_ON, false);
			return;
		}
		tag.putBoolean(GPS_ON, active);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide) {
			boolean currentState = isGPSOn(stack);
			if (!currentState && isOutOfEnergy(stack)) {
				player.displayClientMessage(Component.translatable("message.xaeros_gps.no_energy")
						.withStyle(ChatFormatting.RED), true);
			} else {
				setGPSState(stack, !currentState);
				level.playSound(null,
						player.getX(),
						player.getY(),
						player.getZ(),
						SoundEvents.UI_BUTTON_CLICK.get(),
						SoundSource.PLAYERS,
						0.5f,
						!currentState ? 1.2f : 0.8f);
			}
		}
		return InteractionResultHolder.consume(stack); // I like consume
	}

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
						setGPSState(stack, false);
					}
				}
			} else {
				// No battery, no life B)
				setGPSState(stack, false);
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.is(newStack.getItem());
	}

	@Override
	public void appendHoverText(ItemStack stack,
			@Nullable Level level,
			List<Component> tooltipComponents,
			TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
		boolean isShiftDown =
				false; // Pond use this for doing shift events because if you run this on server it will crash B)
		if (level != null && level.isClientSide()) {
			isShiftDown = Screen.hasShiftDown();
		}
		if (isShiftDown) {
			tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.gps1").withStyle(ChatFormatting.WHITE));
			tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.gps2").withStyle(ChatFormatting.WHITE));
		} else {
			tooltipComponents.add(Component.translatable("tooltip.xaeros_gps.shift").withStyle(ChatFormatting.GRAY));
		}
	}
}