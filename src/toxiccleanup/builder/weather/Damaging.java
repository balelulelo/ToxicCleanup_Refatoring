package toxiccleanup.builder.weather;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.builder.Damage;

/**
 * Indicates something can deal/generate 'damage'.
 */
public interface Damaging {
    public Damage getDamage(Dimensions dimensions, Positionable position);

    public Damage getDamage();
}
