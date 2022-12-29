package ca.bkaw.praeter.gui.components;

import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.font.RenderSetupContext;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A panel provides no functionality, but renders an indented area that looks like
 * a slot, but bigger. New components can extend panel to use the panel as a base
 * for the rendering.
 */
public class Panel extends GuiComponent {
    /**
     * The main background color of a panel.
     */
    public static final Color BACKGROUND_COLOR = new Color(139, 139, 139);

    /**
     * The darker color used on the edges of a panel.
     */
    public static final Color DARK_COLOR = new Color(55, 55, 55);

    /**
     * The lighter color used on the edges of a panel.
     */
    public static final Color LIGHT_COLOR = Color.WHITE;

    /**
     * Create a new {@link Panel}.
     *
     * @param x The x position of the component, in slots.
     * @param y The y position of the component, in slots.
     * @param width The width of the component, in slots.
     * @param height The height of the component, in slots.
     */
    public Panel(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    @SuppressWarnings("RedundantThrows") // subclasses may want to throw
    public void onSetup(RenderSetupContext context) throws IOException {
        int width = this.width * GuiUtils.SLOT_SIZE;
        int height = this.height * GuiUtils.SLOT_SIZE;
        BufferedImage image = createPanelImage(width, height);
        context.getBackground().drawImage(image, 0, 0);
    }

    /**
     * Create a panel image with the specified width and height.
     *
     * @param width The width of the image.
     * @param height The height of the image.
     * @return The created image.
     */
    public static BufferedImage createPanelImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        // Will everything with the background color
        graphics.setColor(BACKGROUND_COLOR);
        graphics.fillRect(0, 0, width, height);

        // Left and top edge
        graphics.setColor(DARK_COLOR);
        graphics.fillRect(0, 0, width - 1, 1);
        graphics.fillRect(0, 0, 1, height - 1);

        // Right and bottom edge
        graphics.setColor(LIGHT_COLOR);
        graphics.fillRect(width - 1, 1, 1, height - 1);
        graphics.fillRect(1, height - 1, width - 1, 1);

        return image;
    }
}
