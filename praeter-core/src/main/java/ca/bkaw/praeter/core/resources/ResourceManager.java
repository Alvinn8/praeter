package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.pack.send.ResourcePackSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
