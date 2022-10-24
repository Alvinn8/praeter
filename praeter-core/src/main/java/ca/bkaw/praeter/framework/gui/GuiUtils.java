package ca.bkaw.praeter.framework.gui;

import java.util.function.IntConsumer;

/**
 * A utility class for guis.
 */
public final class GuiUtils {
    private GuiUtils() {}

    /**
     * Loop trough each slot in a 9-slot-wide inventory in a rectangle.
     *
     * @param x The leftmost x coordinate.
     * @param y The topmost y coordinate.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     * @param slotConsumer A consumer called with each slot.
     */
    public static void forEachSlot(int x, int y, int width, int height, IntConsumer slotConsumer) {
        for (int i = 0; i < width; i++) {
            int cx = x + i;
            for (int j = 0; j < height; j++) {
                int cy = y + j;
                int slot = cy * 9 + cx;
                slotConsumer.accept(slot);
            }
        }
    }

    /**
     * Loop trough each slot in a 9-slot-wide inventory in the rectangle created by
     * the component type.
     *
     * @param componentType The component type.
     * @param slotConsumer A consumer called with each slot.
     */
    public static void forEachSlot(GuiComponentType<?, ?> componentType, IntConsumer slotConsumer) {
        forEachSlot(
            componentType.getX(),
            componentType.getY(),
            componentType.getWidth(),
            componentType.getHeight(),
            slotConsumer
        );
    }

    /**
     * Get the x coordinate from a slot in a 9-slot-wide inventory.
     *
     * @param slot The slot.
     * @return The x coordinate.
     */
    public static int getX(int slot) {
        return slot % 9;
    }

    /**
     * Get the y coordinate from a slot in a 9-slot-wide inventory.
     *
     * @param slot The slot.
     * @return The y coordinate.
     */
    public static int getY(int slot) {
        return slot / 9;
    }
}
