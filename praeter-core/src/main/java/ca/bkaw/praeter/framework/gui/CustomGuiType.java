package ca.bkaw.praeter.framework.gui;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * A type of custom gui.
 *
 * @see CustomGui
 */
public class CustomGuiType {
    private final Supplier<CustomGui> constructor;
    private final List<GuiComponentType<?>> componentTypes;
    private Plugin plugin;

    private CustomGuiType(Supplier<CustomGui> constructor,
                          List<GuiComponentType<?>> componentTypes) {
        this.constructor = constructor;
        this.componentTypes = componentTypes;
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
     * Create a builder for a custom gui type.
     *
     * @param constructor The function that constructs a new instance of the custom gui.
     * @return The builder.
     */
    public static Builder builder(Supplier<CustomGui> constructor) {
        return new Builder(constructor);
    }

    /**
     * A builder for {@link CustomGuiType}.
     *
     * @see #builder(Supplier)
     */
    public static class Builder {
        private final Supplier<CustomGui> constructor;
        private final List<GuiComponentType<?>> componentTypes = new ArrayList<>();

        private Builder(Supplier<CustomGui> constructor) {
            this.constructor = constructor;
        }

        /**
         * Add {@link GuiComponentType component types} to the inventory type.
         *
         * @param componentTypes The component types to add.
         * @return The builder, for chaining.
         */
        @Contract("_ -> this")
        public Builder add(GuiComponentType<?>... componentTypes) {
            Collections.addAll(this.componentTypes, componentTypes);
            return this;
        }

        /**
         * Create the {@link CustomGuiType} from this builder.
         *
         * @return The created {@link CustomGuiType}.
         */
        public CustomGuiType build() {
            return new CustomGuiType(this.constructor, this.componentTypes);
        }
    }
}
