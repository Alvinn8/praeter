package ca.bkaw.praeter.plugin.test;

import ca.bkaw.praeter.gui.components.Button;
import ca.bkaw.praeter.gui.components.Slot;
import ca.bkaw.praeter.gui.components.ToggleButton;
import ca.bkaw.praeter.gui.font.FontGuiRenderer;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import net.kyori.adventure.text.Component;

public class TestGui extends CustomGui {
    public static final Button.Type BUTTON_1 = new Button.Type(
        new TestButtonRenderer(),
        0, 0,
        2, 1
    );
    public static final ToggleButton.Type BUTTON_2 = new ToggleButton.Type(
        new TestToggleButtonRenderer(),
        0, 4,
        2, 1
    );
    public static final Button.Type TEMP_COMPONENT = new Button.Type(
        new TestRenderer(),
        0, 0,
        1, 1
    );
    public static final Slot.Type SLOT_1 = new Slot.Type(5, 1);

    public static final CustomGuiType TYPE = CustomGuiType.builder()
        .height(6)
        .title(Component.text("Test Gui"))
        .renderer(new FontGuiRenderer())
        .add(BUTTON_1, BUTTON_2, TEMP_COMPONENT, SLOT_1)
        .build();

    public TestGui() {
        super(TYPE);

        get(BUTTON_2).setOnClick(event -> {
            toggleButton2();
            event.playClickSound();
        });
    }

    public void toggleButton2() {
        ToggleButton button = get(BUTTON_2);
        button.setEnabled(!button.isEnabled());
        update();
    }
}