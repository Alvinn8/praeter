[Table of Contents](../table_of_contents.md)

# Creating components
> Do you want to create a custom slot? There is a specific article for that: [Slots](slots.md).

Create a class and extend the desired component. For brand-new components,
extend `GuiComponent`. Create a constructor matching super where the x, y,
width, and height is set.
```java
public class ExampleComponent extends GuiComponent {
    public ExampleComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
}
```

## Rendering by drawing on the background

To render something, override the `onSetup` method. If you extend something
other than `GuiComponent` you may want to keep the `super.onSetup(context)` if
you wish to keep the rendering from the superclass.

```java
@Override
public void onSetup(RenderSetupContext context) throws IOException {
    context.getBackground().drawImage(new NamespacedKey("foo", "gui/bar.png"), 0, 0);
}
```

This would render the image located at `assets/foo/textures/gui/bar.png`. The
coordinates (0, 0) correspond to the top-left corner of the component. The scale
is in pixels, where 18 pixels (`GuiUtils.SLOT_SIZE`) is the size of one slot.
Keep this in mind when creating the textures to make them the right size.

## Components with custom state
Many components are not as simple as above, and you may want to store state and
render different images depending on the state. To do that, we will need to
create a custom `State` class. This is commonly done as an inner class. We will
also need to override two methods, `createState` and `get`.
```java
public class ExampleComponent extends GuiComponent {
    public ExampleComponent(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void onSetup(RenderSetupContext context) throws IOException {
        context.getBackground().drawImage(new NamespacedKey("foo", "gui/bar.png"), 0, 0);
    }

    @Override
    public GuiComponent.State createState() {
        return new ExampleComponent.State();
    }

    @Override
    public ExampleComponent.State get(CustomGui gui) {
        return (ExampleComponent.State) super.get(gui);
    }

    public class State extends GuiComponent.State {
        
    }
 }
```
If you are extending a component other than `GuiComponent` that has extra state,
for example `Slot`,  change `extends GuiComponent.State` with `extends Slot.State`.

The inner class can also be a static class.

Important to note is the `createState` and `get` methods. In `createState`,
simply return a new instance of the state class we just created. In the `get`
method, we **change the return type** to the new class, and cast the result
from super. This ensures we get the right type when calling `get` from our
`CustomGui` instances.

> In this example, we type `ExampleComponent.State`, which is the same as simply
> `State`. This is done for clarify to differentiate between the different state
> classes.

You are now free to create fields, getters, and setters in `State` that can be
modified during the lifespan of the component.

## Rendering items

In the state, you can override the `renderItems` method to display items in the
inventory. It is common to use the `GuiUtils.forEachSlot` utility method to loop
trough each slot that the component affects.

```java
...

public class State extends GuiComponent.State {
    @Override
    public void renderItems(Inventory inventory) {
        GuiUtils.forEachSlot(ExampleComponent.this, slot -> {
            inventory.setItem(slot, new ItemStack(Material.DIAMOND));
        });
    }
}
```

These items can not be changed by the player. For that you should use a `Slot`.

## Hover text
It is common to wait to use items to display a text message when the player
hovers the component. There is therefore a method called `setHoverText` on
`State`. This method can be called from the `CustomGui`. If you want to render
hover text as part of your render, you can call `setHoverText` in the
constructor, or if you need the hover text to change, call `renderItems` before
calling the super method.

An example that renders the current percentage each update:
```java
...

 public class State extends GuiComponent.State {
    private float percentage;

    @Override
    public void renderItems(Inventory inventory) {
        this.setHoverText(List.of(
            Component.text(Math.round(this.percentage * 100) + "%")
        ));
        super.renderItems(inventory);
    }
    
    ...
}
```

An example that sets the hover text once, meaning it can be changed later by
the gui.
```java
...

public class State extends GuiComponent.State {
    public State() {
        this.setHoverText(List.of(
            Component.text("This is the first line of the hover text."),
            Component.text("And this is the second one", NamedTextColor.YELLOW)
        ));
    }
}
```

## Rendering using custom fonts
When using custom state that can change, you may want to alter the rendering of
the component depending on the state. To do this, we can no longer simply draw
on the background. We must instead prepare drawable sequences of font characters
that will render something, and determine which of these to render. In other
words, you cannot freely draw things. You must prepare what you want to draw
before it is rendered so that the textures can be put in the resource pack
beforehand.

These drawable sequences of font characters are known as `FontSequence`s. You
can create them in the `onSetup` method, store them, and then render them in
the `onRender` method of the state.

Let's create a component that can toggle between two states: dog and cat, and
depending on the state, display an image of a dog or a cat.
```java
public class ExampleComponent extends GuiComponent {
    private FontSequence dog;
    private FontSequence cat;

    public ExampleComponent(int x, int y) {
        // Pass 1, 1 as the width, height because the images are always 18x18
        // pixels (one slot)
        super(x, y, 1, 1);
    }

    @Override
    public void onSetup(RenderSetupContext context) throws IOException {
        // Prepare the images now
        
        this.dog = context.newFontSequence()
            .drawImage(new NamespacedKey("example", "gui/dog.png"), 0, 0)
            .build();

        this.cat = context.newFontSequence()
            .drawImage(new NamespacedKey("example", "gui/cat.png"), 0, 0)
            .build();
    }

    @Override
    public GuiComponent.State createState() {
        return new ExampleComponent.State();
    }

    @Override
    public ExampleComponent.State get(CustomGui gui) {
        return (ExampleComponent.State) super.get(gui);
    }

    public class State extends GuiComponent.State {
        private boolean isDog = true;

        @Override
        public void onRender(RenderDispatcher renderDispatcher) {
            // Render the desired image now
            if (isDog) {
                renderDispatcher.render(dog);
            } else {
                renderDispatcher.render(cat);
            }
        }

        public void setDog() {
            this.isDog = true;
        }

        public void setCat() {
            this.isDog = false;
        }
        
        public void toggle() {
            this.isDog = !this.isDog;
        }
    }
}
```

Depending on the field `isDog`, one of the prepared images will be rendered. The
gui can access the state using `EXAMPLE_COMPONENT.get(this)` and can there
change the state using the `setDog`, `setCat` and `toggle` methods. Remember to
call `update` on the gui after changing the state so the components rerender.

Here is an example usage of the component where pressing the component toggles
whether a dog or a cat is displayed.
```java
public class PetGui extends CustomGui {
    private static final ExampleComponent EXAMPLE_COMPONENT = new ExampleComponent(4, 1);

    public static final CustomGuiType TYPE = CustomGuiType.builder()
        .title(Component.text("Dog or cat?"))
        .height(3)
        .add(EXAMPLE_COMPONENT)
        .build();

    public PetGui() {
        super(TYPE);

        EXAMPLE_COMPONENT.get(this).setOnClick(context -> {
            context.playClickSound();
            EXAMPLE_COMPONENT.get(this).toggle();
            update();
        });
    }
}
```

The API is coded in this limiting way because it needs to be that way. All
textures and other assets need to be put in the resource pack way before the
first render ever takes place. There are ways to further customize rendering,
for example to dynamically change the x position of renders to create for
example progress bars or other components that don't simply display images.
Read more about that in [Advanced FontSequence usages](advanced_fontsequence_usages.md).