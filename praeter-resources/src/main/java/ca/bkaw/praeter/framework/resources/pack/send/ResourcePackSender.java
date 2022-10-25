package ca.bkaw.praeter.framework.resources.pack.send;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * A sender responsible for sending resource packs to players.
 * <p>
 * The server can only send resource packs to clients using an HTTP url. Resource
 * pack senders are responsible for crafting a URL to send to clients.
 * <p>
 * Resource pack senders are required to be able to send packs from the main
 * praeter resource packs directory. No garantees are made on whether sending other
 * resource packs is implemented. If unsure, check {@link #canSend(Object)}.
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
    void send(Object resourcePack, Player player, boolean required, @Nullable Component prompt);
    // TODO replace Object with a baked resource pack

    /**
     * Check whether the specified resource pack can be sent.
     *
     * @param resourcePack The resource pack.
     * @return Whether it can be sent.
     */
    boolean canSend(Object resourcePack);
}
