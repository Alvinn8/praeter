package ca.bkaw.praeter.gui.component;

import ca.bkaw.praeter.gui.gui.ItemGuiRenderer;

/**
 * A {@link GuiComponentRenderer} that uses items to render components.
 * <p>
 * For usage with a {@link ItemGuiRenderer}.
 *
 * @param <C> The type of the {@link GuiComponent component} that this renderer
 *            will render.
 * @param <T> The type of the {@link GuiComponentType component type} this
 *            renderer will render.
 */
public interface ItemGuiComponentRenderer<C extends GuiComponent, T extends GuiComponentType<C, T>> extends GuiComponentRenderer<C, T> {
}
