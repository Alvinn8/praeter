package ca.bkaw.praeter.core.resources.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOriginResolver;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract builder for a {@link FontSequence}.
 */
public abstract class AbstractFontSequenceBuilder<T extends AbstractFontSequenceBuilder<T>> {
    private final List<ResourcePack> resourcePacks;
    private final List<Font> fonts;
    private final List<FontCharIdentifier> fontChars = new ArrayList<>();
    private DrawOrigin origin;

    public AbstractFontSequenceBuilder(List<ResourcePack> resourcePacks,
                                       NamespacedKey fontKey,
                                       DrawOrigin origin) throws IOException {
        this.resourcePacks = resourcePacks;
        this.origin = origin;
        this.fonts = new ArrayList<>(this.resourcePacks.size());
        for (ResourcePack pack : this.resourcePacks) {
            this.fonts.add(new Font(pack, fontKey));
        }
    }

    /**
     * Create the {@link FontSequence} from this builder.
     *
     * @return The font sequence.
     */
    public FontSequence build() {
        return new FontSequence(this.fontChars);
    }

    protected abstract T getThis();

    /**
     * Shift the cursor to the left by the specified amount of pixels.
     * <p>
     * This will alter the x-position of all subsequent renders.
     *
     * @param pixels The amount of pixels to shift to the left.
     * @return The builder, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    @Contract("_ -> this")
    public T shiftLeft(int pixels) throws IOException {
        this.shiftRight(-pixels);
        return getThis();
    }

    /**
     * Shift the cursor to the right by the specified amount of pixels.
     * <p>
     * This will alter the x-position of all subsequent renders.
     *
     * @param pixels The amount of pixels to shift to the right.
     * @return The builder, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    @Contract("_ -> this")
    public T shiftRight(int pixels) throws IOException {
        SpaceFontCharIdentifier fontChar = new SpaceFontCharIdentifier(pixels);
        for (Font font : this.fonts) {
            font.addFontChar(fontChar);
        }
        this.fontChars.add(fontChar);
        return getThis();
    }

    /**
     * Get the {@link DrawOriginResolver} that is responsible for resolving the origin
     * to absolute coordinates for the font sequence builder to use.
     *
     * @return The origin resolver.
     */
    protected abstract DrawOriginResolver getOriginResolver();

    /**
     * Set the drawing origin. All subsequent drawing operations will be relative to
     * the set origin.
     *
     * @param origin The origin.
     */
    public void setOrigin(DrawOrigin origin) {
        this.origin = origin;
    }

    /**
     * Draw an image.
     *
     * @param textureKey The key of the texture to render. The key is relative to the
     *                   textures folder and must contain the file extension.
     * @param offsetX The x offset to render the image at, in pixels, relative to the origin.
     * @param offsetY The y offset to render the image at, in pixels, relative to the origin.
     * @return The builder, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    @Contract("_, _, _ -> this")
    public T drawImage(NamespacedKey textureKey, int offsetX, int offsetY) throws IOException {
        offsetX += this.getOriginResolver().resolveOriginX(this.origin);
        offsetY += this.getOriginResolver().resolveOriginY(this.origin);

        // x offset: shift right with spaces (and shift back afterwards)
        // y offset: use the character ascent
        this.shiftRight(offsetX);
        int ascent = -offsetY;

        // Read the texture
        // Find the image manually instead of using ResourcePack#getTexturePath because
        // we want to ensure that the key contains the file extension.
        Path texturePath = Praeter.get().getResourceManager().getPacks().getResource(
            this.resourcePacks,
            "assets/" + textureKey.getNamespace() + "/textures/" + textureKey.getKey()
        );
        BufferedImage image = ImageIO.read(Files.newInputStream(texturePath));
        int size = Math.max(image.getWidth(), image.getHeight());

        // Bitmap font providers don't allow the ascent to be larger than the height of
        // the character. If that is the case we must create a new image that is big
        // enough. The rest of the area will just be transparent.
        if (ascent > size) {
            size = ascent;
        }

        // If the current image isn't already the expected size (it's either not a
        // square or we need to resize it)
        // TODO does it need to be a square?
        if (image.getWidth() != size || image.getHeight() != size) {
            BufferedImage createdImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = createdImage.getGraphics();

            // Draw the image in the top-left corner.
            graphics.drawImage(image, 0, 0, null);

            // Create the new key for the image
            String createdKey = textureKey.getKey();
            int extIndex = createdKey.lastIndexOf('.');
            createdKey = textureKey.getNamespace() + '/'
                + createdKey.substring(0, extIndex)
                + "_" + size
                + textureKey.getKey().substring(extIndex);
            textureKey = new NamespacedKey(Praeter.GENERATED_NAMESPACE, createdKey);

            // Save the image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(createdImage, "png", stream);
            byte[] bytes = stream.toByteArray();
            // Save the image to all packs
            for (ResourcePack resourcePack : this.resourcePacks) {
                Path path = resourcePack.getTexturePath(textureKey);
                Files.createDirectories(path.getParent());
                Files.write(path, bytes);
            }
        }

        // Add the font character to the fonts
        BitmapFontCharIdentifier fontChar = new BitmapFontCharIdentifier(textureKey, size, ascent);
        this.fontChars.add(fontChar);
        for (Font font : this.fonts) {
            font.addFontChar(fontChar);
        }

        // Shift back, and the image has an effective width we need to move back by,
        // and an additional pixel for the single-pixel-wide space after the character.
        this.shiftLeft(offsetX + getEffectiveWidth(image) + 1);

        return getThis();
    }

    /**
     * Get the effective width of the image, the width the game will advance the text
     * "cursor" by.
     *
     * @param image The image.
     * @return The effective width.
     */
    public static int getEffectiveWidth(BufferedImage image) {
        // Don't count transparent columns to the right
        int x;
        for (x = image.getWidth() - 1; x >= 0; x--) {
            for (int y = 0; y < image.getHeight(); y++) {
                int argb = image.getRGB(x, y);
                int alpha = argb & 0xFF000000;
                if (alpha != 0) {
                    return x + 1;
                }
            }
        }
        return x + 1;
    }

}
