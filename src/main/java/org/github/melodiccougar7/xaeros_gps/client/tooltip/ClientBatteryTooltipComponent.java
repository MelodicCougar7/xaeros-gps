package org.github.melodiccougar7.xaeros_gps.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

// Class written by ActiveRadar

//TODO Experiment more with this
public class ClientBatteryTooltipComponent implements ClientTooltipComponent {

    //TODO make stack Array
    private final ItemStack battery;

    public ClientBatteryTooltipComponent(BatteryTooltipComponent component) {
        this.battery = component.battery();
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        return 18 + 4 + font.width(battery.getHoverName());
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        graphics.renderItem(battery, x, y + 1);
        graphics.renderItemDecorations(font, battery, x, y + 1);
        graphics.drawString(font, battery.getHoverName(), x + 18 + 4, y + 5, 0xFFFFFF, true);
    }
}
