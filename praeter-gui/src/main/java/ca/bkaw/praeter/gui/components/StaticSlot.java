package ca.bkaw.praeter.gui.components;

import org.bukkit.entity.HumanEntity;

/**
 * A {@link Slot} that cannot be changed by the player.
 */
public class StaticSlot extends Slot {
    /**
     * Create a new {@link StaticSlot}.
     *
     * @param x The x position of the component, in slots.
     * @param y The y position of the component, in slots.
     */
    public StaticSlot(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean mayChange(HumanEntity player) {
        return false;
    }
}
