package ca.bkaw.praeter.framework.plugin.test;

import ca.bkaw.praeter.framework.gui.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiType;
import ca.bkaw.praeter.framework.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.GuiUtils;
import ca.bkaw.praeter.framework.gui.components.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TestButtonRenderer implements GuiComponentRenderer<Button, Button.Type> {

    @Override
    public void renderItems(CustomGuiType customGuiType, CustomGui customGui, Button.Type componentType, Button component, Inventory inventory) {
        GuiUtils.forEachSlot(componentType, slot -> {
            ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            item.editMeta(meta -> meta.displayName(
                Component.text("A button", NamedTextColor.WHITE)
            ));
            inventory.setItem(slot, item);
        });
    }
}
