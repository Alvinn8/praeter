package ca.bkaw.praeter.core.resources.font;

import ca.bkaw.praeter.core.resources.ResourcePackList;
import ca.bkaw.praeter.core.resources.draw.CompositeDrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOriginResolver;
import org.bukkit.NamespacedKey;

import java.io.IOException;

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
                return composite.resolveX(this);
            }
            if (origin == ORIGIN) {
                return 0;
            }
            throw new IllegalArgumentException("The specified origin is not a supported DrawOrigin: " + origin);
        }

        @Override
        public int resolveOriginY(DrawOrigin origin) {
            if (origin instanceof CompositeDrawOrigin composite) {
                return composite.resolveY(this);
            }
            if (origin == ORIGIN) {
                return 0;
            }
            throw new IllegalArgumentException("The specified origin is not a supported DrawOrigin: " + origin);
        }
    };

    public FontSequenceBuilder(ResourcePackList resourcePacks, NamespacedKey fontKey) throws IOException {
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
