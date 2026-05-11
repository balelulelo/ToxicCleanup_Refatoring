package toxiccleanup.builder.machines;

/**
 * Indicates the implementing object has a key adjustable value, used for things such as power,
 * hp etc.
 * See also {@link Machines}, {@link toxiccleanup.builder.player.Harmable} for examples of intended use
 * in other interfaces.
 *
 * @provided
 */
public interface Adjustable {

    /**
     * Adjusts a key internal adjustable value by the given amount.
     * Each implementation may decide how it intends to use the amount for its adjustment.
     *
     * @param amount amount we wish to adjust value by.
     */
    void adjust(int amount);
}
