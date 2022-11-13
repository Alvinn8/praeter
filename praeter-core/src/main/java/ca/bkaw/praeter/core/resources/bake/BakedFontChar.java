package ca.bkaw.praeter.core.resources.bake;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;

/**
 * Information on how to render something using a custom font character.
 *
 * @param fontKey The key of the font where the character is.
 * @param character The character.
 */
public record BakedFontChar(NamespacedKey fontKey, char character) {
    /**
     * Get a component that renders the font character.
     *
     * @return The component.
     */
    public Component asComponent() {
        return Component.text(this.character).font(this.fontKey);
    }
}
