package ca.bkaw.praeter.framework.plugin.test;

import ca.bkaw.praeter.framework.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.CustomGuiRenderer;
import net.kyori.adventure.text.Component;

public class TestCustomGuiRenderer implements CustomGuiRenderer {
    @Override
    public Component getRenderTitle(Component title, CustomGui customGui) {
        return title;
    }
}
