package ca.bkaw.praeter.plugin.test;

import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.components.Button;
import ca.bkaw.praeter.core.resources.bake.FontSequence;
import ca.bkaw.praeter.gui.font.FontGuiComponentRenderer;
import ca.bkaw.praeter.gui.font.RenderDispatcher;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class TestButtonRenderer implements FontGuiComponentRenderer<Button, Button.Type> {

    private FontSequence fontSequence;

    @Override
    public void onSetup(CustomGuiType customGuiType, Button.Type componentType, RenderSetupContext context) throws IOException {
        this.fontSequence = context.newFontSequence()
                .renderImage(new NamespacedKey("minecraft", "item/diamond.png"), 5, 5)
                .build();
    }

    @Override
    public void onRender(CustomGuiType customGuiType, CustomGui customGui, Button.Type componentType, Button component, RenderDispatcher renderDispatcher) {
        renderDispatcher.render(this.fontSequence);
    }

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
