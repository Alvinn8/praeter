package ca.bkaw.praeter.framework.inventory.components;

import ca.bkaw.praeter.framework.inventory.CustomInventory;
import ca.bkaw.praeter.framework.inventory.InventoryComponent;
import ca.bkaw.praeter.framework.inventory.InventoryComponentType;

/**
 * A button in a {@link CustomInventory} that can be enabled and disabled.
 * <p>
 * When the button is disabled the click handler will not be called. <!-- TODO -->
 */
public class ToggleButton extends InventoryComponent {
    /**
     * Create a {@link InventoryComponentType} for a {@link ToggleButton}.
     */
    public static class Type extends InventoryComponentType<ToggleButton> {
        /**
         * Create a new type for {@link ToggleButton}.
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
