package ca.bkaw.praeter.framework.gui.font;

import ca.bkaw.praeter.framework.resources.bake.FontCharIdentifier;
import ca.bkaw.praeter.framework.resources.font.FontSequence;
import ca.bkaw.praeter.framework.resources.pack.ResourcePack;
import ca.bkaw.praeter.framework.resources.pack.font.BitmapFontProvider;
import ca.bkaw.praeter.framework.resources.pack.font.Font;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A builder for a {@link FontSequence} that specializes in operations relating to
 * font usage in GUIs.
 */
public class GuiFontSequenceBuilder {
    /**
     * The y offset from the title of a gui (where custom fonts are placed to render a
     * gui) to the top-left pixel of the top-left slot, slot (0, 0).
     */
    public static final int ORIGIN_OFFSET_X = 0; // TODO find value

    /**
     * The y offset from the title of a gui (where custom fonts are placed to render a
     * gui) to the top-left pixel of the top-left slot, slot (0, 0).
     */
    public static final int ORIGIN_OFFSET_Y = 0; // TODO find value

    /**
     * The width/height of a slot, measured in pixels.
     */
    public static final int SLOT_SIZE = 18; // TODO confirm

    private final List<ResourcePack> resourcePacks;
    private final List<Font> fonts;
    private final List<FontCharIdentifier> fontChars = new ArrayList<>();

    /* package-private */ GuiFontSequenceBuilder(List<ResourcePack> resourcePacks,
                                                 NamespacedKey fontKey) throws IOException {
        this.resourcePacks = resourcePacks;
        this.fonts = new ArrayList<>(this.resourcePacks.size());
        for (ResourcePack pack : this.resourcePacks) {
            this.fonts.add(new Font(pack, fontKey));
        }
    }

    public FontSequence build() {
        return new FontSequence(this.fontChars);
    }

    public GuiFontSequenceBuilder renderImage(NamespacedKey textureKey, int pixelX, int pixelY) throws IOException {
        // TODO x offset
        for (Font font : this.fonts) {
            BitmapFontProvider provider = new BitmapFontProvider(textureKey, pixelY, font.getNextCharAsList());
            font.addProvider(provider);
            this.fontChars.add(provider.createIdentifier());
        }
        return this;
    }
}
