package ca.bkaw.praeter.gui.font;

import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.font.FontSequence;
import ca.bkaw.praeter.gui.component.GuiComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

/**
 * An object created when a gui is rendered.
 *
 * @see GuiComponent.State#onRender(RenderDispatcher)
 */
public class RenderDispatcher {
    private final BakedResourcePack bakedResourcePack;
    private final List<Component> components = new ArrayList<>();
    private Component title;

    /**
     * Create a new render dispatcher.
     *
     * @param bakedResourcePack The baked resource pack to get baked font chars from.
     */
    public RenderDispatcher(BakedResourcePack bakedResourcePack) {
        this.bakedResourcePack = bakedResourcePack;
    }

    /**
     * Add the specified font sequence to the render title of the gui.
     * <p>
     * This renders the font sequence, which can use characters that render textures,
     * etc. to render the gui and its components.
     *
     * @param fontSequence The font sequence to add.
     */
    public void render(FontSequence fontSequence) {
        this.components.addAll(fontSequence.getChars(this.bakedResourcePack));
    }

    /**
     * Add a component that is rendered as the title of the gui.
     *
     * @param component The component.
     */
    public void addTitle(Component component) {
        this.title = component;
    }

    /**
     * Get the text component holding all font sequences to render.
     *
     * @return The text component.
     */
    public Component toComponent() {
        TextComponent.Builder comps = Component.text().color(NamedTextColor.WHITE).append(this.components);
        return Component.textOfChildren(comps, this.title);
    }
}
