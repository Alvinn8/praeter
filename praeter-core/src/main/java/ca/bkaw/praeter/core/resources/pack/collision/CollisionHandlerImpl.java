package ca.bkaw.praeter.core.resources.pack.collision;

import ca.bkaw.praeter.core.resources.pack.Pack;

import java.nio.file.Path;

/**
 * The default {@link CollisionHandler} implementation.
 * <p>
 * Will handle collisions in the following ways:
 * <ul>
 *     <li>The pack.mcmeta from pack B will not be copied.</li>
 * </ul>
 */
public class CollisionHandlerImpl implements CollisionHandler {
    public static final CollisionHandlerImpl INSTANCE = new CollisionHandlerImpl();
    private CollisionHandlerImpl() {}

    @Override
    public void handleCollision(Pack resourcePackA, Pack resourcePackB, Path pathA, Path pathB) throws ResourceCollisionException {
        String stringPath = resourcePackA.getRoot().relativize(pathA).toString();

        if ("pack.mcmeta".equals(stringPath)) {
            // Do nothing (don't copy the file)
            return;
        }

        throw new ResourceCollisionException(stringPath);
    }
}