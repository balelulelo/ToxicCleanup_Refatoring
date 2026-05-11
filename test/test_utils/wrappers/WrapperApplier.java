package test_utils.wrappers;

import java.util.Arrays;
import java.util.List;

public interface WrapperApplier <T> {

    Wrapper<T> wrap(T object);

    default List<Wrapper<T>> wrap(List<T> objects) {
        return objects.stream().map(this::wrap).toList();
    }

    @SuppressWarnings("unchecked")
    default Wrapper<T>[] wrap(T[] objects) {
        return (Wrapper<T>[]) Arrays.stream(objects).map(this::wrap).toArray();
    }

}
