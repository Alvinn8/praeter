package ca.bkaw.praeter.core.resources.draw;

/**
 * An object that is capable of resolving a {@link DrawOrigin} into absolute pixels.
 */
public interface DrawOriginResolver {
    /**
     * Resolve the x coordinate to an absolute position.
     *
     * @param origin The origin.
     * @return The x position.
     */
    int resolveOriginX(DrawOrigin origin);

    /**
     * Resolve the y coordinate to an absolute position.
     *
     * @param origin The origin.
     * @return The y position.
     */
    int resolveOriginY(DrawOrigin origin);
}
