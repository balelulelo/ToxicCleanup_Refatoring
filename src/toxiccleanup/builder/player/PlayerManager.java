package toxiccleanup.builder.player;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.world.World;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Direction;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;

import java.util.List;

/**
 * Manages all aspects of the player's state and behaviour during the game. This class:
 *
 * <ul>
 *   <li>Holds and controls the {@link Cleaner} entity that is rendered on screen.</li>
 *   <li>Tracks the player's current HP (starts at 10, clamped to [0, 10]) and exposes it
 *       via the {@link Harmable} interface.</li>
 *   <li>Each tick, reads keyboard input (WASD) to move the player one tile per movement
 *       timer interval, updates the facing sprite accordingly, and enforces window boundaries.</li>
 *   <li>After moving, determines which tiles are currently under the player and calls
 *       {@link toxiccleanup.builder.entities.PlayerOverHook#playerOver} on each of them so tiles can react
 *       (e.g. a {@link toxiccleanup.builder.entities.tiles.Chasm} deals damage, a
 *       {@link toxiccleanup.builder.entities.tiles.Dirt} listens for build input).</li>
 *   <li>When the player's HP reaches 0, the dead sprite is shown and movement/interaction stops.</li>
 * </ul>
 *
 * @hint The player manager should hold an instance of {@link Cleaner}.
 * @multistage
 */
public class PlayerManager implements Player {
    private static final int MAX_HP = 10;
    private static final int SPEED = 1;
    private final Cleaner player;
    //used to track if players recently moved
    private final TickTimer movementTimer = new RepeatingTimer(10);
    private int hp;

    /**
     * Constructs a new {@link PlayerManager}, creating an internal {@link Cleaner} entity at
     * the given pixel position and initialising the player's HP to the maximum (10).
     *
     * @param position the pixel position at which to spawn the {@link Cleaner} entity.
     * @requires the position to be a valid position within the game window
     * (x and y &ge; 0 and &le; window size).
     *
     * @provided
     */
    public PlayerManager(Positionable position) {
        super();
        player = new Cleaner(position);
        hp = MAX_HP;
    }

    /**
     * Advances player state for one game tick.
     *
     * <p>The managed player entity is ticked first.</p>
     *
     * <p>If the player is alive and one or more movement keys are pressed, the player moves by
     * exactly one tile in a single direction. The movement keys are:</p>
     *
     * <table>
     * <caption>&nbsp;</caption>
     * <tr><th>Key</th><th>Direction</th></tr>
     * <tr><td>w</td><td>NORTH</td></tr>
     * <tr><td>s</td><td>SOUTH</td></tr>
     * <tr><td>a</td><td>WEST</td></tr>
     * <tr><td>d</td><td>EAST</td></tr>
     * </table>
     *
     * <p>If multiple movement keys are pressed, only one movement is applied. The priority order is
     * {@code w}, then {@code s}, then {@code a}, then {@code d}.</p>
     *
     * <p>After movement, this method processes interactions with any tiles currently overlapping the
     * player's position by invoking their player-over behaviour.</p>
     *
     * <p>If the player is not alive, the player does not move, and the dead sprite is shown.</p>
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Starter code only; The method signature is provided without a body.
     *
     * Movement is performed as a one-tile step
     * (equivalent to {@code player.move(direction, 1)} when that helper is available). Boundaries should have a half
     * tile offset for visual reasons, so the player can only move to half a tile before the edge of the window.
     *
     *  <p>Read movement input using {@code state.getKeys().isDown(char)}
     *  (for {@code 'w'}, {@code 's'}, {@code 'a'}, {@code 'd'}).</p>
     *
     * @param state the current state of the engine.
     * @param game  the current state of the game.
     * @ensures player moves within the game window boundaries, and only if alive.
     *
     * @provided
     */
    @Override
    public void tick(EngineState state, GameState game) {
        player.tick(state, game);
        if (!isAlive()) {
            player.setDeadSprite();
        }
        useControls(state, game);
    }

    /**
     * Returns whether the player is currently alive. Used by {@link toxiccleanup.builder.ToxicCleanup#tick}
     * to decide whether to display the game-over screen and stop gameplay, and by
     * {@link #tick} to decide whether to show the dead sprite and skip movement.
     *
     * @return {@code true} if the player's HP is greater than 0; {@code false} if HP is 0.
     *
     * @provided
     */
    public boolean isAlive() {
        return hp > 0;
    }

    /**
     * Returns the current pixel position of the {@link Cleaner} entity as a new
     * {@link Position} snapshot. Used by {@link toxiccleanup.builder.world.ToxicWorld} to
     * determine which tiles are currently under the player, and by other components that
     * need to know where the player is.
     *
     * @return a new {@link Positionable} containing the player's current x and y coordinates.
     * @provided
     */
    public Positionable getPosition() {
        return new Position(this.player.getX(), this.player.getY());
    }

