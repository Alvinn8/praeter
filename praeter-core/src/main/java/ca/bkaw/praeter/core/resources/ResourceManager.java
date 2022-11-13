package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.pack.send.ResourcePackSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

// TODO
public class ResourceManager {
    // TODO
    private ResourcePack mainResourcePack;
    private ResourcePack vanillaAssets;
    private BakedResourcePack mainBakedResourcePack;
    private ResourcePackSender resourcePackSender;

    /**
     * Get the baked resource pack to currently use for the specified player. The
     * player will have that pack applied.
     *
     * @param player The player.
     * @return The baked resource pack.
     */
    @NotNull
    public BakedResourcePack getBakedResourcePack(Player player) {
        if (player.hasResourcePack()) {
            return this.mainBakedResourcePack;
        }
        throw new IllegalArgumentException("The player does not have a resource pack in " +
            "this world.");
    }

    /**
     * Get a list of resource packs where the specified plugin is enabled.
     *
     * @param plugin The plugin.
     * @return The list of resource packs.
     */
    public List<ResourcePack> getResourcePacks(Plugin plugin) {
        return Collections.singletonList(this.mainResourcePack); // TODO
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

    public ResourcePack getMainResourcePack() {
        return mainResourcePack;
    }

    public BakedResourcePack getMainBakedResourcePack() {
        return mainBakedResourcePack;
    }

    public ResourcePack getVanillaAssets() {
        return vanillaAssets;
    }

    public void setMainResourcePack(ResourcePack mainResourcePack) {
        this.mainResourcePack = mainResourcePack;
    }

    public void setVanillaAssets(ResourcePack vanillaAssets) {
        this.vanillaAssets = vanillaAssets;
    }

    public void setMainBakedResourcePack(BakedResourcePack mainBakedResourcePack) {
        this.mainBakedResourcePack = mainBakedResourcePack;
    }

    public ResourcePackSender getResourcePackSender() {
        return this.resourcePackSender;
    }

    public void setResourcePackSender(ResourcePackSender resourcePackSender) {
        if (this.resourcePackSender != null) {
            this.resourcePackSender.remove();
        }
        this.resourcePackSender = resourcePackSender;
    }
}
