package ca.bkaw.praeter.framework.gui.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link InventoryHolder} for a custom gui.
 * <p>
 * Allows for easily identifying inventories that render a custom gui, and provides
 * a way to get the respective {@link CustomGui}.
 */
public class CustomGuiHolder implements InventoryHolder {
    private final CustomGui customGui;

    public CustomGuiHolder(CustomGui customGui) {
        this.customGui = customGui;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.customGui.getInventory();
    }

    /**
     * Get the {@link CustomGui} that is rendering the inventory this holder represents.
     *
     * @return The custom gui.
     */
    public CustomGui getCustomGui() {
        return this.customGui;
    }
}
