package ca.bkaw.praeter.framework.gui;

import net.kyori.adventure.text.Component;

/**
 * A {@link CustomGuiRenderer} that renders components in the gui using items.
 * <p>
 * No resource pack is used.
 */
public class ItemGuiRenderer implements CustomGuiRenderer {
    @Override
    public void onSetup(CustomGuiType customGuiType) {}

    @Override
    public Component getRenderTitle(Component title, CustomGui customGui) {
        return title;
    }
}
