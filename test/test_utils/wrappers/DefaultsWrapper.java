package test_utils.wrappers;

/**
 * Abstract class representing a Wrapper to dynamically override or add functionality.
 * This defaults wrapper overrides the toString(), equals(), and hashCode() methods to use
 * those of the wrapped Object, with the addition of attempting to unwrap in {@link DefaultsWrapper#equals(Object)}.
 * DefaultWrappers make it easier to chain wrappers together without overriding these methods back to {@link Object} default.
 * @param <T> Generic Type of Object being Wrapped.
 */
public abstract class DefaultsWrapper<T> extends Wrapper <T> {

    /**
     * Constructor for a DefaultsWrapper that sets what Object is being Wrapped.
     * @param wrapped the Object to be wrapped.
     */
    DefaultsWrapper(T wrapped) {
        super(wrapped);
    }

    /**
     * @return toString() of wrapped object.
     */
    @Override
    public String toString() {
        return wrapped.toString();
    }

    /**
     * Indicates whether some other object is "equal to" the wrapped object.
     * Either directly compares the wrapped object to other, or if other is wrapped,
     * unwraps it and then compares wrapped.
     * @param obj other object to compare.
     * @return iff the wrapped contents is equal.
     */
    @Override
    public boolean equals(Object obj) {
        // If we have a wrapper that contains the same element as us, check if the wrapped are equal.
        if (obj instanceof Wrapper<?> wrap) {
            return this.wrapped.equals(wrap.wrapped);
        }

        return this.wrapped.equals(obj);
    }

    /**
     * @return hashCode() of wrapped object.
     */
    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }
}
