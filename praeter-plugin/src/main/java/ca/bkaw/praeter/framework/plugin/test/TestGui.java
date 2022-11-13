package ca.bkaw.praeter.framework.plugin.test;

import ca.bkaw.praeter.framework.gui.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiType;
import ca.bkaw.praeter.framework.gui.components.Button;
import ca.bkaw.praeter.framework.gui.components.ToggleButton;
import ca.bkaw.praeter.framework.gui.font.FontGuiRenderer;
import net.kyori.adventure.text.Component;

public class TestGui extends CustomGui {
    public static final Button.Type BUTTON_1 = new Button.Type(
        new TestButtonRenderer(),
        0, 0,
        2, 1
    );
    public static final ToggleButton.Type BUTTON_2 = new ToggleButton.Type(
        new TestToggleButtonRenderer(), // null is not allowed, just no impl. yet
        0, 4,
        2, 1
    );

    public static final CustomGuiType TYPE = CustomGuiType.builder()
        .height(6)
        .title(Component.text("Test Gui"))
        .renderer(new FontGuiRenderer())
        .add(BUTTON_1, BUTTON_2)
        .build();

    public TestGui() {
        super(TYPE);

        get(BUTTON_2).setOnClick(event -> toggleButton2());
    }

    public void toggleButton2() {
        ToggleButton button = get(BUTTON_2);
        button.setEnabled(!button.isEnabled());
        update();
    }
}