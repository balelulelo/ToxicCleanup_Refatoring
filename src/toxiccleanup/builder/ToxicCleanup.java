package toxiccleanup.builder;

import toxiccleanup.builder.entities.tiles.Dirt;
import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.entities.tiles.ToxicField;
import toxiccleanup.builder.machines.LightningRod;
import toxiccleanup.builder.machines.MachinesManager;
import toxiccleanup.builder.machines.Teleporter;
import toxiccleanup.builder.player.PlayerManager;
import toxiccleanup.builder.weather.*;
import toxiccleanup.builder.ui.GuiManager;
import toxiccleanup.builder.world.ToxicWorld;
import toxiccleanup.builder.world.WorldBuilder;
import toxiccleanup.builder.world.WeatherBuilder;
import toxiccleanup.builder.world.WorldLoadException;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Game;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.timing.RepeatingTimer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ToxicCleanup} is a tile-based game in which the player cleans up toxic waste fields
 * using machines powered by solar panels and transported via teleporters.
 *
 * <p>The game ends when either all toxic fields are cleared (win) or the player's HP reaches
 * zero (lose). Player HP is periodically reduced while any toxic fields remain.
 *
 * @multistage
 */
public class ToxicCleanup implements Game {
    private static final int DAMAGE_INTERVAL = 1800; // 1 HP every 30 seconds at 60 ticks/s
    private final PlayerManager playerManager;
    private final MachinesManager machineManager;
    private final GuiManager guiManager;
    private final ToxicWorld world;
    private final Weather weather;
    private final RepeatingTimer damageTimer = new RepeatingTimer(DAMAGE_INTERVAL);

    public ToxicCleanup(Dimensions dimensions, Positionable starterTeleporterPosition, String worldFilepath, String weatherFilepath) throws IOException, WorldLoadException {
        final int halfTileOffset = dimensions.tileSize() / 2;
        final int playerX = dimensions.tileSize() + halfTileOffset;
        final int playerY = dimensions.tileSize() + halfTileOffset;

        this.playerManager = new PlayerManager(new Position(playerX, playerY));

        this.world = WorldBuilder.fromFile(dimensions, worldFilepath);
        this.machineManager = new MachinesManager(7);
        this.guiManager = new GuiManager();
        this.weather = WeatherBuilder.fromFile(dimensions, weatherFilepath);
        this.spawnTeleporter(starterTeleporterPosition, dimensions);
    }

    /**
     * Constructs an instance of {@link ToxicCleanup} using default settings
     * for player location, map loading, and starting power, and spawns a starter
     * {@link Teleporter} at the given position.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Calculates the player's starting pixel
     * position based on tile coordinates (5, 5) and initialises the
     * {@link PlayerManager} with that position.
     *
     * <p><span style="color:#2E75B2;">Stage 1:</span> Loads the game world from the map file using
     * {@link WorldBuilder#fromFile} with the given dimensions.
     *
     * <p><span style="color:#F5D000;">Stage 2:</span> Initialises the {@link MachinesManager} and {@link GuiManager}.
     *
     * <p><span style="color:#D02F83;">Stage 4:</span> spawns a starter
     * {@link Teleporter} at the given position.
     *
     * @param dimensions                the dimensions of the game window, used to calculate tile
     *                                  positions and place entities on the grid.
     * @param starterTeleporterPosition the tile position at which to spawn the initial
     *                                  {@link Teleporter}.
     * @throws IOException        if the target map file could not be read.
     * @throws WorldLoadException if the target map file failed to parse.
     * @hint map file can be found at "resources/wasteland.map".
     * @provided
     */
    public ToxicCleanup(Dimensions dimensions, Positionable starterTeleporterPosition)
            throws IOException, WorldLoadException {
        final int playerX = 5 * dimensions.tileSize() + dimensions.tileSize() / 2;
        final int playerY = 5 * dimensions.tileSize() + dimensions.tileSize() / 2;

        this.playerManager = new PlayerManager(new Position(playerX, playerY));

        this.world = WorldBuilder.fromFile(dimensions, "resources/wasteland.map");
        this.machineManager = new MachinesManager();
        this.guiManager = new GuiManager();
        this.weather = WeatherBuilder.fromFile(dimensions, "resources/wasteland_weather.map");
        this.spawnTeleporter(starterTeleporterPosition, dimensions);
    }

