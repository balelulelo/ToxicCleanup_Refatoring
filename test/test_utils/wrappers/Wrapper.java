package test_utils.wrappers;

/**
 * Abstract class representing a Wrapper to dynamically override or add functionality.
 * @param <T> Generic Type of Object being Wrapped.
 */
public abstract class Wrapper<T> {
    public T wrapped;

    /**
     * Constructor for a Wrapper that sets what Object is being Wrapped.
     * @param wrapped the Object to be wrapped.
     */
    Wrapper(T wrapped) {
        this.wrapped = wrapped;
    }

}
