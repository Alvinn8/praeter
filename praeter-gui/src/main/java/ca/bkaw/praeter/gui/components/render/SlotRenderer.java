package ca.bkaw.praeter.gui.components.render;

import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.components.Slot;
import ca.bkaw.praeter.gui.font.BackgroundGuiComponentRenderer;
import ca.bkaw.praeter.gui.font.GuiBackgroundPainter;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.inventory.Inventory;

public class SlotRenderer implements BackgroundGuiComponentRenderer<Slot, Slot.Type> {
    @Override
    public void renderItems(CustomGuiType customGuiType, CustomGui customGui, Slot.Type componentType, Slot component, Inventory inventory) {
        inventory.setItem(componentType.getY() * 9 + componentType.getX(), component.getItemStack());
    }

    @Override
    public void draw(CustomGuiType customGuiType, Slot.Type componentType, GuiBackgroundPainter background) {
        background.carve(
            componentType.getX() * GuiUtils.SLOT_SIZE,
            componentType.getY() * GuiUtils.SLOT_SIZE,
            componentType.getWidth() * GuiUtils.SLOT_SIZE,
            componentType.getHeight() * GuiUtils.SLOT_SIZE
        );
    }
}
