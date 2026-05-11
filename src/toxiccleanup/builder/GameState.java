package toxiccleanup.builder;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.machines.Machines;
import toxiccleanup.builder.player.Player;
import toxiccleanup.builder.weather.Weather;
import toxiccleanup.builder.world.World;

/**
 * An interface to the game state information, including world, player and machines.
 *
 * @provided
 */
public interface GameState {
    /**
     * Returns the current state of the game world.
     *
     * <p>The returned world is mutable, that is, calling mutator methods such as {@link
     * World#place(Tile)} will modify the world.
     *
     * @return The game world.
     */
    World getWorld();

    /**
     * Returns the current state of the player. Useful for retrieving the player's location.
     *
     * @return the current player.
     */
    Player getPlayer();


    /**
     * Returns the current state of the power system and access to methods for creating machines
     * when sufficient power is available. Useful for querying current power, building new machines, and checking machine-related state.
     *
     * @return the current state of the power system and access to methods for creating machines
     * when sufficient power is available
     */
    Machines getMachines();

    /**
     * Returns the current state of the weather system and access to methods for add Weather phenomena.
     *
     * @return the current state of the weather system and access to methods for add Weather phenomena.
     */
    Weather getWeather();
}
