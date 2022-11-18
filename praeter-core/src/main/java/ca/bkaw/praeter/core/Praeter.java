package ca.bkaw.praeter.core;

import ca.bkaw.praeter.core.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * The main static-state praeter.
 */
public class Praeter {
    private static final Praeter instance = new Praeter();

    /**
     * Get the {@link Praeter} instance.
     *
     * @return The instance.
     */
    public static Praeter get() {
        return instance;
    }

    private Praeter() {}

    /**
     * The praeter namespace used in {@link org.bukkit.NamespacedKey}s.
     */
    public static final String NAMESPACE = "praeter";

    /**
     * The namespace used for generated resources.
     */
    public static final String GENERATED_NAMESPACE = "generated";

    private final ResourceManager resourceManager = new ResourceManager();
    private Logger logger;

    /**
     * Get the {@link ResourceManager}.
     *
     * @return The resource manager.
     */
    @NotNull
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    /**
     * Get the logger used by praeter.
     *
     * @return The logger.
     */
    @ApiStatus.Internal
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * Set the logger used by praeter.
     *
     * @param logger The logger.
     */
    @ApiStatus.Internal
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
