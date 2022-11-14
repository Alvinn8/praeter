package ca.bkaw.praeter.gui.gui;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.gui.component.ComponentMap;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    private final ComponentMap components = new ComponentMap();
    @Nullable
    private Inventory inventory;
    @Nullable
    private Component currentRenderTitle;
    @Nullable
    private Player pendingPlayer;

    /**
     * Create a new {@link CustomGui} instance.
     *
     * @param type The type of custom inventory. Usually from a static constant.
     */
    public CustomGui(CustomGuiType type) {
        this.type = type;
        for (GuiComponentType<?, ?> componentType : this.type.getComponentTypes()) {
            this.createComponent(componentType);
        }
    }

    private <C extends GuiComponent> void createComponent(GuiComponentType<C, ?> componentType) {
        // This method is used in the constructor
        // and is required for generics to line up
        this.components.put(componentType, componentType.create());
    }

    /**
     * Open the gui for the specified player.
     *
     * @param player The player to open the gui for.
     */
    public void show(Player player) {
        if (this.inventory == null) {
            // We must set pending player to allow the getPlayers method to be populated
            // with the player that is about to view the gui. This allows getting the
            // correct baked resource pack.
            this.pendingPlayer = player;
            this.update();
            this.pendingPlayer = null;
        }
        player.openInventory(this.inventory);
    }

    /**
     * Re-render the gui.
     * <p>
     * This will re-render all components.
     */
    public void update() {
        // Create the title to use
        Component renderTitle = this.type.getRenderer().getRenderTitle(this.getTitle(), this);
        Praeter.get().getLogger().info(GsonComponentSerializer.gson().serialize(renderTitle)); // TODO debug code

        // In case the title has changed we need to recreate the inventory
        // and open it again for all viewers
        List<HumanEntity> viewers = null;
        if (!renderTitle.equals(this.currentRenderTitle) && this.inventory != null) {
            viewers = new ArrayList<>(this.inventory.getViewers());
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
     * Get a list of players that are viewing the gui.
     *
     * @return The list of playrs.
     */
    public List<Player> getViewers() {
        List<Player> players = new ArrayList<>();
        if (this.inventory != null) {
            for (HumanEntity viewer : this.inventory.getViewers()) {
                if (viewer instanceof Player player) {
                    players.add(player);
                }
            }
        }
        if (this.pendingPlayer != null && !players.contains(this.pendingPlayer)) {
            players.add(this.pendingPlayer);
        }
        return players;
    }

    /**
     * Loop trough each component in the gui.
     *
     * @param consumer The consumer for the entries.
     */
    public void forEachComponent(ComponentMap.ForEachConsumer consumer) {
        this.components.forEach(consumer);
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
     * <p>
     * Will be null if the gui has not been rendered (updated) for the first time.
     *
     * @return The inventory, or null.
     */
    @Nullable
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
