package ca.bkaw.praeter.core.resources.send;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An implementation of {@link ResourcePackSender} that sends resource packs via
 * an HTTP server running on another port.
 */
public class HttpServerResourcePackSender implements ResourcePackSender {
    /**
     * The default port used for the HTTP server.
     */
    public static final int DEFAULT_PORT = 50864;

    private final HttpServer server;
    private final int port;

    public HttpServerResourcePackSender() throws IOException {
        this.port = Praeter.get().getConfig().sender().httpServer().port();

        this.server = HttpServer.create(new InetSocketAddress("0.0.0.0", this.port), 0);
        this.server.start();
        Praeter.get().getLogger().info("Started an HTTP Server on port " + this.port + " that " +
                "will send resource packs to players.");
    }

    @Override
    public void send(@NotNull BakedResourcePack resourcePack, @NotNull Player player, boolean required, @Nullable Component prompt) {
        Path file = Utils.getPath(resourcePack);
        String path = "/";
        Handler handler = new Handler(file);
        try {
            handler.context = this.server.createContext(path, handler);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        Utils.sendRequest(resourcePack, file, player, required, prompt, this.port, path);
    }

    @Override
    public void remove() {
        this.server.stop(1);
    }

    private class Handler implements HttpHandler {
        private final Path path;
        private HttpContext context;

        private Handler(Path path) {
            this.path = path;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (InputStream inputStream = Files.newInputStream(this.path)) {
                exchange.sendResponseHeaders(200, Files.size(this.path));
                inputStream.transferTo(exchange.getResponseBody());
                exchange.close();
            } finally {
                server.removeContext(context);
                // TODO if the pack fails to download the first time due to the hash changing,
                //  the second attempt will also fail because of this call to removeContext
            }
        }
    }
}
