package ca.bkaw.praeter.core.resources.font;

import ca.bkaw.praeter.core.resources.bake.BakedFontChar;
import org.bukkit.NamespacedKey;

/**
 * A {@link FontCharIdentifier} for single-character bitmap providers.
 *
 * @param textureKey The key of the texture to render.
 * @param height The height of the character.
 * @param ascent The ascent of the character.
 *
 * @see BakedFontChar
 * @see BitmapFontProvider
 */
public record BitmapFontCharIdentifier(NamespacedKey textureKey, Integer height, int ascent) implements FontCharIdentifier {
}
