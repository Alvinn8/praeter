package ca.bkaw.praeter.core.resources.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.ResourcePackList;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOriginResolver;
import ca.bkaw.praeter.core.resources.draw.DrawTextUtils;
import ca.bkaw.praeter.core.resources.draw.Drawable;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import org.bukkit.NamespacedKey;
import org.bukkit.map.MapFont;
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
public abstract class AbstractFontSequenceBuilder<T extends AbstractFontSequenceBuilder<T>> implements Drawable<T> {
    private static int nextGeneratedFilename = 1;
    private final ResourcePackList resourcePacks;
    private final List<Font> fonts;
    private final List<FontCharIdentifier> fontChars = new ArrayList<>();
    private DrawOrigin origin;
    private boolean hasNewLayerChar = false;

    public AbstractFontSequenceBuilder(ResourcePackList resourcePacks,
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
     * Insert a character that ensures things rendered before this character are
     * rendered behind and the things that come after are rendered in front of the
     * things that come behind.
     * <p>
     * It almost works as declaring a "new layer".
     *
     * @throws IOException If an I/O error occurs.
     */
    protected void newLayer() throws IOException {
        // We create what is known as a "splitting" character. This character is large
        // enough that rendering is split, causing predictable z-index ordering.

        NamespacedKey key = new NamespacedKey(Praeter.NAMESPACE, "split.png");

        // A height of -2 means the character will shift left enough to cancel out its
        // own shift to the right, effectively making it a zero-width character.
        BitmapFontCharIdentifier fontChar = new BitmapFontCharIdentifier(key, -2, -Short.MAX_VALUE);
        if (!this.hasNewLayerChar) {
            // A transparent 256x256 image is large enough to split rendering.
            BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);

            // One pixel is set to an almost-transparent color. This is required to be able
            // to create a zero-width character. If it is fully transparent, the game always
            // shifts by one pixel to the right, regardless of the height.
            image.setRGB(255, 0, 0x11000000);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            byte[] bytes = stream.toByteArray();
            for (ResourcePack resourcePack : this.resourcePacks) {
                Path path = resourcePack.getTexturePath(key);
                Files.createDirectories(path.getParent());
                Files.write(path, bytes);
            }
            for (Font font : this.fonts) {
                font.addFontChar(fontChar);
            }
            this.hasNewLayerChar = true;
        }
        this.fontChars.add(fontChar);
    }

    /**
     * Get the {@link DrawOriginResolver} that is responsible for resolving the origin
     * to absolute coordinates for the font sequence builder to use.
     *
     * @return The origin resolver.
     */
    protected abstract DrawOriginResolver getOriginResolver();

    @Override
    public DrawOrigin getOrigin() {
        return this.origin;
    }

    @Override
    public void setOrigin(DrawOrigin origin) {
        this.origin = origin;
    }

    @Override
    @Contract("_, _, _ -> this")
    public T drawImage(NamespacedKey textureKey, int x, int y) throws IOException {
        x += this.getOriginResolver().resolveOriginX(this.origin);
        y += this.getOriginResolver().resolveOriginY(this.origin);

        // Ensure the key ends with .png, which is required for fonts
        if (!textureKey.getKey().endsWith(".png")) {
            textureKey
                = new NamespacedKey(textureKey.getNamespace(), textureKey.getKey() + ".png");
        }

        // To ensure the things drawn last are drawn on top, we need to insert a special
        // character. It almost works as declaring a "new layer". We do that now to
        // ensure the coming image will be displayed in front of everything before it in
        // case there is overlap.
        this.newLayer();

        // x offset: shift right with spaces (and shift back afterwards)
        // y offset: use the character ascent
        this.shiftRight(x);
        int ascent = -y;

        // Read the texture
        Path texturePath = this.resourcePacks.getTexturePath(textureKey);
        BufferedImage image = ImageIO.read(Files.newInputStream(texturePath));
        int height = Math.max(image.getWidth(), image.getHeight());

        // Bitmap font providers don't allow the ascent to be larger than the height of
        // the character. If that is the case we must create a new image that is big
        // enough. The rest of the area will just be transparent.
        if (ascent > height) {
            height = ascent;
        }

        // If the current image isn't already the expected height
        if (image.getHeight() != height) {
            BufferedImage createdImage = new BufferedImage(image.getWidth(), height, BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = createdImage.getGraphics();

            // Draw the image in the top-left corner.
            graphics.drawImage(image, 0, 0, null);

            // Create the new key for the image
            String createdKey = textureKey.getKey();
            int extIndex = createdKey.lastIndexOf('.');
            createdKey = textureKey.getNamespace() + '/'
                + createdKey.substring(0, extIndex)
                + "_" + height
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
        BitmapFontCharIdentifier fontChar = new BitmapFontCharIdentifier(textureKey, height, ascent);
        this.fontChars.add(fontChar);
        for (Font font : this.fonts) {
            font.addFontChar(fontChar);
        }

        // Shift back, and the image has an effective width we need to move back by,
        // and an additional pixel for the single-pixel-wide space after the character.
        this.shiftLeft(x + getEffectiveWidth(image) + 1);

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

    @Override
    public T drawImage(BufferedImage image, int x, int y) throws IOException {
        // Write the image as a texture in the resource packs
        NamespacedKey textureKey = new NamespacedKey(Praeter.GENERATED_NAMESPACE, nextGeneratedFilename++ + ".png");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        byte[] bytes = stream.toByteArray();
        for (ResourcePack resourcePack : this.resourcePacks) {
            Path texturePath = resourcePack.getTexturePath(textureKey);
            Files.createDirectories(texturePath.getParent());
            Files.write(texturePath, bytes);
        }
        // Then draw the image from that texture
        return drawImage(textureKey, x, y);
    }

    @Override
    public T drawText(String text, int x, int y, Color color, MapFont font) throws IOException {
        // Create an image to draw the text on
        int width = DrawTextUtils.getTextWidth(text, font);
        int height = DrawTextUtils.getTextHeight(text, font);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Draw the text on the image
        DrawTextUtils.drawText(image, 0, 0, text, color, font);

        // Draw the image at the right place
        return drawImage(image, x, y);
    }
}
