package ca.bkaw.praeter.core.resources.apply;

import ca.bkaw.praeter.core.resources.ResourceManager;

/**
 * An object responsible for applying resource packs to players. The applier must
 * ensure that a player has the correct resource pack upon entering a world.
 * <p>
 * The applier also controls the prompt to send the resource pack with.
 * <p>
 * Plugins are free to implement their own appliers and tell praeter to use it
 * by calling {@link ResourceManager#setResourcePackApplier(ResourcePackApplier)}.
 */
public interface ResourcePackApplier {
}
