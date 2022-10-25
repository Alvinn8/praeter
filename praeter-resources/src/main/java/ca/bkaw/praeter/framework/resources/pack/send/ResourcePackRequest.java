package ca.bkaw.praeter.framework.resources.pack.send;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A request for a player to apply a resource pack.
 * <p>
 * Will attempt to apply the resource pack twice which works as a workaround for
 * <a href="https://bugs.mojang.com/browse/MC-164316">MC-164316</a>.
 */
public class ResourcePackRequest {
    private final Player player;
    private final String url;
    private final String hash;
    private final boolean required;
    private final Component prompt;
    private boolean secondAttempt = false;

    public ResourcePackRequest(Player player, String url, String hash, boolean required, @Nullable Component prompt) {
        this.player = player;
        this.url = url;
        this.hash = hash;
        this.required = required;
        this.prompt = prompt;

        // TODO add to list of pending requests?
    }

    /**
     * Whether this request has attempted twice at applying the resource pack.
     *
     * @return Whether the second attempt has been attempted.
     */
    public boolean isSecondAttempt() {
        return this.secondAttempt;
    }

    /**
     * Send the resource pack to the player.
     */
    public void send() {
        this.player.setResourcePack(this.url, this.hash, this.required, this.prompt);
    }

    /**
     * Send the resource pack to the player again, and change the {@link #secondAttempt} field to true.
     */
    public void resend() {
        this.secondAttempt = true;
        this.send();
    }
}