package ca.bkaw.praeter.gui.gui;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.components.Slot;
import com.google.common.collect.ImmutableList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * The manager for items and slots in a {@link CustomGui}.
 */
public class SlotManager {
    private final CustomGui gui;
    private final List<Slot> slots;

    public SlotManager(CustomGui gui) {
        this.gui = gui;

        ImmutableList.Builder<Slot> slots = ImmutableList.builder();
        for (GuiComponent component : this.gui.getComponentMap().getComponents()) {
            if (component instanceof Slot slot) {
                slots.add(slot);
            }
        }
        this.slots = slots.build();
    }

    /**
     * Get an immutable list of the slots in the gui.
     *
     * @return The list of slots.
     */
    @Unmodifiable
    public List<Slot> getSlots() {
        return this.slots;
    }

    /**
     * Get the {@link Slot} at the specified position.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The slot, or null.
     */
    @Nullable
    public Slot getSlot(int x, int y) {
        GuiComponentType<?, ?> componentType = this.gui.getComponentTypeAt(x, y);
        if (componentType == null) {
            return null;
        }
        GuiComponent guiComponent = this.gui.get(componentType);
        if (guiComponent instanceof Slot slot) {
            return slot;
        }
        return null;
    }

    /**
     * Check whether the item can be shift clicked into the gui.
     *
     * @param itemStack The item to check if it fits.
     * @return Whether the item fits.
     */
    public boolean canShiftInto(@NotNull ItemStack itemStack) {
        for (Slot slot : this.slots) {
            ItemStack slotItem = slot.getItemStack();
            if (slotItem == null) {
                // An empty slot
                return true;
            }
            if (slotItem.isSimilar(itemStack)) {
                // There is a similar item, does the item fit?
                if (slotItem.getAmount() < slotItem.getMaxStackSize()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Shift click an item into the gui.
     *
     * @param itemStack The item to insert.
     * @return The amount of items left.
     */
    public int shiftInto(@NotNull ItemStack itemStack) {
        int amount = itemStack.getAmount();
        for (Slot slot : this.slots) {
            ItemStack slotItem = slot.getItemStack();
            if (slotItem != null) {
                if (slotItem.isSimilar(itemStack)) {
                    int maxInsertAmount = slotItem.getMaxStackSize() - slotItem.getAmount();
                    int requestedInsertAmount = itemStack.getAmount();
                    int insertAmount = Math.min(requestedInsertAmount, maxInsertAmount);
                    slotItem.setAmount(slotItem.getAmount() + insertAmount);
                    amount -= insertAmount;
                    if (amount <= 0) {
                        return 0;
                    }
                }
            } else if (slot.supportsHolding(itemStack)) {
                // No item here, we can shift click into this slot
                slot.setItemStack(itemStack.clone());
                // All the items were consumed
                return 0;
            }
        }
        return amount;
    }
}
