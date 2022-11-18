package ca.bkaw.praeter.core.resources.font;

import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.util.List;

/**
 * A generic builder for a {@link FontSequence}.
 */
public class FontSequenceBuilder extends AbstractFontSequenceBuilder<FontSequenceBuilder> {
    public FontSequenceBuilder(List<ResourcePack> resourcePacks, NamespacedKey fontKey, int originX, int originY) throws IOException {
        super(resourcePacks, fontKey, originX, originY);
    }

    @Override
    protected FontSequenceBuilder getThis() {
        return this;
    }
}
