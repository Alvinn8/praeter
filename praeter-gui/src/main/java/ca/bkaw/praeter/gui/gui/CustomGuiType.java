package ca.bkaw.praeter.gui.gui;

import ca.bkaw.praeter.gui.PraeterGui;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentLike;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * A type of custom gui.
 *
 * @see CustomGui
 */
public class CustomGuiType {
    private final CustomGuiRenderer renderer = new CustomGuiRenderer();
    private Plugin plugin;
    private final List<GuiComponent> components;
    private final int height;
    private final Component title;

    private CustomGuiType(List<GuiComponent> components,
                          int height,
                          Component title) {
        this.components = ImmutableList.copyOf(components);
        this.height = height;
        this.title = title;
    }

    /**
     * Get whether this custom gui type has been registered.
     *
     * @return Whether registered.
     */
    public boolean isRegistered() {
        return this.plugin != null;
    }

    /**
     * Get the plugin that registered the custom gui type.
     *
     * @return The plugin
     * @throws IllegalStateException If the custom gui type has not been registered yet.
     */
    public Plugin getPlugin() {
        if (this.plugin == null) {
            throw new IllegalStateException("The custom gui type has not been registered.");
        }
        return this.plugin;
    }

    /**
     * Set the plugin that registered the custom gui type.
     * <p>
     * This method is for internal use. Use {@link ca.bkaw.praeter.core.Registry#register(Object, NamespacedKey, Plugin)}
     * on {@link PraeterGui#getGuiRegistry()} to register the gui.
     *
     * @param plugin The plugin.
     */
    @ApiStatus.Internal
    public void setPlugin(Plugin plugin) {
        if (this.plugin != null) {
            throw new IllegalStateException("Cannot change the registered plugin.");
        }
        this.plugin = plugin;
    }

    /**
     * Get a list of components.
     *
     * @return The list.
     */
    @Unmodifiable
    public List<GuiComponent> getComponents() {
        return this.components;
    }

    /**
     * Get the height of this gui, also known as the amount of rows.
     *
     * @return The height.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Get the title of the gui.
     * <p>
     * If this is null, the {@link CustomGui} subclass must override the method
     * {@link CustomGui#getTitle()}. For example to provide a dynamic title.
     *
     * @return The title.
     */
    @Nullable
    public Component getTitle() {
        return this.title;
    }

    /**
     * Get the renderer that is responsible for rendering this custom gui type.
     *
     * @return The renderer.
     */
    public CustomGuiRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Create a builder for a custom gui type.
     *
     * @return The builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder for {@link CustomGuiType}.
     *
     * @see #builder()
     */
    public static class Builder {
        private final List<GuiComponent> components = new ArrayList<>();
        private int height = 6;
        private Component title;

        private Builder() {}

        /**
         * Add {@link GuiComponent}s to the inventory type.
         *
         * @param components The components to add.
         * @return The builder, for chaining.
         */
        @Contract("_ -> this")
        public Builder add(GuiComponentLike... components) {
            for (GuiComponentLike componentLike : components) {
                if (componentLike instanceof GuiComponent component) {
                    this.components.add(component);
                } else {
                    componentLike.addTo(this);
                }
            }
            return this;
        }

        /**
         * Set the height of the gui, also known as the amount of rows.
         *
         * @param height The height. [1-6] (inclusive, inclusive)
         * @return The builder, for chaining.
         */
        @Contract("_ -> this")
        public Builder height(int height) {
            this.height = height;
            return this;
        }

        /**
         * Set the title of the gui.
         * <p>
         * The title can also be changed dynamically by overriding
         * {@link CustomGui#getTitle()}.
         *
         * @param title The title.
         * @return The builder, for chaining.
         * @see CustomGui#getTitle()
         */
        @Contract("_ -> this")
        public Builder title(@Nullable Component title) {
            this.title = title;
            return this;
        }

        /**
         * Create the {@link CustomGuiType} from this builder.
         *
         * @return The created {@link CustomGuiType}.
         */
        public CustomGuiType build() {
            return new CustomGuiType(this.components, this.height, this.title);
        }
    }
}
