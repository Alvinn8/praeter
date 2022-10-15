package ca.bkaw.praeter.framework.gui.components;

import ca.bkaw.praeter.framework.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.GuiComponent;
import ca.bkaw.praeter.framework.gui.GuiComponentType;

/**
 * A button in a {@link CustomGui}.
 */
public class Button extends GuiComponent {
    /**
     * The {@link GuiComponentType} for a {@link Button}.
     */
    public static class Type extends GuiComponentType<Button> {
        /**
         * Create a new type for {@link Button}.
         *
         * @param x The x position of the component.
         * @param y The y position of the component.
         * @param width The width position of the component.
         * @param height The height position of the component.
         */
        public Type(int x, int y, int width, int height) {
            super(x, y, width, height);
        }

        @Override
        public Button create() {
            return new Button();
        }
    }
}
