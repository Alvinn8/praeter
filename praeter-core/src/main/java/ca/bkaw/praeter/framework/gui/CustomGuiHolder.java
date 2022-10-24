package ca.bkaw.praeter.framework.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

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
}
