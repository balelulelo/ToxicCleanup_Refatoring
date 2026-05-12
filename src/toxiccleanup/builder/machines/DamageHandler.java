package toxiccleanup.builder.machines;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.weather.Weather;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;

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
     * Checks whether the given position is currently receiving damage from the weather system.
     * applies that damage to this handler if the given position is indeed receiving damage.
     *
     * <p>This method combines the repeated damage check pattern used in {@code tick()}
     * methods of {@link SolarPanel} and {@link Teleporter} into a single reusable
     * method call. This method avoid duplicate implementations across those classes.</p>
     *
     * <p>Note: purposely avoided modifying {@link Pump} damage check because the spec mentioned that
     * we are not supposed to refactor {@link Pump}</p>
     *
     * <p>Requires: {@code weather}, {@code dimensions}, and {@code position} must not be
     * {@code null}.</p>
     * <p>Ensures: if the weather system returns a {@link Damage} for the given
     * position, {@code isDamaged()} will return {@code true} at the end.</p>
     *
     * @param weather    the current weather system to query for damage.
     * @param dimensions the screen and tile dimensions used for pixel-to-tile conversion.
     * @param position   the position of the machine to check for incoming damage.
     * @return {@code true} if this the machine in the given position is in the damaged state after the check,
     *          otherwise return {@code false}.
     */

    public boolean checkAndApplyDamage(Weather weather, Dimensions dimensions, Positionable position){
        Damage dmg = weather.getDamage(dimensions, position);
        if (dmg != null){
            setDamage(dmg);
        }
        return isDamaged();
    }

    /**
     * Returns whether this damageable Object is currently in its damaged state or not.
     *
     * @return {@code true} if this damageable Object is damaged. otherwise return {@code false}.
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
     * Resets the damageable object into the Undamaged state
     */
    @Override
    public void repairDamage() {
        this.damaged = false;
    }
}
