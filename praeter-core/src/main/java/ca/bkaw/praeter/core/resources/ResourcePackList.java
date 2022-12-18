package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

/**
 * A list of resource packs.
 * <p>
 * This class provides useful methods for accessing resources that should exist in
 * all packs in a list.
 */
public class ResourcePackList implements Iterable<ResourcePack> {
    private final List<ResourcePack> list;

    public ResourcePackList(List<ResourcePack> list) {
        this.list = list;
    }

    @NotNull
    @Override
    public Iterator<ResourcePack> iterator() {
        return this.list.iterator();
    }

    public int size() {
        return this.list.size();
    }

    /**
     * Get a resource that exists in all packs in this list. Or if not found, search
     * vanilla assets too.
     * <p>
     * If the resource is found in one of the packs, but not another, an exception
     * will be thrown.
     *
     * @param filePath The file path to search for.
     * @return The path.
     * @throws RuntimeException If the resource could not be found, or mismatch
     * between the provided packs was detected.
     */
    @NotNull
    public Path getResource(String filePath) {
        Path foundPath = null;
        for (ResourcePack resourcePack : this) {
            Path path = resourcePack.getPath(filePath);
            if (Files.exists(path)) {
                foundPath = path;
            } else if (foundPath != null) {
                // The resource was found in one pack, but not in another
                throw new RuntimeException("The resource '" + filePath + "' was found in one pack, but not in another.");
            }
        }
        if (foundPath != null) {
            return foundPath;
        }
        // The resource was not found in the packs, lets search the vanilla assets
        Path path = Praeter.get().getResourceManager().getPacks().getVanillaAssets().getPath(filePath);
        if (Files.exists(path)) {
            return path;
        } else {
            throw new RuntimeException("The resource '" + filePath + "' was not found.");
        }
    }

    /**
     * Get the path to a model resource that exists in all packs in this list. Or if
     * not found, search vanilla assets too.
     * <p>
     * The key is relative to the "textures" folder, but "item", "block", etc. folders
     * must be provided in the key. The file extension (.json) may optionally be
     * provided in the key.
     *
     * @param namespacedKey The namespaced key of the model to get the path.
     * @return The path of the model.
     */
    @NotNull
    public Path getModelPath(NamespacedKey namespacedKey) {
        return this.getResource(ResourcePack.getModelStringPath(namespacedKey));
    }

    /**
     * Get the path to a texture resource that exists in all packs in this list. Or if
     * not found, search vanilla assets too.
     * <p>
     * The key is relative to the "textures" folder, but "item", "block", etc. folders
     * must be provided in the key. The file extension (.png) may optionally be
     * provided in the key.
     *
     * @param namespacedKey The namespaced key of the texture to get the string path.
     * @return The path of the texture.
     */
    @NotNull
    public Path getTexturePath(NamespacedKey namespacedKey) {
        return this.getResource(ResourcePack.getTextureStringPath(namespacedKey));
    }
}