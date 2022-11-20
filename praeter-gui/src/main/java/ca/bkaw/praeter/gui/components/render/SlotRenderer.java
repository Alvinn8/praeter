package ca.bkaw.praeter.gui.components.render;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.font.FontSequence;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.components.Slot;
import ca.bkaw.praeter.gui.font.FontGuiComponentRenderer;
import ca.bkaw.praeter.gui.font.GuiBackgroundPainter;
import ca.bkaw.praeter.gui.font.RenderDispatcher;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A renderer for a {@link Slot} that uses fonts to render the slot texture.
 */
// TODO nevermind, we can't render slots like this. We need to carve out
//  transparency in the background so that the real background renders trough,
//  which has hover!
//  So we need the system of background-modifying component renderers.
@Deprecated
public class SlotRenderer implements FontGuiComponentRenderer<Slot, Slot.Type> {
    /**
     * The key to the place where the slot texture is stored.
     */
    public static final NamespacedKey SLOT_TEXTURE_KEY
        = new NamespacedKey(Praeter.GENERATED_NAMESPACE, "gui/slot.png");

    /**
     * The x coordinate in the generic_54 texture where the first slot starts.
     */
    public static final int SLOT_PIXEL_X = GuiBackgroundPainter.HORIZONTAL_PADDING;

    /**
     * The y coordinate in the generic_54 texture where the first slot starts.
     */
    public static final int SLOT_PIXEL_Y = GuiBackgroundPainter.TOP_PADDING;

    private FontSequence fontSequence;

    @Override
    public void onSetup(CustomGuiType customGuiType, Slot.Type componentType, RenderSetupContext context) throws IOException {
        // Ensure the slot texture exists in all resource packs
        byte[] slotTexture = null;
        for (ResourcePack resourcePack : context.getResourcePacks()) {
            Path path = resourcePack.getTexturePath(SLOT_TEXTURE_KEY);
            if (Files.notExists(path)) {
                if (slotTexture == null) {
                    slotTexture = this.createSlotTexture();
                }
                Files.createDirectories(path.getParent());
                Files.write(path, slotTexture);
            }
        }

        this.fontSequence = context.newFontSequence()
            .renderImage(SLOT_TEXTURE_KEY,
                componentType.getX() * GuiUtils.SLOT_SIZE,
                componentType.getY() * GuiUtils.SLOT_SIZE)
            .build();
    }

    private byte[] createSlotTexture() throws IOException {
        ResourcePack vanillaAssets = Praeter.get().getResourceManager().getPacks().getVanillaAssets();
        Path generic54Path = vanillaAssets.getTexturePath(GuiBackgroundPainter.GENERIC_54_KEY);

        BufferedImage generic54 = ImageIO.read(Files.newInputStream(generic54Path));

        // Get the part of generic_54 that has a slot
        BufferedImage slot = generic54.getSubimage(SLOT_PIXEL_X, SLOT_PIXEL_Y,
            GuiUtils.SLOT_SIZE, GuiUtils.SLOT_SIZE);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(slot, "png", stream);
        return stream.toByteArray();
    }

    @Override
    public void onRender(CustomGuiType customGuiType, CustomGui customGui, Slot.Type componentType, Slot component, RenderDispatcher renderDispatcher) {
        renderDispatcher.render(this.fontSequence);
    }

    @Override
    public void renderItems(CustomGuiType customGuiType, CustomGui customGui, Slot.Type componentType, Slot component, Inventory inventory) {

    }
}
