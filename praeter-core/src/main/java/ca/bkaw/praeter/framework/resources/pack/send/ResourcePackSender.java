package ca.bkaw.praeter.framework.resources.pack.send;

import ca.bkaw.praeter.framework.resources.bake.BakedResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * A sender responsible for sending resource packs to players.
 * <p>
 * The server can only send resource packs to clients using an HTTP url. Resource
 * pack senders are responsible for crafting a URL to send to clients.
 * <p>
 * Resource pack senders are required to be able to send packs created by praeter
 * that are used to render custom additions to the game. No guarantees are made on
 * whether sending user-created resource packs is implemented.
 */
public interface ResourcePackSender {
    /**
     * Send the specified resource pack to the player.
     *
     * @param resourcePack The resource pack to send to the player.
     * @param player The player to send the resource pack to.
     * @param required Whether accepting the resource pack is mandatory.
     * @param prompt The optional prompt to display.
     *
     * @see Player#setResourcePack(String, String, boolean, Component)
     */
    void send(BakedResourcePack resourcePack, Player player, boolean required, @Nullable Component prompt);

    /**
     * Called when the resource pack sender is being removed. Can be used to clean up.
     */
    void remove();
}
