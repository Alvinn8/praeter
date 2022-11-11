package ca.bkaw.praeter.framework.resources.pack;

import ca.bkaw.praeter.framework.resources.PraeterResources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A {@link Pack} that contains assets for the client like models and textures.
 */
public class ResourcePack extends Pack {
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
        return 11; // TODO
    }

    private Path getResourcePath(NamespacedKey resourceLocation, String folder, String extension) {
        return this.getPath("assets/" + resourceLocation.getNamespace() + "/" + folder + "/" + resourceLocation.getKey() + "." + extension);
    }

    /**
     * Get the path to a model resource in this resource pack.
     * <p>
     * Note that this does not include the folders "item", "block", etc., that must be
     * present in the namespaced key.
     *
     * @param namespacedKey The namespaced key for the model to get the path of.
     * @return The path of the model.
     */
    public Path getModelPath(NamespacedKey namespacedKey) {
        return this.getResourcePath(namespacedKey, "models", "json");
    }

    /**
     * Get the path to a texture resource in this resource pack.
     * <p>
     * Note that this does not include the folders "item", "block", etc., that must be
     * present in the namespaced key.
     *
     * @param namespacedKey The namespaced key for the texture to get the path of.
     * @return The path of the texture.
     */
    public Path getTexturePath(NamespacedKey namespacedKey) {
        return this.getResourcePath(namespacedKey, "textures", "png");
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
    // TODO add value argument, or make it default to hashcode?
    public int addCustomModelData(NamespacedKey vanillaModel, NamespacedKey model) throws IOException {
        Path path = this.getModelPath(vanillaModel);
        if (!Files.exists(path)) {
            // Copy from vanilla assets
            ResourcePack vanillaAssets = PraeterResources.get().getResourceManager().getVanillaAssets();
            Path vanillaPath = vanillaAssets.getModelPath(vanillaModel);
            if (!Files.exists(vanillaPath)) {
                throw new IllegalArgumentException("The model " + vanillaModel + " was not found in the vanilla resource pack.");
            }
            Files.createDirectories(path.getParent());
            Files.copy(vanillaPath, path);
        }
        JsonResource jsonResource = new JsonResource(this, path);
        JsonArray overrides;
        if (jsonResource.getJson().has("overrides")) {
            overrides = jsonResource.getJson().get("overrides").getAsJsonArray();
        } else {
            overrides = new JsonArray();
            jsonResource.getJson().add("overrides", overrides);
        }
        String modelKey = model.toString();

        // Look for an existing custom model data for this model
        for (JsonElement existingOverrideElement : overrides) {
            JsonObject existingOverride = existingOverrideElement.getAsJsonObject();
            JsonObject predicate = existingOverride.get("predicate").getAsJsonObject();
            if (!predicate.has("custom_model_data")) continue;

            if (predicate.size() == 1 && modelKey.equals(existingOverride.get("model").getAsString())) {
                // Cool, the model we were trying to add already existed and has no other predicates.
                return predicate.get("custom_model_data").getAsInt();
            }
        }
        int value = 1;
        findFreeValue:
        while (true) {
            for (JsonElement existingOverrideElement : overrides) {
                JsonObject existingOverride = existingOverrideElement.getAsJsonObject();
                JsonObject predicate = existingOverride.get("predicate").getAsJsonObject();
                if (!predicate.has("custom_model_data")) {
                    continue;
                }
                int existingValue = predicate.get("custom_model_data").getAsInt();
                if (existingValue == value) {
                    // This custom model data value is occupied, increment it and look again
                    value++;
                    continue findFreeValue;
                }
            }
            // We finished the loop without hitting the continue statement, we have found
            // a custom model data value that isn't used.
            break;
        }

        JsonObject override = new JsonObject();
        JsonObject predicate = new JsonObject();
        override.add("predicate", predicate);
        predicate.addProperty("custom_model_data", value);
        override.addProperty("model", modelKey);
        overrides.add(override);
        jsonResource.save();
        return value;
    }
}
