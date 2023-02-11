package ca.bkaw.praeter.core.config;

import ca.bkaw.praeter.core.resources.send.ResourcePackSender;
import org.jetbrains.annotations.Nullable;

/**
 * The root praeter configuration.
 */
public interface PraeterConfig {
    /**
     * Get the {@link ResourcePackSender} configuration.
     *
     * @return The sender configuration section.
     */
    SenderConfig sender();

    /**
     * The configuration section containing the {@link ResourcePackSender}
     * configuration.
     */
    interface SenderConfig {
        /**
         * Get the configured {@link SenderType} the user desires to use.
         *
         * @return The sender.
         */
        SenderType sender();

        /**
         * Get the common configuration for {@link ResourcePackSender} implementations.
         *
         * @return The common config.
         */
        CommonConfig common();

        /**
         * Get the configuration used for the {@link SenderType#HTTP_SERVER} sender type.
         *
         * @return The HTTP server config.
         */
        HttpServerConfig httpServer();

        /**
         * The configured sender to use.
         */
        enum SenderType {
            HTTP_SERVER,
            BUILT_IN_TCP
        }

        /**
         * Common configuration for {@link ResourcePackSender} implementations.
         *
         * @see ResourcePackSender.Utils
         */
        interface CommonConfig {
            /**
             * Get the configured hostname that can reach the server. If null, the server's
             * public ip will be fetched using an online API.
             *
             * @return The hostname, or null.
             */
            @Nullable String hostname();
        }

        /**
         * The configuration for the {@link SenderType#HTTP_SERVER} sender type.
         */
        interface HttpServerConfig {
            /**
             * Get the port that the HTTP server should listen to.
             *
             * @return The port.
             */
            int port();
        }
    }
}
