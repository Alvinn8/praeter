package ca.bkaw.praeter.core;

import org.bukkit.World;

/**
 * An interface implemented by plugins that use praeter.
 * <p>
 * When a plugin implements this interface, the "assets" folder in the plugin's
 * resources will be included in all resource packs that the plugin defines itself
 * as enabled in.
 */
public interface PraeterPlugin {
    /**
     * Whether the plugin is enabled in the specified world.
     * <p>
     * Praeter uses this to determine whether resources should be included in the
     * resource pack used in that world, if the server owner decides to use per-world
     * resource packs.
     *
     * @param world The world.
     * @return Whether the plugin is enabled in the specified world.
     */
    boolean isEnabledIn(World world);
}
