package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.font.FontSequenceBuilder;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import org.bukkit.NamespacedKey;

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
     * The y offset from the title of a gui (where custom fonts are placed to render a
     * gui) to the top-left pixel of the top-left slot, slot (0, 0).
     */
    public static final int ORIGIN_OFFSET_X = -3;

    /**
     * The y offset from the title of a gui (where custom fonts are placed to render a
     * gui) to the top-left pixel of the top-left slot, slot (0, 0).
     */
    public static final int ORIGIN_OFFSET_Y = 2;

    private final List<ResourcePack> resourcePacks;

    public RenderSetupContext(List<ResourcePack> resourcePacks) {
        this.resourcePacks = resourcePacks;
    }

    /**
     * Create a new builder for a font sequence.
     * <p>
     * The origin of the {@link FontSequenceBuilder} will be the top-left pixel of the
     * top-left slot, slot (0, 0).
     *
     * @return The builder.
     * @throws IOException If an I/O error occurs.
     */
    public FontSequenceBuilder newFontSequence() throws IOException {
        NamespacedKey fontKey = new NamespacedKey(Praeter.NAMESPACE, "gui"); // TODO use gui key?
        return new FontSequenceBuilder(this.resourcePacks, fontKey, ORIGIN_OFFSET_X, ORIGIN_OFFSET_Y);
    }
}
