package ca.bkaw.praeter.framework.resources.pack.font;

import ca.bkaw.praeter.framework.resources.pack.JsonResource;
import ca.bkaw.praeter.framework.resources.pack.ResourcePack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A font in a {@link ResourcePack}.
 */
public class Font {
    private final ResourcePack pack;
    private final NamespacedKey key;
    private final JsonResource fontJson;

    /**
     * Load a font by namespaced key.
     * <p>
     * Will create the font if it does not already exist.
     *
     * @param pack The pack of the font.
     * @param key The key of the font.
     * @throws IOException If an I/O error occurs.
     */
    public Font(ResourcePack pack, NamespacedKey key) throws IOException {
        this.pack = pack;
        this.key = key;
        Path path = this.pack.getPath("assets")
            .resolve(key.getNamespace())
            .resolve("font")
            .resolve(key + ".json");
        if (Files.exists(path)) {
            this.fontJson = new JsonResource(this.pack, path);
        } else {
            Files.createDirectories(path.getParent());

            JsonObject json = new JsonObject();
            json.add("providers", new JsonArray());

            this.fontJson = new JsonResource(this.pack, path, json);
            this.fontJson.save();
        }
    }

    /**
     * Get the resource pack the font is in.
     *
     * @return The resource pack.
     */
    public ResourcePack getPack() {
        return this.pack;
    }

    /**
     * Get the key of the font.
     *
     * @return The key.
     */
    public NamespacedKey getKey() {
        return this.key;
    }

    /**
     * Add the specified bitmap font provider to this font.
     *
     * @param provider The provider.
     * @throws IOException If an I/O error occurs.
     */
    private void addProvider(BitmapFontProvider provider) throws IOException {
        JsonObject json = this.fontJson.getJson();
        JsonArray providers = json.get("providers").getAsJsonArray();
        for (JsonElement element : providers) {
            JsonObject jsonObject = element.getAsJsonObject();
            if (provider.isEqual(jsonObject)) {
                // Great, we already had this provider!
                System.out.println("debug: provider already exists");
                return;
            }
        }
        System.out.println("debug: adding provider");
        providers.add(provider.asJsonObject());
        this.fontJson.save();
    }

    /**
     * Get the next free character to use.
     *
     * @return The character.
     */
    public char getNextChar() {
        int i = 0xe001;
        JsonArray providers = this.fontJson.getJson().getAsJsonArray("providers");
        freeValueLoop:
        while (true) {
            char c = (char) i;
            for (JsonElement element : providers) {
                JsonArray chars = element.getAsJsonObject().getAsJsonArray("chars");
                for (JsonElement element2 : chars) {
                    if (element2.getAsString().indexOf(c) > 0) {
                        // This character is occupied, lets increment and try again
                        i++;
                        continue freeValueLoop;
                    }
                }
            }
            // This point was reached without hitting the continue statement.
            // We have an unused character.
            return c;
        }
    }
}
