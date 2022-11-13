package ca.bkaw.praeter.core.resources.pack;

import ca.bkaw.praeter.core.resources.pack.collision.CollisionHandler;
import ca.bkaw.praeter.core.resources.pack.collision.CollisionHandlerImpl;
import ca.bkaw.praeter.core.resources.pack.collision.ResourceCollisionException;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

/**
 * A pack that contains resources, assets and data.
 * <p>
 * This class can be used to access, modify files, merge packs, and other stuff.
 * <p>
 * JSON resources can be gotten by getting the path of the resource using
 * utility methods like {@link ResourcePack#getModelPath(NamespacedKey)} and then
 * by constructing an instance of {@link JsonResource} by calling
 * {@link JsonResource#JsonResource(Pack, Path)}.
 * <p>
 * A JSON resource can be created in a similar way, but by calling
 * {@link JsonResource#JsonResource(Pack, Path, JsonObject)} with the
 * {@link JsonObject} as the third parameter. This resource then needs to be
 * saved with {@link JsonResource#save()}.
 *
 * @see ResourcePack
 */
public abstract class Pack {
    private static final Gson GSON = new Gson();

    private final Path root;

    protected Pack(Path root) {
        this.root = root;
    }

    protected static Path validateDirectoryPath(Path directory) throws IOException {
        if (Files.exists(directory) && !Files.isDirectory(directory)) {
            throw new IllegalArgumentException("The specified path exists but is not a directory.");
        }
        Files.createDirectories(directory);
        return directory;
    }

    protected static Path openZip(Path zipFile) throws IOException {
        URI uri = URI.create("jar:" + zipFile.toUri());
        FileSystem fileSystem = FileSystems.newFileSystem(uri, ImmutableMap.of("create", true));
        return fileSystem.getPath(".").normalize();
    }

    /**
     * Get the root path of this pack.
     *
     * @return The root path.
     */
    public Path getRoot() {
        return this.root;
    }

    /**
     * Get a path within the pack.
     * <p>
     * Example of getting the pack.mcmeta file from the pack:
     * <br>
     * {@code pack.getPath("pack.mcmeta");}
     * <p>
     * Note that the returned path may belong to a file system that
     * has a root that isn't the resource pack root, so it is not safe
     * to call {@code resolve} with a string starting with a slash.
     *
     * @param path The path to get. Leading slashes will be removed.
     * @return The path within the pack.
     */
    public Path getPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return this.root.resolve(path);
    }

    /**
     * Gets the server's current pack format for this pack type.
     *
     * @return The pack format.
     */
    protected abstract int getCurrentPackFormat();

    /**
     * Create the pack by creating the {@code pack.mcmeta} file.
     * <p>
     * The pack format will be set to the server's current pack format as per
     * {@link #getCurrentPackFormat()}.
     *
     * @param description A description of the pack.
     * @throws IllegalStateException If the pack already exists.
     * @throws IOException If an I/O error occurs.
     */
    public void create(String description) throws IOException {
        Path path = this.getPath("pack.mcmeta");

        if (Files.exists(path)) {
            throw new IllegalArgumentException("Tried to create a pack but one " +
                "already existed. The pack already has a pack.mcmeta file.");
        }

        JsonObject root = new JsonObject();
        JsonObject pack = new JsonObject();
        root.add("pack", pack);
        pack.addProperty("pack_format", this.getCurrentPackFormat());
        pack.addProperty("description", description);
        Files.writeString(path, GSON.toJson(root), StandardCharsets.UTF_8);
    }

    /**
     * Include all resources from the other pack into this pack.
     * <p>
     * This is the same as calling {@code pack.include(other, CollisionHandlerImpl.INSTANCE, null)}.
     *
     * @param other The pack to include resources from.
     * @throws ResourceCollisionException If a collision occurred that could not be
     * resolved by the default {@link CollisionHandlerImpl}.
     * @throws IOException If something goes wrong while copying the files.
     * @see #include(Pack, CollisionHandler, Predicate)
     */
    public void include(Pack other) throws ResourceCollisionException, IOException {
        this.include(other, CollisionHandlerImpl.INSTANCE, null);
    }

    /**
     * Include all resources from the other pack into this pack.
     * <p>
     * This is the same as calling {@code resourcePack.include(other, CollisionHandlerImpl.INSTANCE, null)}.
     *
     * @param other The pack to include resources from.
     * @throws ResourceCollisionException If a collision occurred that could not be
     * resolved by the default {@link CollisionHandlerImpl}.
     * @throws IOException If something goes wrong while copying the files.
     * @see #include(Pack, CollisionHandler, Predicate)
     */
    public void include(@NotNull Pack other,
                        @Nullable Predicate<String> filter) throws ResourceCollisionException, IOException {
        this.include(other, CollisionHandlerImpl.INSTANCE, filter);
    }

    /**
     * Include all resources from the other pack into this pack.
     *
     * @param other The pack to include resources from.
     * @param collisionHandler Resolves collisions when both packs have a file of
     *                         the same name.
     * @param filter Determines which resources should be included and not.
     * @throws ResourceCollisionException If a collision occurred that could not be
     * resolved by the {@link CollisionHandler}.
     * @throws IOException If something goes wrong while copying the files.
     */
    public void include(@NotNull Pack other,
                        @NotNull CollisionHandler collisionHandler,
                        @Nullable Predicate<String> filter) throws ResourceCollisionException, IOException {
        Path thisRoot = this.getRoot();
        Path otherRoot = other.getRoot();
        final ResourceCollisionException[] exception = {null}; // ugly, but we have to
        Files.walkFileTree(otherRoot, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                String relative = otherRoot.relativize(dir).toString();
                if (filter != null && !filter.test(relative)) return FileVisitResult.CONTINUE;

                Files.createDirectories(thisRoot.resolve(relative));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path otherFile, BasicFileAttributes attrs) throws IOException {
                String relative = otherRoot.relativize(otherFile).toString();
                if (filter != null && !filter.test(relative)) return FileVisitResult.CONTINUE;

                Path thisFile = thisRoot.resolve(relative);
                if (Files.exists(thisFile)) {
                    // The file already exists, we have a collision
                    try {
                        collisionHandler.handleCollision(Pack.this, other, thisFile, otherFile);
                    } catch (ResourceCollisionException e) {
                        exception[0] = e;
                        return FileVisitResult.TERMINATE;
                    }
                } else {
                    Files.copy(otherFile, thisFile);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (exception[0] != null) throw exception[0];
    }
}
