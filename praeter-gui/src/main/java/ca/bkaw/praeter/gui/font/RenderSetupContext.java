package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
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

    private final List<ResourcePack> resourcePacks;

    public RenderSetupContext(List<ResourcePack> resourcePacks) {
        this.resourcePacks = resourcePacks;
    }

    /**
     * Create a new builder for a font sequence.
     *
     * @return The builder, for chaining.
     * @throws IOException If an I/O error occurs.
     */
    public GuiFontSequenceBuilder newFontSequence() throws IOException {
        return new GuiFontSequenceBuilder(this.resourcePacks, FONT_KEY);
    }

    /**
     * Get the resource packs where all assets need to be present.
     *
     * @return The immutable list of resource packs.
     */
    @Unmodifiable
    public List<ResourcePack> getResourcePacks() {
        return this.resourcePacks;
    }
}
