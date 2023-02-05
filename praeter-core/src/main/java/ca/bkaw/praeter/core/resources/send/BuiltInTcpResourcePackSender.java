package ca.bkaw.praeter.core.resources.send;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link ResourcePackSender} implementation that sends resource packs by
 * replying to HTTP requests received on the server's TCP connections.
 */
@ChannelHandler.Sharable
public class BuiltInTcpResourcePackSender extends ChannelInboundHandlerAdapter implements ResourcePackSender {
    private static final String HANDLER_NAME = "praeter_resource_pack_sender";
    private static final Key HANDLER_KEY = Key.key(Praeter.NAMESPACE, HANDLER_NAME);
    private static final String PATH_PREFIX = "/praeter/";

    public BuiltInTcpResourcePackSender() throws ReflectiveOperationException {
        this.inject();
        Praeter.get().getLogger().info("Using the server's TCP connections to send resource packs.");
    }

    @Override
    public void send(@NotNull BakedResourcePack resourcePack, @NotNull Player player, boolean required, @Nullable Component prompt) {
        Path file = Utils.getPath(resourcePack);
        String id = Praeter.get().getResourceManager().getBakedPacks().getId(resourcePack);
        String path = PATH_PREFIX + id;
        int port = Bukkit.getPort();
        Utils.sendRequest(resourcePack, file, player, required, prompt, port, path);
    }

    @Override
    public void remove() {

    }

    private void inject() throws ReflectiveOperationException {
        // Implement the ChannelInitializeListener interface using a proxy
        Class<?> listenerClass = Class.forName("io.papermc.paper.network.ChannelInitializeListener");
        Object listener = Proxy.newProxyInstance(
            BuiltInTcpResourcePackSender.class.getClassLoader(),
            new Class[]{ listenerClass },
            (proxy, method, args) -> {
                if ("afterInitChannel".equals(method.getName())) {
                    Channel channel = (Channel) args[0];
                    channel.pipeline().addFirst(HANDLER_NAME, this);
                    return null;
                }
                return method.invoke(proxy, args);
            });

        // Add the listener
        Class<?> holderClass = Class.forName("io.papermc.paper.network.ChannelInitializeListenerHolder");
        Method method = holderClass.getMethod("addListener", Key.class, listenerClass);
        method.invoke(null, HANDLER_KEY, listener);
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byteBuf.markReaderIndex();
        if (!this.handle(ctx, byteBuf)) {
            // handle returned false, reset reader and call the super method to let vanilla
            // handle the connection.
            byteBuf.resetReaderIndex();
            super.channelRead(ctx, msg);
        }
    }

    private boolean handle(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        // There needs to be at least 14 bytes for "GET /praeter/<resource pack id>" to fit.
        if (byteBuf.capacity() < 14) return false;

        // Start by efficiently comparing byte by byte as this is some hot networking
        // code. This code runs for every received packet.

        if (byteBuf.readByte() != 'G') return false;
        if (byteBuf.readByte() != 'E') return false;
        if (byteBuf.readByte() != 'T') return false;
        if (byteBuf.readByte() != ' ') return false;

        // An HTTP GET request was received.
        // Let's ensure it is requesting a resource pack from praeter

        byte[] pathBytes = PATH_PREFIX.getBytes(StandardCharsets.UTF_8);
        for (byte pathByte : pathBytes) {
            if (byteBuf.readByte() != pathByte) return false;
        }

        // Read the pack id from the path

        StringBuilder resourcePackId = new StringBuilder();
        byte b;
        while (byteBuf.readableBytes() > 0 && (b = byteBuf.readByte()) != ' ') {
            resourcePackId.append((char) b);
        }

        // Get the requested pack

        BakedResourcePack resourcePack = Praeter.get().getResourceManager()
            .getBakedPacks().getById(resourcePackId.toString());

        if (resourcePack == null) {
            // Sorry, we do not gracefully reply with an HTTP 404 response. We just close
            // the connection.
            ctx.close();

            // Return true, we have handled the packet.
            return true;
        }

        try {
            Path path = Utils.getPath(resourcePack);

            long contentLength = Files.size(path);

            String headerText =
                """
                    HTTP/1.1 200 OK
                    Server: Praeter
                    Content-Type: application/zip
                    Content-Length: %d
                                
                    """.formatted(contentLength);
            byte[] headerBytes = headerText.getBytes(StandardCharsets.UTF_8);

            ByteBuf response = Unpooled.buffer(headerBytes.length + (int) contentLength);

            response.writeBytes(headerBytes);

            ByteBufOutputStream stream = new ByteBufOutputStream(response);
            Files.copy(path, stream);
            stream.close();

            // Send the response
            ctx.pipeline().firstContext().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

            // Return true, we have handled the packet
            return true;
        } catch (IOException e) {
            Praeter.get().getLogger().severe("Failed to reply with resource pack.");
            e.printStackTrace();

            // Sorry, we do not gracefully reply with an HTTP 500 response. We just close
            // the connection.
            ctx.close();

            // Return true, we have handled the packet.
            return true;
        }
    }
}
