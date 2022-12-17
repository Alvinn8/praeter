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

    public DrawOrigin getOrigin() {
        return this.origin;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    @Override
    public String toString() {
        return "CompositeDrawOrigin{" +
            "origin=" + origin +
            ", offsetX=" + offsetX +
            ", offsetY=" + offsetY +
            '}';
    }
}
