package ca.bkaw.praeter.core.resources.font;

/**
 * A {@link FontCharIdentifier} for space providers.
 *
 * @param advance The amount of pixels to advance right, may be negative.
 */
public record SpaceFontCharIdentifier(int advance) implements FontCharIdentifier {
}
