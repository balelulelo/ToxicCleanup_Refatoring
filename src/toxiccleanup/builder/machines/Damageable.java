package toxiccleanup.builder.machines;

import toxiccleanup.builder.Damage;

/**
 * Indicates the implementing object has a damaged and undamaged state that can be changed.
 *
 * @provided
 */
public interface Damageable {

    /**
     * Returns if this damageable Object is or is not in its damaged state.
     *
     * @return if this damageable Object is or is not in its damaged state.
     */
    boolean isDamaged();

    /**
     * Sets the Damageable Object to it's damaged state.
     *
     * <p>Ensures: {@code isDamaged()} returns {@code true} after this call.</p>
     *
     * @param dmg the {@link Damage} instance that caused this object to become damaged.
     */
    void setDamage(Damage dmg);

    /**
     * Sets the Damageable Object to it's undamaged
     *
     * <p>Ensures: {@code isDamaged()} returns {@code false} after this call.</p>
     */
    void repairDamage();
}
