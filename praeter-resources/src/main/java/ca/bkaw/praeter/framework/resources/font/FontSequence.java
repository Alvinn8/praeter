package ca.bkaw.praeter.framework.resources.font;

import ca.bkaw.praeter.framework.resources.MissingAssetException;
import ca.bkaw.praeter.framework.resources.bake.BakedFontChar;
import ca.bkaw.praeter.framework.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.framework.resources.bake.FontCharIdentifier;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Unmodifiable;

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
     * Get a text component that renders this font sequence.
     *
     * @param pack The baked resource pack to get baked font chars from.
     * @return The text component.
     */
    public Component getChars(BakedResourcePack pack) {
        TextComponent.Builder component = Component.text();
        for (FontCharIdentifier fontCharIdentifier : this.fontChars) {
            BakedFontChar fontChar = pack.getFontChar(fontCharIdentifier);
            if (fontChar == null) {
                throw new MissingAssetException("Expected the font char to be baked, but it was " +
                    "not found. fontCharIdentifier: " + fontCharIdentifier);
            }
            component.append(fontChar.asComponent());
        }
        return component.build();
    }
}
