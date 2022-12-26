package ca.bkaw.praeter.plugin.test;

import ca.bkaw.praeter.core.resources.font.FontSequence;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.font.GuiFontSequenceBuilder;
import ca.bkaw.praeter.gui.font.RenderDispatcher;
import ca.bkaw.praeter.gui.font.RenderSetupContext;
import ca.bkaw.praeter.gui.gui.CustomGui;

import java.io.IOException;

public class TestComponent extends GuiComponent {
    /**
     * Create a new {@link GuiComponent}.
     *
     * @param x      The x position of the component, in slots.
     * @param y      The y position of the component, in slots.
     * @param width  The width of the component, in slots.
     * @param height The height of the component, in slots.
     */
    public TestComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    private FontSequence fontSequence;

    @Override
    public State createState() {
        return new State();
    }

    @Override
    public State get(CustomGui gui) {
        return (State) super.get(gui);
    }

    @Override
    public void onSetup(RenderSetupContext context) throws IOException {
        GuiFontSequenceBuilder builder = context.newFontSequence();
        for (int i = -5; i < 5; i++) {
            // builder.drawImage(NamespacedKey.minecraft("item/diamond.png"), 36, i * 18);
        }
        this.fontSequence = builder.build();
    }

    public class State extends GuiComponent.State {
        @Override
        public void onRender(RenderDispatcher renderDispatcher) {
            renderDispatcher.render(fontSequence);
        }
    }
}
