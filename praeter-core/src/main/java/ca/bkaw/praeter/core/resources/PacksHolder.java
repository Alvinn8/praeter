package ca.bkaw.praeter.core.resources;

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
}
