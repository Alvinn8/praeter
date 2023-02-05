package ca.bkaw.praeter.core.resources.send;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.ResourceManager;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    void send(@NotNull BakedResourcePack resourcePack,
              @NotNull Player player,
              boolean required,
              @Nullable Component prompt);

    /**
     * Called when the resource pack sender is being removed. Can be used to clean up.
     */
    void remove();

    /**
     * Utilities and shared code for {@link ResourcePackSender} implementations.
     */
    final class Utils {
        private static final String CHECK_IP_URL = "https://checkip.amazonaws.com/";

        private static String localHostname;
        private static String remoteHostname;

        /**
         * Get the path of a resource pack.
         * <p>
         * The file will exist when this method returns. If the file does not exist and
         * exception is thrown.
         *
         * @param resourcePack The resource pack to get the path of.
         * @return The path.
         */
        @NotNull
        public static Path getPath(@NotNull BakedResourcePack resourcePack) {
            ResourceManager resourceManager = Praeter.get().getResourceManager();
            Path resourcePacksFolder = resourceManager.getResourcePacksFolder();
            String filename = resourceManager.getBakedPacks().getId(resourcePack) + ".zip";
            Path path = resourcePacksFolder.resolve(filename);
            if (Files.exists(path)) {
                return path;
            }
            throw new IllegalArgumentException("Can not send the specified pack.");
        }

        /**
         * Send a resource pack request to the player. It is the responsibility of the
         * {@link ResourcePackSender} to ensure this request is handled.
         *
         * @param resourcePack The resource pack being sent.
         * @param file The path of the resource pack being sent.
         * @param player The player to send to.
         * @param required Whether the resource pack application is mandatory.
         * @param prompt The prompt to display to the player.
         * @param port The port to use in the url.
         * @param path The path to use in the url. Should start with "/".
         */
        public static void sendRequest(BakedResourcePack resourcePack,
                                Path file,
                                Player player,
                                boolean required,
                                @Nullable Component prompt,
                                int port,
                                String path) {
            byte[] hash;
            try {
                byte[] bytes = Files.readAllBytes(file);
                hash = MessageDigest.getInstance("SHA-1").digest(bytes);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            String url = "http://" + Utils.getHostnameFor(player) + ":" + port + path;
            ResourceManager resourceManager = Praeter.get().getResourceManager();

            ResourcePackRequest request = new ResourcePackRequest(
                player, resourcePack, resourceManager, url, hash, required, prompt
            );
            request.send();
        }

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
                try (InputStream stream = new URL(CHECK_IP_URL).openStream()) {
                    remoteHostname = new String(stream.readAllBytes()).trim();
                } catch (IOException e) {
                    Praeter.get().getLogger().severe("Failed to get public ip for getting the " +
                        "url to send to players when sending praeter resources.");
                    e.printStackTrace();
                    remoteHostname = getLocalhost();
                }
            }
            return remoteHostname;
        }
    }
}