    /**
     * Support method only here for testing purposes, so we can teleport
     * the player to given locations.
     *
     * @param position position we wish to move the player to.
     */
    public void movePlayer(Positionable position) {
        playerManager.setPosition(position);
    }

    /**
     * Support method only here for testing purposes, so we can set the game to
     * a specified power level to test from.
     *
     * @param power amount we wish to set the internal current power to.
     */
    public void setPower(int power) {
        machineManager.setPower(power);
    }

    /**
     * Lets us spawn a teleporter outside the game loop, useful as maps generally
     * should start with at least one teleporter already on them.
     *
     * @param position   position we wish to spawn the teleporter at
     * @param dimensions used for tile size calculations
     */
    private void spawnTeleporter(Positionable position, Dimensions dimensions) {
        final List<Tile> tilesAtPosition = world.tilesAtPosition(position, dimensions);
        if (tilesAtPosition.size() != 1) {
            throw new IllegalStateException("Only should be one tile at the given location!");
        }
        final Tile tile = tilesAtPosition.getFirst(); //should only be one tile so get it
        if (tile instanceof Dirt dirt) {
            final int originalPower = machineManager.getPower();
            //add the cost of the teleporter power to our resources to ensure can afford
            machineManager.adjust(Teleporter.COST);
            dirt.pave();
            dirt.attemptSpawnTeleporter(machineManager);
            machineManager.setPower(originalPower); //set power back to what it was before
        }
    }

    /**
     * Advances the game by one frame.
     *
     * <p>Each call updates active game systems (world, player, and GUI), applies end-state checks,
     * and enforces periodic toxicity damage during ongoing play.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Starter code only; No method body is provided.
     *
     * <p><span style="color:#14CC2A;">Stage 0:</span> Ticks the player by creating a new
     * {@link ToxicCleanupGameState} passing the {@link PlayerManager} and passing it along with the engine
     * state to {@link PlayerManager#tick}.
     *
     * <p><span style="color:#F5D000;">Stage 2:</span> Updates the {@link ToxicCleanupGameState} to include the
     * {@link ToxicWorld} and {@link MachinesManager}. Ticks the {@link ToxicWorld} and {@link GuiManager} each frame.
     *
     * <p><span style="color:#D02F83;">Stage 4:</span> After ticking the world, checks if the player is no longer alive.
     * and displays the game-over screen, checks if no toxic fields remain and displays the win screen,
     * and advances the damage timer dealing 1 damage to the player every 1800 ticks.
     *
     * @param engine current engine input/state.
     * @ensures If the player is dead at end-state evaluation, the lose overlay is shown.
     * @ensures If no toxic fields remain at end-state evaluation, the win overlay is shown.
     * @ensures If neither end condition holds, gameplay progresses normally and periodic damage
     * is applied when the damage timer finishes.
     * @provided
     */
    public void tick(EngineState engine) {
        final GameState game = new ToxicCleanupGameState(world, playerManager, machineManager, weather);

        world.tick(engine, game);

        if (!playerManager.isAlive()) {
            playerManager.tick(engine, game);
            guiManager.lose(engine);
            return;
        }

        if (!world.isToxic()) {
            guiManager.win(engine);
            return;
        }
        playerManager.tick(engine, game);
        guiManager.tick(engine, game);
        weather.tick(engine, game);
        machineManager.tick(engine, game);

        damageTimer.tick();
        if (damageTimer.isFinished()) {
            playerManager.adjust(1);
        }
    }

    /**
     * Returns all renderables for the current frame in back-to-front draw order. For example the tiles must be
     * before the player in the resultant list.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Creates a new list of renderables and adds
     * the player's renderables from {@link PlayerManager#render()} to it, then returns the list.
     *
     * <p><span style="color:#2E75B2;">Stage 1:</span> Adds the world's renderables from
     * {@link ToxicWorld#render()} to the list.
     *
     * <p><span style="color:#F5D000;">Stage 2:</span> Adds the GUI renderables from
     * {@link GuiManager#render()} to the list.
     *
     * @return A list of renderables to draw, in back-to-front order.
     * @provided
     */
    @Override
    public List<Renderable> render() {
        final List<Renderable> renderables = new ArrayList<>();
        renderables.addAll(world.render());
        renderables.addAll(playerManager.render());
        renderables.addAll(weather.render());
        renderables.addAll(guiManager.render());
        return renderables;
    }
}
