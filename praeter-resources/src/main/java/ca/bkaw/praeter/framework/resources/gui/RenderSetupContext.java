package ca.bkaw.praeter.framework.resources.gui;

import ca.bkaw.praeter.framework.gui.component.GuiComponentType;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiType;
import ca.bkaw.praeter.framework.resources.pack.ResourcePack;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.util.List;

/**
 * Context when setting up component type renderers.
 *
 * @see FontGuiComponentRenderer#onSetup(CustomGuiType, GuiComponentType, RenderSetupContext)
 */
public class RenderSetupContext {
    private List<ResourcePack> getResourcePacks() {
        throw new UnsupportedOperationException(); // TODO
    }

    public GuiFontSequenceBuilder newFontSequence() throws IOException {
        NamespacedKey fontKey = new NamespacedKey("praeter", "gui"); // TODO use gui key?
        return new GuiFontSequenceBuilder(this.getResourcePacks(), fontKey);
    }
}
