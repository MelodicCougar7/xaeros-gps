package org.github.melodiccougar7.xaeros_gps.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.github.melodiccougar7.xaeros_gps.XaerosGPS;

@SuppressWarnings("removal")
public class GPSNetwork {
	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel CHANNEL =
			NetworkRegistry.newSimpleChannel(new ResourceLocation(XaerosGPS.MODID, "main"),
					() -> PROTOCOL_VERSION,
					PROTOCOL_VERSION::equals,
					PROTOCOL_VERSION::equals);

	private static int packetId = 0;

	public static void register() {
		CHANNEL.registerMessage(packetId++,
				GPSTogglePacket.class,
				GPSTogglePacket::encode,
				GPSTogglePacket::decode,
				GPSTogglePacket::handle);
	}
}
