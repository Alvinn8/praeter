package ca.bkaw.praeter.gui.component;

import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.jetbrains.annotations.ApiStatus;

/**
 * Something that can be represented as one or more {@link GuiComponent}s.
 */
public interface GuiComponentLike {
    /**
     * Add the component or components to the builder.
     *
     * @param builder The builder.
     */
    @ApiStatus.Internal
    void addTo(CustomGuiType.Builder builder);
}
