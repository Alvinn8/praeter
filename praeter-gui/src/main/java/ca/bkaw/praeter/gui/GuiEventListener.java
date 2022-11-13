package ca.bkaw.praeter.gui;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentClickEvent;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Consumer;

public class GuiEventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof CustomGuiHolder customGuiHolder)) {
            return;
        }
        // The user clicked while a custom gui was open
        CustomGui customGui = customGuiHolder.getCustomGui();
        event.setCancelled(true);

        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            // The user clicked the top inventory,
            // the one that contains the components.
            int slot = event.getSlot();
            int x = GuiUtils.getX(slot);
            int y = GuiUtils.getY(slot);
            GuiComponentType<?, ?> componentType = customGui.getComponentTypeAt(x, y);
            if (componentType != null) {
                // The user clicked a component
                GuiComponent guiComponent = customGui.get(componentType);
                Consumer<GuiComponentClickEvent> clickHandler = guiComponent.getClickHandler();
                if (clickHandler != null) {
                    // Call the click handler on the component
                    GuiComponentClickEvent guiEvent = new GuiComponentClickEvent(event);
                    clickHandler.accept(guiEvent);
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
