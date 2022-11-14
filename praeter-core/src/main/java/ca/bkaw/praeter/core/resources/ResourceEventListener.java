package ca.bkaw.praeter.core.resources;

import ca.bkaw.praeter.core.resources.pack.send.ResourcePackRequest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        ResourcePackRequest resourcePackRequest = this.resourceManager.getPendingRequests().get(event.getPlayer());
        if (resourcePackRequest == null) {
            return;
        }

        switch (event.getStatus()) {
            case FAILED_DOWNLOAD -> {
                if (!resourcePackRequest.isSecondAttempt()) {
                    // If the second attempt has not been attempted, send the pack again.
                    // This works around a bug, see ResourcePackRequest.
                    resourcePackRequest.resend();
                }
            }
            case SUCCESSFULLY_LOADED, DECLINED ->
                this.resourceManager.getPendingRequests().remove(event.getPlayer());
        }
    }
}
