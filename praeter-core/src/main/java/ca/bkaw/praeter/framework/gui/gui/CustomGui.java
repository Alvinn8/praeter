package ca.bkaw.praeter.framework.gui.gui;

import ca.bkaw.praeter.framework.gui.component.ComponentMap;
import ca.bkaw.praeter.framework.gui.component.GuiComponent;
import ca.bkaw.praeter.framework.gui.component.GuiComponentType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    private final CustomGuiRenderer renderer;
    private final ComponentMap components = new ComponentMap();
    private Inventory inventory;
    private Component currentRenderTitle;

    /**
     * Create a new {@link CustomGui} instance.
     *
     * @param type The type of custom inventory. Usually from a static constant.
     * @param renderer The renderer to use for this custom gui.
     */
    public CustomGui(CustomGuiType type, CustomGuiRenderer renderer) {
        this.type = type;
        this.renderer = renderer;
        this.create();
    }

    /**
     * Open the gui for the specified player.
     *
     * @param player The player to open the gui for.
     */
    public void show(Player player) {
        player.openInventory(this.inventory);
    }

    private void create() {
        for (GuiComponentType<?, ?> componentType : this.type.getComponentTypes()) {
            this.createComponent(componentType);
        }
        this.update();
    }

    private <C extends GuiComponent> void createComponent(GuiComponentType<C, ?> componentType) {
        // This method is used in the constructor and
        // is required for generics to line up
        this.components.put(componentType, componentType.create());
    }

    /**
     * Re-render the gui.
     * <p>
     * This will re-render all components.
     */
    public void update() {
        // Create the title to use
        Component renderTitle = this.renderer.getRenderTitle(this.getTitle(), this);

        // In case the title has changed we need to recreate the inventory
        // and open it again for all viewers
        List<HumanEntity> viewers = null;
        if (!renderTitle.equals(this.currentRenderTitle) && this.inventory != null) {
            viewers = this.inventory.getViewers();
            this.inventory = null;
        }

        if (this.inventory == null) {
            // Create the inventory
            int slotCount = this.type.getHeight() * 9;
            CustomGuiHolder holder = new CustomGuiHolder(this);
            this.inventory = Bukkit.createInventory(holder, slotCount, renderTitle);
            this.currentRenderTitle = renderTitle;

            // If the inventory was recreated with a new title,
            // open the new inventory for the viewers
            if (viewers != null) {
                viewers.forEach(viewer -> viewer.openInventory(this.inventory));
            }
        }

        // Clear the items
        this.inventory.clear();

        // Let components render items
        this.components.forEach(this::renderComponent);
    }

    private <C extends GuiComponent, T extends GuiComponentType<C, T>>
    void renderComponent(T componentType, C component) {
        // generics can not be used in lambda methods, but method references are okay
        // so this is a method
        componentType.getRenderer().renderItems(this.type, this, componentType,
            component, this.inventory);
    }

    /**
     * Get the title to use for this custom gui.
     * <p>
     * Subclasses can override this method to provide a dynamic title.
     * <p>
     * If this method is not overridden, a title must be set in the custom gui type
     * using {@link CustomGuiType.Builder#title(Component)}.
     *
     * @return The title.
     */
    @NotNull
    public Component getTitle() {
        Component title = this.type.getTitle();
        if (title == null) {
            throw new RuntimeException("No title for custom gui. Please either set a title " +
                "in the custom gui type builder using title(), or override the getTitle() " +
                "method in CustomGui to create a dynamic title.");
        }
        return title;
    }

    /**
     * Get the {@link CustomGuiType} of this custom gui.
     *
     * @return The type.
     */
    @NotNull
    public CustomGuiType getType() {
        return this.type;
    }

    /**
     * Get the {@link GuiComponent} from the {@link GuiComponentType}.
     * <p>
     * The component type must be registered in this {@link #getType() gui type} by
     * calling {@link CustomGuiType.Builder#add(GuiComponentType[])}.
     *
     * @param componentType The component type.
     * @param <T> The class of the component.
     * @return The component.
     * @throws IllegalArgumentException If the component type was not registered.
     */
    @NotNull
    public <T extends GuiComponent> T get(GuiComponentType<T, ?> componentType) {
        T component = this.components.get(componentType);
        if (component == null) {
            throw new IllegalArgumentException("The component type did not exist in this " +
                "custom gui, did you forget to add it to the custom gui type?");
        }
        return component;
    }

    /**
     * Get the inventory that is rendering this custom gui.
     *
     * @return The inventory.
     */
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Get the component type at the specified coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The component type.
     */
    @Nullable
    public GuiComponentType<?, ?> getComponentTypeAt(int x, int y) {
        for (GuiComponentType<?, ?> c : this.components.getComponentTypes()) {
            if (x >= c.getX() && x < c.getX() + c.getWidth()
            &&  y >= c.getY() && y < c.getY() + c.getHeight()) {
                return c;
            }
        }
        return null;
    }
}
