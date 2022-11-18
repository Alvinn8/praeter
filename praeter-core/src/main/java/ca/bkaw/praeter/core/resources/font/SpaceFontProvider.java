package ca.bkaw.praeter.core.resources.font;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * A {@link FontProvider} for character that only advance the cursor.
 * <p>
 * Note that mutating the provider will mutate the json.
 */
public class SpaceFontProvider implements FontProvider {
    private final JsonObject json = new JsonObject();

    /**
     * Map the specified character to advance by the set amount.
     *
     * @param c The character.
     * @param advance The amount to advance.
     */
    public void add(char c, int advance) {
        this.json.addProperty(String.valueOf(c), advance);
    }

    /**
     * Check whether this space font provider has a character that advances by the
     * specified amount.
     *
     * @param advance The amount to advance by.
     * @return Whether a character exists.
     */
    public boolean has(int advance) {
        for (Map.Entry<String, JsonElement> entry : this.json.entrySet()) {
            if (entry.getValue().getAsInt() == advance) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JsonObject asJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "space");
        json.add("advances", this.json);
        return json;
    }
}
