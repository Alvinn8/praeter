package ca.bkaw.praeter.core.model;

import ca.bkaw.praeter.core.resources.pack.JsonResource;
import ca.bkaw.praeter.core.resources.pack.ResourcePack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper around a model in a {@link ResourcePack}.
 * <p>
 * Changes made to the model and its element will be reflected on the json.
 */
@ApiStatus.Experimental
public class Model {
    public static final String PARENT = "parent";
    public static final String ELEMENTS = "elements";
    public static final String GROUPS = "groups";
    public static final String DISPLAY = "display";

    private final @NotNull JsonObject json;
    private @Nullable NamespacedKey parent;
    private @Nullable ModelElementList elements;
    private @Nullable ModelDisplay display;

    /**
     * Create a model that wraps around the json object as the root of the model.
     *
     * @param json The json root of the model.
     */
    public Model(@NotNull JsonObject json) {
        this.json = json;
    }

    /**
     * Create a model that wraps around the {@link JsonResource}. Changes made to the
     * model will be reflected on the {@link JsonResource} instance.
     *
     * @param jsonResource The json resource.
     */
    public Model(@NotNull JsonResource jsonResource) {
        this(jsonResource.getJson());
    }

    /**
     * Create an empty model.
     */
    public Model() {
        this(new JsonObject());
    }

    /**
     * Convert a json array with length 3 to a vector.
     *
     * @param array The array of length 3, holding only numbers.
     * @return The vector.
     */
    @NotNull
    public static Vector jsonArrayToVector(@NotNull JsonArray array) {
        return new Vector(
            array.get(0).getAsDouble(),
            array.get(1).getAsDouble(),
            array.get(2).getAsDouble()
        );
    }

    /**
     * Update the json array to describe the vector.
     *
     * @param vector The vector.
     * @param array The json array to update.
     */
    static void vectorToJsonArray(@NotNull Vector vector, @NotNull JsonArray array) {
        array.set(0, new JsonPrimitive(vector.getX()));
        array.set(1, new JsonPrimitive(vector.getY()));
        array.set(2, new JsonPrimitive(vector.getZ()));
    }

    /**
     * Get the key to the parent model.
     *
     * @return The key to the parent model, or null.
     */
    @Nullable
    public NamespacedKey getParent() {
        if (this.parent == null) {
            if (!this.json.has(PARENT)) {
                return null;
            }
            String parentString = this.json.get(PARENT).getAsString();
            this.parent = NamespacedKey.fromString(parentString);
        }
        return this.parent;
    }

    /**
     * Set the key to the parent model.
     *
     * @param parent The parent model.
     */
    public void setParent(@Nullable NamespacedKey parent) {
        this.parent = parent;
        if (this.parent == null) {
            this.json.remove(PARENT);
        } else {
            this.json.addProperty(PARENT, parent.toString());
        }
    }

    /**
     * Get all the elements in this model. Returns null if the model has no elements.
     * <p>
     * Changing the list or the elements in the list will modify the model json.
     *
     * @return The list of all elements.
     */
    @Nullable
    public ModelElementList getAllElements() {
        if (this.elements == null) {
            if (!this.json.has(ELEMENTS)) {
                return null;
            }
            JsonArray json = this.json.getAsJsonArray(ELEMENTS);
            this.elements = new ModelElementList(json);
        }
        return this.elements;
    }

    /**
     * Set the elements of this model. Setting to null removes the elements from the
     * model json.
     *
     * @param list The list of elements, or null.
     */
    public void setElements(@Nullable ModelElementList list) {
        this.elements = list;
        if (this.elements == null) {
            this.json.remove(ELEMENTS);
        } else {
            this.json.add(ELEMENTS, this.elements.getJson());
        }
    }

    /**
     * Return a {@link ModelElementList} from a list of indexes to the elements.
     *
     * @param indexes The list of indexes.
     * @return The list.
     * @throws IllegalStateException if the model has no elements.
     */
    @NotNull
    public ModelElementList getElements(IntList indexes) {
        ModelElementList allElements = this.getAllElements();
        if (allElements == null) {
            throw new IllegalStateException("This model has no elements.");
        }
        ModelElementList list = new ModelElementList(new JsonArray(indexes.size()));
        for (int i = 0; i < indexes.size(); i++) {
            int index = indexes.getInt(i);
            ModelElement element = allElements.getElement(index);
            list.add(element);
        }
        return list;
    }

    /**
     * Get the list of Blockbench groups in this model. Returns null if the model has
     * no groups.
     * <p>
     * Mutating the list does not change the model json.
     *
     * @return The list of groups.
     */
    @Nullable
    public List<ModelGroup> getGroups() {
        if (!this.json.has(GROUPS)) {
            return null;
        }
        JsonArray groups = this.json.getAsJsonArray(GROUPS);
        List<ModelGroup> list = new ArrayList<>();
        for (JsonElement group : groups) {
            if (group.isJsonObject()) {
                list.add(new ModelGroup(group.getAsJsonObject()));
            }
        }
        return list;
    }

    /**
     * Get the {@link ModelDisplay} of the model.
     *
     * @return The display.
     */
    @Nullable
    public ModelDisplay getDisplay() {
        if (this.display == null) {
            if (!this.json.has(DISPLAY)) {
                return null;
            }
            JsonObject json = this.json.getAsJsonObject(DISPLAY);
            this.display = new ModelDisplay(json);
        }
        return this.display;
    }

    /**
     * Set the {@link ModelDisplay} of the model. Setting to null removes the display
     * from the model json.
     *
     * @param display The display, or null.
     */
    public void setDisplay(@Nullable ModelDisplay display) {
        this.display = display;
        if (this.display == null) {
            this.json.remove(DISPLAY);
        } else {
            this.json.add(DISPLAY, this.display.getJson());
        }
    }

    /**
     * Get the json object this model is wrapping.
     *
     * @return The json.
     */
    @NotNull
    public JsonObject getJson() {
        return this.json;
    }

    /**
     * Create a deep copy of this model.
     *
     * @return The copy.
     */
    @NotNull
    public Model deepCopy() {
        return new Model(this.json.deepCopy());
    }
}
