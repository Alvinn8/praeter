package ca.bkaw.praeter.core.resources.bake;

import ca.bkaw.praeter.core.resources.pack.font.BitmapFontProvider;
import org.bukkit.NamespacedKey;

/**
 * Identifying information for a font character.
 * <p>
 * Only works for single-character bitmap providers.
 *
 * @param textureKey The key of the texture to render.
 * @param height The height of the character.
 * @param ascent The ascent of the character.
 *
 * @see BakedResourcePack#getFontChar(FontCharIdentifier)
 * @see BakedFontChar
 * @see BitmapFontProvider
 */
public record FontCharIdentifier(NamespacedKey textureKey, Integer height, int ascent) {
}
