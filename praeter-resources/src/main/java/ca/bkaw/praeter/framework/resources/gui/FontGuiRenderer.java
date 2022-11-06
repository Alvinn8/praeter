package ca.bkaw.praeter.framework.resources.gui;

import ca.bkaw.praeter.framework.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.component.GuiComponentType;
import ca.bkaw.praeter.framework.gui.component.ItemGuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiRenderer;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiType;
import net.kyori.adventure.text.Component;

/**
 * A {@link CustomGuiRenderer} that uses custom fonts to render the gui.
 * <p>
 * Components should use {@link FontGuiComponentRenderer} as their renderer.
 */
public class FontGuiRenderer implements CustomGuiRenderer {
    @Override
    public boolean supports(GuiComponentRenderer<?, ?> componentRenderer) {
        return componentRenderer instanceof FontGuiComponentRenderer<?, ?>
            || componentRenderer instanceof ItemGuiComponentRenderer<?,?>;
    }

    @Override
    public void onSetup(CustomGuiType customGuiType) {
        RenderSetupContext context = new RenderSetupContext(); // TODO

        for (GuiComponentType<?, ?> componentType : customGuiType.getComponentTypes()) {
            GuiComponentRenderer<?, ?> componentRenderer = componentType.getRenderer();
            if (componentRenderer instanceof FontGuiComponentRenderer<?, ?> fontComponentRenderer) {
                // TODO fix generics
                fontComponentRenderer.onSetup(customGuiType, /* componentType */ null, context);
            }
        }
    }

    @Override
    public Component getRenderTitle(Component title, CustomGui customGui) {
        return null;
    }
}
