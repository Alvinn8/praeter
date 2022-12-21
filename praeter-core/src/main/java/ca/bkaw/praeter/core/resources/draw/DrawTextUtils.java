package ca.bkaw.praeter.core.resources.draw;

import org.bukkit.map.MapFont;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A utility class for drawing text.
 */
public class DrawTextUtils {
    /**
     * Draw text.
     *
     * @param image The image to draw on.
     * @param x The x coordinate to start at.
     * @param y The y coordinate to start at.
     * @param text The text to draw.
     * @param color The color to draw the text as.
     * @param font The font to use when drawing the text.
     */
    public static void drawText(BufferedImage image, int x, int y, String text, Color color, MapFont font) {
        int startX = x;
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                x = startX;
                y += font.getHeight() + 1;
            }
            MapFont.CharacterSprite characterSprite = font.getChar(c);
            if (characterSprite == null) {
                throw new IllegalArgumentException("Cannot draw the character: '"+ c +"'");
            }

            for(int rows = 0; rows < font.getHeight(); ++rows) {
                for(int cols = 0; cols < characterSprite.getWidth(); ++cols) {
                    if (characterSprite.get(rows, cols)) {
                        image.setRGB(x + cols, y + rows, color.getRGB());
                    }
                }
            }

            x += characterSprite.getWidth() + 1;
        }
    }

    /**
     * Get the width, in pixels, that a text will occupy.
     *
     * @param text The text.
     * @param font The font.
     * @return The width.
     */
    public static int getTextWidth(String text, MapFont font) {
        int maxWidth = 0;
        int width = 0;
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                // A new line, reset the count
                if (width > maxWidth) {
                    maxWidth = width;
                }
                width = 0;
            }
            MapFont.CharacterSprite characterSprite = font.getChar(c);
            if (characterSprite == null) {
                throw new IllegalArgumentException("Cannot draw the character: '"+ c +"'");
            }
            width += characterSprite.getWidth() + 1;
        }
        if (width > maxWidth) {
            maxWidth = width;
        }
        return maxWidth;
    }
}
