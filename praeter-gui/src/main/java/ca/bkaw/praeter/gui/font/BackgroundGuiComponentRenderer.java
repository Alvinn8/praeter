package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.gui.CustomGuiType;

import java.io.IOException;

/**
 * A {@link GuiComponentRenderer} that renders components by drawing them on the
 * background.
 *
 * @param <C> The type of the {@link GuiComponent component} that this renderer
 *            will render.
 * @param <T> The type of the {@link GuiComponentType component type} this
 *            renderer will render.
 */
public interface BackgroundGuiComponentRenderer<C extends GuiComponent, T extends GuiComponentType<C, T>> extends GuiComponentRenderer<C, T> {
    /**
     * Draw the component on the background.
     * <p>
     * This will only be called during startup. It is not possible to change what is
     * rendered on the background dynamically.
     *
     * @param customGuiType The custom gui type the component type is a part of.
     * @param componentType The component type this renderer should render.
     * @param background The background where drawing operations can be performed.
     */
    void draw(CustomGuiType customGuiType, T componentType, GuiBackgroundPainter background) throws IOException;
}
