package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.ResourcePackList;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.util.List;

/**
 * Context when setting up component type renderers.
 * <p>
 * The font sequences created here can later be rendered by a call in the {@link
 * RenderDispatcher}.
 *
 * @see FontGuiComponentRenderer#onSetup(CustomGuiType, GuiComponentType, RenderSetupContext)
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

    private final ResourcePackList resourcePacks;
    private DrawOrigin origin = GuiUtils.GUI_SLOT_ORIGIN;

    public RenderSetupContext(ResourcePackList resourcePacks) {
        this.resourcePacks = resourcePacks;
    }

    /**
     * Change the origin for subsequent {@link GuiFontSequenceBuilder}s.
     *
     * @param origin The origin.
     * @see GuiFontSequenceBuilder#setOrigin(DrawOrigin)
     */
    public void setOrigin(DrawOrigin origin) {
        this.origin = origin;
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
     * Get the resource packs where all assets need to be present.
     *
     * @return The immutable list of resource packs.
     */
    public ResourcePackList getResourcePacks() {
        return this.resourcePacks;
    }
}
