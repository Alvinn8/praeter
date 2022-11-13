package ca.bkaw.praeter.gui.component;

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
}
