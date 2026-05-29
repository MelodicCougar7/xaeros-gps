package org.github.melodiccougar7.xaeros_gps.controls;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.github.melodiccougar7.xaeros_gps.XaerosGPS;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = XaerosGPS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)

public class XGPSKeybinds{

    public static KeyMapping TOGGLE_GPS;
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        LOGGER.info("keybinds registered");
        TOGGLE_GPS = new KeyMapping(
                "key.xaeros_gps.toggle_gps",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.categories.xaeros_gps"
        );
        event.register(TOGGLE_GPS);
    }
}

