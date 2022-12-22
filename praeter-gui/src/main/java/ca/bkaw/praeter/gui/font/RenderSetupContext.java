package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.ResourcePackList;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.component.GuiComponent;
import org.bukkit.NamespacedKey;

import java.io.IOException;

/**
 * Context when setting up component renderers.
 * <p>
 * The font sequences created here can later be rendered by a call in the {@link
 * RenderDispatcher}.
 *
 * @see GuiComponent#onSetup(RenderSetupContext)
 */
public class RenderSetupContext {
    /**
     * The key of the font where all characters to render the font are placed.
     */
    // We use the same font key for all guis so common characters like spaces can
    // be reused. Characters are also not stored past restart, so it does not matter
    // that they will change frequently. (This differs from for example if the
    // characters were used in item lore)
    public static final NamespacedKey FONT_KEY = new NamespacedKey(Praeter.NAMESPACE, "gui");

    private final GuiBackgroundPainter background;
    private final ResourcePackList resourcePacks;
    private DrawOrigin origin = GuiUtils.GUI_SLOT_ORIGIN;

    public RenderSetupContext(GuiBackgroundPainter background, ResourcePackList resourcePacks) {
        this.background = background;
        this.resourcePacks = resourcePacks;
    }

    /**
     * Change the origin for subsequent {@link GuiFontSequenceBuilder}s and for the
     * background.
     *
     * @param origin The origin.
     * @see GuiFontSequenceBuilder#setOrigin(DrawOrigin)
     * @see GuiBackgroundPainter#setOrigin(DrawOrigin)
     */
    public void setOrigin(DrawOrigin origin) {
        this.origin = origin;
        this.background.setOrigin(origin);
    }

    /**
     * Create a new builder for a font sequence.
     *
     * @return The builder, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    public GuiFontSequenceBuilder newFontSequence() throws IOException {
        return new GuiFontSequenceBuilder(this.resourcePacks, FONT_KEY, this.origin);
    }

    /**
     * Get the background that can be pained upon.
     *
     * @return The background.
     */
    public GuiBackgroundPainter getBackground() {
        return this.background;
    }

    /**
     * Get the resource packs where all assets need to be present.
     *
     * @return The immutable list of resource packs.
     */
    public ResourcePackList getResourcePacks() {
        return this.resourcePacks;
    }
}
