package toxiccleanup.builder.machines;

import toxiccleanup.builder.Damage;

/**
 * Class for managing a damaged state, currently wraps a basic boolean
 * but other implementations could wrap around a more sophisticated health systems in the future.
 *
 * @provided
 */
public class DamageHandler implements Damageable {
    private boolean damaged = false;

    public DamageHandler() {
    }

    /**
     * Returns if this damageable Object is or is not in its damaged state.
     *
     * @return if this damageable Object is or is not in its damaged state.
     */
    @Override
    public boolean isDamaged() {
        return this.damaged;
    }

    /**
     * Sets the Damageable Object to it's damaged state.
     */
    @Override
    public void setDamage(Damage dmg) {
        this.damaged = true;
    }

    /**
     * Sets the Damageable Object to it's undamaged
     */
    @Override
    public void repairDamage() {
        this.damaged = false;
    }
}
