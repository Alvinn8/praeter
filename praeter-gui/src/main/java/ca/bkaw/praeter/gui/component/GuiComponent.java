package ca.bkaw.praeter.gui.component;

import ca.bkaw.praeter.core.ItemUtils;
import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.bake.BakedItemModel;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.font.RenderDispatcher;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * A component inside a {@link CustomGuiType}.
 * <p>
 * Components are created when the {@link CustomGuiType gui type} is created.
 * <p>
 * Instances of {@link GuiComponent.State} are created for each opened gui.
 */
public class GuiComponent implements GuiComponentLike {
    protected final int x, y, width, height;

    /**
     * Create a new {@link GuiComponent}.
     *
     * @param x The x position of the component, in slots.
     * @param y The y position of the component, in slots.
     * @param width The width of the component, in slots.
     * @param height The height of the component, in slots.
     */
    public GuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Create the {@link GuiComponent.State} for this component.
     * <p>
     * Subclasses that change the state class need to override this method and return
     * the new type.
     *
     * @return The created instance.
     */
    @ApiStatus.OverrideOnly
    public GuiComponent.State createState() {
        return new GuiComponent.State();
    }

    /**
     * Get the state of this component in the specified gui.
     * <p>
     * Subclasses that change the state class need to override this method and change
     * the return type.
     *
     * @param gui The gui.
     * @return The state.
     */
    public GuiComponent.State get(CustomGui gui) {
        return gui.getStateFor(this);
    }

    /**
     A method called during startup when the custom gui type is being created.
     * <p>
     * This allows for preparation that needs to be performed during startup, for
     * example to allow generation of textures and other assets that need to be
     * included in resource packs.
     *
     * @param context The render setup context.
     * @throws IOException If an I/O error occurs.
     */
    public void onSetup(RenderSetupContext context) throws IOException {}

    /**
     * Get the x coordinate, measured in slots from the top left.
     *
     * @return The coordinate.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get the y coordinate, measured in slots from the top left.
     *
     * @return The coordinate.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Get the width of the component, measured in slots.
     *
     * @return The width.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get the height of the component, measured in slots.
     *
     * @return The height.
     */
    public int getHeight() {
        return this.height;
    }

    @Override
    public void addTo(CustomGuiType.Builder builder) {
        builder.add(this);
    }

    /**
     * An object created for each opened gui.
     * <p>
     * The state holds data that can change during the lifespan of the server.
     * <p>
     * Get using {@link #get(CustomGui)}.
     * <p>
     * Subclasses can be static classes or inner classes.
     */
    public class State {
        private Consumer<GuiClickContext> clickHandler;
        protected List<Component> hoverText;

        /**
         * Set the callback to call when the user clicks the component.
         * <p>
         * There can only be one handler. Calling this twice will override the previous
         * handler.
         *
         * @param clickHandler The click handler.
         */
        public void setOnClick(Consumer<GuiClickContext> clickHandler) {
            this.clickHandler = clickHandler;
        }

        /**
         * Get the consumer to call when the component is clicked.
         *
         * @return The click handler.
         */
        @Nullable
        public Consumer<GuiClickContext> getClickHandler() {
            return this.clickHandler;
        }

        /**
         * Set the hover text of the component.
         * <p>
         * Some components may not support changing the hover text using this method.
         *
         * @param hoverText The hover text.
         */
        public void setHoverText(List<Component> hoverText) {
            this.hoverText = hoverText;
        }

        /**
         * Called when the component is being rendered.
         * <p>
         * Is called before {@link #renderItems(Inventory)} to create the title of the gui.
         *
         * @param renderDispatcher The render dispatcher where font sequences can be appended.
         */
        public void onRender(RenderDispatcher renderDispatcher) {}

        /**
         * Render the component by placing items.
         * <p>
         * It is common to use {@link GuiUtils#forEachSlot(GuiComponent, IntConsumer)} to
         * loop trough each slot and place items.
         *
         * @param inventory The inventory to place items in.
         */
        public void renderItems(Inventory inventory) {
            if (this.hoverText != null) {
                // Since the transparent item is added to all packs, getting it from main is
                // fine in this case, but somewhat ugly.
                BakedResourcePack main = Praeter.get().getResourceManager().getBakedPacks().getMain();
                BakedItemModel itemModel = main.getItemModel(ItemUtils.TRANSPARENT_ITEM);
                if (itemModel != null) {
                    // Should never be null
                    ItemStack item = new ItemStack(itemModel.material());
                    item.editMeta(meta -> meta.setCustomModelData(itemModel.customModelData()));
                    ItemUtils.setItemText(item, this.hoverText);
                    GuiUtils.forEachSlot(GuiComponent.this, slot -> inventory.setItem(slot, item));
                }
            }
        }

    }

}
