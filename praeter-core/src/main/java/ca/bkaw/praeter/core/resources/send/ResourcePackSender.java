package ca.bkaw.praeter.core.resources.send;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;

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

    /**
     * Utilities and shared code for {@link ResourcePackSender} implementations.
     */
    final class Utils {
        private static String localHostname;
        private static String remoteHostname;

        /**
         * Get the hostname that the player can use to connect to this server.
         * <p>
         * This will be the server's public ip unless the player connected from a local
         * address.
         *
         * @param player The player.
         * @return The hostname the player can use to connect to this server.
         */
        @NotNull
        public static String getHostnameFor(Player player) {
            // Check if the player joined from a local address
            // and in that case return local
            InetSocketAddress socketAddress = player.getAddress();
            if (socketAddress != null) {
                InetAddress address = socketAddress.getAddress();
                if (address.isSiteLocalAddress() || address.isLoopbackAddress()) {
                    return getLocalhost();
                }
            }

            // Otherwise, it's a normal remote player, return the public ip
            return getRemoteHostname();
        }

        /**
         * Return the local ip that can be used to connect to the server from the same
         * network.
         *
         * @return The local ip.
         */
        @NotNull
        private static String getLocalhost() {
            if (localHostname == null) {
                try {
                    localHostname = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    Praeter.get().getLogger().warning("Failed to get local ip for getting the url " +
                        "to send to players when sending resource packs.");
                    e.printStackTrace();
                    localHostname = "localhost";
                }
            }
            return localHostname;
        }

        /**
         * Get the server's public ip.
         *
         * @return The remote hostname.
         */
        @NotNull
        private static String getRemoteHostname() {
            if (remoteHostname == null) {
                try (InputStream stream = new URL("http://checkip.amazonaws.com/").openStream()) {
                    remoteHostname = new String(stream.readAllBytes()).trim();
                } catch (IOException e) {
                    System.err.println("Failed to get public ip for getting the url to send to players when sending praeter resources.");
                    e.printStackTrace();
                    remoteHostname = getLocalhost();
                }
            }
            return remoteHostname;
        }
    }
}
