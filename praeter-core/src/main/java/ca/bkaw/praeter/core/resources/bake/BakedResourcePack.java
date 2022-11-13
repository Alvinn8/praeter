package ca.bkaw.praeter.core.resources.bake;

import ca.bkaw.praeter.core.resources.pack.JsonResource;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
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
    private final Map<FontCharIdentifier, BakedFontChar> fontChars;

    /**
     * Create a new baked resource pack.
     * <p>
     * Immutable copies will be created for all maps.
     *
     * @param itemModels The map of baked item models.
     * @see #bake(ResourcePack)
     */
    public BakedResourcePack(Map<NamespacedKey, BakedItemModel> itemModels,
                             Map<FontCharIdentifier, BakedFontChar> fontChars) {
        this.itemModels = ImmutableMap.copyOf(itemModels);
        this.fontChars = ImmutableMap.copyOf(fontChars);
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
     * Get the baked font char from the font char identifier.
     *
     * @param identifier The font char identifier.
     * @return The baked font character.
     */
    @Nullable
    public BakedFontChar getFontChar(FontCharIdentifier identifier) {
        return this.fontChars.get(identifier);
    }

    /**
     * Bake the specified resource pack.
     * <p>
     * This will scan the resource pack to create mappings for models and font chars.
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
        Map<FontCharIdentifier, BakedFontChar> fontChars = bakeFontChars(pack);

        return new BakedResourcePack(itemModels, fontChars);
    }

    private static Map<NamespacedKey, BakedItemModel> bakeItemModels(ResourcePack pack) throws IOException {
        Map<NamespacedKey, BakedItemModel> itemModels = new HashMap<>();

        Path itemPath = pack.getPath("assets/minecraft/item");
        if (Files.notExists(itemPath)) {
            return itemModels;
        }
        try (Stream<Path> s = Files.list(itemPath)) {
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

    private static Map<FontCharIdentifier, BakedFontChar> bakeFontChars(ResourcePack pack) throws IOException {
        Map<FontCharIdentifier, BakedFontChar> fontChars = new HashMap<>();

        Path assetsPath = pack.getPath("assets");
        if (Files.notExists(assetsPath)) {
            return fontChars;
        }
        try (Stream<Path> s = Files.list(assetsPath)) {
            for (Path namespacePath : s
                .filter(Files::isDirectory)
                .filter(path -> Files.isDirectory(path.resolve("font")))
                .toList()) {
                try (Stream<Path> s2 = Files.list(namespacePath.resolve("font"))) {
                    for (Path path : s2
                        .filter(path -> path.toString().endsWith(".json"))
                        .toList()) {
                        JsonResource jsonResource = new JsonResource(pack, path);
                        for (JsonElement element : jsonResource.getJson().getAsJsonArray("providers")) {
                            JsonObject provider = element.getAsJsonObject();
                            if (!"bitmap".equals(provider.get("type").getAsString())) {
                                continue;
                            }
                            JsonArray chars = provider.getAsJsonArray("chars");
                            if (chars.size() != 1) {
                                continue;
                            }
                            String charString = chars.get(0).getAsString();
                            if (charString.length() != 1) {
                                continue;
                            }
                            int ascent = provider.get("ascent").getAsInt();
                            Integer height = provider.has("height") ? provider.get("height").getAsInt() : null;
                            NamespacedKey textureKey = NamespacedKey.fromString(provider.get("file").getAsString());

                            String namespace = namespacePath.getFileName().toString();
                            String fontName = path.getFileName().toString();
                            fontName = fontName.substring(0, fontName.length() - ".json".length());
                            NamespacedKey fontKey = new NamespacedKey(namespace, fontName);

                            FontCharIdentifier identifier = new FontCharIdentifier(textureKey, height, ascent);
                            fontChars.put(identifier, new BakedFontChar(fontKey, charString.charAt(0)));
                        }
                    }
                }
            }
        }

        return fontChars;
    }
}
