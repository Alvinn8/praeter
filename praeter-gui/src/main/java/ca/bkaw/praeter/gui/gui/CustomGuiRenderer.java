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
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.font.GuiBackgroundPainter;
import ca.bkaw.praeter.gui.font.RenderDispatcher;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

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
        GuiBackgroundPainter backgroundPainter;

        // Create the background
        try {
            backgroundPainter = new GuiBackgroundPainter(customGuiType.getHeight(), resourcePacks);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create GUI background.", e);
        }

        RenderSetupContext context = new RenderSetupContext(backgroundPainter, resourcePacks);

        // Call component onSetup methods
        for (GuiComponent component : customGuiType.getComponents()) {
            // All drawing operations on a component renderer will use that component's
            // position as the origin. This allows components to simply draw at (0, 0) to
            // draw at the component's location.
            DrawOrigin origin = GuiUtils.GUI_SLOT_ORIGIN.add(
                component.getX() * GuiUtils.SLOT_SIZE,
                component.getY() * GuiUtils.SLOT_SIZE
            );
            context.setOrigin(origin);
            try {
                component.onSetup(context);
            } catch (IOException e) {
                throw new RuntimeException("Failed to set up renderer for component " + component.getClass().getSimpleName(), e);
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
    @Nullable
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
            return null;
        }

        RenderDispatcher renderDispatcher = new RenderDispatcher(bakedResourcePack);

        // The background goes first, it should be behind everything
        renderDispatcher.render(this.background);

        // Then draw components
        for (GuiComponent component : customGui.getType().getComponents()) {
            GuiComponent.State state = component.getState(customGui);
            state.onRender(renderDispatcher);
        }

        // Finally, draw the actual title
        renderDispatcher.addTitle(title);

        return renderDispatcher.toComponent();
    }
}
