package ca.bkaw.praeter.gui.components;

import ca.bkaw.praeter.core.resources.draw.DrawTextUtils;
import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import ca.bkaw.praeter.gui.gui.CustomGui;
import org.bukkit.NamespacedKey;
import org.bukkit.map.MinecraftFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A button in a {@link CustomGui}.
 */
public class Button extends GuiComponent {
    /**
     * The texture key to the "widgets" texture.
     */
    public static final NamespacedKey WIDGETS_TEXTURE
        = NamespacedKey.minecraft("gui/widgets.png");

    /**
     * The y position of the button sprite.
     */
    public static final int OFFSET_Y = 66;

    /**
     * The width of the button sprite.
     */
    public static final int WIDTH = 200;

    /**
     * The height of the button sprite.
     */
    public static final int HEIGHT = 20;

    /**
     * The height of the bottom of the button sprite.
     */
    public static final int BOTTOM_HEIGHT = 3;

    /**
     * The width of the end of the button.
     */
    public static final int END_WIDTH = 2;

    protected final String text;

    /**
     * Create a new {@link Button}.
     *
     * @param text The text to display on the button. Keep it short.
     * @param x The x position of the component, in slots.
     * @param y The y position of the component, in slots.
     * @param width The width of the component, in slots.
     * @param height The height of the component, in slots.
     */
    public Button(String text, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.text = text;
    }

    /**
     * Create a button texture.
     *
     * @param button The vanilla button sprite to use as the background.
     * @param width The width of the desired button.
     * @param height The height of the desired button. (Currently, must be 18.)
     * @return The created texture.
     */
    public static BufferedImage createButtonImage(BufferedImage button, int width, int height) {
        if (height != 18) {
            throw new UnsupportedOperationException("Cannot make buttons that are higher than one row yet, sorry!");
        }
        BufferedImage image = new BufferedImage(
            width,
            height,
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D graphics = image.createGraphics();

        // Draw the bulk of the image
        graphics.drawImage(button, 0, 0, null);

        // Draw the bottom edge
        BufferedImage bottom = button.getSubimage(0, HEIGHT - BOTTOM_HEIGHT, width, BOTTOM_HEIGHT);
        graphics.drawImage(bottom, 0, height - BOTTOM_HEIGHT, null);

        // Draw the right edge
        BufferedImage right = button.getSubimage(WIDTH - END_WIDTH, 0, END_WIDTH, height);
        graphics.drawImage(right, width - END_WIDTH, 0, END_WIDTH, height, null);

        // Draw the bottom right corner
        BufferedImage bottomRight = button.getSubimage(WIDTH - END_WIDTH, HEIGHT - BOTTOM_HEIGHT, END_WIDTH, BOTTOM_HEIGHT);
        graphics.drawImage(bottomRight, width - END_WIDTH, height - BOTTOM_HEIGHT, null);

        return image;
    }

    @Override
    public void onSetup(RenderSetupContext context) throws IOException {
        // Read the vanilla button sprite from the widgets texture
        Path widgetsPath = context.getResourcePacks().getTexturePath(WIDGETS_TEXTURE);
        BufferedImage widgets = ImageIO.read(Files.newInputStream(widgetsPath));
        BufferedImage button = widgets.getSubimage(0, OFFSET_Y, WIDTH, HEIGHT);

        // Create the image for the button
        int width = getWidth() * GuiUtils.SLOT_SIZE;
        int height = getHeight() * GuiUtils.SLOT_SIZE;

        BufferedImage image = createButtonImage(button, width, height);

        // Draw the final image on the background
        context.getBackground().drawImage(image, 0, 0);

        int textX = (width - DrawTextUtils.getTextWidth(this.text, MinecraftFont.Font)) / 2;
        int textY = (height - MinecraftFont.Font.getHeight()) / 2;
        context.getBackground().drawText(this.text, textX, textY, Color.WHITE);
    }
}
