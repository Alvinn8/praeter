package ca.bkaw.praeter.core.resources.bake;

import ca.bkaw.praeter.core.resources.MissingAssetException;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * A sequence of {@link FontCharIdentifier font characters}.
 *
 * @param fontChars The immutable list of font characters.
 */
public record FontSequence(@Unmodifiable List<FontCharIdentifier> fontChars) {
    /**
     * Create a new {@link FontSequence} from a list of font character identifiers
     * that will be copied to an immutable list.
     *
     * @param fontChars The list of font characters.
     */
    public FontSequence(List<FontCharIdentifier> fontChars) {
        this.fontChars = ImmutableList.copyOf(fontChars);
    }

    /**
     * Get a list of text components that render this font sequence.
     *
     * @param pack The baked resource pack to get baked font chars from.
     * @return The text components.
     */
    public List<Component> getChars(BakedResourcePack pack) {
        List<Component> components = new ArrayList<>();
        for (FontCharIdentifier fontCharIdentifier : this.fontChars) {
            BakedFontChar fontChar = pack.getFontChar(fontCharIdentifier);
            if (fontChar == null) {
                throw new MissingAssetException("Expected the font char to be baked, but it was " +
                    "not found. fontCharIdentifier: " + fontCharIdentifier);
            }
            components.add(fontChar.asComponent());
        }
        return components;
    }
}
