package ca.bkaw.praeter.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Utility class for working with items.
 */
public class ItemUtils {

    /**
     * The key to the model for a fully transparent item.
     * <p>
     * Praeter will ensure this model is available in all packs.
     */
    public static final NamespacedKey TRANSPARENT_ITEM = new NamespacedKey(Praeter.NAMESPACE, "item/transparent");

    private static final Component NORMALIZED_WRAPPER = Component.text()
        .color(NamedTextColor.WHITE)
        .decoration(TextDecoration.ITALIC, false)
        .build();

    /**
     * Normalize the text, ensuring it renders without italics or unexpected colors.
     *
     * @param content The content component.
     * @return The normalized component.
     */
    public static Component normalizeText(Component content) {
        return NORMALIZED_WRAPPER.append(content);
    }

    /**
     * Set the text on an item. The text will be normalized.
     *
     * @param item The item.
     * @param text The text.
     */
    public static void setItemText(ItemStack item, List<Component> text) {
        Component nameContent = text.size() > 0 ? text.get(0) : Component.empty();
        List<Component> loreContent = text.size() > 1
            ? text.subList(1, text.size())
            : Collections.emptyList();

        Component name = normalizeText(nameContent);
        List<Component> lore = loreContent.stream()
            .map(ItemUtils::normalizeText)
            .toList();

        item.editMeta(meta -> {
            meta.displayName(name);
            meta.lore(lore);
        });
    }
}