    /**
     * Moves the internal {@link Cleaner} entity to the given pixel position by directly setting
     * its x and y coordinates. Used by {@link toxiccleanup.builder.machines.Teleporter#playerOver} to
     * teleport the player to a new location instantly.
     *
     * @param mockPosition the x and y pixel coordinates to move the player to.
     * @provided
     */
    public void setPosition(Positionable mockPosition) {
        this.player.setX(mockPosition.getX());
        this.player.setY(mockPosition.getY());
    }

    private void useControls(EngineState state, GameState game) {
        movementTimer.tick();
        if (!isAlive()) {
            return;
        }
        final World world = game.getWorld();
        Direction direction = null;
        if (state.getKeys().isDown('w')) {
            direction = Direction.NORTH;
        } else if (state.getKeys().isDown('s')) {
            direction = Direction.SOUTH;
        } else if (state.getKeys().isDown('a')) {
            direction = Direction.WEST;
        } else if (state.getKeys().isDown('d')) {
            direction = Direction.EAST;
        }
        if (direction != null) {
            if (movementTimer.isFinished()) {
                player.move(direction, SPEED * state.getDimensions().tileSize());
            }
            enforceBoundaries(state.getDimensions());
        }

        final List<Tile> underPlayer = world.tilesAtPosition(getPosition(), state.getDimensions());

        handlePlayerOver(state, game, underPlayer);
    }

    /**
     * Takes a given {@link Dimensions} and enforces the {@link Player}s position to stay
     * within those bounds.
     * Adjusted by half a tile for visual offsetting.
     *
     * @param dimensions - dimensions we want to keep the player within
     *
     *
     */
    private void enforceBoundaries(Dimensions dimensions) {
        final int offset = dimensions.tileSize() / 2;
        final int effectiveWindowSize = dimensions.windowSize() - offset;

        //X Axis enforcement
        if (player.getX() > effectiveWindowSize) {
            player.setX(effectiveWindowSize);
        } else if (player.getX() < offset) {
            player.setX(offset);
        }

        //Y Axis enforcement
        if (player.getY() > effectiveWindowSize) {
            player.setY(effectiveWindowSize);
        } else if (player.getY() < offset) {
            player.setY(offset);
        }
    }

    /**
     * Returns the player's current HP. HP starts at 10 and decreases when the player
     * stands on a {@link toxiccleanup.builder.entities.tiles.Chasm} or when the game's periodic damage
     * timer fires. HP is always in the range [0, {@link #getMaxHp()}].
     *
     * @return the current HP value.
     * @provided
     */
    public int getHp() {
        return hp;
    }

    /**
     * Returns the player's maximum HP. The player starts with this value and cannot exceed it.
     * The number of {@link toxiccleanup.builder.ui.Heart} icons shown in the HUD equals this value when
     * the player is at full health.
     *
     * @return {@code 10}, the maximum HP the player can have.
     * @provided
     */
    public int getMaxHp() {
        return PlayerManager.MAX_HP;
    }


    /**
     * Takes a given engine state, game state, and list of {@link Tile}s that are currently
     * under the player to trigger their
     * {@link toxiccleanup.builder.entities.PlayerOverHook#playerOver(EngineState, GameState)}.
     *
     * @param state       current engine state, used for keys and mouse interactions
     * @param game        current game state, used for retrieving, player, world or machine
     *                    related state.
     * @param underPlayer current tiles determined to be under the players current location
     *
     */
    private void handlePlayerOver(EngineState state, GameState game, List<Tile> underPlayer) {
        for (Tile tile : underPlayer) {
            tile.playerOver(state, game);
        }
    }

    /**
     * Returns the renderables that represent the player for the current frame. The player
     * manager only renders the {@link Cleaner} entity itself - a single-element list containing
     * the cleaner, which the engine draws at the cleaner's current pixel position.
     *
     * @return a single-element list containing the {@link Cleaner} entity to be rendered.
     *
     * @provided
     */
    @Override
    public List<Renderable> render() {
        return List.of(player);
    }

    /**
     *
     *
     * Subtracts the given amount from the player's HP score, then clamps the result to the
     * range [0, {@link #getMaxHp()}]. A positive {@code amount} causes damage; the interface
     * convention (from {@link toxiccleanup.builder.machines.Adjustable}) uses positive values to reduce HP.
     * HP cannot go below 0 or above the maximum.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Starter code only; The method signature is provided without a body.
     *
     * @param amount amount to subtract from the player's HP (positive = damage).
     * @provided
     */
    @Override
    public void adjust(int amount) {
        hp -= amount;
        hp = Math.clamp(hp, 0, MAX_HP);
    }
}
