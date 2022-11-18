package ca.bkaw.praeter.core.resources;

import org.jetbrains.annotations.NotNull;

/**
 * A holder for packs.
 *
 * @param <T> The type of the pack.
 */
public class PacksHolder<T> {
    private final T main;

    public PacksHolder(T main) {
        this.main = main;
    }

    /**
     * Get the main pack where all plugin assets are included.
     *
     * @return The main pack.
     */
    public T getMain() {
        return this.main;
    }

    /**
     * Get the id of the pack.
     * <p>
     * All packs available in the {@link PacksHolder} will have ids.
     *
     * @param pack The pack.
     * @return The id.
     * @throws IllegalArgumentException If the pack does not have an id.
     */
    @NotNull
    public String getId(T pack) {
        if (pack == this.main) {
            return "main";
        }
        throw new IllegalArgumentException("The specified pack does not have an id.");
    }
}
