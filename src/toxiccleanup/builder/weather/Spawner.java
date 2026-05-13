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
    /**
     * Creates a new {@link GameEntity} at the given position.
     *
     * <p>Requires: {@code position} must not be {@code null}.</p>
     * <p>Ensures: returns a non-null {@link GameEntity} at the given position.</p>
     *
     * @param position the position at which to spawn the new entity.
     * @return a new {@link GameEntity} at the given position.
     */
    public GameEntity spawn(Positionable position);
}
