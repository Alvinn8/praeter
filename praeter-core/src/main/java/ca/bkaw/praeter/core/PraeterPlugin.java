package ca.bkaw.praeter.core;

import ca.bkaw.praeter.core.resources.pack.ResourcePack;
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

    /**
     * A method called after the assets of this plugin has been included into the
     * specified resource pack.
     *
     * @param resourcePack The resource pack that assets have been included into.
     */
    default void onIncludeAssets(ResourcePack resourcePack) {}

    /**
     * A method called after the packs have been baked.
     */
    default void onPacksBaked() {} // TODO better name
}
