package ca.bkaw.praeter.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A registry that maps an {@link NamespacedKey id} to a value.
 *
 * @param <T> The type of the thing being registered.
 */
public class Registry<T> {
    protected final BiMap<NamespacedKey, T> map = HashBiMap.create();

    /**
     * Register the value as the specified unique id.
     *
     * @param value he value to register.
     * @param id The id to register as.
     * @param plugin The plugin that is registering the value.
     * @throws IllegalArgumentException If the id is occupied.
     */
    public void register(@NotNull T value, @NotNull NamespacedKey id, @NotNull Plugin plugin) {
        if (this.map.containsKey(id)) {
            throw new IllegalArgumentException("An value is already registered " +
                "with the id: " + id);
        }
        if (this.map.containsValue(value)) {
            throw new IllegalArgumentException("The value has already been registered with id "
                + this.getId(value) + " but attempted to register it again with id " + id);
        }
        this.map.put(id, value);
    }

    /**
     * Get the id of the registered value.
     *
     * @param value The value to get the id of.
     * @return The id.
     * @throws IllegalArgumentException If the value had not been registered.
     */
    @NotNull
    public NamespacedKey getId(T value) {
        if (!this.map.containsValue(value)) {
            throw new IllegalArgumentException("The value has not been registered.");
        }
        return this.map.inverse().get(value);
    }
}
