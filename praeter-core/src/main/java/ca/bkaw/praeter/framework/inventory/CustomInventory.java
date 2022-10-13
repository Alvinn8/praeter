package ca.bkaw.praeter.framework.inventory;

/**
 * A custom inventory.
 * <p>
 * Instances of subclasses to this class will be created for each custom inventory
 * that is opened.
 *
 * @see CustomInventoryType
 */
public abstract class CustomInventory {
    private final CustomInventoryType type;

    /**
     * Create a new {@link CustomInventory} instance.
     *
     * @param type The type of custom inventory. Usually from a static constant.
     */
    public CustomInventory(CustomInventoryType type) {
        this.type = type;
    }

    public <T extends InventoryComponent> T get(InventoryComponentType<T> componentType) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
