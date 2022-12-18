package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A {@link PacksHolder} that holds resource packs, and additionally the vanilla
 * assets.
 */
public class ResourcePacksHolder extends PacksHolder<ResourcePack> {
    private ResourcePack vanillaAssets;

    public ResourcePacksHolder(ResourcePack main) {
        super(main);
    }

    /**
     * Get the client-side vanilla assets (the "Default" resource pack).
     *
     * @return The vanilla assets.
     */
    @NotNull
    public ResourcePack getVanillaAssets() {
        if (this.vanillaAssets == null) {
            throw new RuntimeException("Vanilla assets was not set.");
        }
        return this.vanillaAssets;
    }

    /**
     * Set the vanilla assets.
     *
     * @param vanillaAssets The vanilla assets.
     */
    @ApiStatus.Internal
    public void setVanillaAssets(@NotNull ResourcePack vanillaAssets) {
        this.vanillaAssets = vanillaAssets;
    }

    /**
     * Get a resource that exists in all the specified packs. Or if not found, search
     * vanilla assets too.
     * <p>
     * If the resource is found in one of the packs, but not another, an exception
     * will be thrown.
     *
     * @param resourcePacks The resource packs to search.
     * @param filePath The file path to search for.
     * @return The path.
     * @throws RuntimeException If the resource could not be found, or mismatch
     * between the provided packs was detected.
     */
    @Deprecated(forRemoval = true)
    @NotNull
    public Path getResource(List<ResourcePack> resourcePacks, String filePath) {
        Path foundPath = null;
        for (ResourcePack resourcePack : resourcePacks) {
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
        Path path = this.vanillaAssets.getPath(filePath);
        if (Files.exists(path)) {
            return path;
        } else {
            throw new RuntimeException("The resource '" + filePath + "' was not found.");
        }
    }

}
