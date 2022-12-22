package ca.bkaw.praeter.gui.components;

import ca.bkaw.praeter.core.resources.draw.DrawTextUtils;
import ca.bkaw.praeter.core.resources.font.FontSequence;
import ca.bkaw.praeter.gui.GuiUtils;
import ca.bkaw.praeter.gui.component.GuiClickContext;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.font.RenderDispatcher;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import ca.bkaw.praeter.gui.gui.CustomGui;
import org.bukkit.map.MinecraftFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * A button in a {@link CustomGui} that can be enabled and disabled.
 * <p>
 * When the button is disabled the click handler will not be called.
 */
public class DisableableButton extends Button {
    /**
     * The y position of the disabled button sprite.
     */
    public static final int DISABLED_OFFSET_Y = 46;

    /**
     * The text color to use for disabled buttons.
     */
    public static final Color DISABLED_TEXT_COLOR = new Color(158, 158, 158);

    private FontSequence enabled;
    private FontSequence disabled;

    /**
     * Create a new {@link DisableableButton}.
     *
     * @param text The text to display on the button. Keep it short.
     * @param x The x position of the component, in slots.
     * @param y The y position of the component, in slots.
     * @param width The width of the component, in slots.
     * @param height The height of the component, in slots.
     */
    public DisableableButton(String text, int x, int y, int width, int height) {
        super(text, x, y, width, height);
    }

    @Override
    public DisableableButton.State createState() {
        return new DisableableButton.State();
    }

    @Override
    public DisableableButton.State getState(CustomGui gui) {
        return (DisableableButton.State) super.getState(gui);
    }

    @Override
    public void onSetup(RenderSetupContext context) throws IOException {
        // Read the vanilla button sprite from the widgets texture
        Path widgetsPath = context.getResourcePacks().getTexturePath(WIDGETS_TEXTURE);
        BufferedImage widgets = ImageIO.read(Files.newInputStream(widgetsPath));
        BufferedImage enabledButton = widgets.getSubimage(0, OFFSET_Y, WIDTH, HEIGHT);
        BufferedImage disabledButton = widgets.getSubimage(0, DISABLED_OFFSET_Y, WIDTH, HEIGHT);

        // Create the images for the button
        int width = getWidth() * GuiUtils.SLOT_SIZE;
        int height = getHeight() * GuiUtils.SLOT_SIZE;

        BufferedImage enabledImage = createButtonImage(enabledButton, width, height);
        BufferedImage disabledImage = createButtonImage(disabledButton, width, height);

        int textX = (width - DrawTextUtils.getTextWidth(this.text, MinecraftFont.Font)) / 2;
        int textY = (height - MinecraftFont.Font.getHeight()) / 2;

        // Create the font sequences for when the button is enabled and for when
        // it is disabled. The state chooses which to render.
        this.enabled = context.newFontSequence()
            .drawImage(enabledImage, 0, 0)
            .drawText(this.text, textX, textY, Color.WHITE)
            .build();

        this.disabled = context.newFontSequence()
            .drawImage(disabledImage, 0, 0)
            .drawText(this.text, textX, textY, DISABLED_TEXT_COLOR)
            .build();

        // TODO: overlap issues... complex stuff with splitting vs non-splitting spaces/text/font
    }

    /**
     * The {@link GuiComponent.State} that holds whether the disableable button is
     * enabled or disabled.
     */
    // Button has no specific state, so this is actually the same
    // as doing extends GuiComponent.State
    public class State extends Button.State {
        private boolean isEnabled = true;
        private Consumer<GuiClickContext> enabledClickHandler;
        private Consumer<GuiClickContext> disabledClickHandler;

        public State() {
            // We must use super.setOnClick because we change this.setOnClick to set the
            // enabled click handler.
            super.setOnClick(context -> {
                if (isEnabled) {
                    enabledClickHandler.accept(context);
                } else {
                    disabledClickHandler.accept(context);
                }
            });
        }

        @Override
        public void onRender(RenderDispatcher renderDispatcher) {
            if (isEnabled) {
                renderDispatcher.render(enabled);
            } else {
                renderDispatcher.render(disabled);
            }
        }

        /**
         * Set the callback to call when the button is pressed. The callback will only be
         * called if the button is enabled.
         * <p>
         * There can only be one handler. Calling this twice will override the previous
         * handler.
         *
         * @param clickHandler The click handler.
         */
        @Override
        public void setOnClick(Consumer<GuiClickContext> clickHandler) {
            this.enabledClickHandler = clickHandler;
        }

        /**
         * Set the callback to call when the button is disabled and pressed.
         * <p>
         * There can only be one handler. Calling this twice will override the previous
         * handler.
         *
         * @param clickHandler The click handler.
         */
        public void setOnDisabledClick(Consumer<GuiClickContext> clickHandler) {
            this.disabledClickHandler = clickHandler;
        }

        /**
         * Get whether the button is enabled.
         * <p>
         * When a button is disabled the click handler will not be called.
         *
         * @return Whether enabled.
         */
        public boolean isEnabled() {
            return this.isEnabled;
        }

        /**
         * Set whether the button is enabled.
         *
         * @param enabled Whether the button should be enabled or not.
         * @see #isEnabled()
         */
        public void setEnabled(boolean enabled) {
            this.isEnabled = enabled;
        }
    }
}
