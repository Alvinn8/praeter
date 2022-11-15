package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.bake.FontCharIdentifier;
import ca.bkaw.praeter.core.resources.bake.FontSequence;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.core.resources.pack.font.Font;
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

    public void shiftLeft(int pixels) {}

    public void shiftRight(int pixels) {}

    private GuiFontSequenceBuilder renderImageRaw(NamespacedKey textureKey, int offsetX, int offsetY) throws IOException {
        this.shiftRight(offsetX);

        int ascent = -offsetY;

        // Read the texture
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
            textureKey = new NamespacedKey("generated", createdKey);

            // Save the image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(createdImage, "png", stream);
            byte[] bytes = stream.toByteArray();
            // Save the image to all packs
            for (ResourcePack resourcePack : this.resourcePacks) {
                Path path = resourcePack.getPath("assets/generated/textures/" + createdKey);
                Files.createDirectories(path.getParent());
                Files.write(path, bytes);
            }
        }

        // Add the font character to the fonts
        FontCharIdentifier fontChar = new FontCharIdentifier(textureKey, size, ascent);
        this.fontChars.add(fontChar);
        for (Font font : this.fonts) {
            font.addFontChar(fontChar);
        }

        int width = 0; // TODO determine the effective width
        this.shiftLeft(offsetX + width);

        return this;
    }

    public GuiFontSequenceBuilder renderImage(NamespacedKey textureKey, int pixelX, int pixelY) throws IOException {
        return renderImageRaw(textureKey,
            pixelX + ORIGIN_OFFSET_X,
            pixelY + ORIGIN_OFFSET_Y);
    }
}
