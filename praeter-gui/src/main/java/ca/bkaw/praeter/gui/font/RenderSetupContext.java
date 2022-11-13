package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.util.List;

/**
 * Context when setting up component type renderers.
 *
 * @see FontGuiComponentRenderer#onSetup(CustomGuiType, GuiComponentType, RenderSetupContext)
 */
public class RenderSetupContext {
    private final List<ResourcePack> resourcePacks;

    public RenderSetupContext(List<ResourcePack> resourcePacks) {
        this.resourcePacks = resourcePacks;
    }

    public GuiFontSequenceBuilder newFontSequence() throws IOException {
        NamespacedKey fontKey = new NamespacedKey("praeter", "gui"); // TODO use gui key?
        return new GuiFontSequenceBuilder(this.resourcePacks, fontKey);
    }
}