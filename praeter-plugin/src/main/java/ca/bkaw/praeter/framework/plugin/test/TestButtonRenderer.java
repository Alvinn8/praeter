package ca.bkaw.praeter.framework.plugin.test;

import ca.bkaw.praeter.framework.gui.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiType;
import ca.bkaw.praeter.framework.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.GuiUtils;
import ca.bkaw.praeter.framework.gui.components.Button;
import ca.bkaw.praeter.framework.resources.font.FontSequence;
import ca.bkaw.praeter.framework.resources.gui.FontGuiComponentRenderer;
import ca.bkaw.praeter.framework.resources.gui.RenderDispatcher;
import ca.bkaw.praeter.framework.resources.gui.RenderSetupContext;
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
                .renderImage(new NamespacedKey("praetertest", "texture1"), 10, 10)
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
