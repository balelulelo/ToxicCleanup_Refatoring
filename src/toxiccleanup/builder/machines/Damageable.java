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
     */
    void setDamage(Damage dmg);

    /**
     * Sets the Damageable Object to it's undamaged
     */
    void repairDamage();
}
