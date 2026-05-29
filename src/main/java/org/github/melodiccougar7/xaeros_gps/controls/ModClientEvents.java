package org.github.melodiccougar7.xaeros_gps.controls;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.github.melodiccougar7.xaeros_gps.XaerosGPS;
import org.github.melodiccougar7.xaeros_gps.client.tooltip.BatteryTooltipComponent;
import org.github.melodiccougar7.xaeros_gps.client.tooltip.ClientBatteryTooltipComponent;

@Mod.EventBusSubscriber(modid = XaerosGPS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onRegisterTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BatteryTooltipComponent.class, ClientBatteryTooltipComponent::new);
    }
}
