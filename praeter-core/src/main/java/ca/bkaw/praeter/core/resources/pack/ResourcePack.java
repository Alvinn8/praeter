package ca.bkaw.praeter.core.resources.pack;

import ca.bkaw.praeter.core.Praeter;
import ca.bkaw.praeter.core.resources.CustomModelDataStore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Pack} that contains assets for the client like models and textures.
 */
public class ResourcePack extends Pack {
    public static final String CUSTOM_MODEL_DATA = "custom_model_data";
    public static final String OVERRIDES = "overrides";
    public static final String PREDICATE = "predicate";

    protected ResourcePack(Path root) {
        super(root);
    }

    /**
     * Load a resource pack from a directory and use that directory as the root of the
     * pack.
     * <p>
     * There must not be a resource pack in the folder as it can later be created
     * using {@link Pack#create(String)}.
     * <p>
     * The folder will be created if it does not exist.
     *
     * @param directory The directory to load from
     * @return The loaded resource pack.
     * @throws IllegalArgumentException If the specified path already exists but is not a directory.
     * @throws IOException If an I/O error occurs.
     */
    public static ResourcePack loadDirectory(Path directory) throws IOException {
        return new ResourcePack(validateDirectoryPath(directory));
    }

    /**
     * Load a resource pack from a zip file and use the root of the zip as the root of
     * the resource pack.
     * <p>
     * The zip file will be created if it does not exist.
     *
     * @param zipFile The path of the zip file to read.
     * @return The loaded or created resource pack.
     * @throws IOException If an I/O error occurs.
     */
    public static ResourcePack loadZip(Path zipFile) throws IOException {
        return new ResourcePack(openZip(zipFile));
    }

    @Override
    protected int getCurrentPackFormat() {
        return 12;
    }

    private static String getResourcePath(NamespacedKey namespacedKey, String folder, String extension) {
        String dotExtension = "." + extension;
        String key = namespacedKey.getKey();
        if (!key.endsWith(dotExtension)) {
            key += dotExtension;
        }
        return "assets/" + namespacedKey.getNamespace() + "/" + folder + "/" + key;
    }

    /**
     * Get the string path to a model resource in a resource pack.
     * <p>
     * The key is relative to the "textures" folder, but "item", "block", etc. folders
     * must be provided in the key. The file extension (.json) may optionally be
     * provided in the key.
     *
     * @param namespacedKey The namespaced key of the model to get the string path.
     * @return The string path of the model.
     */
    public static String getModelStringPath(NamespacedKey namespacedKey) {
        return getResourcePath(namespacedKey, "models", "json");
    }

    /**
     * Get the path to a model resource in this resource pack.
     * <p>
     * The key is relative to the "textures" folder, but "item", "block", etc. folders
     * must be provided in the key. The file extension (.json) may optionally be
     * provided in the key.
     *
     * @param namespacedKey The namespaced key of the model to get the path.
     * @return The path of the model.
     */
    public Path getModelPath(NamespacedKey namespacedKey) {
        return this.getPath(getModelStringPath(namespacedKey));
    }

    /**
     * Get the string path to a texture resource in a resource pack.
     * <p>
     * The key is relative to the "textures" folder, but "item", "block", etc. folders
     * must be provided in the key. The file extension (.png) may optionally be
     * provided in the key.
     *
     * @param namespacedKey The namespaced key of the texture to get the string path.
     * @return The string path of the texture.
     */
    public static String getTextureStringPath(NamespacedKey namespacedKey) {
        return getResourcePath(namespacedKey, "textures", "png");
    }

    /**
     * Get the path to a texture resource in this resource pack.
     * <p>
     * The key is relative to the "textures" folder, but "item", "block", etc. folders
     * must be provided in the key. The file extension (.png) may optionally be
     * provided in the key.
     *
     * @param namespacedKey The namespaced key of the texture to get the string path.
     * @return The path of the texture.
     */
    public Path getTexturePath(NamespacedKey namespacedKey) {
        return this.getPath(getTextureStringPath(namespacedKey));
    }

    private void copyModelFromVanilla(Path path, NamespacedKey vanillaModel) throws IOException {
        // Copy from vanilla assets
        ResourcePack vanillaAssets
            = Praeter.get().getResourceManager().getPacks().getVanillaAssets();
        Path vanillaPath = vanillaAssets.getModelPath(vanillaModel);
        if (!Files.exists(vanillaPath)) {
            throw new IllegalArgumentException("The model " + vanillaModel + " was not " +
                "found in the vanilla resource pack.");
        }
        Files.createDirectories(path.getParent());
        Files.copy(vanillaPath, path);
    }

