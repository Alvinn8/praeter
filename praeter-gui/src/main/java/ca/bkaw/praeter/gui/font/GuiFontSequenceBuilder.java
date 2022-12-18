package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.resources.ResourcePackList;
import ca.bkaw.praeter.core.resources.draw.CompositeDrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOriginResolver;
import ca.bkaw.praeter.core.resources.font.AbstractFontSequenceBuilder;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.gui.GuiUtils;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.List;

/**
 * A builder for a {@link ca.bkaw.praeter.core.resources.font.FontSequence} that
 * specialized for use in custom guis.
 * <p>
 * The origin is the top-left pixel of the top-left slot, slot, (0, 0).
 * <p>
 * Therefore, to render something at the top-left pixel of a slot (x, y), use:
 * {@code x * GuiUtils.SLOT_SIZE, y * GuiUtils.SLOT_SIZE}. ({@link GuiUtils#SLOT_SIZE})
 */
public class GuiFontSequenceBuilder extends AbstractFontSequenceBuilder<GuiFontSequenceBuilder> {
    /**
     * The x offset from the title of a gui (where custom fonts are placed to render a
     * gui) to the top-left pixel of the top-left slot, slot (0, 0).
     */
    public static final int ORIGIN_OFFSET_X = -1;

    /**
     * The y offset from the title of a gui (where custom fonts are placed to render a
     * gui) to the top-left pixel of the top-left slot, slot (0, 0).
     */
    public static final int ORIGIN_OFFSET_Y = 4;

    /**
     * The {@link DrawOriginResolver} that resolves origins into positions in a font
     * sequence rendered at the title of a gui.
     */
    public static final DrawOriginResolver ORIGIN_RESOLVER = new DrawOriginResolver() {
        @Override
        public int resolveOriginX(DrawOrigin origin) {
            if (origin instanceof CompositeDrawOrigin composite) {
                return composite.resolveX(this);
            }
            if (origin == GuiUtils.GUI_SLOT_ORIGIN) {
                return ORIGIN_OFFSET_X;
            }
            throw new IllegalArgumentException("The specified origin is not a supported DrawOrigin: " + origin);
        }

        @Override
        public int resolveOriginY(DrawOrigin origin) {
            if (origin instanceof CompositeDrawOrigin composite) {
                return composite.resolveY(this);
            }
            if (origin == GuiUtils.GUI_SLOT_ORIGIN) {
                return ORIGIN_OFFSET_Y;
            }
            throw new IllegalArgumentException("The specified origin is not a supported DrawOrigin: " + origin);
        }
    };

    public GuiFontSequenceBuilder(ResourcePackList resourcePacks,
                                  NamespacedKey fontKey,
                                  DrawOrigin origin) throws IOException {
        super(resourcePacks, fontKey, origin);
    }

    @Override
    protected GuiFontSequenceBuilder getThis() {
        return this;
    }

    @Override
    protected DrawOriginResolver getOriginResolver() {
        return ORIGIN_RESOLVER;
    }

    // This class mostly overrides methods to change the javadoc to clarify how
    // things work with guis.

    /**
     * Shift the cursor to the left by the specified amount of pixels.
     * <p>
     * This will alter the x-position of all subsequent renders, even for other
     * component renderers. It is therefore important to shift back to avoid
     * all subsequent components being shifted. See the Praeter documentation.
     *
     * @param pixels The amount of pixels to shift to the left.
     * @return The builder, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    @Contract("_ -> this")
    public GuiFontSequenceBuilder shiftLeft(int pixels) throws IOException {
        return super.shiftLeft(pixels);
    }

    /**
     * Shift the cursor to the right by the specified amount of pixels.
     * <p>
     * This will alter the x-position of all subsequent renders, even for other
     * component renderers. It is therefore important to shift back to avoid
     * all subsequent components being shifted. See the Praeter documentation.
     *
     * @param pixels The amount of pixels to shift to the right.
     * @return The builder, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    @Contract("_ -> this")
    public GuiFontSequenceBuilder shiftRight(int pixels) throws IOException {
        return super.shiftRight(pixels);
    }
}
