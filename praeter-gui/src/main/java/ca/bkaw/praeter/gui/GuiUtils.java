package ca.bkaw.praeter.gui;

import ca.bkaw.praeter.core.resources.draw.CompositeDrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.font.GuiBackgroundPainter;

import java.util.function.IntConsumer;

/**
 * A utility class for guis.
 */
public final class GuiUtils {
    /**
     * The width/height of a slot, measured in pixels.
     */
    public static final int SLOT_SIZE = 18;

    /**
     * A {@link DrawOrigin} for the top-left pixel of the top-left slot (0, 0).
     */
    public static final DrawOrigin GUI_SLOT_ORIGIN = new DrawOrigin() {
        @Override
        public String toString() {
            return "GUI_SLOT_ORIGIN";
        }
    };

    /**
     * A {@link DrawOrigin} for the top-left pixel of the gui window.
     */
    public static final DrawOrigin GUI_WINDOW_ORIGIN = new CompositeDrawOrigin(
        GUI_SLOT_ORIGIN,
        -GuiBackgroundPainter.HORIZONTAL_PADDING,
        -GuiBackgroundPainter.TOP_PADDING
    );

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
     * the component.
     *
     * @param component The component.
     * @param slotConsumer A consumer called with each slot.
     */
    public static void forEachSlot(GuiComponent component, IntConsumer slotConsumer) {
        forEachSlot(
            component.getX(),
            component.getY(),
            component.getWidth(),
            component.getHeight(),
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

    /**
     * Get the slot index in a 9-slot-wide inventory from a pair of coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The slot.
     */
    public static int getSlot(int x, int y) {
        return y * 9 + x;
    }
}
