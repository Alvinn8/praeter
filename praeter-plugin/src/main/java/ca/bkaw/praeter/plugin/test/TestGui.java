package ca.bkaw.praeter.plugin.test;

import ca.bkaw.praeter.gui.components.Button;
import ca.bkaw.praeter.gui.components.DisableableButton;
import ca.bkaw.praeter.gui.components.Slot;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class TestGui extends CustomGui {
    public static final Button BUTTON_1 = new Button("Hello", 0, 0, 2, 1);
    public static final DisableableButton BUTTON_2 = new DisableableButton("Button", 0, 4, 2, 1);
    public static final DisableableButton BUTTON_3 = new DisableableButton("Hello there", 0, 2, 4, 1);
    public static final Slot SLOT_1 = new Slot(5, 1);
    public static final Slot SLOT_2 = new Slot(6, 1);
    public static final Slot SLOT_3 = new Slot(7, 2);
    public static final TestComponent TEST = new TestComponent(0, 0, 0, 0);

    public static final CustomGuiType TYPE = CustomGuiType.builder()
        .height(6)
        .title(Component.text("Test Gui"))
        .add(BUTTON_1, BUTTON_2, BUTTON_3, SLOT_1, SLOT_2, SLOT_3, TEST)
        .build();

    public TestGui() {
        super(TYPE);

        BUTTON_1.getState(this).setOnClick(context -> {
            DisableableButton.State button3 = BUTTON_3.getState(this);
            button3.setEnabled(!button3.isEnabled());
            update();
        });

        BUTTON_2.getState(this).setOnClick(context -> {
            toggleButton2();
            context.playClickSound();
        });

        BUTTON_3.getState(this).setOnClick(context -> {
            context.playClickSound();
            context.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
        });

        BUTTON_3.getState(this).setOnDisabledClick(context ->
            context.getPlayer().playSound(context.getPlayer(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f)
        );
    }

    public void toggleButton2() {
        DisableableButton.State button = BUTTON_2.getState(this);
        button.setEnabled(!button.isEnabled());
        update();
    }
}