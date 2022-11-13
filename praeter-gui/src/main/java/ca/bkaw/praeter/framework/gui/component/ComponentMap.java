package ca.bkaw.praeter.framework.gui.component;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An object that maps component types to a component in a generic-safe way.
 */
public class ComponentMap {
    private final Map<GuiComponentType<?, ?>, GuiComponent> map = new HashMap<>();

    /**
     * Put a component type to the component.
     *
     * @param type The component type.
     * @param component The component to map to.
     * @param <C> The type of the component.
     */
    public <C extends GuiComponent> void put(GuiComponentType<C, ?> type, C component) {
        this.map.put(type, component);
    }

    /**
     * Get a component from a component type.
     *
     * @param type The component type.
     * @param <C> The type of the component.
     * @return The component.
     */
    @Nullable
    public <C extends GuiComponent> C get(GuiComponentType<C, ?> type) {
        // This is safe because we only insert into the map where the generics line up
        // noinspection unchecked
        return (C) this.map.get(type);
    }

    /**
     * A consumer used by {@link #forEach(ForEachConsumer)}.
     */
    public interface ForEachConsumer {
        /**
         * Consume a pair in a component map.
         *
         * @param componentType The component type.
         * @param component The component.
         * @param <C> The type of the component.
         * @param <T> The type of the component type.
         */
        <C extends GuiComponent, T extends GuiComponentType<C, T>> void accept(T componentType, C component);
    }

    /**
     * Loop trough each entry in the map.
     *
     * @param consumer The consumer for the component type and component.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void forEach(ForEachConsumer consumer) {
        this.map.forEach(((componentType0, component) ->
            // Ugly cast to a raw type, but we know the generics will line up
            consumer.accept((GuiComponentType) componentType0, component)
        ));
    }

    /**
     * Get an unmodifiable set of the component types stored as keys in this map.
     *
     * @return The set.
     */
    public Set<GuiComponentType<?, ?>> getComponentTypes() {
        return Collections.unmodifiableSet(this.map.keySet());
    }
}
