package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.ResourcePackList;
import ca.bkaw.praeter.core.resources.draw.CompositeDrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.core.resources.draw.DrawOriginResolver;
import ca.bkaw.praeter.core.resources.draw.DrawTextUtils;
import ca.bkaw.praeter.core.resources.draw.Drawable;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.gui.GuiUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.map.MapFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An object responsible for painting the background in a custom gui.
 */
public class GuiBackgroundPainter implements Drawable<GuiBackgroundPainter> {
    /**
     * The key to the generic_54 texture, relative to the textures folder and including
     * the file extension.
     */
    public static final NamespacedKey GENERIC_54_TEXTURE
        = NamespacedKey.minecraft("gui/container/generic_54.png");

    /**
     * The amount of pixels of padding that exist on the left and right side of the
     * slots. Aka, the size of the parts on the edges of the slots.
     */
    public static final int HORIZONTAL_PADDING = 7;

    /**
     * The amount of pixels between the top edge and the slots.
     */
    public static final int TOP_PADDING = 17;

    /**
     * The width of the background image.
     */
    public static final int WIDTH
        = HORIZONTAL_PADDING + 9 * GuiUtils.SLOT_SIZE + HORIZONTAL_PADDING;

    /**
     * The height of the top edge, including all the curvature.
     */
    public static final int TOP_EDGE_HEIGHT = 4;

    /**
     * The color used in the inventory background.
     */
    public static final Color BACKGROUND_GRAY = new Color(198, 198, 198);

    /**
     * A {@link DrawOriginResolver} that resolves origins into positions on the
     * background.
     */
    public static final DrawOriginResolver ORIGIN_RESOLVER = new DrawOriginResolver() {
        @Override
        public int resolveOriginX(DrawOrigin origin) {
            if (origin instanceof CompositeDrawOrigin composite) {
                return composite.resolveX(this);
            }
            if (origin == GuiUtils.GUI_SLOT_ORIGIN) {
                return HORIZONTAL_PADDING;
            }
            throw new IllegalArgumentException("The specified origin is not a supported DrawOrigin: " + origin);
        }

        @Override
        public int resolveOriginY(DrawOrigin origin) {
            if (origin instanceof CompositeDrawOrigin composite) {
                return composite.resolveY(this);
            }
            if (origin == GuiUtils.GUI_SLOT_ORIGIN) {
                return TOP_PADDING;
            }
            throw new IllegalArgumentException("The specified origin is not a supported DrawOrigin: " + origin);
        }
    };

    private final BufferedImage image;
    private final ResourcePackList resourcePacks;
    private DrawOrigin origin = GuiUtils.GUI_SLOT_ORIGIN;

    public GuiBackgroundPainter(int rows, ResourcePackList resourcePacks) throws IOException {
        int height = TOP_PADDING + rows * GuiUtils.SLOT_SIZE;
        this.image = new BufferedImage(WIDTH, height, BufferedImage.TYPE_INT_ARGB);
        this.resourcePacks = resourcePacks;
        this.paintBackground();
    }

    /**
     * Get the image to use as the background.
     *
     * @return The background image.
     */
    public BufferedImage getImage() {
        return this.image;
    }

    private void paintBackground() throws IOException {
        ResourcePack vanillaAssets = Praeter.get().getResourceManager().getPacks().getVanillaAssets();
        Path generic54Path = vanillaAssets.getTexturePath(GENERIC_54_TEXTURE);

        BufferedImage generic54 = ImageIO.read(Files.newInputStream(generic54Path));

        // Get the top edge to insert into the generated image
        BufferedImage topEdge = generic54.getSubimage(0, 0, WIDTH, TOP_EDGE_HEIGHT);

        // Get the row right below the top edge. This is the row of pixels we will loop
        // for the rest of the image.
        BufferedImage pixelRow = generic54.getSubimage(0, TOP_EDGE_HEIGHT + 1, WIDTH, 1);

        Graphics2D graphics = this.image.createGraphics();

        // Draw the top edge
        graphics.drawImage(topEdge, 0, 0, null);

        // Draw the rest of the image
        for (int y = TOP_EDGE_HEIGHT; y < this.image.getHeight(); y++) {
            graphics.drawImage(pixelRow, 0, y, null);
        }
    }

    @Override
    public DrawOrigin getOrigin() {
        return this.origin;
    }

    @Override
    public void setOrigin(DrawOrigin origin) {
        this.origin = origin;
    }

    /**
     * Carve out the specified area by replacing the pixels with transparency.
     * <p>
     * When carving over a slot, this results in the slot from the vanilla gui showing
     * trough, including the hover.
     *
     * @param x The x coordinate, relative to the {@link #getOrigin() origin}.
     * @param y The y coordinate, relative to the {@link #getOrigin() origin}.
     * @param width The width.
     * @param height The height.
     */
    public void carve(int x, int y, int width, int height) {
        x += ORIGIN_RESOLVER.resolveOriginX(this.origin);
        y += ORIGIN_RESOLVER.resolveOriginY(this.origin);
        for (int offsetX = 0; offsetX < width; offsetX++) {
            for (int offsetY = 0; offsetY < height; offsetY++) {
                int pixelX = x + offsetX;
                int pixelY = y + offsetY;
                this.image.setRGB(pixelX, pixelY, 0);
            }
        }
    }

    @Override
    public GuiBackgroundPainter drawImage(NamespacedKey textureKey, int x, int y) throws IOException {
        // Read the image
        Path texturePath = this.resourcePacks.getTexturePath(textureKey);
        BufferedImage image = ImageIO.read(Files.newInputStream(texturePath));

        // Draw the image
        this.drawImage(image, x, y);

        return this;
    }

    @Override
    public GuiBackgroundPainter drawImage(BufferedImage image, int x, int y) {
        x += ORIGIN_RESOLVER.resolveOriginX(this.origin);
        y += ORIGIN_RESOLVER.resolveOriginY(this.origin);

        Graphics2D graphics = this.image.createGraphics();
        graphics.drawImage(image, x, y, null);
        return this;
    }

    @Override
    public GuiBackgroundPainter drawText(String text, int x, int y, Color color, MapFont font) {
        x += ORIGIN_RESOLVER.resolveOriginX(this.origin);
        y += ORIGIN_RESOLVER.resolveOriginY(this.origin);

        DrawTextUtils.drawText(this.image, x, y, text, color, font);

        return this;
    }
}
