package ca.bkaw.praeter.gui.components;

import ca.bkaw.praeter.gui.component.GuiComponentLike;
import ca.bkaw.praeter.gui.gui.CustomGuiType;

import java.util.ArrayList;
import java.util.List;

/**
 * A group of slots in a gui.
 */
public class SlotGroup<T extends Slot> implements GuiComponentLike {
    private final List<T> slots;

    private SlotGroup(List<T> slots) {
        this.slots = slots;
    }

    /**
     * A constructor for a slot, for example {@code Slot::new}.
     */
    public interface SlotConstructor<T extends Slot> {
        /**
         * Construct a new slot.
         *
         * @param x The x coordinate of the slot.
         * @param y The y coordinate of the slot.
         * @return The slot.
         */
        T create(int x, int y);
    }

    /**
     * Create a box of slots.
     *
     * @param x The x coordinate of the first slot.
     * @param y The y coordinate of the first slot.
     * @param width The amount of horizontal slots.
     * @param height The amount of vertical slots.
     * @param slotConstructor The type of slot to use, for example {@code Slot::new}.
     * @return The slot group.
     */
    public static <T extends Slot> SlotGroup<T> box(int x, int y, int width, int height, SlotConstructor<T> slotConstructor) {
        List<T> slots  = new ArrayList<>(x * y);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                T slot = slotConstructor.create(x + i, y + j);
                slots.add(slot);
            }
        }
        return new SlotGroup<T>(slots);
    }

    /**
     * Get a slot by index. Slots always count rows first.
     *
     * @param index The index of the slot.
     * @return The slot.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public T getSlot(int index) {
        return this.slots.get(index);
    }

    @Override
    public void addTo(CustomGuiType.Builder builder) {
        for (T slot : this.slots) {
            builder.add(slot);
        }
    }
}
