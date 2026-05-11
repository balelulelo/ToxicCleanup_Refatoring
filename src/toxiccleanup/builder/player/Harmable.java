package toxiccleanup.builder.player;

import toxiccleanup.builder.machines.Adjustable;

/**
 * Indicates the implementing class has a Hp score that can be adjusted as per {@link Adjustable},
 * has a current hp that can be accessed via {@link Harmable#getHp()} and a max hp that can
 * be accessed via {@link Harmable#getMaxHp()}.
 *
 * @provided
 */
public interface Harmable extends Adjustable {

    /**
     * Returns how much hp this {@link Harmable} currently has.
     *
     * @return how much hp this {@link Harmable} currently has.
     */
    int getHp();

    /**
     * Returns how much max hp this {@link Harmable} currently has.
     *
     * @return how much max hp this {@link Harmable} currently has.
     */
    int getMaxHp();
}
