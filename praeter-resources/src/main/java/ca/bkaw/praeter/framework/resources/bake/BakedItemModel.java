package ca.bkaw.praeter.framework.resources.bake;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * Information on how to render an item model.
 *
 * @param material The material to apply custom model data to.
 * @param customModelData The custom-model-data value to apply to render this item model.
 * @see BakedResourcePack#getItemModel(NamespacedKey)
 */
public record BakedItemModel(Material material, int customModelData) {
}
