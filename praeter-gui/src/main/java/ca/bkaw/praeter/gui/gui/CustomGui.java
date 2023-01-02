package ca.bkaw.praeter.gui.gui;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentLike;
import ca.bkaw.praeter.gui.components.Slot;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final Map<GuiComponent, GuiComponent.State> components = new HashMap<>();
    private final List<Slot.State> slots;
    @Nullable
    private Inventory inventory;
    @Nullable
    private Component currentRenderTitle;
    @Nullable
    private Player pendingPlayer;
    private boolean isReopening;

    /**
     * Create a new {@link CustomGui} instance.
     *
     * @param type The type of custom inventory. Usually from a static constant.
     */
    public CustomGui(CustomGuiType type) {
        this.type = type;
        if (!this.type.isRegistered()) {
            throw new IllegalStateException("Tried to create a custom gui that was not registered.");
        }
        ImmutableList.Builder<Slot.State> slots = ImmutableList.builder();
        for (GuiComponent component : this.type.getComponents()) {
            GuiComponent.State state = component.createState();
            this.components.put(component, state);
            if (state instanceof Slot.State slot) {
                slots.add(slot);
            }
        }
        this.slots = slots.build();
    }

    /**
     * Called when the player closes the gui.
     *
     * @param player The player that closed the gui.
     * @param event The event that contains more details about the close.
     */
    public void onClose(Player player, InventoryCloseEvent event) {}

    /**
     * Open the gui for the specified player.
     *
     * @param player The player to open the gui for.
     */
    public void show(Player player) {
        if (this.inventory == null || this.currentRenderTitle == null) {
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

        // In case the title has changed we need to recreate the inventory
        // and open it again for all viewers
        List<HumanEntity> viewers = null;
        if (!Objects.equals(this.currentRenderTitle, renderTitle) && this.inventory != null) {
            viewers = new ArrayList<>(this.inventory.getViewers());
            this.inventory = null;
        }

        if (this.inventory == null) {
            // Create the inventory
            int slotCount = this.type.getHeight() * 9;
            CustomGuiHolder holder = new CustomGuiHolder(this);
            // The render title may be null if the gui is being rendered before it is shown
            // to a player. In that case, set the currentRenderTitle to null, but render an
            // empty component (because it may not be null)
            this.currentRenderTitle = renderTitle;
            if (renderTitle == null) {
                renderTitle = Component.empty();
            }
            this.inventory = Bukkit.createInventory(holder, slotCount, renderTitle);
        }

        // Clear the items
        this.inventory.clear();

        // Let components render items
        for (GuiComponent.State state : this.components.values()) {
            state.renderItems(this.inventory);
        }

        // If the inventory was recreated with a new title,
        // open the new inventory for the viewers
        if (viewers != null) {
            this.isReopening = true;
            viewers.forEach(viewer -> viewer.openInventory(this.inventory));
            this.isReopening = false;
        }
    }

    /**
     * Get a list of players that are viewing the gui.
     *
     * @return The list of players.
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
     * Get the {@link GuiComponent.State} from the {@link GuiComponent.State}.
     * <p>
     * The component must be registered in this {@link #getType() gui type} by
     * calling {@link CustomGuiType.Builder#add(GuiComponentLike...)}.
     * <p>
     * Using {@link GuiComponent#get(CustomGui)} is preferred over this method
     * due to it providing the correct return type for the state.
     *
     * @param component The component.
     * @return The component state.
     * @throws IllegalArgumentException If the component was not registered.
     */
    @ApiStatus.Internal
    @NotNull
    public GuiComponent.State getStateFor(@NotNull GuiComponent component) {
        GuiComponent.State state = this.components.get(component);
        if (state == null) {
            throw new IllegalArgumentException("The component did not exist in this " +
                "custom gui, did you forget to add it to the custom gui type?");
        }
        return state;
    }

    /**
     * Get whether the inventory that is displaying this gui is currently being
     * reopened. This flag is used to determine whether the inventory was closed, or
     * whether the player closed the gui.
     *
     * @return Whether reopening.
     */
    @ApiStatus.Internal
    public boolean isReopening() {
        return this.isReopening;
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
     * Get the component at the specified coordinates.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The component.
     */
    @Nullable
    public GuiComponent getComponentAt(int x, int y) {
        for (GuiComponent c : this.components.keySet()) {
            if (x >= c.getX() && x < c.getX() + c.getWidth()
            &&  y >= c.getY() && y < c.getY() + c.getHeight()) {
                return c;
            }
        }
        return null;
    }

    /**
     * Get an immutable list of the slots in the gui.
     *
     * @return The list of slots.
     */
    @Unmodifiable
    public List<Slot.State> getSlots() {
        return this.slots;
    }

    /**
     * Get the {@link Slot.State} at the specified position.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The slot, or null.
     */
    @Nullable
    public Slot.State getSlot(int x, int y) {
        GuiComponent component = this.getComponentAt(x, y);
        if (component == null) {
            return null;
        }
        GuiComponent.State state = component.get(this);
        if (state instanceof Slot.State slot) {
            return slot;
        }
        return null;
    }
}
