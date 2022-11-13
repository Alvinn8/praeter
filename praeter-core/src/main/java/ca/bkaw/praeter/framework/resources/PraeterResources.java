package ca.bkaw.praeter.framework.resources;

/**
 * The main static-state praeter resources.
 */
public class PraeterResources {
    private static final PraeterResources instance = new PraeterResources();

    /**
     * Get the {@link PraeterResources} instance.
     *
     * @return The instance.
     */
    public static PraeterResources get() {
        return instance;
    }

    private PraeterResources() {}

    private final ResourceManager resourceManager = new ResourceManager();

    /**
     * Get the {@link ResourceManager}.
     *
     * @return The resource manager.
     */
    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }
}
