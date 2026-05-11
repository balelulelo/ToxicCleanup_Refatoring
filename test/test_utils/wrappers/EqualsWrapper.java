package test_utils.wrappers;

import java.util.function.BiFunction;

/**
 * Custom wrapping class that adds an equals method using a custom comparison function.
 */
public class EqualsWrapper<T> extends DefaultsWrapper<T> {

    private final BiFunction<T, T, Boolean> comparisonFunction;

    public EqualsWrapper(T data, BiFunction<T, T, Boolean> comparisonFunction) {
        super(data);
        this.comparisonFunction = comparisonFunction;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (this.wrapped.getClass() == obj.getClass()) {
            // The other is the same as out subtype. Even though not wrapped we can still compare.
            if (this.wrapped == obj) {
                return true;
            }
            return comparisonFunction.apply(this.wrapped, (T) obj);
        }

        if (this.getClass() == obj.getClass()) {
            // We are both wrapped, lets see if we can compare
            if (this.wrapped.getClass() == ((Wrapper<?>) obj).wrapped.getClass()) {
                // Both our subObjects are the same type, lets run our compare.
                return comparisonFunction.apply(this.wrapped, ((Wrapper<T>) obj).wrapped);
            }
        }
        return false;
    }
}


