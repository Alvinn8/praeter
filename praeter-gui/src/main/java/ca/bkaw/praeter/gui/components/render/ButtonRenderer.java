package ca.bkaw.praeter.gui.components.render;

import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.components.Button;
import ca.bkaw.praeter.gui.font.BackgroundGuiComponentRenderer;
import ca.bkaw.praeter.gui.font.GuiBackgroundPainter;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ButtonRenderer implements BackgroundGuiComponentRenderer<Button, Button.Type> {
    /**
     * The texture key to the "widgets" texture.
     */
    public static final NamespacedKey WIDGETS_TEXTURE
        = NamespacedKey.minecraft("gui/widgets.png");

    /**
     * The y position of the button sprite.
     */
    public static final int BUTTON_OFFSET_Y = 66;

    /**
     * The height of the button sprite.
     */
    public static final int BUTTON_HEIGHT = 20;

    /**
     * The height of the bottom of the button sprite.
     */
    public static final int BUTTON_BOTTOM_HEIGHT = 3;

    /**
     * The y position of the bottom of the button sprite.
     */
    public static final int BUTTON_BOTTOM_OFFSET_Y
        = BUTTON_OFFSET_Y + BUTTON_HEIGHT - BUTTON_BOTTOM_HEIGHT + 2;

    /**
     * The width of the end of the button.
     */
    public static final int BUTTON_END_WIDTH = 2;

    /**
     * The x position of the end of the button sprite.
     */
    public static final int BUTTON_END_OFFSET_X = 198;

    @Override
    public void draw(CustomGuiType customGuiType, Button.Type componentType, GuiBackgroundPainter background) throws IOException {
        int width = componentType.getWidth() * GuiUtils.SLOT_SIZE;
        int height = componentType.getHeight() * GuiUtils.SLOT_SIZE;
        if (height != 18) {
            throw new UnsupportedOperationException("Cannot make buttons that are higher than one slow yet, sorry!");
        }
        BufferedImage image = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D graphics = image.createGraphics();

        Path widgetsPath = background.getResourcePacks().getTexturePath(WIDGETS_TEXTURE);
        BufferedImage widgets = ImageIO.read(Files.newInputStream(widgetsPath));

        // Draw the bulk of the button
        BufferedImage main = widgets.getSubimage(0, BUTTON_OFFSET_Y, width, height);
        graphics.drawImage(main, 0, 0, null);

        // Draw the bottom edge
        BufferedImage bottom = widgets.getSubimage(0, BUTTON_BOTTOM_OFFSET_Y, width, BUTTON_BOTTOM_HEIGHT);
        graphics.drawImage(bottom, 0, BUTTON_HEIGHT - BUTTON_BOTTOM_HEIGHT, width, BUTTON_BOTTOM_HEIGHT, null);

        // Draw the right edge
        BufferedImage end = widgets.getSubimage(BUTTON_END_OFFSET_X, BUTTON_OFFSET_Y, BUTTON_END_WIDTH, height);
        graphics.drawImage(end, width - BUTTON_END_WIDTH, 0, BUTTON_END_WIDTH, height, null);

        // Draw the final image on the background
        background.drawImage(image, 0, 0);

        // TODO bottom right corner + bottom not quite right
        // TODO text
    }

    @Override
    public void renderItems(CustomGuiType customGuiType, CustomGui customGui, Button.Type componentType, Button component, Inventory inventory) {

    }

}
