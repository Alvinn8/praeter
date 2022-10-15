package ca.bkaw.praeter.framework.test;

import ca.bkaw.praeter.framework.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.CustomGuiType;
import ca.bkaw.praeter.framework.gui.components.Button;
import ca.bkaw.praeter.framework.gui.components.ToggleButton;

public class TestGui extends CustomGui {
    public static final Button.Type BUTTON_1 = new Button.Type(0, 0, 1, 2);
    public static final ToggleButton.Type BUTTON_2 = new ToggleButton.Type(0, 4, 1, 2);

    public static final CustomGuiType TYPE = CustomGuiType
            .builder(TestGui::new)
            .add(BUTTON_1, BUTTON_2)
            .build();

    public TestGui() {
        super(TYPE);
    }

    public void toggleButton2() {
        ToggleButton button = get(BUTTON_2);
        button.setEnabled(!button.isEnabled());
    }
}