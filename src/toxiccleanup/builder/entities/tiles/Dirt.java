package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.machines.LightningRod;
import toxiccleanup.builder.machines.Machines;
import toxiccleanup.builder.machines.SolarPanel;
import toxiccleanup.builder.machines.Teleporter;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;

/**
 * A {@link Dirt} tile is a buildable ground tile with two visual states: <b>unpaved</b> and
 * <b>paved</b>. Tiles start unpaved. When the player stands on an unpaved dirt tile and presses
 * the pave key ('f'), the tile transitions to the paved state. It is rendered using the default
 * sprite of {@link SpriteGallery#dirt}
 *
 * <p>Once paved and with no machines already on it, the player can build machines by clicking:
 * <ul>
 *   <li><b>Left-click</b>: attempts to build a {@link SolarPanel} (costs 3 power).</li>
 *   <li><b>Right-click</b>: attempts to build a {@link Teleporter} (costs 2 power).</li>
 * </ul>
 *
 * <p>Both actions delegate to {@link Machines} which checks whether sufficient
 * power is available before constructing the machine. If power is insufficient, nothing is built.
 *
 * @multistage
 */
public class Dirt extends Tile implements PlayerOverHook {

    private static final char PAVE_KEY = 'f';
    private static final SpriteGroup dirtArt = SpriteGallery.dirt;
    private static final SpriteGroup pavedArt = SpriteGallery.paved;
    private boolean paved = false;

    /**
     * Constructs a new unpaved dirt tile at the given position.
     *
     * @param position The position we wish to place this newly constructed tile at.
     * @requires position.getX() >= 0, position.getX() is less than the window width
     * @requires position.getY() >= 0, position.getY() is less than the window height
     */
    public Dirt(Positionable position) {
        super(position, dirtArt);
        setSprite(dirtArt.getSprite("default"));
    }

    /**
     * Returns whether this dirt tile has been paved. A paved tile uses the paved sprite group
     * and can have machines ({@link SolarPanel} or {@link Teleporter}) built on it.
     *
     * @return {@code true} if the tile has been paved; {@code false} if it is still unpaved dirt.
     */
    public boolean isPaved() {
        return paved;
    }

    /**
     * Transitions this tile from unpaved to paved. Sets the internal paved flag to
     * {@code true} and switches the tile's sprite group to the paved art so it renders
     * accordingly. Has no effect if the tile is already paved (calling it multiple times
     * is safe).
     */
    public void pave() {
        paved = true;
        setArt(pavedArt);
    }

    /**
     * Asks the machine system to build a {@link SolarPanel} at this tile's position. If the
     * spawn succeeds (i.e. sufficient power was available), the new solar panel is placed on
     * top of this tile via {@link #placeOn}. If the spawn returns {@code null} (insufficient
     * power), nothing happens.
     *
     * @param spawner the {@link Machines} instance used to attempt spawning the {@link SolarPanel}.
     */
    public void attemptSpawnSolarPanel(Machines spawner) {
        SolarPanel spawned = spawner.spawnSolarPanel(this);
        if (spawned != null) {
            placeOn(spawned);
        }
    }

    /**
     * Asks the machine system to build a {@link LightningRod} at this tile's position. If the
     * spawn succeeds (i.e. sufficient power was available), the new lightning rod is placed on
     * top of this tile via {@link #placeOn}. If the spawn returns {@code null} (insufficient
     * power), nothing happens.
     *
     * @param spawner the {@link Machines} instance used to attempt spawning the {@link SolarPanel}.
     */
    public void attemptSpawnLightningRod(Machines spawner) {
        LightningRod spawned = spawner.spawnLightningRod(this);
        if (spawned != null) {
            placeOn(spawned);
        }
    }

    /**
     * Asks the machine system to build a {@link Teleporter} at this tile's position. If the
     * spawn succeeds (i.e. sufficient power was available), the new teleporter is placed on
     * top of this tile via {@link #placeOn}, and its position is registered for future
     * teleportation. If the spawn returns {@code null} (insufficient power), nothing happens.
     *
     * @param spawner the {@link Machines} instance used to attempt spawning the {@link Teleporter}.
     */
    public void attemptSpawnTeleporter(Machines spawner) {
        Teleporter spawned = spawner.spawnTeleporter(this);
        if (spawned != null) {
            placeOn(spawned);
        }
    }


    /**
     * Handles player interaction while the player is on this dirt tile.
     *
     * <p>Behaviour:
     * - If the tile is unpaved and the pave key ('f') is pressed, the tile becomes paved.
     * - If the tile is paved and has no stacked entities, build input may place one machine:
     * left-click attempts to place a {@link SolarPanel}, right-click attempts to place a {@link Teleporter}.
     * - Any placed machine is added to this tile; if placement fails (e.g. insufficient power), no machine is added.
     * - Player-over behaviour of stacked entities on this tile is also applied.
     *
     * @param state current engine input/state.
     * @param game  current game state.
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        for (PlayerOverHook playerOverHook : getStackedEntitiesWithPlayerOverHook()) {
            playerOverHook.playerOver(state, game);
        }

        //calc if player is within a single tiles distance of this tile
        attemptPave(state);

        //Machine Building/Placement
        final boolean tileBuildable = getStackedEntities().isEmpty() && isPaved();
        final boolean leftPressed = state.getMouse().isLeftPressed();
        final boolean rightPressed = state.getMouse().isRightPressed();
        final boolean rPressed = state.getKeys().isDown('r');

        //jb:must be if else rather than guard pattern. otherwise we would
        //need to check for stacked entities in each case.
        if (tileBuildable && leftPressed) {
            attemptSpawnSolarPanel(game.getMachines());
        } else if (tileBuildable && rightPressed) {
            attemptSpawnTeleporter(game.getMachines());
        } else if (tileBuildable && rPressed) {
            attemptSpawnLightningRod(game.getMachines());
        }
    }

    private void attemptPave(EngineState state) {
        if (state.getKeys().isDown(Dirt.PAVE_KEY)) {
            pave();
        }
    }
}