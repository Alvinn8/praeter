package ca.bkaw.praeter.framework.resources.pack.send;

import ca.bkaw.praeter.framework.resources.bake.BakedResourcePack;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
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
    public static final int PORT = 50864;

    private final HttpServer server;

    public HttpServerResourcePackSender() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        this.server.start();
        // TODO logger
        System.out.println("Started an HTTP Server on port " + PORT + " that will send " +
            "resource packs to players.");
    }

    @Override
    public void send(BakedResourcePack resourcePack, Player player, boolean required, @Nullable Component prompt) {
        Path file = Path.of("plugins/praeter/internal/resourcepacks/main.zip"); // TODO
        if (Files.exists(file)) {
            String path = "/praeter/main.zip";
            Handler handler = new Handler(file);
            handler.context = this.server.createContext(path, handler);
        } else {
            throw new IllegalArgumentException("Can not send the specified pack.");
        }
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
                inputStream.transferTo(exchange.getResponseBody());
                exchange.close();
            } finally {
                server.removeContext(context);
            }
        }
    }
}
