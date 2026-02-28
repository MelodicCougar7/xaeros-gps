package org.github.melodiccougar7.xaeros_gps.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.github.melodiccougar7.xaeros_gps.common.items.ItemGPS;
import org.github.melodiccougar7.xaeros_gps.utils.GPSUtil;

import java.util.function.Supplier;

public class GPSTogglePacket {

	private final boolean active;

	public GPSTogglePacket(boolean active) {
		this.active = active;
	}

	public static void encode(GPSTogglePacket msg, FriendlyByteBuf buf) {
		buf.writeBoolean(msg.active);
	}

	public static GPSTogglePacket decode(FriendlyByteBuf buf) {
		return new GPSTogglePacket(buf.readBoolean());
	}

	public static void handle(GPSTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player == null)
				return;
			ItemStack gps = GPSUtil.getGpsItem(player);
			if (!gps.isEmpty() && gps.getItem() instanceof ItemGPS itemGPS) {
				itemGPS.setGPSState(gps, msg.active);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
