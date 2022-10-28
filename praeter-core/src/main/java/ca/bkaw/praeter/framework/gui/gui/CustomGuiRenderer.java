package ca.bkaw.praeter.framework.gui.gui;

import ca.bkaw.praeter.framework.gui.component.GuiComponent;
import net.kyori.adventure.text.Component;

/**
 * A renderer responsible for rendering a {@link CustomGui custom gui} and its
 * {@link GuiComponent components}.
 *
 * @see ItemGuiRenderer
 */
public interface CustomGuiRenderer {
    /**
     * A method called during startup when the custom gui type is being created.
     * <p>
     * This allows for preparation that needs to be performed during startup, for
     * example to allow renderers from {@code praeter-resources} to generate textures
     * and other assets that need to be included in resource packs.
     *
     * @param customGuiType The custom gui type.
     */
    void onSetup(CustomGuiType customGuiType);

    /**
     * Get the final title to use for the gui.
     * <p>
     * If {@code praeter-resources} is used, custom fonts can be used in the title can
     * be used for rendering purposes.
     *
     * @param title The title that should be displayed for the gui.
     * @param customGui The custom gui that is being rendered.
     * @return The final, rendered, title.
     */
    Component getRenderTitle(Component title, CustomGui customGui);
}
