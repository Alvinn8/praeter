package ca.bkaw.praeter.gui;

import ca.bkaw.praeter.gui.component.GuiClickContext;
import ca.bkaw.praeter.gui.component.GuiComponent;
import ca.bkaw.praeter.gui.components.Slot;
import ca.bkaw.praeter.gui.gui.CustomGui;
import ca.bkaw.praeter.gui.gui.CustomGuiHolder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * The event listener that handles interactions with custom guis.
 * <p>
 * The majority of the code relates to item movement between custom gui slots and
 * the player's own inventory.
 */
public class GuiEventListener implements Listener {
    private final Plugin plugin;

    public GuiEventListener(Plugin plugin) {
        this.plugin = plugin;
    }

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
                case PICKUP_ALL, PICKUP_HALF -> {
                    Slot.State slot = customGui.getSlot(x, y);
                    if (slot != null && slot.mayChange(player) && cursor == null) {
                        ItemStack itemStack = slot.getItemStack();
                        if (itemStack != null) {
                            ItemStack pickedUpStack = itemStack.clone();
                            int newCursorAmount = pickedUpStack.getAmount();
                            if (action == InventoryAction.PICKUP_HALF) {
                                // round up
                                newCursorAmount = (int) (newCursorAmount / 2.0 + 0.5);
                            }
                            int leftOverAmount = pickedUpStack.getAmount() - newCursorAmount;
                            itemStack.setAmount(leftOverAmount);
                            pickedUpStack.setAmount(newCursorAmount);
                            player.setItemOnCursor(pickedUpStack);
                            slot.onChange(player);
                            update = true;
                        }
                    }
                }
            }

            // Place

            switch (action) {
                case PLACE_ALL, PLACE_SOME, PLACE_ONE -> {
                    Slot.State slot = customGui.getSlot(x, y);
                    if (slot != null && slot.mayChange(player) && cursor != null && slot.canHold(cursor)) {
                        ItemStack itemStack = slot.getItemStack();
                        int amount;
                        if (itemStack != null) {
                            amount = itemStack.getAmount();
                        } else {
                            amount = 0;
                        }
                        if (itemStack == null || itemStack.isSimilar(cursor)) {
                            int remaining = 0;
                            if (action == InventoryAction.PLACE_ONE) {
                                amount += 1;
                                remaining = cursor.getAmount() - 1;
                            } else {
                                amount += cursor.getAmount();
                            }
                            if (itemStack != null) {
                                if (amount > itemStack.getMaxStackSize()) {
                                    remaining = amount - itemStack.getMaxStackSize();
                                    amount = itemStack.getMaxStackSize();
                                }
                                itemStack.setAmount(amount);
                            } else {
                                itemStack = cursor.clone();
                                itemStack.setAmount(amount);
                                slot.setItemStack(itemStack);
                            }
                            if (remaining > 0) {
                                cursor.setAmount(remaining);
                                player.setItemOnCursor(cursor);
                            } else {
                                player.setItemOnCursor(null);
                            }
                            slot.onChange(player);
                            update = true;
                        }
                    }
                }
            }

            // Swap with cursor

            if (action == InventoryAction.SWAP_WITH_CURSOR && cursor != null) {
                Slot.State slot = customGui.getSlot(x, y);
                if (slot != null && slot.mayChange(player) && slot.canHold(cursor)) {
                    ItemStack itemStack = slot.getItemStack();
                    if (itemStack != null) {
                        player.setItemOnCursor(itemStack);
                        slot.setItemStack(cursor);
                        slot.onChange(player);
                        update = true;
                    }
                }
            }

            // Drop from slot

            if (action == InventoryAction.DROP_ALL_SLOT || action == InventoryAction.DROP_ONE_SLOT) {
                Slot.State slot = customGui.getSlot(x, y);
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
                        slot.onChange(player);
                        // Maybe more actions can benefit from letting vanilla handle things and
                        // predicting the outcome?
                    }
                }
            }

            // Clone

            if (action == InventoryAction.CLONE_STACK) {
                Slot.State slot = customGui.getSlot(x, y);
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
            for (Slot.State slot : customGui.getSlots()) {
                ItemStack slotItem = slot.getItemStack();
                if (slotItem != null) {
                    if (slotItem.isSimilar(currentItem) && slot.mayChange(player) && slot.canHold(currentItem)) {
                        int maxInsertAmount = slotItem.getMaxStackSize() - slotItem.getAmount();
                        int requestedInsertAmount = currentItem.getAmount();
                        int insertAmount = Math.min(requestedInsertAmount, maxInsertAmount);
                        slotItem.setAmount(slotItem.getAmount() + insertAmount);
                        slot.onChange(player);
                        update = true;
                        currentItem.setAmount(currentItem.getAmount() - insertAmount);
                        if (currentItem.getAmount() <= 0) {
                            break;
                        }
                    }
                } else if (slot.canHold(currentItem) && slot.mayChange(player)) {
                    // No item here, we can shift click into this slot
                    slot.setItemStack(currentItem.clone());
                    slot.onChange(player);
                    update = true;
                    // All the items were consumed
                    event.setCurrentItem(null);
                    break;
                }
            }
        }

        if (top && action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Slot.State slot = customGui.getSlot(x, y);
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
                            slot.onChange(player);
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
                            slot.onChange(player);
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
            Slot.State slot = customGui.getSlot(x, y);
            if (itemToSwap != null && slot != null && slot.mayChange(player) && slot.canHold(itemToSwap)) {
                ItemStack itemStack = slot.getItemStack();
                if (itemStack != null) {
                    slot.setItemStack(itemToSwap);
                    slot.onChange(player);
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
            for (Slot.State slot : customGui.getSlots()) {
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
                        slot.onChange(player);
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
            GuiComponent component = customGui.getComponentAt(x, y);
            if (component != null) {
                // The user clicked a component
                GuiComponent.State state = component.get(customGui);
                Consumer<GuiClickContext> clickHandler = state.getClickHandler();
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

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryView view = event.getView();
        InventoryHolder holder = view.getTopInventory().getHolder();
        if (!(holder instanceof CustomGuiHolder customGuiHolder)) {
            return;
        }
        event.setCancelled(true);
        CustomGui customGui = customGuiHolder.getCustomGui();

        boolean onlyBottom = true;
        for (Integer rawSlot : event.getRawSlots()) {
            Inventory inventory = view.getInventory(rawSlot);
            if (inventory == view.getTopInventory()) {
                onlyBottom = false;
            }
        }
        if (onlyBottom) {
            // Items were only dragged in the bottom inventory
            // Let vanilla handle it.
            event.setCancelled(false);
        } else {
            // The drag contained slots in the custom gui
            boolean update = false;
            IntList rawSlots = new IntArrayList();
            HumanEntity player = event.getWhoClicked();
            for (Integer rawSlotInteger : event.getRawSlots()) {
                int rawSlot = rawSlotInteger;
                Inventory inventory = view.getInventory(rawSlot);
                if (inventory == view.getTopInventory()) {
                    int slotNumber = view.convertSlot(rawSlot);
                    int x = GuiUtils.getX(slotNumber);
                    int y = GuiUtils.getY(slotNumber);
                    Slot.State slot = customGui.getSlot(x, y);
                    if (slot != null && slot.mayChange(player) && slot.canHold(event.getOldCursor())) {
                        ItemStack itemStack = slot.getItemStack();
                        if (itemStack == null || itemStack.isSimilar(event.getOldCursor())) {
                            // There is a slot that we can move into
                            rawSlots.add(rawSlot);
                        }
                    }
                } else {
                    ItemStack existingItem = view.getItem(rawSlot);
                    if (existingItem == null || existingItem.getType().isAir() || existingItem.getAmount() <= 0 || existingItem.isSimilar(event.getOldCursor())) {
                        rawSlots.add(rawSlot);
                    }
                }

            }
            if (rawSlots.size() == 0) {
                return;
            }
            int cursorAmount = event.getOldCursor().getAmount();
            ItemStack addItem = event.getOldCursor().clone();
            switch (event.getType()) {
                case SINGLE -> addItem.setAmount(1);
                case EVEN -> addItem.setAmount(cursorAmount / rawSlots.size());
            }
            for (int i = 0; i < rawSlots.size(); i++) {
                int rawSlot = rawSlots.getInt(i);
                Inventory inventory = view.getInventory(rawSlot);
                ItemStack existingItem;
                if (inventory == view.getTopInventory()) {
                    int slotNumber = view.convertSlot(rawSlot);
                    int x = GuiUtils.getX(slotNumber);
                    int y = GuiUtils.getY(slotNumber);
                    Slot.State slot = customGui.getSlot(x, y);
                    if (slot != null) { // Should always be true
                        existingItem = slot.getItemStack();
                    } else {
                        existingItem = null;
                    }
                } else {
                    existingItem = view.getItem(rawSlot);
                }
                ItemStack newItem = addItem.clone();
                int insertAmount = newItem.getAmount();
                if (existingItem != null) {
                    int newAmount = insertAmount + existingItem.getAmount();
                    if (newAmount > newItem.getMaxStackSize()) {
                        // All the items do not fit
                        // Add as much as we can
                        insertAmount = newItem.getMaxStackSize() - existingItem.getAmount();
                        newItem.setAmount(newItem.getMaxStackSize());
                    }
                    newItem.setAmount(newAmount);
                }
                if (inventory == view.getTopInventory()) {
                    int slotNumber = view.convertSlot(rawSlot);
                    int x = GuiUtils.getX(slotNumber);
                    int y = GuiUtils.getY(slotNumber);
                    Slot.State slot = customGui.getSlot(x, y);
                    if (slot != null) { // Should always be true
                        slot.setItemStack(newItem);
                        update = true;
                    }
                } else {
                    view.setItem(rawSlot, newItem);
                }
                cursorAmount -= insertAmount;
            }
            ItemStack newCursor = event.getOldCursor().clone();
            newCursor.setAmount(cursorAmount);
            // Unfortunately, CraftBukkit resets the cursor to the old cursor after the
            // event is called if the event is cancelled, This means we have to set the
            // cursor one tick later, hopefully players don't have time to dupe in one
            // tick...
            this.plugin.getServer().getScheduler().runTask(this.plugin, () ->
                player.setItemOnCursor(newCursor)
            );
            if (update) {
                customGui.update();
            }
        }
    }
}
