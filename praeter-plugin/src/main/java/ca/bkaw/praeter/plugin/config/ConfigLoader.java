package ca.bkaw.praeter.plugin.config;

import ca.bkaw.praeter.core.config.PraeterConfig;
import ca.bkaw.praeter.core.config.PraeterConfig.SenderConfig;
import ca.bkaw.praeter.core.config.PraeterConfig.SenderConfig.CommonConfig;
import ca.bkaw.praeter.core.config.PraeterConfig.SenderConfig.HttpServerConfig;
import ca.bkaw.praeter.core.config.PraeterConfig.SenderConfig.SenderType;
import ca.bkaw.praeter.core.resources.send.HttpServerResourcePackSender;
import ca.bkaw.praeter.plugin.config.PraeterConfigImpl.SenderConfigImpl.CommonConfigImpl;
import ca.bkaw.praeter.plugin.config.PraeterConfigImpl.SenderConfigImpl.HttpServerConfigImpl;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * A class responsible for loading the {@link PraeterConfig} from yml.
 */
public class ConfigLoader {
    public static final String DEFAULT_SENDER
        = SenderType.BUILT_IN_TCP.name().toLowerCase(Locale.ROOT);
    private final FileConfiguration yml;

    public ConfigLoader(FileConfiguration yml) {
        this.yml = yml;
    }

    public PraeterConfig loadConfig() throws InvalidConfigurationException {
        this.yml.options().copyDefaults(true);

        SenderConfig senderConfig = this.loadSenderConfig();

        return new PraeterConfigImpl(senderConfig);
    }

    private SenderConfig loadSenderConfig() throws InvalidConfigurationException {
        ConfigurationSection config = this.getSection(this.yml, "sender");
        this.yml.setComments("sender", List.of(
            "The resource pack sender is responsible for sending the resource pack to",
            "players."
        ));

        // sender
        if (!config.isSet("sender")) {
            config.set("sender", DEFAULT_SENDER);
        }
        String senderString = config.getString("sender", DEFAULT_SENDER);
        config.setComments("sender", List.of(
            "The resource pack sender to use. Choose between \"built_in_tcp\" and",
            "\"http_server\". (Default: " + DEFAULT_SENDER + ").",
            "",
            "built_in_tcp sends resource packs on the same port as the server. This means",
            "no extra port forwarding is necessary.",
            "",
            "http_server sends resource packs by starting an HTTP server on a different",
            "port. This port must be accessible by players, so extra port forwarding may be",
            "necessary. The port can be configured below under the \"sender.http_server\"",
            "section."
        ));
        SenderType senderType = switch (senderString.toLowerCase(Locale.ROOT).trim()) {
            case "built_in_tcp" -> SenderType.BUILT_IN_TCP;
            case "http_server" -> SenderType.HTTP_SERVER;
            default -> throw new InvalidConfigurationException(
                "sender.sender must be \"built_in_tcp\" or \"http_server\""
            );
        };

        CommonConfig commonConfig = this.loadCommonSenderConfig(config);
        HttpServerConfig httpServerConfig = this.loadHttpServerSenderConfig(config);

        return new PraeterConfigImpl.SenderConfigImpl(senderType, commonConfig, httpServerConfig);
    }

    private CommonConfig loadCommonSenderConfig(ConfigurationSection parent) {
        ConfigurationSection config = this.getSection(parent, "common");
        parent.setComments("common", List.of(
            "Common configuration for resource pack senders."
        ));

        // hostname
        String hostname = config.getString("hostname");
        if (hostname == null) {
            config.set("hostname", "auto");
        }
        config.setComments("hostname", List.of(
            "The hostname that players use to reach the server. If set to \"auto\", the",
            "server's public ip will be fetched using an online API. (Default: auto)"
        ));
        if ("auto".equals(hostname)) {
            hostname = null;
        }

        return new CommonConfigImpl(hostname);
    }

    private HttpServerConfig loadHttpServerSenderConfig(ConfigurationSection parent) {
        ConfigurationSection config = this.getSection(parent, "http_server");
        parent.setComments("http_server", List.of(
            "Configuration for the \"http_server\" sender. Only applies if sender is set",
            "to \"http_server\"."
        ));

        if (!config.isSet("port")) {
            config.set("port", HttpServerResourcePackSender.DEFAULT_PORT);
        }
        int port = config.getInt("port");
        config.setComments("port", List.of(
            "The port that the http server should listen on. " +
                "(Default: " + HttpServerResourcePackSender.DEFAULT_PORT + ")"
        ));

        return new HttpServerConfigImpl(port);
    }

    @NotNull
    private ConfigurationSection getSection(ConfigurationSection parent, String key) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) {
            return parent.createSection(key);
        }
        return section;
    }
}
