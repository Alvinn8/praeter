package ca.bkaw.praeter.core.resources.pack.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;

import java.util.List;

/**
 * A bitmap provider for a font.
 *
 * @param textureKey The namespaced key of the texture, relative to the namespace's
 *                   textures folder. The file extension must be present.
 * @param height     The height of the character.
 * @param ascent     The vertical shift of the character.
 * @param chars      The list of characters.
 *
 * @see Font
 */
public record BitmapFontProvider(
    NamespacedKey textureKey,
    int height,
    int ascent,
    List<String> chars
) {
    public static final int DEFAULT_HEIGHT = 8;

    public BitmapFontProvider {
        if (this.ascent() > this.height()) {
            throw new IllegalArgumentException("Ascent can not be higher than height.");
        }
    }

    /**
     * Check if this bitmap font provider is the same as the one defined in json.
     *
     * @param json The json.
     * @return Whether they are equal.
     */
    @Deprecated
    public boolean isEqual(JsonObject json) {
        if (!("bitmap".equals(json.get("type").getAsString())
            && this.textureKey.equals(NamespacedKey.fromString(json.get("file").getAsString()))
            && this.ascent == json.get("ascent").getAsInt())) {
            return false;
        }
        /*
        if (this.height != null == json.has("height")) {
            if (this.height != json.get("height").getAsInt()) {
                return false;
            }
        }
        */
        JsonArray chars = json.get("chars").getAsJsonArray();
        if (chars.size() != this.chars.size()) {
            return false;
        }

        for (int i = 0; i < chars.size(); i++) {
            JsonElement element = chars.get(i);
            String str = this.chars.get(i);
            if (!str.equals(element.getAsString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get a json object representing this bitmap provider.
     *
     * @return The json object.
     */
    public JsonObject asJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "bitmap");
        json.addProperty("file", this.textureKey.toString());
        json.addProperty("ascent", this.ascent);
        json.addProperty("height", this.height);
        JsonArray chars = new JsonArray();
        this.chars.forEach(chars::add);
        json.add("chars", chars);
        return json;
    }
}
