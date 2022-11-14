package ca.bkaw.praeter.plugin.test;

import ca.bkaw.praeter.core.resources.bake.FontSequence;
import ca.bkaw.praeter.gui.components.Button;
import ca.bkaw.praeter.gui.font.FontGuiComponentRenderer;
import ca.bkaw.praeter.gui.font.GuiFontSequenceBuilder;
import ca.bkaw.praeter.gui.font.RenderDispatcher;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class TestRenderer implements FontGuiComponentRenderer<Button, Button.Type> {
    private FontSequence fontSequence;

    @Override
    public void onSetup(CustomGuiType customGuiType, Button.Type componentType, RenderSetupContext context) throws IOException {
        GuiFontSequenceBuilder builder = context.newFontSequence();
        for (int i = 0; i < 10; i++) {
            builder.renderImage(NamespacedKey.minecraft("item/diamond.png"), 0, i * 16);
        }
        this.fontSequence = builder.build();
    }

    @Override
    public void onRender(CustomGuiType customGuiType, CustomGui customGui, Button.Type componentType, Button component, RenderDispatcher renderDispatcher) {
        renderDispatcher.render(this.fontSequence);
    }

    @Override
    public void renderItems(CustomGuiType customGuiType, CustomGui customGui, Button.Type componentType, Button component, Inventory inventory) {

    }

}
