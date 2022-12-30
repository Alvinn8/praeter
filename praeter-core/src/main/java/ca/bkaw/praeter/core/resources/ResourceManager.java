package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.apply.ResourcePackApplier;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.send.ResourcePackRequest;
import ca.bkaw.praeter.core.resources.send.ResourcePackSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The manager of resource packs.
 */
public class ResourceManager {
    private final Map<Player, ResourcePackRequest> pendingRequests = new HashMap<>();
    private final Map<Player, BakedResourcePack> appliedPacks = new HashMap<>();
    private Path resourcePacksFolder;
    private ResourcePacksHolder packs;
    private PacksHolder<BakedResourcePack> bakedPacks;
    private ResourcePackSender resourcePackSender;
    private ResourcePackApplier resourcePackApplier;

    /**
     * Get the baked resource pack to currently use for the specified player. The
     * player will have that pack applied.
     *
     * @param player The player.
     * @return The baked resource pack.
     */
    @NotNull
    public BakedResourcePack getBakedResourcePack(Player player) {
        return this.appliedPacks.get(player);
    }

    /**
     * Get a list of resource packs where the specified plugin is enabled.
     *
     * @param plugin The plugin.
     * @return The list of resource packs.
     */
    public ResourcePackList getResourcePacks(Plugin plugin) {
        return new ResourcePackList(Collections.singletonList(this.packs.getMain())); // TODO
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
     * It is only recommended to get the baked resource packs after startup. Before
     * that, the packs have not been baked yet.
     *
     * @return The {@link PacksHolder}.
     */
    @NotNull
    public PacksHolder<BakedResourcePack> getBakedPacks() {
        if (this.bakedPacks == null) {
            // throw new IllegalStateException("Packs have not been baked yet.");
            Praeter.get().getLogger().warning("Call to getBakedPacks before packs have been " +
                "baked! A temporary bake will be created, this will affect performance. Please " +
                "only access getBakedPacks after startup.");
            new Exception("stack trace").printStackTrace();
            try {
                BakedResourcePack main = BakedResourcePack.bake(this.packs.getMain());
                return new PacksHolder<>(main);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
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
     * Get the {@link ResourcePackApplier} used to determine when to apply resource
     * packs.
     *
     * @return The applier.
     */
    @NotNull
    public ResourcePackApplier getResourcePackApplier() {
        return this.resourcePackApplier;
    }

    /**
     * Get the {@link ResourcePackApplier} used to determine when to apply resource
     * packs.
     *
     * @param resourcePackApplier The applier.
     */
    public void setResourcePackApplier(@NotNull ResourcePackApplier resourcePackApplier) {
        this.resourcePackApplier = resourcePackApplier;
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

    /**
     * Get the mutable map of applied resource packs.
     *
     * @return The map.
     */
    @ApiStatus.Internal
    public Map<Player, BakedResourcePack> getAppliedPacks() {
        return appliedPacks;
    }

    /**
     * Get the folder where resource packs are stored.
     * <p>
     * It is not recommended to interact with this folder before packs have been baked
     * and closed.
     *
     * @return The path to the folder.
     */
    public Path getResourcePacksFolder() {
        if (this.resourcePacksFolder == null) {
            throw new IllegalStateException();
        }
        return this.resourcePacksFolder;
    }

    /**
     * Set the folder where resource packs are stored.
     *
     * @param resourcePacksFolder The folder.
     */
    @ApiStatus.Internal
    public void setResourcePacksFolder(Path resourcePacksFolder) {
        if (this.resourcePacksFolder != null) {
            throw new IllegalStateException("Can not change the resource packs folder.");
        }
        this.resourcePacksFolder = resourcePacksFolder;
    }
}
