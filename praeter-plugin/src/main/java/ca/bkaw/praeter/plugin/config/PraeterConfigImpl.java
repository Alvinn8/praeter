package ca.bkaw.praeter.plugin.config;

import ca.bkaw.praeter.core.config.PraeterConfig;

/**
 * Implementation of {@link PraeterConfig}. Loaded by {@link ConfigLoader}.
 *
 * @param sender The sender config.
 */
public record PraeterConfigImpl(SenderConfig sender) implements PraeterConfig {

    public record SenderConfigImpl(
        SenderType sender,
        CommonConfig common,
        HttpServerConfig httpServer
    ) implements SenderConfig {

        public record CommonConfigImpl(String hostname) implements CommonConfig {}
        public record HttpServerConfigImpl(int port) implements HttpServerConfig {}
    }
}
