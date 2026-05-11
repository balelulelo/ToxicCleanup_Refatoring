package test_utils.wrappers;

import java.util.function.Function;

public class ToStringWrapperApplier<T> implements WrapperApplier <T> {

    Function<T, String> stringFunction;

    public ToStringWrapperApplier(Function<T, String> stringFunction) {
        this.stringFunction = stringFunction;
    }

    @Override
    public ToStringWrapper<T> wrap(T object){
        return new ToStringWrapper<>(object, this.stringFunction);
    }
}
