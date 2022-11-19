package ca.bkaw.praeter.gui;

import org.jetbrains.annotations.NotNull;

/**
 * Static-state for {@code praeter-gui}.
 */
public class PraeterGui {
    private static final PraeterGui instance = new PraeterGui();

    /**
     * Get the {@link PraeterGui} instance.
     *
     * @return The instance.
     */
    public static PraeterGui get() {
        return instance;
    }

    private PraeterGui() {}

    private final GuiRegistry guiRegistry = new GuiRegistry();

    /**
     * Get the {@link GuiRegistry} where custom guis can be registered.
     *
     * @return The registry.
     */
    @NotNull
    public GuiRegistry getGuiRegistry() {
        return this.guiRegistry;
    }
}
