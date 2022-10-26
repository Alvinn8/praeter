package ca.bkaw.praeter.framework.resources.bake;

import ca.bkaw.praeter.framework.resources.pack.JsonResource;
import ca.bkaw.praeter.framework.resources.pack.ResourcePack;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A resource pack that has been generated and finished.
 * <p>
 * The baked resource pack can be used to get models by keys, which avoids having
 * to store magic custom model data numbers.
 */
public class BakedResourcePack {
    private final Map<NamespacedKey, BakedItemModel> itemModels;

    /**
     * Create a new baked resource pack.
     * <p>
     * Immutable copies will be created for all maps.
     *
     * @param itemModels The map of baked item models.
     * @see #bake(ResourcePack)
     */
    public BakedResourcePack(Map<NamespacedKey, BakedItemModel> itemModels) {
        this.itemModels = ImmutableMap.copyOf(itemModels);
    }

    /**
     * Get the baked item model from the namespaced key of the model.
     *
     * @param key The key of the model.
     * @return The baked model.
     */
    @Nullable
    public BakedItemModel getItemModel(NamespacedKey key) {
        return this.itemModels.get(key);
    }

    /**
     * Bake the specified resource pack.
     * <p>
     * This will scan the resource pack to create mappings for models.
     * <p>
     * If the resource pack is changed, changes will not be reflected in the baked
     * resource pack. It is therefore recommended that the resource pack is closed
     * after it has been baked.
     *
     * @param pack The resource pack.
     * @return The mapped resource pack.
     * @throws IOException If an I/O error occurs.
     */
    public static BakedResourcePack bake(ResourcePack pack) throws IOException {
        Map<NamespacedKey, BakedItemModel> itemModels = bakeItemModels(pack);

        return new BakedResourcePack(itemModels);
    }

    private static Map<NamespacedKey, BakedItemModel> bakeItemModels(ResourcePack pack) throws IOException {
        Map<NamespacedKey, BakedItemModel> itemModels = new HashMap<>();

        // Loop through all vanilla items to look for custom model data
        try (Stream<Path> s = Files.list(pack.getPath("assets/minecraft/item"))) {
            for (Path path : s
                .filter(path -> path.toString().endsWith(".json"))
                .toList()) {
                JsonResource jsonResource = new JsonResource(pack, path);
                if (jsonResource.getJson().has("overrides")) {
                    JsonArray overrides = jsonResource.getJson().getAsJsonArray("overrides");
                    for (JsonElement e : overrides) {
                        JsonObject override = e.getAsJsonObject();
                        if (override.has("predicate") && override.has("custom_model_data")) {
                            int customModelData = override.get("custom_model_data").getAsInt();
                            String model = override.get("model").getAsString();
                            NamespacedKey modelKey = NamespacedKey.fromString(model);
                            String vanillaItem = path.toString().substring("assets/minecraft/models/item/".length());
                            vanillaItem = vanillaItem.substring(0, vanillaItem.length() - ".json".length());
                            Material vanillaMaterial = Material.matchMaterial(vanillaItem);
                            itemModels.put(modelKey, new BakedItemModel(vanillaMaterial, customModelData));
                        }
                    }
                }
            }
        }

        return itemModels;
    }
}
