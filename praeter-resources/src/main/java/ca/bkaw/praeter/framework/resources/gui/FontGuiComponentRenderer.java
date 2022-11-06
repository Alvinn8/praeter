package ca.bkaw.praeter.framework.resources.gui;

import ca.bkaw.praeter.framework.gui.component.GuiComponent;
import ca.bkaw.praeter.framework.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.component.GuiComponentType;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiType;

/**
 * A {@link GuiComponentRenderer} that renders components using custom fonts.
 * <p>
 * For usage with a {@link FontGuiRenderer}.
 *
 * @param <C> The type of the {@link GuiComponent component} that this renderer
 *            will render.
 * @param <T> The type of the {@link GuiComponentType component type} this
 *            renderer will render.
 */
public interface FontGuiComponentRenderer<C extends GuiComponent, T extends GuiComponentType<C, T>> extends GuiComponentRenderer<C, T> {
    /**
     * A method called during startup when the custom gui type is being created.
     * <p>
     * This allows for preparation that needs to be performed during startup, for
     * example to allow generation of textures and other assets that need to be
     * included in resource packs.
     *
     * @param customGuiType The custom gui type the component type is a part of.
     * @param componentType The component type this renderer should render.
     * @param context The render setup context that can be used to use custom fonts.
     */
    void onSetup(CustomGuiType customGuiType, T componentType, RenderSetupContext context);

}
