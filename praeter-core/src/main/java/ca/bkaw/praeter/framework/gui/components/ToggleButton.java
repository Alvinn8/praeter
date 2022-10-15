package ca.bkaw.praeter.framework.gui.components;

import ca.bkaw.praeter.framework.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.GuiComponent;
import ca.bkaw.praeter.framework.gui.GuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.GuiComponentType;

/**
 * A button in a {@link CustomGui} that can be enabled and disabled.
 * <p>
 * When the button is disabled the click handler will not be called. <!-- TODO -->
 */
public class ToggleButton extends GuiComponent {
    /**
     * Create a {@link GuiComponentType} for a {@link ToggleButton}.
     */
    public static class Type extends GuiComponentType<ToggleButton> {
        /**
         * Create a new type for {@link ToggleButton}.
         *
         * @param renderer The renderer for the component.
         * @param x The x position of the component.
         * @param y The y position of the component.
         * @param width The width position of the component.
         * @param height The height position of the component.
         */
        public Type(GuiComponentRenderer<ToggleButton, ToggleButton.Type> renderer, int x, int y, int width, int height) {
            super(renderer, x, y, width, height);
        }

        @Override
        public ToggleButton create() {
            return new ToggleButton();
        }
    }

    private boolean enabled = true;

    /**
     * Get whether the button is enabled.
     * <p>
     * When a button is not enabled the click handler will not be called. <!-- TODO -->
     *
     * @return Whether enabled.
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Set whether the button is enabled.
     *
     * @param enabled Whether the button should be enabled or not.
     * @see #isEnabled()
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
