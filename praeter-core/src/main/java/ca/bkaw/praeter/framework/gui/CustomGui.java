package ca.bkaw.praeter.framework.gui;

/**
 * A custom graphical user interface.
 * <p>
 * Instances of subclasses to this class will be created for each custom gui that
 * is opened.
 *
 * @see CustomGuiType
 */
public abstract class CustomGui {
    private final CustomGuiType type;

    /**
     * Create a new {@link CustomGui} instance.
     *
     * @param type The type of custom inventory. Usually from a static constant.
     */
    public CustomGui(CustomGuiType type) {
        this.type = type;
    }

    public <T extends GuiComponent> T get(GuiComponentType<T> componentType) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
