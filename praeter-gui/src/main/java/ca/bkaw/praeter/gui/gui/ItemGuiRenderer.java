package ca.bkaw.praeter.gui.gui;

import ca.bkaw.praeter.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.gui.component.ItemGuiComponentRenderer;
import net.kyori.adventure.text.Component;

/**
 * A {@link CustomGuiRenderer} that renders components in the gui using items.
 * <p>
 * Components should use
 * <p>
 * No resource pack is used.
 */
public class ItemGuiRenderer implements CustomGuiRenderer {
    @Override
    public boolean supports(GuiComponentRenderer<?, ?> componentRenderer) {
        return componentRenderer instanceof ItemGuiComponentRenderer<?, ?>;
    }

    @Override
    public void onSetup(CustomGuiType customGuiType) {}

    @Override
    public Component getRenderTitle(Component title, CustomGui customGui) {
        return title;
    }
}
