package ca.bkaw.praeter.framework.resources.pack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A json resource (file) in a {@link Pack}.
 */
public class JsonResource {
    public static final Gson GSON = new Gson();

    private final Pack pack;
    private final Path path;
    private final JsonObject json;

    /**
     * Read a JsonResource from the pack. The file must exist in the pack.
     *
     * @param pack The pack.
     * @param path The path to the file.
     * @throws IOException If an I/O exception occurs while reading the file.
     * @throws JsonSyntaxException If the JSON is not properly formatted.
     */
    public JsonResource(Pack pack, Path path) throws IOException, JsonSyntaxException {
        this.pack = pack;
        this.path = path;

        JsonElement jsonElement = JsonParser.parseReader(Files.newBufferedReader(path));
        this.json = jsonElement.getAsJsonObject();
    }

    /**
     * Create a new JsonResource in the pack with the specified JSON.
     * <p>
     * If a file already exists on the path and {@link #save()} is invoked the file
     * will be overwritten.
     *
     * @param pack The pack.
     * @param path The path of the file.
     * @param json The json to use for the resource.
     */
    public JsonResource(Pack pack, Path path, JsonObject json) {
        this.pack = pack;
        this.path = path;
        this.json = json;
    }

    /**
     * Get the pack this resource belongs to.
     *
     * @return The pack.
     */
    public Pack getPack() {
        return this.pack;
    }

    /**
     * The path of the resource as can be gotten from {@link Pack#getPath(String)}.
     *
     * @return The path of this resource.
     */
    public Path getPath() {
        return this.path;
    }

    /**
     * Get the JSON of this resource.
     * <p>
     * Modify this JSON tree to change the contents of this resource.
     * <p>
     * Call {@link #save()} to write the changes to the pack.
     *
     * @return The JSON object.
     */
    public JsonObject getJson() {
        return this.json;
    }

    /**
     * Save the resource to the resource pack, updating the file to contain the JSON
     * in this resource, {@link #getJson()}.
     *
     * @throws IOException If an I/O error occurs
     */
    public void save() throws IOException {
        this.save(GSON);
    }

    /**
     * Save the resource to the pack using a specified {@link Gson} instance
     * to write the JSON.
     * <p>
     * The file in the pack will be updated to contain the {@link #getJson() JSON} in
     * this resource.
     *
     * @param gson The Gson instance to use when writing the json.
     * @throws IOException If an I/O error occurs
     */
    public void save(Gson gson) throws IOException {
        Files.writeString(this.path, gson.toJson(this.json));
    }
}
