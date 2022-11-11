package ca.bkaw.praeter.framework.resources;

import ca.bkaw.praeter.framework.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.framework.resources.pack.ResourcePack;
import ca.bkaw.praeter.framework.resources.pack.send.ResourcePackSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    public ResourcePack getMainResourcePack() {
        return mainResourcePack;
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
