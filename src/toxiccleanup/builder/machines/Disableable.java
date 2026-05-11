package toxiccleanup.builder.machines;

/**
 * Indicates the implementing object has a disabled and enabled state that can be changed.
 *
 * @provided
 */
public interface Disableable {
    /**
     * Returns if this disableable Object is or is not in its disabled state.
     *
     * @return if this disableable Object is or is not in its disabled state.
     */
    boolean isDisabled();

    /**
     * Sets the Disableable Object to it's disabled state.
     */
    void disable();

    /**
     * Sets the Disableable Object to its enabled state.
     */
    void enable();
}
