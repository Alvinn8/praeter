package ca.bkaw.praeter.gui.components.render;

import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.components.Slot;
import ca.bkaw.praeter.gui.font.BackgroundGuiComponentRenderer;
import ca.bkaw.praeter.gui.font.GuiBackgroundPainter;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.inventory.Inventory;

/**
 * A renderer for a {@link Slot}.
 */
public class SlotRenderer implements BackgroundGuiComponentRenderer<Slot, Slot.Type> {
    private static final SlotRenderer INSTANCE = new SlotRenderer();

    private SlotRenderer() {}

    /**
     * Get the renderer that renders a normal slot.
     *
     * @return The renderer.
     */
    public static SlotRenderer slot() {
        return INSTANCE;
    }

    @Override
    public void renderItems(CustomGuiType customGuiType, CustomGui customGui, Slot.Type componentType, Slot component, Inventory inventory) {
        inventory.setItem(GuiUtils.getSlot(componentType.getX(), componentType.getY()), component.getItemStack());
    }

    @Override
    public void draw(CustomGuiType customGuiType, Slot.Type componentType, GuiBackgroundPainter background) {
        background.carve(0, 0, GuiUtils.SLOT_SIZE, GuiUtils.SLOT_SIZE);
    }
}
