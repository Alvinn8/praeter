package ca.bkaw.praeter.framework.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * An object that maps component types to a component.
 */
public class ComponentMap {
    private final Map<GuiComponentType<?, ?>, GuiComponent> map = new HashMap<>();

    /**
     * Put a component type to the component.
     *
     * @param type The component type.
     * @param component The component to map to.
     * @param <T> The type of the component.
     */
    public <T extends GuiComponent> void put(GuiComponentType<T, ?> type, T component) {
        this.map.put(type, component);
    }

    /**
     * Get a component from a component type.
     *
     * @param type The component type.
     * @param <T> The type of the component.
     * @return The component.
     */
    public <T extends GuiComponent> T get(GuiComponentType<T, ?> type) {
        // This is safe because we only insert into the map where the generics line up
        // noinspection unchecked
        return (T) this.map.get(type);
    }

    // todo you can not use this as a lambda
    interface ForEachConsumer {
        <C extends GuiComponent, T extends GuiComponentType<C, T>> void accept(GuiComponentType<C, T> componentType, C component);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void forEach(ForEachConsumer consumer) {
        this.map.forEach(((componentType0, component) ->
            consumer.accept((GuiComponentType) componentType0, component)
        ));
    }
}
