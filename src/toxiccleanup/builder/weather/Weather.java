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

    public void addSpawnPoint(WeatherSpawnPoint spawnPoint);

    public void addWeather(GameEntity weather);

    public boolean isObscuring(Dimensions dimensions, Positionable position);

    public boolean isDamaging(Dimensions dimensions, Positionable position);

    /**
     * Recieves the position of a lightning rod and adjusts the weather system accordingly.
     *
     * @param position - position of the lightning rod that the weather should be adjusted for.
     */
    public void applyLightningRod(Positionable position);
}
