package ca.bkaw.praeter.core.resources.send;

import ca.bkaw.praeter.core.resources.ResourceManager;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A request for a player to apply a resource pack.
 * <p>
 * Will attempt to apply the resource pack twice which works as a workaround for
 * <a href="https://bugs.mojang.com/browse/MC-164316">MC-164316</a>.
 * <p>
 * Will also work around another bug where SUCCESSFULLY_LOADED is sent despite the
 * pack application failing.
 */
public class ResourcePackRequest {
    private final Player player;
    private final BakedResourcePack resourcePack;
    private final ResourceManager resourceManager;
    private final String url;
    private final byte[] hash;
    private final boolean required;
    private final Component prompt;
    private boolean secondAttempt = false;
    private long acceptedAt = -1;

    public ResourcePackRequest(Player player,
                               BakedResourcePack resourcePack,
                               ResourceManager resourceManager,
                               String url,
                               byte[] hash,
                               boolean required,
                               @Nullable Component prompt) {
        this.player = player;
        this.resourcePack = resourcePack;
        this.resourceManager = resourceManager;
        this.url = url;
        this.hash = hash;
        this.required = required;
        this.prompt = prompt;
    }

    /**
     * Whether this request can be attempted again or if it already has attempted twice
     * at applying the resource pack.
     *
     * @return Whether the second attempt has been attempted.
     */
    public boolean canTryAgain() {
        return !this.secondAttempt;
    }

    /**
     * Send the resource pack to the player.
     */
    public void send() {
        this.resourceManager.getPendingRequests().put(this.player, this);
        this.player.setResourcePack(this.url, this.hash, this.prompt, this.required);
    }

    /**
     * Send the resource pack to the player again, and change the {@link #secondAttempt}
     * field to true.
     */
    public void resend() {
        this.secondAttempt = true;
        this.send();
    }

    /**
     * Get the baked resource pack being sent.
     *
     * @return The baked resource pack.
     */
    public BakedResourcePack getResourcePack() {
        return this.resourcePack;
    }

    /**
     * Mark that the resource pack was accepted.
     */
    public void accepted() {
        this.acceptedAt = System.currentTimeMillis();
    }

    /**
     * Check whether the pack was applied too quickly.
     *
     * @return Whether too soon.
     */
    public boolean isTooSoon() {
        long diff = System.currentTimeMillis() - this.acceptedAt;
        // Less than a second is deemed too soon, and is probably a failed download
        // that sends successful packets for some reason.
        System.out.println("It took " + diff + " ms");
        return diff < 3000;
    }
}