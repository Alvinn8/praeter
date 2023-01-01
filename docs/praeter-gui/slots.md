# Slots
You can create slots where the player can put items.

Create and add slots like any other component.

```java
private static final Slot SLOT_1 = new Slot(5, 1);
private static final Slot SLOT_2 = new Slot(6, 1);
private static final Slot SLOT_3 = new Slot(7, 2);

public static final CustomGuiType TYPE = CustomGuiType.builder()
    .title(Component.text("Example GUI"))
    .height(3)
    .add(SLOT_1, SLOT_2, SLOT_3)
    .build();
```

Praeter will handle item movement automatically.

You can access the item currently in a slot from the slot's state:
`SLOT_1.get(this).getItemStack();`

You can also change the item by calling `setItemStack`, for example in the
constructor to set the starting item that the player can take.

You may also create a `StaticSlot` that the player can not change, but retains
the same appearance as a slot.

## Slot groups

You may want to create a group of slots, for example to store many items.
Instead of creating each slot individually, which can get quite repetitive,
you can use a `SlotGroup`.
```java
public class ExampleGui extends CustomGui {
    private static final SlotGroup<Slot> LEFT_SLOTS = SlotGroup.box(0, 0, 3, 6, Slot::new);
    private static final SlotGroup<Slot> RIGHT_SLOTS = SlotGroup.box(7, 0, 3, 6, Slot::new);
    
    public static final CustomGuiType TYPE = CustomGuiType.builder()
        .title(Component.text("A lot of slots."))
        .height(6)
        .add(LEFT_SLOTS, RIGHT_SLOTS)
        .build();
    
    public ExampleGui() {
        super(TYPE);
        
        LEFT_SLOTS.getSlot(0).get(this).setItemStack(new ItemStack(Material.DIAMOND));
    }
}
```
This creates a total of 36 slots. They can be accessed by their index by
calling `getSlot(...)`, which returns the `Slot`. The index is relative
to the slot group.

You can change `Slot::new` to `StaticSlot::new`, or your own `Slot` subclass,
to create instances of that instead.

## Custom Slots
You can create custom slots to customize which items are allowed in the slot,
when the slot can be changed, and to call a method when the slot is changed.

The following is a slot where the player can only insert diamonds.
```java
public class DiamondSlot extends Slot {
    public DiamondSlot(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean canHold(@NotNull ItemStack itemStack) {
        return itemStack.getType() == Material.DIAMOND;
    }
}
```

The following is a slot that the player cannot change. The item stack can only
be changed by calling `setItemStack` on the state.

```java
public class StaticSlot extends Slot {
    public StaticSlot(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean mayChange(HumanEntity player) {
        return false;
    }
}
```

## Custom slots with state
The methods above are on the component, but sometimes you may want to create a
slot where for example changing it is sometimes possible, and sometimes not. The
methods therefore also exist on the state and can be overridden there.
```java
public class BucketSlot extends Slot {
    private FontSequence lock;

    public BucketSlot(int x, int y) {
        super(x, y);
    }

    @Override
    public void onSetup(RenderSetupContext context) throws IOException {
        // Call super to render the normal slot
        super.onSetup(context);
        
        // Render a bucket outline
        context.getBackground().drawImage(new NamespacedKey("example", "gui/bucket_outline.png"), 0, 0);

        // Prepare the lock icon
        this.lock = context.newFontSequence()
            .drawImage(new NamespacedKey("example", "gui/lock.png"), 0, 0)
            .build();
    }

    public enum BucketType {
        LAVA,
        WATER
    }

    @Override
    public Slot.State createState() {
        return new BucketSlot.State();
    }

    @Override
    public BucketSlot.State get(CustomGui gui) {
        return (State) super.get(gui);
    }

    public class State extends Slot.State {
        private BucketType bucketType = BucketType.WATER;
        private boolean locked;

        @Override
        public void onRender(RenderDispatcher renderDispatcher) {
            // We don't need super.onRender, the background is already rendered automatically
            if (locked) {
                renderDispatcher.render(lock);
            }
        }

        // Note how these methods are now overridden in Slot.State, not Slot
        @Override
        public boolean canHold(@NotNull ItemStack itemStack) {
            return switch (this.bucketType) {
                case WATER -> itemStack.getType() == Material.WATER_BUCKET;
                case LAVA -> itemStack.getType() == Material.LAVA_BUCKET;
            };
        }

        @Override
        public boolean mayChange(HumanEntity player) {
            return !locked || player.hasPermission("example.override_lock");
        }
        
        // getters, setters ...
    }
}
```

This component can dynamically determine whether water or lava buckets can be
placed in the slot and can also be locked. When that happens a lock icon will
appear and the player can no longer change the contents of the slot (nether add
nor remove items). Unless the player has the `example.override_lock` permission,
in which case they can always change the slot. Note that items always display on
top, so the lock may not be visible if there are items in the way.

## onChange
Additionally, there is an `onChange` method on the `Slot.State`. This method is
called whenever the item in the slot changes.
```java
...

public class State extends Slot.State {
    @Override
    public void onChange(HumanEntity player) {
        ItemStack itemStack = getItemStack();
        if (itemStack != null && itemStack.getType() == Material.TNT) {
            player.getLocation().createExplosion(3);
        }
    }
}
```
The method is called after the change has happened, and the gui will update
shortly after to render the new changes.