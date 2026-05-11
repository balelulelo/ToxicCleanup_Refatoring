package test_utils.wrappers;

import java.util.function.BiFunction;

public class EqualsWrapperApplier <T> implements WrapperApplier <T> {

    BiFunction<T, T, Boolean> comparisonFunction;

    public EqualsWrapperApplier(BiFunction<T, T, Boolean> comparisonFunction) {
        this.comparisonFunction = comparisonFunction;
    }

    @Override
    public EqualsWrapper<T> wrap(T object){
        return new EqualsWrapper<>(object, this.comparisonFunction);
    }
}
