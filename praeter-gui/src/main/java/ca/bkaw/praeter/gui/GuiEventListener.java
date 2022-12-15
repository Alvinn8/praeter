package ca.bkaw.praeter.gui;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentClickContext;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiHolder;
import ca.bkaw.praeter.gui.gui.SlotManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class GuiEventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof CustomGuiHolder customGuiHolder)) {
            return;
        }
        // The user clicked while a custom gui was open
        event.setCancelled(true);

        CustomGui customGui = customGuiHolder.getCustomGui();
        SlotManager slotManager = customGui.getSlotManager();
        boolean top = event.getClickedInventory() == event.getView().getTopInventory();
        boolean bottom = event.getClickedInventory() == event.getView().getBottomInventory();
        InventoryAction action = event.getAction();

        if (bottom && action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem != null && slotManager.canShiftInto(currentItem)) {
                slotManager.shiftInto(currentItem);
            }
        }

        if (top) {
            // The user clicked the top inventory,
            // the one that contains the components.
            int slot = event.getSlot();
            int x = GuiUtils.getX(slot);
            int y = GuiUtils.getY(slot);
            GuiComponentType<?, ?> componentType = customGui.getComponentTypeAt(x, y);
            if (componentType != null) {
                // The user clicked a component
                GuiComponent guiComponent = customGui.get(componentType);
                Consumer<GuiComponentClickContext> clickHandler = guiComponent.getClickHandler();
                if (clickHandler != null) {
                    // Call the click handler on the component
                    GuiComponentClickContext context = new GuiComponentClickContext(event);
                    clickHandler.accept(context);
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof CustomGuiHolder customGuiHolder)) {
            return;
        }
        event.setCancelled(true);
    }
}
