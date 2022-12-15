package ca.bkaw.praeter.gui.components;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.components.render.SlotRenderer;
import org.bukkit.inventory.ItemStack;
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

    @Nullable
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        if (itemStack != null && itemStack.getType().isAir()) {
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
    public boolean supportsHolding(ItemStack itemStack) {
        return true;
    }
}
