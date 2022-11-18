package ca.bkaw.praeter.gui.font;

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

    public GuiFontSequenceBuilder(List<ResourcePack> resourcePacks,
                                  NamespacedKey fontKey) throws IOException {
        super(resourcePacks, fontKey, ORIGIN_OFFSET_X, ORIGIN_OFFSET_Y);
    }

    @Override
    protected GuiFontSequenceBuilder getThis() {
        return this;
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

    /**
     * Render an image.
     *
     * @param textureKey The key of the texture to render. The key is relative to the
     *                   textures folder and must contain the file extension.
     * @param offsetX The x offset to render the image at, in pixels, relative to the
     *                top-left pixel of the top-left slot, slot (0, 0).
     * @param offsetY The y offset to render the image at, in pixels, relative to the
     *                top-left pixel of the top-left slot, slot (0, 0).
     * @return The builder, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    @Contract("_, _, _ -> this")
    public GuiFontSequenceBuilder renderImage(NamespacedKey textureKey, int offsetX, int offsetY) throws IOException {
        return super.renderImage(textureKey, offsetX, offsetY);
    }
}
