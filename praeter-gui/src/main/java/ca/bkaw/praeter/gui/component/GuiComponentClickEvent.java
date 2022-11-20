package ca.bkaw.praeter.gui.component;

import net.kyori.adventure.sound.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

/**
 * Holds data that is passed to the {@link GuiComponent#setOnClick(Consumer)}
 * callback.
 * <p>
 * Please note that this is not a bukkit event!
 */
public class GuiComponentClickEvent {
    private final InventoryClickEvent event;

    public GuiComponentClickEvent(InventoryClickEvent event) {
        this.event = event;
    }

    /**
     * Get the {@link InventoryClickEvent} that caused the click.
     * <p>
     * Can be used to get information about the click such as {@link InventoryClickEvent
     * #isRightClick()}, etc.
     *
     * @return The event.
     */
    public InventoryClickEvent getEvent() {
        return this.event;
    }

    /**
     * Play a {@link org.bukkit.Sound#UI_BUTTON_CLICK} sound to the player that clicked.
     */
    public void playClickSound() {
        this.event.getWhoClicked().playSound(
            Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.MASTER, 1, 1)
        );
    }
}
