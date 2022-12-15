package ca.bkaw.praeter.gui;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiComponentClickContext;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.components.Slot;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiHolder;
import ca.bkaw.praeter.gui.gui.SlotManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * The event listener that handles interactions with custom guis.
 * <p>
 * The majority of the code relates to item movement between custom gui slots and
 * the player's own inventory.
 */
public class GuiEventListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof CustomGuiHolder customGuiHolder)) {
            return;
        }
        // The user clicked while a custom gui was open
        event.setCancelled(true);

        // Variables
        CustomGui customGui = customGuiHolder.getCustomGui();
        SlotManager slotManager = customGui.getSlotManager();
        Inventory topInventory = event.getView().getTopInventory();
        Inventory bottomInventory = event.getView().getBottomInventory();
        boolean top = event.getClickedInventory() == topInventory;
        boolean bottom = event.getClickedInventory() == bottomInventory;
        InventoryAction action = event.getAction();
        ItemStack cursor = event.getCursor();
        HumanEntity player = event.getWhoClicked();
        int slotNumber = event.getSlot();
        int x = GuiUtils.getX(slotNumber);
        int y = GuiUtils.getY(slotNumber);
        boolean update = false;

        if (cursor != null && cursor.getType().isAir()) {
            cursor = null;
        }

        // Pick up

        if (bottom) {
            switch (action) {
                case PICKUP_ALL, PICKUP_HALF, PICKUP_ONE, PICKUP_SOME ->
                    event.setCancelled(false);
                    // Let vanilla handle the normal pickup
            }
        }

        if (top) {
            switch (action) {
                case PICKUP_ALL, PICKUP_HALF, PICKUP_ONE, PICKUP_SOME -> {
                    Slot slot = slotManager.getSlot(x, y);
                    if (slot != null) {
                        ItemStack itemStack = slot.getItemStack();
                        if (itemStack != null && canPickUp(itemStack, cursor)) {
                            ItemStack pickedUpStack = itemStack.clone();
                            int newCursorAmount = pickedUpStack.getAmount();
                            if (action == InventoryAction.PICKUP_ONE) {
                                newCursorAmount = 1;
                            } else if (action == InventoryAction.PICKUP_HALF) {
                                newCursorAmount /= 2;
                            }
                            if (cursor != null) {
                                newCursorAmount += cursor.getAmount();
                            }
                            if (newCursorAmount > pickedUpStack.getMaxStackSize()) {
                                int leftOverAmount = newCursorAmount - pickedUpStack.getMaxStackSize();
                                itemStack.setAmount(leftOverAmount);
                            } else {
                                slot.setItemStack(null);
                            }
                            player.setItemOnCursor(pickedUpStack);
                            update = true;
                        }
                    }
                }
            }
        }

        // Place

        if (bottom) {
            switch (action) {
                case PLACE_ALL, PLACE_SOME, PLACE_ONE ->
                    event.setCancelled(false);
                    // Let vanilla handle the normal place
            }
        }

        if (top) {
            switch (action) {
                case PLACE_ALL, PLACE_SOME, PLACE_ONE -> {
                    Slot slot = slotManager.getSlot(x, y);
                    if (slot != null) {
                        ItemStack itemStack = slot.getItemStack();
                        if (itemStack == null) {
                            player.setItemOnCursor(null);
                            slot.setItemStack(cursor);
                            update = true;
                        } else if (cursor != null && itemStack.isSimilar(cursor)) {
                            int amount;
                            int remaining = 0;
                            if (action == InventoryAction.PLACE_ONE) {
                                amount = itemStack.getAmount() + 1;
                                remaining = cursor.getAmount() - 1;
                            } else {
                                amount = itemStack.getAmount() + cursor.getAmount();
                            }
                            if (amount > itemStack.getMaxStackSize()) {
                                remaining = amount - itemStack.getMaxStackSize();
                                amount = itemStack.getMaxStackSize();
                            }
                            itemStack.setAmount(amount);
                            if (remaining > 0 ) {
                                cursor.setAmount(remaining);
                                player.setItemOnCursor(cursor);
                            } else {
                                player.setItemOnCursor(null);
                            }
                            update = true;
                        }
                    }
                }
            }
        }

        // Shift clicking

        if (bottom && action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem != null && slotManager.canShiftInto(currentItem)) {
                int newAmount = slotManager.shiftInto(currentItem);
                currentItem.setAmount(newAmount);
                update = true;
            }
        }

        if (top && action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Slot slot = slotManager.getSlot(x, y);
            if (slot != null) {
                ItemStack itemStack = slot.getItemStack();
                if (itemStack != null) {
                    bottomInventory.addItem(itemStack);
                    slot.setItemStack(null);
                    update = true;
                    // TODO
                }
            }
        }

        // Component onClick

        if (top) {
            GuiComponentType<?, ?> componentType = customGui.getComponentTypeAt(x, y);
            if (componentType != null) {
                // The user clicked a component
                GuiComponent guiComponent = customGui.get(componentType);
                Consumer<GuiComponentClickContext> clickHandler = guiComponent.getClickHandler();
                if (clickHandler != null) {
                    // Call the click handler on the component
                    GuiComponentClickContext context = new GuiComponentClickContext(event);
                    clickHandler.accept(context);
                }
            }
        }

        if (update) {
            customGui.update();
        }
    }

    private boolean canPickUp(ItemStack toPickUp, ItemStack cursor) {
        if (cursor == null) {
            return true;
        }
        if (!cursor.isSimilar(toPickUp)) {
            return false;
        }
        return cursor.getAmount() < cursor.getMaxStackSize();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof CustomGuiHolder customGuiHolder)) {
            return;
        }
        event.setCancelled(true);

        boolean onlyBottom = true;
        for (Integer rawSlot : event.getRawSlots()) {
            Inventory inventory = event.getView().getInventory(rawSlot);
            if (inventory == event.getView().getTopInventory()) {
                onlyBottom = false;
            }
        }
        if (onlyBottom) {
            // Items were only dragged in the bottom inventory
            // Let vanilla handle it.
            event.setCancelled(false);
        }
    }
}
