package ca.bkaw.praeter.core.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * A perspective where a {@link Model} can be displayed.
 *
 * @see ModelDisplay
 */
public enum Perspective {
    /** The model when displayed on the head of an entity. */
    HEAD,
    /** The model when displayed in a gui. */
    GUI,
    /** The model when displayed as a dropped item on the ground. */
    GROUND,
    /** The model when displayed in an item frame. */
    FIXED,
    /** The model when displayed in the first-person's right hand. */
    FIRSTPERSON_RIGHTHAND,
    /** The model when displayed in the first-person's left hand. */
    FIRSTPERSON_LEFTHAND,
    /** The model when displayed in the third-person's right hand. */
    THIRDPERSON_RIGHTHAND,
    /** The model when displayed in the third-person's left hand. */
    THIRDPERSON_LEFTHAND,
    ;

    private final String id;

    Perspective() {
        this.id = this.name().toLowerCase(Locale.ROOT);
    }

    /**
     * Return the id used in the json to identify this perspective.
     *
     * @return The id.
     */
    @NotNull
    public String id() {
        return this.id;
    }

    /**
     * Iterate through the first-person perspectives.
     *
     * @return The iterator.
     */
    public static Iterable<Perspective> firstPerson() {
        return List.of(FIRSTPERSON_LEFTHAND, FIRSTPERSON_RIGHTHAND);
    }

    /**
     * Iterate through the third-person perspectives.
     *
     * @return The iterator.
     */
    public static Iterable<Perspective> thirdPerson() {
        return List.of(THIRDPERSON_RIGHTHAND, THIRDPERSON_LEFTHAND);
    }

    /**
     * Iterate through the right-hand perspectives.
     *
     * @return The iterator.
     */
    public static Iterable<Perspective> rightHand() {
        return List.of(FIRSTPERSON_RIGHTHAND, THIRDPERSON_RIGHTHAND);
    }

    /**
     * Iterate through the left-hand perspectives.
     *
     * @return The iterator.
     */
    public static Iterable<Perspective> leftHand() {
        return List.of(FIRSTPERSON_LEFTHAND, THIRDPERSON_LEFTHAND);
    }
}
