package ca.bkaw.praeter.gui.gui;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.ResourceManager;
import ca.bkaw.praeter.core.resources.ResourcePackList;
import ca.bkaw.praeter.core.resources.bake.BakedResourcePack;
import ca.bkaw.praeter.core.resources.draw.DrawOrigin;
import ca.bkaw.praeter.core.resources.font.FontSequence;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.PraeterGui;
import ca.bkaw.praeter.gui.component.ComponentMap;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentRenderer;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.font.*;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A renderer responsible for rendering a {@link CustomGui custom gui} and its
 * {@link GuiComponent components}.
 */
public class CustomGuiRenderer {
    private FontSequence background;

    /**
     * A method called during startup when the custom gui type is being created.
     * <p>
     * This allows for preparation that needs to be performed during startup, for
     * example to allow renderers from {@code praeter-resources} to generate textures
     * and other assets that need to be included in resource packs.
     *
     * @param customGuiType The custom gui type.
     */
    public void onSetup(CustomGuiType customGuiType) {
        ResourcePackList resourcePacks = Praeter.get().getResourceManager().getResourcePacks(customGuiType.getPlugin());
        RenderSetupContext context = new RenderSetupContext(resourcePacks);
        GuiBackgroundPainter backgroundPainter;

        // Create the background
        try {
            backgroundPainter = new GuiBackgroundPainter(customGuiType.getHeight(), resourcePacks);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create GUI background.", e);
        }

        // Call component onSetup methods
        for (GuiComponentType<?, ?> componentType : customGuiType.getComponentTypes()) {
            try {
                // TODO fix generics...
                forEachComponentType((GuiComponentType) componentType, customGuiType, context, backgroundPainter);
            } catch (IOException e) {
                throw new RuntimeException("Failed to set up renderer for component " + componentType.getClass().getSimpleName(), e);
            }
        }

        // Create the background
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(backgroundPainter.getImage(), "png", stream);
            NamespacedKey id = PraeterGui.get().getGuiRegistry().getId(customGuiType);
            NamespacedKey backgroundKey = new NamespacedKey(Praeter.GENERATED_NAMESPACE,
                "gui/background/" + id.getNamespace() + '/' + id.getKey() + ".png");
            byte[] bytes = stream.toByteArray();
            for (ResourcePack resourcePack : resourcePacks) {
                Path modelPath = resourcePack.getTexturePath(backgroundKey);
                Files.createDirectories(modelPath.getParent());
                Files.write(modelPath, bytes);
            }
            context.setOrigin(GuiUtils.GUI_WINDOW_ORIGIN);
            this.background = context.newFontSequence().drawImage(backgroundKey, 0, 0).build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write GUI background.", e);
        }
    }

    private <C extends GuiComponent, T extends GuiComponentType<C, T>> void forEachComponentType(T componentType, CustomGuiType customGuiType, RenderSetupContext context, GuiBackgroundPainter background) throws IOException {
        GuiComponentRenderer<C, T> renderer = componentType.getRenderer();
        // All drawing operations on a component renderer will use that component's
        // position as the origin. This allows components to simply draw at (0, 0) to
        // draw at the component's location.
        DrawOrigin origin = GuiUtils.GUI_SLOT_ORIGIN.add(
            componentType.getX() * GuiUtils.SLOT_SIZE,
            componentType.getY() * GuiUtils.SLOT_SIZE
        );
        if (renderer instanceof FontGuiComponentRenderer<C, T> fontComponentRenderer) {
            context.setOrigin(origin);
            fontComponentRenderer.onSetup(customGuiType, componentType, context);
        }
        if (renderer instanceof BackgroundGuiComponentRenderer<C,T> backgroundComponentRenderer) {
            background.setOrigin(origin);
            backgroundComponentRenderer.draw(customGuiType, componentType, background);
        }
    }

    /**
     * Get the final title to use for the gui.
     * <p>
     * If {@code praeter-resources} is used, custom fonts can be used in the title can
     * be used for rendering purposes.
     *
     * @param title The title that should be displayed for the gui.
     * @param customGui The custom gui that is being rendered.
     * @return The final, rendered, title.
     */
    public Component getRenderTitle(Component title, CustomGui customGui) {
        BakedResourcePack bakedResourcePack = null;
        ResourceManager resourceManager = Praeter.get().getResourceManager();
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
            if (true) {
                System.out.println("customGui.getViewers().size() = " + customGui.getViewers().size());
            }
            return Component.empty();
        }

        RenderDispatcher renderDispatcher = new RenderDispatcher(bakedResourcePack);

        renderDispatcher.render(this.background);

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
