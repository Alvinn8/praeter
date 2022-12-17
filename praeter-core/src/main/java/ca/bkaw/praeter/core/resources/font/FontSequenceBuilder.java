package ca.bkaw.praeter.core.resources.font;

import ca.bkaw.praeter.core.resources.draw.CompositeDrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOriginResolver;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.util.List;

/**
 * A generic builder for a {@link FontSequence}.
 */
public class FontSequenceBuilder extends AbstractFontSequenceBuilder<FontSequenceBuilder> {
    public static final DrawOrigin ORIGIN = new DrawOrigin() {
        @Override
        public String toString() {
            return "FontSequenceBuilder#ORIGIN";
        }
    };

    public static final DrawOriginResolver ORIGIN_RESOLVER = new DrawOriginResolver() {
        @Override
        public int resolveOriginX(DrawOrigin origin) {
            if (origin instanceof CompositeDrawOrigin composite) {
                return this.resolveOriginX(composite.getOrigin()) + composite.getOffsetX();
            }
            if (origin == ORIGIN) {
                return 0;
            }
            throw new IllegalArgumentException("The specified origin is not a supported DrawOrigin: " + origin);
        }

        @Override
        public int resolveOriginY(DrawOrigin origin) {
            if (origin instanceof CompositeDrawOrigin composite) {
                return this.resolveOriginY(composite.getOrigin()) + composite.getOffsetY();
            }
            if (origin == ORIGIN) {
                return 0;
            }
            throw new IllegalArgumentException("The specified origin is not a supported DrawOrigin: " + origin);
        }
    };

    public FontSequenceBuilder(List<ResourcePack> resourcePacks, NamespacedKey fontKey) throws IOException {
        super(resourcePacks, fontKey, ORIGIN);
    }

    @Override
    protected FontSequenceBuilder getThis() {
        return this;
    }

    @Override
    protected DrawOriginResolver getOriginResolver() {
        return ORIGIN_RESOLVER;
    }
}
