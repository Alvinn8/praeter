package ca.bkaw.praeter.core.resources.apply;

import ca.bkaw.praeter.core.resources.ResourceManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

/**
 * The default {@link ResourcePackApplier} implementation that applies the main
 * resource pack when the player joins.
 */
public class DefaultResourcePackApplier implements Listener, ResourcePackApplier {
    private final ResourceManager resourceManager;
    private final Plugin plugin;

    public DefaultResourcePackApplier(ResourceManager resourceManager, Plugin plugin) {
        this.resourceManager = resourceManager;
        this.plugin = plugin;
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

}
