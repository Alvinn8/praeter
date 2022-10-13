package ca.bkaw.praeter.framework.inventory.components;

import ca.bkaw.praeter.framework.inventory.CustomInventory;
import ca.bkaw.praeter.framework.inventory.InventoryComponent;
import ca.bkaw.praeter.framework.inventory.InventoryComponentType;

/**
 * A button in a {@link CustomInventory}.
 */
public class Button extends InventoryComponent {
    /**
     * The {@link InventoryComponentType} for a {@link Button}.
     */
    public static class Type extends InventoryComponentType<Button> {
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
