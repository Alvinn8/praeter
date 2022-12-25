package ca.bkaw.praeter.gui.component;

import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Holds data that is passed to the {@link GuiComponent.State#setOnClick(Consumer)}
 * callback.
 */
public class GuiClickContext {
    private final InventoryClickEvent event;

    public GuiClickContext(InventoryClickEvent event) {
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
     * Get the player that clicked.
     *
     * @return The player.
     */
    @NotNull
    public Player getPlayer() {
        HumanEntity human = this.event.getWhoClicked();
        if (human instanceof Player player) {
            return player;
        }
        throw new IllegalStateException("The one who clicked was not a player, but was: " + human);
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
