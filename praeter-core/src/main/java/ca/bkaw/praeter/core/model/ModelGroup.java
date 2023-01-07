package ca.bkaw.praeter.core.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A wrapper around a group created by Blockbench in a {@link Model}.
 */
public class ModelGroup {
    private final JsonObject json;
    private @Nullable IntList childElements;
    private @Nullable List<ModelGroup> childGroups;

    public ModelGroup(@NotNull JsonObject json) {
        this.json = json;
    }

    /**
     * Get the name of this group.
     *
     * @return The name of the group.
     */
    @NotNull
    public String getName() {
        return this.json.get("name").getAsString();
    }

    /**
     * Recursively get the indexes of all elements in this group.
     *
     * @return The unmodifiable list of indexes.
     */
    @NotNull
    @UnmodifiableView
    public IntList getAllElements() {
        IntList list = new IntArrayList();
        list.addAll(this.getChildElements());
        for (ModelGroup childGroup : this.getChildGroups()) {
            list.addAll(childGroup.getAllElements());
        }
        return list;
    }

    /**
     * Get the indexes in the {@link Model} of the direct element children of this
     * group.
     *
     * @return The unmodifiable list of indexes.
     */
    @NotNull
    @UnmodifiableView
    public IntList getChildElements() {
        if (this.childElements == null) {
            JsonArray children = this.json.getAsJsonArray("children");
            this.childElements = new IntArrayList();
            for (JsonElement child : children) {
                if (child.isJsonPrimitive()) {
                    this.childElements.add(child.getAsInt());
                }
            }
        }
        return IntLists.unmodifiable(this.childElements);
    }

    /**
     * Get all direct child groups of this group.
     *
     * @return The unmodifiable list of groups.
     */
    @NotNull
    @UnmodifiableView
    public List<ModelGroup> getChildGroups() {
        if (this.childGroups == null) {
            JsonArray children = this.json.getAsJsonArray("children");
            this.childGroups = new ArrayList<>();
            for (JsonElement child : children) {
                if (child.isJsonObject()) {
                    ModelGroup childGroup = new ModelGroup(child.getAsJsonObject());
                    this.childGroups.add(childGroup);
                }
            }
        }
        return Collections.unmodifiableList(this.childGroups);
    }

    /**
     * Create a deep copy of this group and its children.
     *
     * @return The copy.
     */
    @NotNull
    public ModelGroup deepCopy() {
        return new ModelGroup(this.json.deepCopy());
    }
}
