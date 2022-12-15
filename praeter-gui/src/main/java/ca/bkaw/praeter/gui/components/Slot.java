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

    public Slot() {
        // TODO this logic should not be here
        this.setOnClick(event -> {
            ItemStack cursor = event.getEvent().getCursor();
            if (cursor == null || cursor.getType().isAir()) {
                event.getEvent().getWhoClicked().setItemOnCursor(this.itemStack);
                this.itemStack = null;
            } else {
                this.itemStack = cursor;
                event.getEvent().getWhoClicked().setItemOnCursor(null);
            }
            // TODO can't update the gui from here
            //      maybe that's not needed though
        });
    }

    @Nullable
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}
