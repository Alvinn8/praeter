package ca.bkaw.praeter.framework.resources.pack.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A bitmap provider for a font.
 *
 * @see Font
 */
public class BitmapFontProvider {
    private NamespacedKey textureKey;
    private Integer height;
    private int ascent;
    private List<String> chars;

    /**
     * Create a new bitmap font provider, leaving the optional height property unset.
     *
     * @param textureKey The resource location of the texture, relative to the namespace's
     *                   textures folder. The file extension must be present.
     * @param ascent The vertical shift of the character.
     * @param chars The list of characters.
     */
    public BitmapFontProvider(NamespacedKey textureKey, int ascent, List<String> chars) {
        this(textureKey, null, ascent, chars);
    }

    /**
     * Create a new bitmap font provider.
     *
     * @param textureKey The namespaced key of the texture, relative to the namespace's
     *                   textures folder. The file extension must be present.
     * @param height The height of the character.
     * @param ascent The vertical shift of the character.
     * @param chars The list of characters.
     */
    public BitmapFontProvider(NamespacedKey textureKey, @Nullable Integer height, int ascent, List<String> chars) {
        this.textureKey = textureKey;
        this.height = height;
        this.ascent = ascent;
        this.chars = chars;
    }

    public NamespacedKey getTextureKey() {
        return textureKey;
    }

    public void setTextureKey(NamespacedKey textureKey) {
        this.textureKey = textureKey;
    }

    @Nullable
    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(@Nullable Integer height) {
        this.height = height;
    }

    public int getAscent() {
        return this.ascent;
    }

    public void setAscent(int ascent) {
        this.ascent = ascent;
    }

    public List<String> getChars() {
        return this.chars;
    }

    public void setChars(List<String> chars) {
        this.chars = chars;
    }

    /**
     * Check if this bitmap font provider is the same as the one defined in json.
     *
     * @param json The json.
     * @return Whether they are equal.
     */
    public boolean isEqual(JsonObject json) {
        if (!("bitmap".equals(json.get("type").getAsString())
            && this.textureKey.equals(NamespacedKey.fromString(json.get("file").getAsString()))
            && this.ascent == json.get("ascent").getAsInt())) {
            return false;
        }
        if (this.height != null == json.has("height")) {
            if (this.height != json.get("height").getAsInt()) {
                return false;
            }
        }
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
        if (this.height != null) {
            json.addProperty("height", this.height);
        }
        JsonArray chars = new JsonArray();
        for (String str : this.chars) {
            chars.add(str);
        }
        json.add("chars", chars);
        return json;
    }
}
