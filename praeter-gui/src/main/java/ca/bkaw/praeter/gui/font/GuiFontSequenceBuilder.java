package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.font.BitmapFontCharIdentifier;
import ca.bkaw.praeter.core.resources.font.FontCharIdentifier;
import ca.bkaw.praeter.core.resources.font.FontSequence;
import ca.bkaw.praeter.core.resources.font.SpaceFontCharIdentifier;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.core.resources.font.Font;
import org.bukkit.NamespacedKey;

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
 * A builder for a {@link FontSequence} that specializes in operations relating to
 * font usage in GUIs.
 */
public class GuiFontSequenceBuilder {
    /**
     * The y offset from the title of a gui (where custom fonts are placed to render a
     * gui) to the top-left pixel of the top-left slot, slot (0, 0).
     */
    public static final int ORIGIN_OFFSET_X = -3;

    /**
     * The y offset from the title of a gui (where custom fonts are placed to render a
     * gui) to the top-left pixel of the top-left slot, slot (0, 0).
     */
    public static final int ORIGIN_OFFSET_Y = 2;

    /**
     * The width/height of a slot, measured in pixels.
     */
    public static final int SLOT_SIZE = 18;

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

    // todo javadoc
    public void shiftLeft(int pixels) throws IOException {
        this.shiftRight(-pixels);
    }

    public void shiftRight(int pixels) throws IOException {
        SpaceFontCharIdentifier fontChar = new SpaceFontCharIdentifier(pixels);
        for (Font font : this.fonts) {
            font.addFontChar(fontChar);
        }
        this.fontChars.add(fontChar);
    }

    private GuiFontSequenceBuilder renderImageRaw(NamespacedKey textureKey, int offsetX, int offsetY) throws IOException {
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
        this.shiftLeft(offsetX + this.getEffectiveWidth(image) + 1);

        return this;
    }

    /**
     * Get the effective width of the image, the width the game will advance the text
     * "cursor" by.
     *
     * @param image The image.
     * @return The effective width.
     */
    private int getEffectiveWidth(BufferedImage image) {
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

    public GuiFontSequenceBuilder renderImage(NamespacedKey textureKey, int pixelX, int pixelY) throws IOException {
        return renderImageRaw(textureKey,
            pixelX + ORIGIN_OFFSET_X,
            pixelY + ORIGIN_OFFSET_Y);
    }
}
