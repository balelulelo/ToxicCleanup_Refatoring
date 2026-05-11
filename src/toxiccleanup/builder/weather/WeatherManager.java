package toxiccleanup.builder.weather;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.machines.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The concrete implementation of {@link Weather} for the
 * {@link toxiccleanup.builder.ToxicCleanup} game. {@link WeatherManager} is responsible for:
 *
 * <ul>
 *   <li>Holding all {@link WeatherSpawnPoint}s for the game.</li>
 *   <li>Holding all Weather Phenomena {@link GameEntity}s for the game.</li>
 *   <li>Handling the interaction between {@link LightningRod} and {@link Lightning}.</li>
 *   <li>
 *       Answering requests about the overall state of the weather system by other systems
 *       <p>i.e</p>
 *       <ul>
 *           <li>> is a particular location obscured.</li>
 *           <li>> is a particular location currently receiving damage from weather phenomena.</li>
 *       </ul>
 *   </li>
 *   <li>Ticking forward the internal state of all {@link WeatherSpawnPoint}s. </li>
 *   <li>Ticking forward the internal state of all weather {@link GameEntity}s. </li>
 * </ul>
 */
public class WeatherManager implements Weather {
    private final List<WeatherSpawnPoint> spawnPoints = new ArrayList<>();
    private final List<GameEntity> phenomena = new ArrayList<>();

    public WeatherManager() {
    }

    /**
     * Add the given spawnPoint to the weather manager for it to handle ticking it and
     * any other game logic.
     *
     * @param spawnPoint - spawn point we wish top use
     */
    public void addSpawnPoint(WeatherSpawnPoint spawnPoint) {
        spawnPoints.add(spawnPoint);
    }

    /**
     * Adds a GameEntity to be managed by the WeatherManager.
     *
     * @param weather - GameEntity instance of a weather Phenomenon.
     */
    public void addWeather(GameEntity weather) {
        phenomena.add(weather);
    }

    /**
     * Return if the given title location should be currently obscured by the
     * internal weather system.
     *
     * @param dimensions - screen and tile dimensions
     * @param position   - position requesting for the obscured status of
     * @return if the given title location should be currently obscured by the
     * internal weather system.
     */
    @Override
    public boolean isObscuring(Dimensions dimensions, Positionable position) {
        //work out the grid we are checking against
        int gridX = dimensions.pixelToTile(position.getX());
        int gridY = dimensions.pixelToTile(position.getY());

        for (GameEntity weather : phenomena) {
            final int weatherGridX = dimensions.pixelToTile(weather.getX());
            final int weatherGridY = dimensions.pixelToTile(weather.getY());

            if (gridX == weatherGridX && gridY == weatherGridY && weather instanceof Obscuring) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return {@link Damage} the given tile location is currently experiencing otherwise
     * returns null.
     *
     * @param dimensions - screen and tile dimensions
     * @param position   - position requesting for the damage status of
     * @return {@link Damage} the given tile location is currently experiencing otherwise
     * returns null.
     */
    public Damage getDamage(Dimensions dimensions, Positionable position) {
        //work out the grid we are checking against
        int gridX = dimensions.pixelToTile(position.getX());
        int gridY = dimensions.pixelToTile(position.getY());

        for (GameEntity weather : phenomena) {
            final int weatherGridX = dimensions.pixelToTile(weather.getX());
            final int weatherGridY = dimensions.pixelToTile(weather.getY());

            if (gridX == weatherGridX && gridY == weatherGridY && weather instanceof Damaging) {
                return ((Damaging) weather).getDamage();
            }
        }
        return null;
    }

    @Override
    public Damage getDamage() {
        return null;
    }

    /**
     * Return if the given tile location is experiencing damaging conditions.
     *
     * @param dimensions - screen and tile dimensions
     * @param position   - position requesting for the damage status of
     * @return if the given tile location is experiencing damaging conditions.
     */
    @Override
    public boolean isDamaging(Dimensions dimensions, Positionable position) {
        //work out the grid we are checking against
        int gridX = dimensions.pixelToTile(position.getX());
        int gridY = dimensions.pixelToTile(position.getY());

        for (GameEntity weather : phenomena) {
            final int weatherGridX = dimensions.pixelToTile(weather.getX());
            final int weatherGridY = dimensions.pixelToTile(weather.getY());

            if (gridX == weatherGridX && gridY == weatherGridY && weather instanceof Damaging) {
                return true;
            }
        }
        return false;
    }

    /**
     * Receives the position of a {@link LightningRod} and adjusts the weather system accordingly.
     * Moves any {@link Lightning} that are within the radius {@value LightningRod#RADIUS}
     * of the given position to the given position.
     *
     * @param position - position of the lightning rod that the weather should be adjusted for.
     */
    @Override
    public void applyLightningRod(Positionable position) {
        for (GameEntity weather : phenomena) {
            if (weather instanceof Lightning) {
                final Lightning bolt = (Lightning) weather;
                int deltaX = position.getX() - bolt.getX();
                int deltaY = position.getY() - bolt.getY();
                final int distance = (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                if (distance <= LightningRod.RADIUS) {
                    bolt.setX(position.getX());
                    bolt.setY(position.getY());
                }
            }
        }

    }

    /**
     * Advances component state by one game tick using engine and game context.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        for (WeatherSpawnPoint spawnPoint : spawnPoints) {
            spawnPoint.tick(state, game);
        }
        for (GameEntity weather : phenomena) {
            weather.tick(state, game);
        }
        cleanup();
    }

    /**
     * A collection of renderables that should each be displayed.
     *
     * @return A collection of renderables to display.
     */
    @Override
    public List<Renderable> render() {
        final ArrayList<Renderable> renderables = new ArrayList<>();
        renderables.addAll(phenomena);
        return renderables;
    }

    /**
     * Cleanup and removes any weather phenomena that have been marked for removal.
     */
    private void cleanup() {
        for (int i = phenomena.size() - 1; i >= 0; i -= 1) {
            if (phenomena.get(i).isMarkedForRemoval()) {
                phenomena.remove(i);
            }
        }
    }

    /**
     * Generates a simplified {@link String} representation of the WeatherManagers internal state.
     *
     * @return a simplified {@link String} representation of the WeatherManagers internal state.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("WeatherManager:[\n");
        sb.append("Phenomena:" + phenomena.size() + "\n");
        sb.append("SpawnPoints:" + spawnPoints.size() + "\n");
        sb.append("]\n");
        return sb.toString();
    }

}
