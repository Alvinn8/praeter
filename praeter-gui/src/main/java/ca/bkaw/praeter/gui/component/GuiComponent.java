package ca.bkaw.praeter.gui.component;

import ca.bkaw.praeter.gui.gui.CustomGui;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A component inside a {@link CustomGui}.
 * <p>
 * Instances of this class are created for each component in a {@link CustomGui}.
 *
 * @see GuiComponentType
 */
public class GuiComponent {
    private Consumer<GuiClickContext> clickHandler;

    /**
     * Set the callback to call when the user clicks the component.
     * <p>
     * There can only be one handler. Calling this twice will override the previous
     * handler.
     *
     * @param clickHandler The click handler.
     */
    public void setOnClick(Consumer<GuiClickContext> clickHandler) {
        this.clickHandler = clickHandler;
    }

    /**
     * Get the consumer to call when the component is clicked.
     *
     * @return The click handler.
     */
    @Nullable
    public Consumer<GuiClickContext> getClickHandler() {
        return this.clickHandler;
    }
}
