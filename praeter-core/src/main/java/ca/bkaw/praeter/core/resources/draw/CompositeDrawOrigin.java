package ca.bkaw.praeter.core.resources.draw;

/**
 * A {@link DrawOrigin} that is composed of an origin with some extra offset.
 *
 */
public final class CompositeDrawOrigin extends DrawOrigin {
    private final DrawOrigin origin;
    private final int offsetX;
    private final int offsetY;

    /**
     * @param origin The original origin.
     * @param offsetX The additional offset from the origin.
     * @param offsetY The additional offset from the origin.
     */
    public CompositeDrawOrigin(DrawOrigin origin, int offsetX, int offsetY) {
        this.origin = origin;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Resolve the absolute x coordinate of this composite draw origin by using the
     * specified {@link DrawOriginResolver}.
     *
     * @param originResolver The origin resolver.
     * @return The absolute x position.
     */
    public int resolveX(DrawOriginResolver originResolver) {
        return originResolver.resolveOriginX(this.origin) + this.offsetX;
    }

    /**
     * Resolve the absolute y coordinate of this composite draw origin by using the
     * specified {@link DrawOriginResolver}.
     *
     * @param originResolver The origin resolver.
     * @return The absolute y position.
     */
    public int resolveY(DrawOriginResolver originResolver) {
        return originResolver.resolveOriginY(this.origin) + this.offsetY;
    }

    @Override
    public String toString() {
        return "CompositeDrawOrigin{" +
            "origin=" + this.origin +
            ", offsetX=" + this.offsetX +
            ", offsetY=" + this.offsetY +
            '}';
    }
}
