package ca.bkaw.praeter.plugin.test;

import ca.bkaw.praeter.core.resources.font.FontSequence;
import ca.bkaw.praeter.gui.components.ToggleButton;
import ca.bkaw.praeter.gui.font.FontGuiComponentRenderer;
import ca.bkaw.praeter.gui.font.RenderDispatcher;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class TestToggleButtonRenderer implements FontGuiComponentRenderer<ToggleButton, ToggleButton.Type> {
    private FontSequence enabled;
    private FontSequence disabled;

    @Override
    public void onSetup(CustomGuiType customGuiType, ToggleButton.Type componentType, RenderSetupContext context) throws IOException {
        this.enabled = context.newFontSequence()
            .drawImage(new NamespacedKey("praetertest", "button1.png"), 0, 0)
            .build();

        this.disabled = context.newFontSequence()
            .drawImage(new NamespacedKey("praetertest", "button2.png"), 0, 0)
            .build();
    }

    @Override
    public void onRender(CustomGuiType customGuiType, CustomGui customGui, ToggleButton.Type componentType, ToggleButton toggleButton, RenderDispatcher renderDispatcher) {
        if (toggleButton.isEnabled()) {
            renderDispatcher.render(this.enabled);
        } else {
            renderDispatcher.render(this.disabled);
        }
    }

    @Override
    public void renderItems(CustomGuiType customGuiType, CustomGui customGui, ToggleButton.Type componentType, ToggleButton component, Inventory inventory) {
        /*
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
         */
    }
}
