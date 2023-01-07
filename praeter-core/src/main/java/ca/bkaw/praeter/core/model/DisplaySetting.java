package ca.bkaw.praeter.core.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper around display settings for a {@link Perspective} in the
 * {@link ModelDisplay} of a {@link Model}.
 */
public class DisplaySetting {
    public static final String TRANSLATION = "translation";
    public static final String ROTATION = "rotation";
    public static final String SCALE = "scale";
    private final JsonObject json;

    public DisplaySetting(@NotNull JsonObject json) {
        this.json = json;
    }

    /**
     * Create an empty display setting.
     */
    public DisplaySetting() {
        this(new JsonObject());
    }

    @Nullable
    private Vector get(@NotNull String key) {
        if (!this.json.has(key)) {
            return null;
        }
        JsonArray array = this.json.getAsJsonArray(key);
        return Model.jsonArrayToVector(array);
    }

    private void set(@NotNull String key, @Nullable Vector vector) {
        if (vector == null) {
            this.json.remove(key);
            return;
        }
        JsonArray array = this.json.getAsJsonArray(key);
        if (array == null) {
            array = new JsonArray(3);
            for (int i = 0; i < 3; i++) {
                // Temporary values, they will be changed by vectorToJsonArray
                array.add(0);
            }
            this.json.add(key, array);
        }
        Model.vectorToJsonArray(vector, array);
    }

    /**
     * Get the translation vector. Returns null if no translation is defined.
     *
     * @return The translation vector.
     */
    @Nullable
    public Vector getTranslation() {
        return get(TRANSLATION);
    }

    /**
     * Set the translation vector. Null will remove the translation from the json.
     *
     * @param vector The translation vector.
     */
    public void setTranslation(@Nullable Vector vector) {
        set(TRANSLATION, vector);
    }

    /**
     * Get the rotation vector.
     *
     * @return The rotation vector.
     */
    @Nullable
    public Vector getRotation() {
        return get(ROTATION);
    }

    /**
     * Set the rotation vector. Null will remove the rotation from the json.
     *
     * @param vector The rotation vector.
     */
    public void setRotation(@Nullable Vector vector) {
        set(ROTATION, vector);
    }

    /**
     * Get the scale vector.
     *
     * @return The scale vector.
     */
    @Nullable
    public Vector getScale() {
        return get(SCALE);
    }

    /**
     * Set the scale vector. Null will remove the scale from the json.
     *
     * @param vector The scale vector.
     */
    public void setScale(@Nullable Vector vector) {
        set(SCALE, vector);
    }

    /**
     * Get the json that this display setting is wrapping.
     *
     * @return The json.
     */
    @NotNull
    public JsonObject getJson() {
        return this.json;
    }

    /**
     * Create a deep copy of this display setting.
     *
     * @return The copy.
     */
    @NotNull
    public DisplaySetting deepCopy() {
        return new DisplaySetting(this.json.deepCopy());
    }
}
