package ca.bkaw.praeter.framework.test;

import ca.bkaw.praeter.framework.inventory.CustomInventory;
import ca.bkaw.praeter.framework.inventory.CustomInventoryType;
import ca.bkaw.praeter.framework.inventory.components.Button;
import ca.bkaw.praeter.framework.inventory.components.ToggleButton;

public class TestInventory extends CustomInventory {
    public static final Button.Type BUTTON_1 = new Button.Type(0, 0, 1, 2);
    public static final ToggleButton.Type BUTTON_2 = new ToggleButton.Type(0, 4, 1, 2);

    public static final CustomInventoryType TYPE = CustomInventoryType
            .builder(TestInventory::new)
            .add(BUTTON_1, BUTTON_2)
            .build();

    public TestInventory() {
        super(TYPE);
    }

    public void toggleButton2() {
        ToggleButton button = get(BUTTON_2);
        button.setEnabled(!button.isEnabled());
    }
}