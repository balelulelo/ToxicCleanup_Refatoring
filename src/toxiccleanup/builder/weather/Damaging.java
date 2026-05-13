package toxiccleanup.builder.weather;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.builder.Damage;

/**
 * Indicates something can deal/generate 'damage'.
 */
public interface Damaging {
    /**
     * Returns the {@link Damage} this object deals at the given tile position,
     * or {@code null} if it does not deal damage at that location.
     *
     * @param dimensions the screen and tile dimensions for pixel to tile conversion.
     * @param position   the position to check for damage.
     * @return a {@link Damage} instance, or {@code null}.
     */
    public Damage getDamage(Dimensions dimensions, Positionable position);

    /**
     * Returns the {@link Damage} this object currently deals, or {@code null}
     * if it is not currently in a damage-dealing state.
     *
     * @return a {@link Damage} instance, or {@code null}.
     */
    public Damage getDamage();
}
