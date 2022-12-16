package ca.bkaw.praeter.gui.components;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.components.render.SlotRenderer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A slot where the user can take and place items.
 */
public class Slot extends GuiComponent {
    /**
     * The {@link GuiComponentType} for a {@link Slot}.
     */
    public static class Type extends GuiComponentType<Slot, Slot.Type> {

        /**
         * Create a new type for a {@link Slot}.
         *
         * @param x The x position of the slot.
         * @param y The y position of the slot.
         */
        public Type(int x, int y) {
            super(new SlotRenderer(), x, y, 1, 1);
        }

        @Override
        public Slot create() {
            return new Slot();
        }
    }

    private ItemStack itemStack;

    /**
     * Get the item stack stored in this slot.
     * <p>
     * The returned value may be null if there is no item in the slot. The returned
     * item stack will never be empty (have a type of air or an amount of 0).
     *
     * @return The item stack.
     */
    @Nullable
    public ItemStack getItemStack() {
        if (this.itemStack != null
            && (this.itemStack.getType().isAir() || this.itemStack.getAmount() <= 0)) {
            this.itemStack = null;
        }
        return this.itemStack;
    }

    /**
     * Set the item stack stored in this slot.
     * <p>
     * If the item is empty (has a type of air or has an amount of 0) the item stack
     * will be set to null instead.
     * <p>
     * The gui needs to be updated for the item to be rendered again.
     * <p>
     * The item is not validated by {@link #canHold(ItemStack)}, so it is
     * possible, using this method, to place items that a user can not.
     *
     * @param itemStack The item stack that the slot should hold, or null.
     */
    public void setItemStack(@Nullable ItemStack itemStack) {
        if (itemStack != null && (itemStack.getType().isAir() || itemStack.getAmount() <= 0)) {
            itemStack = null;
        }
        this.itemStack = itemStack;
    }

    /**
     * Check whether this slot can hold the specified item stack.
     *
     * @param itemStack The item stack.
     * @return Whether the item can be held.
     */
    public boolean canHold(@NotNull ItemStack itemStack) {
        return true;
    }

    /**
     * Check whether the player is allowed to change the contents of this slot.
     *
     * @param player The player.
     * @return Whether the player may change the slot.
     */
    public boolean mayChange(HumanEntity player) {
        return true;
    }
}
