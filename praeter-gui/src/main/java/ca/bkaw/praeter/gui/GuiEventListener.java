package ca.bkaw.praeter.gui;

import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.component.GuiClickContext;
import ca.bkaw.praeter.gui.component.GuiComponentType;
import ca.bkaw.praeter.gui.components.Slot;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiHolder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
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
        InventoryView view = event.getView();
        Inventory topInventory = view.getTopInventory();
        Inventory bottomInventory = view.getBottomInventory();
        boolean top = event.getClickedInventory() == topInventory;
        boolean bottom = event.getClickedInventory() == bottomInventory;
        InventoryAction action = event.getAction();
        ItemStack cursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        HumanEntity player = event.getWhoClicked();
        int slotNumber = event.getSlot();
        int x = GuiUtils.getX(slotNumber);
        int y = GuiUtils.getY(slotNumber);
        boolean update = false;

        if (cursor != null && cursor.getType().isAir()) {
            cursor = null;
        }

        // Some actions that only involve the cursor, can always be performed
        switch (action) {
            case DROP_ALL_CURSOR, DROP_ONE_CURSOR -> event.setCancelled(false);
            // Let vanilla handle the regular action
        }

        // Most actions in the bottom inventory, the player's inventory,
        // can be performed regularly
        if (bottom) {
            switch (action) {
                case
                    PICKUP_ALL, PICKUP_HALF, PICKUP_ONE, PICKUP_SOME,
                    PLACE_ALL, PLACE_SOME, PLACE_ONE,
                    DROP_ALL_SLOT, DROP_ONE_SLOT,
                    SWAP_WITH_CURSOR, HOTBAR_SWAP, HOTBAR_MOVE_AND_READD, CLONE_STACK
                    -> event.setCancelled(false);
                    // Let vanilla handle the regular action
            }
        }

        // When actions involve the top inventory, the custom gui, we need to handle
        // actions manually, imitating vanilla closely, but with regard to custom slots.
        if (top) {

            // Pick up

            switch (action) {
                case PICKUP_ALL, PICKUP_HALF, PICKUP_ONE, PICKUP_SOME -> {
                    Slot slot = customGui.getSlot(x, y);
                    if (slot != null && slot.mayChange(player)) {
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

            // Place

            switch (action) {
                case PLACE_ALL, PLACE_SOME, PLACE_ONE -> {
                    Slot slot = customGui.getSlot(x, y);
                    if (slot != null && slot.mayChange(player) && cursor != null && slot.canHold(cursor)) {
                        ItemStack itemStack = slot.getItemStack();
                        if (itemStack == null) {
                            player.setItemOnCursor(null);
                            slot.setItemStack(cursor);
                            update = true;
                        } else if (itemStack.isSimilar(cursor)) {
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
                            if (remaining > 0) {
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

            // Swap with cursor

            if (action == InventoryAction.SWAP_WITH_CURSOR && cursor != null) {
                Slot slot = customGui.getSlot(x, y);
                if (slot != null && slot.mayChange(player) && slot.canHold(cursor)) {
                    ItemStack itemStack = slot.getItemStack();
                    if (itemStack != null) {
                        player.setItemOnCursor(itemStack);
                        slot.setItemStack(cursor);
                        update = true;
                    }
                }
            }

            // Drop from slot

            if (action == InventoryAction.DROP_ALL_SLOT || action == InventoryAction.DROP_ONE_SLOT) {
                Slot slot = customGui.getSlot(x, y);
                if (slot != null && slot.mayChange(player)) {
                    ItemStack itemStack = slot.getItemStack();
                    if (itemStack != null) {
                        // There is no api method for dropping items like in the inventory.
                        // So we let vanilla handle it, and predict the change
                        event.setCancelled(false);
                        int newAmount = switch (action) {
                            case DROP_ALL_SLOT -> 0;
                            case DROP_ONE_SLOT -> itemStack.getAmount() - 1;
                            default -> throw new RuntimeException(action.toString());
                        };
                        itemStack.setAmount(newAmount);
                        // Maybe more actions can benefit from letting vanilla handle things and
                        // predicting the outcome?
                    }
                }
            }

            // Clone

            if (action == InventoryAction.CLONE_STACK) {
                Slot slot = customGui.getSlot(x, y);
                if (slot != null) {
                    // Let vanilla handle the clone, we just needed to verify there was a slot
                    // at the clicked position
                    event.setCancelled(false);
                }
            }

        } // ends: if (top)

        // Some actions interact with both inventories. There must always handle
        // actions manually and imitate vanilla behavior.

        // Shift clicking

        if (bottom && action == InventoryAction.MOVE_TO_OTHER_INVENTORY && currentItem != null) {
            for (Slot slot : customGui.getSlots()) {
                ItemStack slotItem = slot.getItemStack();
                if (slotItem != null) {
                    if (slotItem.isSimilar(currentItem) && slot.canHold(currentItem)) {
                        int maxInsertAmount = slotItem.getMaxStackSize() - slotItem.getAmount();
                        int requestedInsertAmount = currentItem.getAmount();
                        int insertAmount = Math.min(requestedInsertAmount, maxInsertAmount);
                        slotItem.setAmount(slotItem.getAmount() + insertAmount);
                        update = true;
                        currentItem.setAmount(currentItem.getAmount() - insertAmount);
                        if (currentItem.getAmount() <= 0) {
                            break;
                        }
                    }
                } else if (slot.canHold(currentItem)) {
                    // No item here, we can shift click into this slot
                    slot.setItemStack(currentItem.clone());
                    update = true;
                    // All the items were consumed
                    event.setCurrentItem(null);
                    break;
                }
            }
        }

        if (top && action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Slot slot = customGui.getSlot(x, y);
            if (slot != null && slot.mayChange(player)) {
                ItemStack itemStack = slot.getItemStack();
                if (itemStack != null) {
                    int firstRawSlot = topInventory.getSize();
                    int lastRawSlot = firstRawSlot + 36;
                    
                    // First attempt to fill existing stacks
                    for (int rawSlot = firstRawSlot; rawSlot < lastRawSlot; rawSlot++) {
                        ItemStack slotItem = view.getItem(rawSlot);
                        if (slotItem != null && slotItem.isSimilar(itemStack)) {
                            int maxInsertAmount = slotItem.getMaxStackSize() - slotItem.getAmount();
                            int requestedInsertAmount = itemStack.getAmount();
                            int insertAmount = Math.min(requestedInsertAmount, maxInsertAmount);
                            slotItem.setAmount(slotItem.getAmount() + insertAmount);
                            itemStack.setAmount(itemStack.getAmount() - insertAmount);
                            update = true;
                            if (itemStack.getAmount() <= 0) {
                                break;
                            }
                        }
                    }
                    
                    // Fill in blank slots
                    for (int rawSlot = lastRawSlot - 1; rawSlot >= firstRawSlot; rawSlot--) {
                        ItemStack slotItem = view.getItem(rawSlot);
                        if (slotItem == null || slotItem.getType().isAir()) {
                            // No item here, we can shift click into this slot
                            view.setItem(rawSlot, itemStack.clone());
                            slot.setItemStack(null);
                            update = true;
                            break;
                        }
                    }
                }
            }
        }

        // Hotbar swap

        if (top && (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD)) {
            PlayerInventory playerInventory = player.getInventory();
            ItemStack itemToSwap = switch (event.getClick()) {
                case SWAP_OFFHAND -> playerInventory.getItemInOffHand();
                case NUMBER_KEY -> playerInventory.getItem(event.getHotbarButton());
                default -> null;
            };
            if (itemToSwap != null && (itemToSwap.getType().isAir() || itemToSwap.getAmount() <= 0)) {
                itemToSwap = null;
            }
            Slot slot = customGui.getSlot(x, y);
            if (itemToSwap != null && slot != null && slot.mayChange(player) && slot.canHold(itemToSwap)) {
                ItemStack itemStack = slot.getItemStack();
                if (itemStack != null) {
                    slot.setItemStack(itemToSwap);
                    update = true;
                    if (action == InventoryAction.HOTBAR_SWAP) {
                        switch (event.getClick()) {
                            case SWAP_OFFHAND -> playerInventory.setItemInOffHand(itemStack);
                            case NUMBER_KEY -> playerInventory.setItem(event.getHotbarButton(), itemStack);
                        }
                    } else {
                        HashMap<Integer, ItemStack> leftover = playerInventory.addItem(itemStack);
                        for (ItemStack leftoverItem : leftover.values()) {
                            player.getWorld().dropItem(player.getLocation(), leftoverItem);
                        }
                    }
                }
            }
        }

        // Collect to cursor

        if (action == InventoryAction.COLLECT_TO_CURSOR && cursor != null) {
            for (Slot slot : customGui.getSlots()) {
                ItemStack slotItem = slot.getItemStack();
                if (slotItem != null && slot.mayChange(player) && cursor.isSimilar(slotItem)) {
                    int newAmount = cursor.getAmount() + slotItem.getAmount();
                    if (newAmount <= cursor.getMaxStackSize()) {
                        // We consume the entire stack
                        slot.setItemStack(null);
                        update = true;
                        cursor.setAmount(newAmount);
                    } else {
                        // We need to consume parts of the stack to fill up the cursor
                        int consumedAmount = cursor.getMaxStackSize() - cursor.getAmount();
                        slotItem.setAmount(slotItem.getAmount() - consumedAmount);
                        update = true;
                        cursor.setAmount(cursor.getMaxStackSize());
                        break;
                    }
                }
            }
            int firstRawSlot = topInventory.getSize();
            int lastRawSlot = firstRawSlot + 36;
            for (int rawSlot = firstRawSlot; rawSlot <= lastRawSlot; rawSlot++) {
                ItemStack slotItem = view.getItem(rawSlot);
                if (slotItem != null && cursor.isSimilar(slotItem)) {
                    int newAmount = cursor.getAmount() + slotItem.getAmount();
                    if (newAmount <= cursor.getMaxStackSize()) {
                        // We consume the entire stack
                        view.setItem(rawSlot, null);
                        cursor.setAmount(newAmount);
                    } else {
                        // We need to consume parts of the stack to fill up the cursor
                        int consumedAmount = cursor.getMaxStackSize() - cursor.getAmount();
                        slotItem.setAmount(slotItem.getAmount() - consumedAmount);
                        cursor.setAmount(cursor.getMaxStackSize());
                        break;
                    }
                }
            }
        }


        // Component onClick

        if (top) {
            GuiComponentType<?, ?> componentType = customGui.getComponentTypeAt(x, y);
            if (componentType != null) {
                // The user clicked a component
                GuiComponent guiComponent = customGui.get(componentType);
                Consumer<GuiClickContext> clickHandler = guiComponent.getClickHandler();
                if (clickHandler != null) {
                    // Call the click handler on the component
                    GuiClickContext context = new GuiClickContext(event);
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

        // TODO handle drag in custom gui
    }
}
