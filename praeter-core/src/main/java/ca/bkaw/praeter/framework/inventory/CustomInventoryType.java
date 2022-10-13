package ca.bkaw.praeter.framework.inventory;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * A type of custom inventory.
 */
public class CustomInventoryType {
    private final Supplier<CustomInventory> constructor;
    private final List<InventoryComponentType<?>> componentTypes;
    private Plugin plugin;

    private CustomInventoryType(Supplier<CustomInventory> constructor,
                                List<InventoryComponentType<?>> componentTypes) {
        this.constructor = constructor;
        this.componentTypes = componentTypes;
    }

    /**
     * Get whether this custom inventory type has been registered.
     *
     * @return Whether registered.
     */
    public boolean isRegistered() {
        return this.plugin != null; // TODO registering
    }

    /**
     * Create a builder for a custom inventory type.
     *
     * @param constructor The function that constructs a new instance of the custom
     *                    inventory.
     * @return The builder.
     */
    public static Builder builder(Supplier<CustomInventory> constructor) {
        return new Builder(constructor);
    }

    /**
     * A builder for {@link CustomInventoryType}.
     *
     * @see #builder(Supplier)
     */
    public static class Builder {
        private final Supplier<CustomInventory> constructor;
        private final List<InventoryComponentType<?>> componentTypes = new ArrayList<>();

        private Builder(Supplier<CustomInventory> constructor) {
            this.constructor = constructor;
        }

        /**
         * Add {@link InventoryComponentType component types} to the inventory type.
         *
         * @param componentTypes The component types to add.
         * @return The builder, for chaining.
         */
        @Contract("_ -> this")
        public Builder add(InventoryComponentType<?>... componentTypes) {
            Collections.addAll(this.componentTypes, componentTypes);
            return this;
        }

        /**
         * Create the {@link CustomInventoryType} from this builder.
         *
         * @return The created {@link CustomInventoryType}.
         */
        public CustomInventoryType build() {
            return new CustomInventoryType(this.constructor, this.componentTypes);
        }
    }
}
