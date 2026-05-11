package toxiccleanup.builder.weather;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.builder.entities.GameEntity;

/**
 * Functional Interface to define a lambda that takes a {@link Positionable}
 * and returns a {@link GameEntity}.
 * Intended for use in systems that wish to regularly spawn various kinds of new {@link GameEntity}s
 * at given positions.
 */
@FunctionalInterface
public interface Spawner {
    public GameEntity spawn(Positionable position);
}
