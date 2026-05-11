package test_utils.wrappers;

import java.util.function.Function;

public class ToStringWrapper <T> extends DefaultsWrapper<T>{

    private final Function<T, String> stringFunction;

    ToStringWrapper(T wrapped, Function<T, String> stringFunction) {
        super(wrapped);
        this.stringFunction = stringFunction;
    }

    @Override
    public String toString() {
        return stringFunction.apply(wrapped);
    }
}
