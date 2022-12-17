package ca.bkaw.praeter.core.resources.draw;

import org.jetbrains.annotations.ApiStatus;

/**
 * The origin coordinates (0, 0) that drawing operations will be relative to.
 */
@ApiStatus.Internal
public abstract class DrawOrigin {
    /**
     * Create a new origin that is offset from this origin.
     *
     * @param offsetX The x offset.
     * @param offsetY The y offset.
     * @return The new origin.
     */
    public DrawOrigin add(int offsetX, int offsetY) {
        return new CompositeDrawOrigin(this, offsetX, offsetY);
    }
}
