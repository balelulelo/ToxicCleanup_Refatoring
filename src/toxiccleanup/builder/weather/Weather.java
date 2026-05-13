package toxiccleanup.builder.weather;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.builder.Tickable;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.ui.RenderableGroup;

/**
 * Interface for managing weather phenomena that exist in this game.
 *
 * @provided
 */
public interface Weather extends Tickable, RenderableGroup, Damaging {

    /**
     * Adds the given {@link WeatherSpawnPoint} to the weather system.
     *
     * @param spawnPoint the spawn point to register with this weather system.
     *
     */
    public void addSpawnPoint(WeatherSpawnPoint spawnPoint);

    /**
     * Adds a weather phenomenon {@link GameEntity} to the weather system.
     *
     * @param weather the weather phenomenon to add.
     */
    public void addWeather(GameEntity weather);

    /**
     * Returns whether the given tile position is currently obscured by a weather phenomenon.
     *
     * @param dimensions the screen and tile dimensions for pixel to tile conversion.
     * @param position   the position to check for obscuring weather.
     * @return {@code true} if an {@link Obscuring} phenomenon shares the given tile.
     */
    public boolean isObscuring(Dimensions dimensions, Positionable position);

    /**
     * Returns whether the given tile position is currently experiencing damaging weather.
     *
     * @param dimensions the screen and tile dimensions for pixel to tile conversion.
     * @param position   the position to check for damaging weather.
     * @return {@code true} if a {@link Damaging} phenomenon shares the given tile.
     */
    public boolean isDamaging(Dimensions dimensions, Positionable position);

    /**
     * Recieves the position of a lightning rod and adjusts the weather system accordingly.
     *
     * @param position position of the lightning rod that the weather should be adjusted for.
     */
    public void applyLightningRod(Positionable position);
}
