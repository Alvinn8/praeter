package ca.bkaw.praeter.framework.gui.gui;

import ca.bkaw.praeter.framework.gui.component.GuiComponentType;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A type of custom gui.
 *
 * @see CustomGui
 */
public class CustomGuiType {
    private Plugin plugin;
    private final List<GuiComponentType<?, ?>> componentTypes;
    private final int height;
    private final Component title;
    private final CustomGuiRenderer renderer;

    private CustomGuiType(List<GuiComponentType<?, ?>> componentTypes,
                          int height,
                          Component title,
                          CustomGuiRenderer renderer) {
        this.componentTypes = ImmutableList.copyOf(componentTypes);
        this.height = height;
        this.title = title;
        this.renderer = renderer;
    }

    /**
     * Get whether this custom gui type has been registered.
     *
     * @return Whether registered.
     */
    public boolean isRegistered() {
        return this.plugin != null; // TODO registering
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
     * This method is for internal use. Use TODO to register the gui.
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
     * Get a collection of component types.
     *
     * @return The collection.
     */
    @Unmodifiable
    public Collection<GuiComponentType<?, ?>> getComponentTypes() {
        return this.componentTypes;
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
        private final List<GuiComponentType<?, ?>> componentTypes = new ArrayList<>();
        private int height = 6;
        private Component title;
        private CustomGuiRenderer renderer;

        private Builder() {}

        /**
         * Add {@link GuiComponentType component types} to the inventory type.
         *
         * @param componentTypes The component types to add.
         * @return The builder, for chaining.
         */
        @Contract("_ -> this")
        public Builder add(GuiComponentType<?, ?>... componentTypes) {
            Collections.addAll(this.componentTypes, componentTypes);
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
         * Set the renderer of the gui.
         * <p>
         * If no renderer is set, a renderer will be chosen automatically based on the gui
         * component renderers.
         *
         * @param renderer The renderer.
         * @return The builder, for chaining.
         */
        @Contract("_ -> this")
        public Builder renderer(CustomGuiRenderer renderer) {
            this.renderer = renderer;
            return this;
        }

        /**
         * Create the {@link CustomGuiType} from this builder.
         *
         * @return The created {@link CustomGuiType}.
         */
        public CustomGuiType build() {
            if (this.renderer == null) {
                // No renderer was set by the user
                // TODO determine one.
                throw new IllegalStateException("No gui renderer was set");
            }
            return new CustomGuiType(this.componentTypes, this.height, this.title,
                this.renderer);
        }
    }
}
