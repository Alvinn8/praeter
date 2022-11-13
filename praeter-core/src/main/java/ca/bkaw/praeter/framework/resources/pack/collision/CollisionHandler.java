package ca.bkaw.praeter.framework.resources.pack.collision;

import ca.bkaw.praeter.framework.resources.pack.Pack;

import java.nio.file.Path;

/**
 * Handles collisions when a pack is being included into another pack.
 *
 * @see #handleCollision(Pack, Pack, Path, Path)
 * @see Pack#include(Pack)
 */
public interface CollisionHandler {
    /**
     * Handle a collision where {@code pathA} already exists but an inclusion was
     * attempted with file {@code pathB}.
     * <p>
     * <strong>A: </strong>Pack and path A is the pack where resources are being
     * included into. {@code packA} is the same instance that {@link Pack#include(
     * Pack)} was called on.
     * <p>
     * <strong>B: </strong>Pack and path B is the pack where resources are being taken
     * from and copied over to pack A. Is the same instance that was passed as the
     * first parameter of {@link Pack#include(Pack)}.
     * <p>
     * When a collision occurs {@link Pack#include(Pack)} does not copy the file so the
     * collision resolver most do the inclusion of the resource if the collision is
     * resolvable, or do nothing of the handler wishes to not include the file.
     *
     * @param packA The resource pack where resources are being copied to
     * @param packB The resource pack where resources are being copied from
     * @param pathA The path in pack A that existed and caused the collision.
     * @param pathB The path in pack B that was attempted to be moved into pack A.
     * @throws ResourceCollisionException If the collision could not be resolved.
     */
    void handleCollision(Pack packA, Pack packB, Path pathA, Path pathB) throws ResourceCollisionException;
}
