package ca.bkaw.praeter.core.model;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper around display settings for a {@link Model}.
 */
public class ModelDisplay {
    private final JsonObject json;
    private final Map<Perspective, DisplaySetting> displaySettings = new HashMap<>(8);

    public ModelDisplay(JsonObject json) {
        this.json = json;
    }

    /**
     * Create an empty model display.
     */
    public ModelDisplay() {
        this(new JsonObject());
    }

    /**
     * Get the {@link DisplaySetting} for the {@link Perspective}. Will return null if
     * the display setting is not defined in the json.
     *
     * @param perspective The perspective.
     * @return The display setting, or null.
     */
    @Nullable
    public DisplaySetting get(@NotNull Perspective perspective) {
        DisplaySetting displaySetting = this.displaySettings.get(perspective);
        if (displaySetting == null) {
            if (!this.json.has(perspective.id())) {
                return null;
            }
            JsonObject json = this.json.getAsJsonObject(perspective.id());
            displaySetting = new DisplaySetting(json);
            this.displaySettings.put(perspective, displaySetting);
        }
        return displaySetting;
    }

    /**
     * Set the {@link DisplaySetting} for the {@link Perspective}. If set to null the
     * display setting will be removed from the json.
     *
     * @param perspective The perspective.
     * @param displaySetting The display setting.
     */
    public void set(@NotNull Perspective perspective, @Nullable DisplaySetting displaySetting) {
        this.displaySettings.put(perspective, displaySetting);
        if (displaySetting == null) {
            this.json.remove(perspective.id());
        } else {
            this.json.add(perspective.id(), displaySetting.getJson());
        }
    }

    /**
     * Get the json object this model display is wrapping.
     *
     * @return The json.
     */
    @NotNull
    public JsonObject getJson() {
        return this.json;
    }

    /**
     * Create a deep copy of this model display.
     *
     * @return The copy.
     */
    @NotNull
    public ModelDisplay deepCopy() {
        return new ModelDisplay(this.json.deepCopy());
    }
}
