package toxiccleanup.builder;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.machines.Machines;
import toxiccleanup.builder.player.Player;
import toxiccleanup.builder.player.PlayerManager;
import toxiccleanup.builder.weather.Weather;
import toxiccleanup.builder.world.World;

/**
 * The {@link ToxicCleanup}-specific implementation of the {@link GameState} interface.
 *
 * <p>This class bundles together the three core components of the game (the {@link World},
 * the {@link PlayerManager}, and the {@link Machines} system)
 * into a single snapshot that is passed to every {@link Tickable#tick} call each frame.
 * Components that receive a {@link GameState} can use it to read or modify the world, query
 * the player's position and HP, and interact with the power and machine system.
 *
 * @multistage
 */
public class ToxicCleanupGameState implements GameState {
    private final World world;
    private final PlayerManager player;
    private final Machines machines;
    private final Weather weather;


    /**
     * Constructs a new {@link ToxicCleanupGameState} wrapping the three core game components.
     * A new instance is created each tick in {@link ToxicCleanup#tick} so that
     * {@link Tickable} components always receive an up-to-date view of the game.
     *
     * @param world    the current game world, used to query and modify tiles.
     * @param player   the player manager, used to query position, HP, and move the player.
     * @param machines the machine manager, used to query power and spawn machines.
     */

    public ToxicCleanupGameState(World world, PlayerManager player, Machines machines, Weather weather) {
        this.world = world;
        this.player = player;
        this.machines = machines;
        this.weather = weather;
    }

    /**
     * Constructs a new {@link ToxicCleanupGameState} wrapping only the player manager.
     * Use this constructor when only player-related state is needed and world or machine
     * access is not required.
     *
     * @param player the player manager, used to query position, HP, and move the player.
     */
    public ToxicCleanupGameState(PlayerManager player) {
        this.world = null;
        this.player = player;
        this.machines = null;
        this.weather = null;
    }

    /**
     * Returns the current state of the game world.
     *
     * <p>The returned world is mutable, that is, calling mutator methods such as {@link
     * World#place(Tile)} will modify the world.
     *
     * @return The game world.
     */
    @Override
    public World getWorld() {
        return world;
    }

    /**
     * Returns the current state of the player. Useful for retrieving the player's location.
     *
     * @return The player of the game.
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the current state of the machine system.
     *
     * @return the {@link Machines} instance, providing access to machine spawning, teleporter
     * locations, and the power system.
     */
    @Override
    public Machines getMachines() {
        return machines;
    }

    /**
     * Returns the current state of the weather system and access to methods for add Weather phenomena.
     *
     * @return the current state of the weather system and access to methods for add Weather phenomena.
     */
    @Override
    public Weather getWeather() {
        return weather;
    }
}
