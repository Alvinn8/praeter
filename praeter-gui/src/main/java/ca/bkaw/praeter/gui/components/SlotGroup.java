package ca.bkaw.praeter.gui.components;

import ca.bkaw.praeter.gui.component.GuiComponentLike;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A rectangular group of slots in a gui.
 */
public class SlotGroup<T extends Slot> implements GuiComponentLike {
    private final List<T> slots;
    private final int width, height;

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
     * Create a rectangular group of slots.
     *
     * @param x The x coordinate of the first slot.
     * @param y The y coordinate of the first slot.
     * @param width The amount of horizontal slots.
     * @param height The amount of vertical slots.
     * @param slotConstructor The type of slot to use, for example {@code Slot::new}.
     */
    public SlotGroup(int x, int y, int width, int height, SlotConstructor<T> slotConstructor) {
        List<T> slots  = new ArrayList<>(x * y);
        for (int offsetY = 0; offsetY < height; offsetY++) {
            for (int offsetX = 0; offsetX < width; offsetX++) {
                T slot = slotConstructor.create(x + offsetX, y + offsetY);
                slots.add(slot);
            }
        }
        this.slots = slots;
        this.width = width;
        this.height = height;
    }

    @Deprecated(forRemoval = true)
    public static <T extends Slot> SlotGroup<T> box(int x, int y, int width, int height, SlotConstructor<T> slotConstructor) {
        return new SlotGroup<>(x, y, width, height, slotConstructor);
    }

    /**
     * Get a slot by index. Slots always count rows first.
     * <p>
     * The index is relative to this slot group.
     *
     * @param index The index of the slot.
     * @return The slot.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @NotNull
    public T getSlot(int index) {
        return this.slots.get(index);
    }

    /**
     * Get a slot by coordinates relative to the slot group.
     *
     * @param x The x coordinate of the slot.
     * @param y The y coordinate of the slot.
     * @return The slot.
     * @throws IndexOutOfBoundsException If the coordinates are out of bounds.
     */
    @NotNull
    public T getSlot(int x, int y) {
        return this.getSlot(x + y * this.width);
    }

    @Override
    public void addTo(CustomGuiType.Builder builder) {
        for (T slot : this.slots) {
            builder.add(slot);
        }
    }

    /**
     * Get the amount of horizontal slots.
     *
     * @return The width.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get the amount of vertical slots.
     *
     * @return The height.
     */
    public int getHeight() {
        return this.height;
    }
}
