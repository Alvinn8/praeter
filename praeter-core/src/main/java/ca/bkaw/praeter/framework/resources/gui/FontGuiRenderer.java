package ca.bkaw.praeter.framework.resources.gui;

import ca.bkaw.praeter.framework.gui.component.ComponentMap;
import ca.bkaw.praeter.framework.gui.component.GuiComponent;
import ca.bkaw.praeter.framework.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.component.GuiComponentType;
import ca.bkaw.praeter.framework.gui.component.ItemGuiComponentRenderer;
import ca.bkaw.praeter.framework.gui.gui.CustomGui;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiRenderer;
import ca.bkaw.praeter.framework.gui.gui.CustomGuiType;
import ca.bkaw.praeter.framework.resources.PraeterResources;
import ca.bkaw.praeter.framework.resources.ResourceManager;
import ca.bkaw.praeter.framework.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.framework.resources.pack.ResourcePack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

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
        List<ResourcePack> resourcePacks = PraeterResources.get().getResourceManager().getResourcePacks(customGuiType.getPlugin());
        RenderSetupContext context = new RenderSetupContext(resourcePacks);

        for (GuiComponentType<?, ?> componentType : customGuiType.getComponentTypes()) {
            // TODO fix generics...
            forEachComponentType((GuiComponentType) componentType, customGuiType, context);
        }
    }

    private <C extends GuiComponent, T extends GuiComponentType<C, T>> void forEachComponentType(T componentType, CustomGuiType customGuiType, RenderSetupContext context) {
        GuiComponentRenderer<C, T> renderer = componentType.getRenderer();
        if (renderer instanceof FontGuiComponentRenderer<C, T> fontComponentRenderer) {
            try {
                fontComponentRenderer.onSetup(customGuiType, componentType, context);
            } catch (IOException e) {
                throw new RuntimeException(e); // TODO handle errors somewhere, dont crash everything
            }
        }
    }

    @Override
    public Component getRenderTitle(Component title, CustomGui customGui) {
        BakedResourcePack bakedResourcePack = null;
        ResourceManager resourceManager = PraeterResources.get().getResourceManager();
        for (Player viewer : customGui.getViewers()) {
            BakedResourcePack playerPack = resourceManager.getBakedResourcePack(viewer);
            if (bakedResourcePack == null) {
                bakedResourcePack = playerPack;
            } else if (bakedResourcePack != playerPack) {
                throw new RuntimeException("Viewers of a gui had different baked resource packs. " +
                    "Are there players from different worlds viewing the same gui?");
            }
        }
        if (bakedResourcePack == null) {
            return Component.empty();
        }

        RenderDispatcher renderDispatcher = new RenderDispatcher(bakedResourcePack);
        customGui.forEachComponent(new ComponentMap.ForEachConsumer() {
            @Override
            public <C extends GuiComponent, T extends GuiComponentType<C, T>> void accept(T componentType, C component) {
                GuiComponentRenderer<C, T> renderer = componentType.getRenderer();
                if (renderer instanceof FontGuiComponentRenderer<C, T> fontComponentRenderer) {
                    fontComponentRenderer.onRender(customGui.getType(), customGui, componentType, component, renderDispatcher);
                }
            }
        });
        renderDispatcher.addTitle(title);
        return renderDispatcher.toComponent();
    }
}