    /**
     * Add a custom model data entry to the vanilla model, redirecting the model
     * to {@code model}.
     * <p>
     * The vanilla model should either exist in this resource pack or in the vanilla
     * one, if not this method will throw an error.
     * <p>
     * See {@link #getModelPath(NamespacedKey)}, "item", "block", etc. folders must
     * be included in the model namespaced key's path.
     *
     * @param vanillaModel The model to add custom model data to.
     * @param model The model to redirect to.
     * @return The custom model data value that needs to be set on the item to redirect the model.
     * @throws IOException If something goes wrong
     * @throws IllegalArgumentException If the {@code vanillaModel} doesn't exist in this pack,
     * and wasn't found in the vanilla assets either.
     * @see #getModelPath(NamespacedKey)
     */
    public int addCustomModelData(NamespacedKey vanillaModel, NamespacedKey model) throws IOException {
        Path path = this.getModelPath(vanillaModel);
        if (!Files.exists(path)) {
            this.copyModelFromVanilla(path, vanillaModel);
        }
        JsonResource jsonResource = new JsonResource(this, path);
        JsonArray overrides;
        if (jsonResource.getJson().has(OVERRIDES)) {
            overrides = jsonResource.getJson().get(OVERRIDES).getAsJsonArray();
        } else {
            overrides = new JsonArray();
            jsonResource.getJson().add(OVERRIDES, overrides);
        }
        String modelKey = model.toString();

        CustomModelDataStore store
            = Praeter.get().getResourceManager().getPacks().getCustomModelDataStore();

        // Look for an existing custom model data for this model
        for (JsonElement existingOverrideElement : overrides) {
            JsonObject existingOverride = existingOverrideElement.getAsJsonObject();
            JsonObject predicate = existingOverride.get(PREDICATE).getAsJsonObject();
            if (!predicate.has(CUSTOM_MODEL_DATA)) continue;

            String existingModel = existingOverride.get("model").getAsString();
            if (predicate.size() == 1 && modelKey.equals(existingModel)) {
                // The model we were trying to add already existed and has no other predicates.
                int customModelData = predicate.get(CUSTOM_MODEL_DATA).getAsInt();
                // Let's ensure the store is aware of this value
                store.set(model, customModelData);
                return customModelData;
            }
        }

        int value = store.has(model) ? store.get(model) : store.next();

        // Ensure this value isn't used, it really shouldn't be, but in case plugins
        // add their own custom model data manually or something. We really don't
        // want duplicates.
        findFreeValue:
        while (true) {
            for (JsonElement existingOverrideElement : overrides) {
                JsonObject existingOverride = existingOverrideElement.getAsJsonObject();
                JsonObject predicate = existingOverride.get(PREDICATE).getAsJsonObject();
                if (!predicate.has(CUSTOM_MODEL_DATA)) {
                    continue;
                }
                int existingValue = predicate.get(CUSTOM_MODEL_DATA).getAsInt();
                if (existingValue == value) {
                    Praeter.get().getLogger().warning("Occupied custom model data value " + value
                        + " existed for " + existingOverride.get("model").getAsString()
                        + " while trying to add " + model);
                    // This custom model data value is occupied, increment it and look again
                    value = store.next();
                    continue findFreeValue;
                }
            }
            // We finished the loop without hitting the continue statement, we have found
            // a custom model data value that isn't used.
            break;
        }

        // Store the custom model data value used so that it stays consistent past
        // restarts and between resource packs.
        store.set(model, value);

        JsonObject override = new JsonObject();
        JsonObject predicate = new JsonObject();
        override.add(PREDICATE, predicate);
        predicate.addProperty(CUSTOM_MODEL_DATA, value);
        override.addProperty("model", modelKey);
        overrides.add(override);

        // Overrides need to be sorted by the custom model data value, otherwise the
        // value that is last in the list will take priority in a way.
        List<JsonObject> overridesArrayList = new ArrayList<>(overrides.size());
        for (JsonElement jsonElement : overrides) {
            overridesArrayList.add(jsonElement.getAsJsonObject());
        }
        overridesArrayList.sort((a, b) -> {
            if (a.has(PREDICATE) && b.has(PREDICATE)) {
                JsonObject predicateA = a.getAsJsonObject(PREDICATE);
                JsonObject predicateB = b.getAsJsonObject(PREDICATE);
                if (predicateA.has(CUSTOM_MODEL_DATA) && predicateB.has(CUSTOM_MODEL_DATA)) {
                    int valueA = predicateA.get(CUSTOM_MODEL_DATA).getAsInt();
                    int valueB = predicateB.get(CUSTOM_MODEL_DATA).getAsInt();
                    return valueA - valueB;
                }
            }
            return 0;
        });
        overrides = new JsonArray(overridesArrayList.size());
        for (JsonObject jsonObject : overridesArrayList) {
            overrides.add(jsonObject);
        }
        jsonResource.getJson().add(OVERRIDES, overrides);

        jsonResource.save();
        return value;
    }

    /**
     * Add an override to the vanilla model, redirecting the model to {@code model}
     * when the predicate is met.
     *
     * @param vanillaModel The model to add the override to.
     * @param model The model to redirect to when the predicate is met.
     * @param predicate The predicate to add with the override.
     * @throws IOException If an I/O error occurs.
     */
    public void addOverride(NamespacedKey vanillaModel, NamespacedKey model, JsonObject predicate) throws IOException {
        Path vanillaModelPath = this.getModelPath(vanillaModel);
        if (!Files.exists(vanillaModelPath)) {
            this.copyModelFromVanilla(vanillaModelPath, vanillaModel);
        }
        JsonResource resource = new JsonResource(this, vanillaModelPath);
        JsonArray overrides;
        if (resource.getJson().has(OVERRIDES)) {
            overrides = resource.getJson().getAsJsonArray(OVERRIDES);
        } else {
            overrides = new JsonArray();
            resource.getJson().add(OVERRIDES, overrides);
        }

        JsonObject override = new JsonObject();
        override.add(PREDICATE, predicate);
        override.addProperty("model", model.toString());
        overrides.add(override);

        resource.save();
    }
}
