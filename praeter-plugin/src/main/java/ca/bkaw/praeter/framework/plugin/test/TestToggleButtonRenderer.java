package ca.bkaw.praeter.framework.plugin.test;

import ca.bkaw.praeter.framework.gui.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiType;
import ca.bkaw.praeter.framework.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.GuiUtils;
import ca.bkaw.praeter.framework.gui.components.ToggleButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TestToggleButtonRenderer implements GuiComponentRenderer<ToggleButton, ToggleButton.Type> {
    @Override
    public void onSetup(CustomGuiType customGuiType, ToggleButton.Type componentType) {
        System.out.println("setting up TestToggleButtonRenderer");
    }

    @Override
    public void renderItems(CustomGuiType customGuiType, CustomGui customGui, ToggleButton.Type componentType, ToggleButton component, Inventory inventory) {
        GuiUtils.forEachSlot(componentType, slot -> {
            ItemStack item = new ItemStack(
                component.isEnabled()
                    ? Material.BLACK_STAINED_GLASS_PANE
                    : Material.GRAY_STAINED_GLASS_PANE
            );
            item.editMeta(meta -> meta.displayName(
                Component.text("A button",
                    component.isEnabled() ? NamedTextColor.WHITE : NamedTextColor.DARK_GRAY)
            ));
            inventory.setItem(slot, item);
        });
    }
}
