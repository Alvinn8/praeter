package ca.bkaw.praeter.core.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * An object that keeps track of custom model data values that have been used and
 * ensures there are no duplicate values. Also makes sure that the value for a
 * model stays consistent even between resource packs and past restarts.
 */
public class CustomModelDataStore {
    private static final String NEXT_VALUE = "next_value";
    private static final String DATA = "data";

    private final Path path;
    private final Object2IntMap<NamespacedKey> map;
    private int nextValue = 1;

    /**
     * Create a new {@link CustomModelDataStore} linked to the specified path.
     *
     * @param path The path of the store.
     */
    public CustomModelDataStore(Path path) throws IOException {
        this.path = path;
        this.map = new Object2IntOpenHashMap<>();
        if (Files.isRegularFile(path)) {
            JsonElement jsonElement = JsonParser.parseReader(Files.newBufferedReader(path));
            JsonObject json = jsonElement.getAsJsonObject();
            this.nextValue = json.get(NEXT_VALUE).getAsInt();
            JsonObject object = json.getAsJsonObject(DATA);
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                NamespacedKey modelKey = NamespacedKey.fromString(entry.getKey());
                int customModelData = entry.getValue().getAsInt();
                this.map.put(modelKey, customModelData);
            }
        }
    }

    /**
     * Check whether the specified model key has a custom model data value in the store.
     *
     * @param modelKey The model key.
     * @return Whether there is a value.
     */
    public boolean has(NamespacedKey modelKey) {
        return this.map.containsKey(modelKey);
    }

    /**
     * Get the custom model data value for the specified model.
     * <p>
     * Ensure there is a value with {@link #has(NamespacedKey)} before,
     * otherwise an exception will be thrown.
     *
     * @param modelKey The model.
     * @return The value.
     */
    public int get(NamespacedKey modelKey) {
        if (!this.map.containsKey(modelKey)) {
            throw new IllegalArgumentException("No value for " + modelKey);
        }
        return this.map.getInt(modelKey);
    }

    /**
     * Set the custom model data value to associate with the model key.
     *
     * @param modelKey The model key.
     * @param customModelData The value.
     */
    public void set(NamespacedKey modelKey, int customModelData) {
        if (this.map.containsKey(modelKey)) {
            int existingValue = this.map.getInt(modelKey);
            if (existingValue == customModelData) {
                // The model already had this value, nothing changed
                return;
            }
        }
        this.map.put(modelKey, customModelData);
    }

    public int next() {
        return this.nextValue++;
    }

    /**
     * Save the store.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void save() throws IOException {
        JsonObject json = new JsonObject();
        JsonObject data = new JsonObject();
        json.add(DATA, data);
        json.addProperty(NEXT_VALUE, this.nextValue);
        for (Object2IntMap.Entry<NamespacedKey> entry : this.map.object2IntEntrySet()) {
            NamespacedKey modelKey = entry.getKey();
            int customModelData = entry.getIntValue();
            data.addProperty(modelKey.toString(), customModelData);
        }
        Files.writeString(this.path, json.toString());
    }
}
