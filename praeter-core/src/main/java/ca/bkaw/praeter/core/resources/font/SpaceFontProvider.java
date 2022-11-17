package ca.bkaw.praeter.core.resources.font;

import com.google.gson.JsonObject;

/**
 * A {@link FontProvider} for character that only advance the cursor.
 * <p>
 * Note that mutating the provider will mutate the json.
 */
public class SpaceFontProvider implements FontProvider {
    private final JsonObject json = new JsonObject();

    public void add(char c, int advance) {
        this.json.addProperty(String.valueOf(c), advance);
    }

    @Override
    public JsonObject asJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "space");
        json.add("advances", this.json);
        return json;
    }
}
