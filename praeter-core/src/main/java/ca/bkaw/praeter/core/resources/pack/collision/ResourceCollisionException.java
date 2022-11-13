package ca.bkaw.praeter.core.resources.pack.collision;

import ca.bkaw.praeter.core.resources.pack.Pack;

/**
 * Thrown when a collision occurred when a resource pack was included in another
 * one using {@link Pack#include(Pack)}} and the {@link CollisionHandler} was
 * unable to resolve the collision.
 */
public class ResourceCollisionException extends Exception {
    public ResourceCollisionException(String message) {
        super(message);
    }
}
