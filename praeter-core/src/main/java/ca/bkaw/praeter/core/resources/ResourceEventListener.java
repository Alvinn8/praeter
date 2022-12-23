package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.resources.send.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;

/**
 * The event listener for resource the resource pack manager.
 */
public class ResourceEventListener implements Listener {
    private final ResourceManager resourceManager;
    private final Plugin plugin;

    public ResourceEventListener(ResourceManager resourceManager, Plugin plugin) {
        this.resourceManager = resourceManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        ResourcePackRequest request = this.resourceManager.getPendingRequests().get(player);
        if (request == null) {
            return;
        }

        System.out.println("event.getStatus() = " + event.getStatus());
        switch (event.getStatus()) {
            case ACCEPTED -> request.accepted();
            case FAILED_DOWNLOAD -> {
                if (request.canTryAgain()) {
                    // If the second attempt has not been attempted, send the pack again.
                    // This works around a bug, see ResourcePackRequest.
                    request.resend();
                }
            }
            case SUCCESSFULLY_LOADED -> {
                if (request.canTryAgain() && request.isTooSoon()) {
                    // The player accepted too fast. It is assumed to be a failed download that
                    // sends a successful packet due to a bug.
                    request.resend();
                } else {
                    this.resourceManager.getAppliedPacks().put(player, request.getResourcePack());
                    this.resourceManager.getPendingRequests().remove(player);

                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.getServer().getScheduler().runTaskLater(this.plugin, () ->
            this.resourceManager.getResourcePackSender().send(
                this.resourceManager.getBakedPacks().getMain(),
                player,
                true,
                Component.text("Please accept the resource pack to see custom additions to the game.")
            )
        , 20L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.resourceManager.getPendingRequests().remove(event.getPlayer());
        this.resourceManager.getAppliedPacks().remove(event.getPlayer());
        System.out.println("removing (size: " + this.resourceManager.getAppliedPacks().size() + ")");
    }
}
