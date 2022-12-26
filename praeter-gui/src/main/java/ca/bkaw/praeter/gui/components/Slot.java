package ca.bkaw.praeter.gui.components;

import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import ca.bkaw.praeter.gui.gui.CustomGui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * A slot where the user can take and place items.
 * <p>
 * Subclasses can override the {@link #canHold(ItemStack)} and
 * {@link #mayChange(HumanEntity)} methods to control the slot. These methods can
 * also be overridden in the state to control this on a per-state basis.
 */
public class Slot extends GuiComponent {
    /**
     * Create a new {@link Slot}.
     *
     * @param x The x position of the component, in slots.
     * @param y The y position of the component, in slots.
     */
    public Slot(int x, int y) {
        super(x, y, 1, 1);
    }

    @Override
    @SuppressWarnings("RedundantThrows") // subclasses may want to throw
    public void onSetup(RenderSetupContext context) throws IOException {
        // Carve out the slot to let the vanilla slot shine trough
        context.getBackground().carve(0, 0, GuiUtils.SLOT_SIZE, GuiUtils.SLOT_SIZE);
    }

    @Override
    public Slot.State createState() {
        return new Slot.State();
    }

    @Override
    public Slot.State get(CustomGui gui) {
        return (Slot.State) super.get(gui);
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

    /**
     * Called right after the slot content has been changed. The gui will update
     * shortly after this method is called.
     *
     * @param player The player that changed the slot.
     */
    public void onChange(HumanEntity player) {}

    public class State extends GuiComponent.State {
        private ItemStack itemStack;

        @Override
        public void renderItems(Inventory inventory) {
            inventory.setItem(GuiUtils.getSlot(x, y), itemStack);
        }

        /**
         * Check whether this slot can hold the specified item stack.
         *
         * @param itemStack The item stack.
         * @return Whether the item can be held.
         */
        public boolean canHold(@NotNull ItemStack itemStack) {
            return Slot.this.canHold(itemStack);
        }

        /**
         * Check whether the player is allowed to change the contents of this slot.
         *
         * @param player The player.
         * @return Whether the player may change the slot.
         */
        public boolean mayChange(HumanEntity player) {
            return Slot.this.mayChange(player);
        }

        /**
         * Called right after the slot content has been changed. The gui will update
         * shortly after this method is called.
         *
         * @param player The player that changed the slot.
         */
        public void onChange(HumanEntity player) {
            Slot.this.onChange(player);
        }

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
    }

}
