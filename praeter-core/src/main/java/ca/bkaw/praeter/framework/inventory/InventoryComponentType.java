package ca.bkaw.praeter.framework.inventory;

/**
 * A component that exists in a {@link CustomInventoryType}.
 * <p>
 * Component types are created when the {@link CustomInventoryType inventory type}
 * is created.
 * <p>
 * Instances of {@link InventoryComponent} are created for each component in each
 * {@link CustomInventory} that is created.
 *
 * @see InventoryComponent
 * @see CustomInventoryType
 */
public abstract class InventoryComponentType<T extends InventoryComponent> {
    private final int x, y, width, height;

    /**
     * Create a new {@link InventoryComponentType}.
     *
     * @param x The x position of the component.
     * @param y The y position of the component.
     * @param width The width position of the component.
     * @param height The height position of the component.
     */
    public InventoryComponentType(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract T create();
}
