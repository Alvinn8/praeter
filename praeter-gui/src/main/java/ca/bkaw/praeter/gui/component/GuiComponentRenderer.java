package ca.bkaw.praeter.gui.component;

import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiRenderer;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.inventory.Inventory;

import java.util.function.IntConsumer;

/**
 * A renderer for a {@link GuiComponentType} that decides how to show the
 * component in the inventory.
 * <p>
 * Multiple renderers can exist for the same component type to allow different
 * looks and styles.
 * <p>
 * If {@code praeter-resources} is used, renderers from that module will render
 * components using a resource pack.
 *
 * @param <C> The type of the {@link GuiComponent component} that this renderer
 *            will render.
 * @param <T> The type of the {@link GuiComponentType component type} this
 *            renderer will render.
 */
public interface GuiComponentRenderer<C extends GuiComponent, T extends GuiComponentType<C, T>> {
    /**
     * Render items in the inventory.
     * <p>
     * This method is called regardless of what {@link CustomGuiRenderer} is used.
     * <p>
     * It is common to use {@link GuiUtils#forEachSlot(GuiComponentType, IntConsumer)}
     * to loop trough each slot and place items.
     *
     * @param customGuiType The custom gui type.
     * @param customGui The custom gui.
     * @param componentType The component type.
     * @param component The component to render.
     * @param inventory The inventory to place items in.
     */
    void renderItems(CustomGuiType customGuiType,
                     CustomGui customGui,
                     T componentType,
                     C component,
                     Inventory inventory);
}
