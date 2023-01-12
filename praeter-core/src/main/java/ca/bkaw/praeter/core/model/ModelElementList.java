package ca.bkaw.praeter.core.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list of {@link ModelElement}s.
 */
public class ModelElementList {
    private final JsonArray json;
    private final List<ModelElement> elements;

    public ModelElementList(@NotNull JsonArray json) {
        this.json = json;
        this.elements = new ArrayList<>(json.size());
        for (int i = 0; i < json.size(); i++) {
            this.elements.add(null);
        }
    }

    /**
     * Move the elements by the specified vector.
     *
     * @param vector The vector with amount to move the elements by.
     */
    public void move(@NotNull Vector vector) {
        for (ModelElement element : this.getElements()) {
            element.move(vector);
        }
    }

    /**
     * Scale all the elements in the list around the specified origin.
     *
     * @param scale The factor to scale by.
     * @param origin The origin to scale around.
     */
    public void scale(@NotNull Vector scale, @NotNull Vector origin) {
        for (ModelElement element : this.getElements()) {
            element.scale(scale, origin);
        }
    }

    /**
     * Get the middle point of this list of element.
     *
     * @return The middle point.
     */
    @NotNull
    public Vector getMiddle() {
        Vector min = new Vector(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        Vector max = new Vector(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        for (ModelElement element : this.elements) {
            Vector elementMiddle = element.getMiddle();
            if (elementMiddle.getX() > min.getX()) min.setX(elementMiddle.getX());
            if (elementMiddle.getY() > min.getY()) min.setY(elementMiddle.getY());
            if (elementMiddle.getZ() > min.getZ()) min.setZ(elementMiddle.getZ());
            if (elementMiddle.getX() < max.getX()) max.setX(elementMiddle.getX());
            if (elementMiddle.getY() < max.getY()) max.setY(elementMiddle.getY());
            if (elementMiddle.getZ() < max.getZ()) max.setZ(elementMiddle.getZ());
        }
        return min.midpoint(max);
    }

    /**
     * Center this list of elements at the origin (8, 8, 8) and return the vector that
     * the elements were moved by.
     * <p>
     * Not to be confused with {@link #getMiddle()}.
     *
     * @return The vector elements were moved by.
     */
    @NotNull
    public Vector center() {
        Vector middle = this.getMiddle();
        Vector diff = new Vector(8, 8, 8).subtract(middle);
        this.move(diff);
        return diff;
    }

    /**
     * Get the {@link ModelElement} at the index.
     *
     * @param index The index.
     * @return The model element.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    @NotNull
    public ModelElement getElement(int index) {
        ModelElement element = this.elements.get(index);
        if (element == null) {
            JsonObject json = this.json.get(index).getAsJsonObject();
            element = new ModelElement(json);
            this.elements.set(index, element);
        }
        return element;
    }

    /**
     * Return an unmodifiable view of the list of wrapped elements.
     *
     * @return The list.
     */
    @NotNull
    @UnmodifiableView
    public List<ModelElement> getElements() {
        for (int i = 0; i < this.elements.size(); i++) {
            if (this.elements.get(i) == null) {
                // Ensure the element has been wrapped
                this.getElement(i);
            }
        }
        return Collections.unmodifiableList(this.elements);
    }

    /**
     * Add an element to the list.
     *
     * @param element The wrapper around the element to add.
     */
    public void add(@NotNull ModelElement element) {
        this.json.add(element.getJson());
        this.elements.add(element);
    }

    /**
     * Get the json array of elements this list is wrapping.
     *
     * @return The json.
     */
    @NotNull
    public JsonArray getJson() {
        return this.json;
    }

    /**
     * Create a deep copy of this list and its elements.
     *
     * @return The copy.
     */
    @NotNull
    public ModelElementList deepCopy() {
        return new ModelElementList(this.json.deepCopy());
    }
}
