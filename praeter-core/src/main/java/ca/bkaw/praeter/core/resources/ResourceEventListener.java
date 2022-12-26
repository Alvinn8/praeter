package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.resources.send.ResourcePackRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

/**
 * The event listener for resource the resource pack manager.
 */
public class ResourceEventListener implements Listener {
    private final ResourceManager resourceManager;

    public ResourceEventListener(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
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
                if (request.isTooSoon() && request.canTryAgain()) {
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.resourceManager.getPendingRequests().remove(event.getPlayer());
        this.resourceManager.getAppliedPacks().remove(event.getPlayer());
        System.out.println("removing (size: " + this.resourceManager.getAppliedPacks().size() + ")");
    }
}
