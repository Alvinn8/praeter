package ca.bkaw.praeter.core.resources.draw;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * An object which may be drawn upon.
 * <p>
 * This interface provides a standard for drawable objects so the same methods can
 * be used in different situations.
 *
 * @param <T> The type of the object.
 */
public interface Drawable<T> {

    /**
     * Get the drawing origin. All drawing operations are relative to the origin.
     *
     * @return The origin.
     */
    DrawOrigin getOrigin();

    /**
     * Set the drawing origin. All subsequent drawing operations will be relative to
     * the set origin.
     *
     * @param origin The origin.
     */
    void setOrigin(DrawOrigin origin);

    /**
     * Draw an image.
     *
     * @param textureKey The key of the texture to render. The key is relative to the
     *                   textures folder and may or may not contain the file extension.
     * @param x The x offset to render the image at, in pixels, relative to the
     *          {@link #getOrigin() origin}.
     * @param y The y offset to render the image at, in pixels, relative to the
     *          {@link #getOrigin() origin}.
     * @return The same instance, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    @Contract("_, _, _ -> this")
    T drawImage(NamespacedKey textureKey, int x, int y) throws IOException;

    /**
     * Draw an image.
     *
     * @param image The image to draw.
     * @param x The x offset to render the image at, in pixels, relative to the
     *          {@link #getOrigin() origin}.
     * @param y The y offset to render the image at, in pixels, relative to the
     *          {@link #getOrigin() origin}.
     * @return The same instance, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    @Contract("_, _, _ -> this")
    T drawImage(BufferedImage image, int x, int y) throws IOException;
}
