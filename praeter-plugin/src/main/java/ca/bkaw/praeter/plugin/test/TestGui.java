package ca.bkaw.praeter.plugin.test;

import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import ca.bkaw.praeter.gui.components.Button;
import ca.bkaw.praeter.gui.components.ToggleButton;
import ca.bkaw.praeter.gui.font.FontGuiRenderer;
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
    public static final Button.Type TEMP_COMPONENT = new Button.Type(
        new TestRenderer(),
        0, 0,
        1, 1
    );

    public static final CustomGuiType TYPE = CustomGuiType.builder()
        .height(6)
        .title(Component.text("Test Gui"))
        .renderer(new FontGuiRenderer())
        .add(BUTTON_1, BUTTON_2, TEMP_COMPONENT)
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