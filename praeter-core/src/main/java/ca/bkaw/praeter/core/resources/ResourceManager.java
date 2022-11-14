package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.core.resources.pack.send.ResourcePackRequest;
import ca.bkaw.praeter.core.resources.pack.send.ResourcePackSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The manager of resource packs.
 */
public class ResourceManager {
    private final Map<Player, ResourcePackRequest> pendingRequests = new HashMap<>();
    private ResourcePacksHolder packs;
    private PacksHolder<BakedResourcePack> bakedPacks;
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
            return this.bakedPacks.getMain();
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
        return Collections.singletonList(this.packs.getMain()); // TODO
    }

    /**
     * Get the {@link ResourcePacksHolder} that contains the resource packs.
     * <p>
     * It is only possible to get the resource packs during startup. After startup, the
     * packs are baked.
     *
     * @return The {@link ResourcePacksHolder}.
     */
    @NotNull
    public ResourcePacksHolder getPacks() {
        if (this.packs == null) {
            throw new IllegalStateException("Packs are only accessible during startup.");
        }
        return this.packs;
    }

    /**
     * Get the {@link PacksHolder} that contains the baked resource packs.
     * <p>
     * It is only possible to get the baked resource packs after startup. Before that,
     * the packs have not been baked yet.
     *
     * @return The {@link PacksHolder}.
     */
    @NotNull
    public PacksHolder<BakedResourcePack> getBakedPacks() {
        if (this.bakedPacks == null) {
            throw new IllegalStateException("Packs have not been baked yet.");
        }
        return this.bakedPacks;
    }

    /**
     * Set the {@link ResourcePacksHolder} for resource packs.
     *
     * @param packs The holder.
     */
    @ApiStatus.Internal
    public void setPacks(@Nullable ResourcePacksHolder packs) {
        this.packs = packs;
    }

    /**
     * Set the {@link PacksHolder} for baked resource packs.
     *
     * @param bakedPacks The holder.
     */
    @ApiStatus.Internal
    public void setBakedPacks(@Nullable PacksHolder<BakedResourcePack> bakedPacks) {
        this.bakedPacks = bakedPacks;
    }

    /**
     * Get the {@link ResourcePackSender}.
     *
     * @return The resource pack sender.
     */
    @NotNull
    public ResourcePackSender getResourcePackSender() {
        if (this.resourcePackSender == null) {
            throw new RuntimeException("No resource pack sender has been set.");
        }
        return this.resourcePackSender;
    }

    /**
     * Set the {@link ResourcePackSender} to use on the server.
     * <p>
     * If a resource pack sender previously was set, the
     * {@link ResourcePackSender#remove()} method will be called on that instance.
     *
     * @param resourcePackSender The resource pack sender.
     */
    public void setResourcePackSender(@NotNull ResourcePackSender resourcePackSender) {
        if (this.resourcePackSender != null) {
            this.resourcePackSender.remove();
        }
        this.resourcePackSender = resourcePackSender;
    }

    /**
     * Get the mutable map of pending resource pack requests.
     *
     * @return The map.
     */
    @ApiStatus.Internal
    public Map<Player, ResourcePackRequest> getPendingRequests() {
        return this.pendingRequests;
    }
}
