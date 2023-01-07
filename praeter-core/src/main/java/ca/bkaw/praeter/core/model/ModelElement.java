package ca.bkaw.praeter.core.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper around an element in a {@link Model}.
 */
public class ModelElement {
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String ROTATION = "rotation";

    private final JsonObject json;
    private @Nullable ModelElementRotation rotation;

    /**
     * Create a new wrapper around the element json.
     *
     * @param json The json for an element.
     */
    public ModelElement(@NotNull JsonObject json) {
        this.json = json;
    }

    /**
     * Create a new element that starts at {@code from} and ends at {@code to}.
     *
     * @param from The starting position.
     * @param to The end position.
     */
    public ModelElement(@NotNull Vector from, @NotNull Vector to) {
        this(new JsonObject());
        this.setFrom(from);
        this.setTo(to);
    }

    /**
     * Move this element by the specified vector.
     *
     * @param vector The vector with amount to move the element by.
     */
    public void move(@NotNull Vector vector) {
        this.setFrom(this.getFrom().add(vector));
        this.setTo(this.getTo().add(vector));
        ModelElementRotation rotation = this.getRotation();
        if (rotation != null) {
            rotation.setOrigin(rotation.getOrigin().add(vector));
        }
    }

    /**
     * Get the middle point of the vector.
     *
     * @return The middle.
     */
    @NotNull
    public Vector getMiddle() {
        return this.getFrom().midpoint(this.getTo());
    }

    /**
     * Scale the element around the specified origin.
     *
     * @param scale The factor to scale by.
     * @param origin The origin to scale around.
     */
    public void scale(Vector scale, Vector origin) {
        Vector from = this.getFrom();
        from.subtract(origin).multiply(scale).add(origin);
        this.setFrom(from);

        Vector to = this.getTo();
        to.subtract(origin).multiply(scale).add(origin);
        this.setTo(to);

        ModelElementRotation rotation = this.getRotation();
        if (rotation != null) {
            Vector rotationOrigin = rotation.getOrigin();
            rotationOrigin.subtract(origin).multiply(scale).add(origin);
            rotation.setOrigin(rotationOrigin);
        }
    }


    /**
     * Get the starting position of the element.
     * <p>
     * Mutating the vector does not affect the element. New instances are returned for
     * each call to the method.
     *
     * @return The starting position.
     */
    @NotNull
    public Vector getFrom() {
        JsonArray from = this.json.getAsJsonArray(FROM);
        return Model.jsonArrayToVector(from);
    }

    /**
     * Set the starting position of the element.
     *
     * @param vector The starting position vector.
     */
    public void setFrom(@NotNull Vector vector) {
        JsonArray array = this.json.getAsJsonArray(FROM);
        Model.vectorToJsonArray(vector, array);
    }

    /**
     * Get the end position of the element.
     * <p>
     * Mutating the vector does not affect the element. New instances are returned for
     * each call to the method.
     *
     * @return The end position.
     */
    @NotNull
    public Vector getTo() {
        JsonArray to = this.json.getAsJsonArray(TO);
        return Model.jsonArrayToVector(to);
    }

    /**
     * Set the end position of the element.
     *
     * @param vector The end position vector.
     */
    public void setTo(@NotNull Vector vector) {
        JsonArray array = this.json.getAsJsonArray(TO);
        Model.vectorToJsonArray(vector, array);
    }

    /**
     * Get the rotation of the element.
     *
     * @return The rotation, or null.
     */
    @Nullable
    public ModelElementRotation getRotation() {
        if (this.rotation == null) {
            if (!this.json.has(ROTATION)) {
                return null;
            }
            JsonObject json = this.json.getAsJsonObject(ROTATION);
            this.rotation = new ModelElementRotation(json);
        }
        return this.rotation;
    }

    /**
     * Set the rotation of the element. If set to null the rotation will be removed.
     *
     * @param rotation The rotation.
     */
    public void setRotation(@Nullable ModelElementRotation rotation) {
        this.rotation = rotation;
        if (this.rotation == null) {
            this.json.remove(ROTATION);
        } else {
            this.json.add(ROTATION, rotation.getJson());
        }
    }

    /**
     * Get the json object this element is wrapping.
     *
     * @return The json.
     */
    @NotNull
    public JsonObject getJson() {
        return this.json;
    }

    /**
     * Create a deep copy of the element.
     *
     * @return The copy.
     */
    @NotNull
    public ModelElement deepCopy() {
        return new ModelElement(this.json.deepCopy());
    }
}
