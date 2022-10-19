package ca.bkaw.praeter.framework.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

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
    }

    /**
     * Open the gui for the specified player.
     *
     * @param player The player to open the gui for.
     */
    public void show(Player player) {
        if (this.inventory == null) {
            this.create();
        }
        player.openInventory(this.inventory);
    }

    private void create() {
        for (GuiComponentType<?, ?> componentType : this.type.getComponentTypes()) {
            this.createComponent(componentType);
        }
        this.render();
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
    public void render() {
        Component renderTitle = this.renderer.getRenderTitle(this.getTitle(), this);

        // In case the title has changed we need to recreate the inventory
        // and open it again for all viewers
        List<HumanEntity> viewers = null;
        if (!renderTitle.equals(this.currentRenderTitle)) {
            viewers = this.inventory.getViewers();
            this.inventory = null;
        }

        if (this.inventory == null) {
            // todo inventory holder
            this.inventory = Bukkit.createInventory(null, this.type.getHeight(), renderTitle);
            // If the inventory was recreated with a new title,
            // open the new inventory for the viewers
            if (viewers != null) {
                viewers.forEach(viewer -> viewer.openInventory(this.inventory));
            }
        }

        this.inventory.clear();

        this.components.forEach(new ComponentMap.ForEachConsumer() {
            @Override
            public <C extends GuiComponent, T extends GuiComponentType<C, T>> void accept(GuiComponentType<C, T> componentType, C component) {
                // todo
                componentType.getRenderer().render(type, (T) componentType, CustomGui.this, component, inventory);
            }
        });
    }

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
    public <T extends GuiComponent> T get(GuiComponentType<T, ?> componentType) {
        T component = this.components.get(componentType);
        if (component == null) {
            throw new IllegalArgumentException("The component type did not exist in this " +
                "custom gui, did you forget to add it to the custom gui type?");
        }
        return component;
    }
}
