package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.gui.GuiUtils;
import org.bukkit.NamespacedKey;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An object responsible for painting the background in a custom gui.
 */
public class GuiBackgroundPainter {
    /**
     * The key to the generic_54 texture, relative to the textures folder and including
     * the file extension.
     */
    public static final NamespacedKey GENERIC_54_KEY
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

    private final BufferedImage image;

    public GuiBackgroundPainter(int rows) throws IOException {
        int height = TOP_PADDING + rows * GuiUtils.SLOT_SIZE;
        this.image = new BufferedImage(WIDTH, height, BufferedImage.TYPE_INT_ARGB);
        this.paint();
    }

    /**
     * Get the image to use as the background.
     *
     * @return The background image.
     */
    public BufferedImage getImage() {
        return this.image;
    }

    private void paint() throws IOException {
        ResourcePack vanillaAssets = Praeter.get().getResourceManager().getPacks().getVanillaAssets();
        Path generic54Path = vanillaAssets.getTexturePath(GENERIC_54_KEY);

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
        for (int y = TOP_EDGE_HEIGHT + 1; y < this.image.getHeight(); y++) {
            graphics.drawImage(pixelRow, 0, y, null);
        }
    }
}