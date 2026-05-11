package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.machines.Adjustable;
import toxiccleanup.builder.machines.Machines;
import toxiccleanup.builder.machines.Pump;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Entity;
import toxiccleanup.engine.game.Positionable;

/**
 * A {@link ToxicField} represents a contaminated flower field tile that can be purified using a
 * {@link Pump}.
 *
 * <p>A toxic field begins fully toxic and progresses through four visual states as its toxicity
 * is reduced via {@link Adjustable#adjust}:
 * <ul>
 *   <li>Starting toxic</li>
 *   <li>Cleanup begun</li>
 *   <li>Cleanup nearly done</li>
 *   <li>Cleanup finished: flowers restored</li>
 * </ul>
 *
 * <p>Rendered using {@link SpriteGallery#toxicField}.
 *
 */
public class ToxicField extends Tile implements PlayerOverHook, Adjustable {
    private static final SpriteGroup art = SpriteGallery.toxicField;
    private static final int MAX_TOXICITY = 6;
    private int toxicity = ToxicField.MAX_TOXICITY;

    /**
     * Constructs a new toxic field tile at the given position. With toxicity 6.
     *
     * @param position The position we wish to place this newly constructed tile at.
     * @requires position.getX() >= 0, position.getX() is less than the window width
     * @requires position.getY() >= 0, position.getY() is less than the window height
     */
    public ToxicField(Positionable position) {
        super(position, art);
    }

    /**
     * Reduces the toxicity of this field by {@code amount}. The tile's sprite
     * is updated to reflect the new toxicity level:
     *
     * <ul>
     *   <li>Toxicity &ge; 3: default toxic appearance.</li>
     *   <li>Toxicity = 2: cleanup starting (slightly less toxic).</li>
     *   <li>Toxicity = 1: cleanup almost done.</li>
     *   <li>Toxicity = 0: fully cleaned - flowers restored. All stacked entities (e.g. the
     *       {@link Pump}) are marked for removal.</li>
     * </ul>
     *
     * @param amount the amount to subtract from the current toxicity level (typically 1).
     */
    @Override
    public void adjust(int amount) {
        toxicity -= amount;
        toxicity = Math.clamp(toxicity, 0, MAX_TOXICITY);

        if (toxicity <= 0) {
            clearField();
        }
        updateArt();
    }

    /**
     * Returns whether this field still contains any toxicity. Used by {@link toxiccleanup.builder.world.ToxicWorld#isToxic()}
     * to determine whether the game has been won. Also used by  the pump-spawning logic to prevent
     * placing a pump on an already-clean field.
     *
     * @return {@code true} if toxicity is greater than 0; {@code false} if the field has been
     * fully cleaned up (toxicity = 0).
     */
    public boolean isToxic() {
        return toxicity > 0;
    }

    /**
     * Marks all stacked entities on this {@link ToxicField} for removal.
     */
    private void clearField() {
        for (Entity entity : this.getStackedEntities()) {
            entity.markForRemoval();
        }
    }

    /**
     * Handles updating the visual representation of the {@link ToxicField} based on internal state.
     */
    private void updateArt() {
        final String spriteName = switch (toxicity) {
            case 0 -> "cleanupdone"; //flower time
            case 1 -> "cleanupmid";
            case 2 -> "cleanupstart";
            default -> "default";
        };
        setSprite(art.getSprite(spriteName));
    }

    /**
     * Attempts to spawn a {@link Pump} and place it on this field.
     *
     * @param spawner
     */
    private void attemptSpawnPump(Machines spawner) {
        if (isToxic()) {
            final Pump spawned = spawner.spawnPump(this, this);
            if (spawned != null) {
                placeOn(spawned);
            }
        }
    }

    /**
     * Handles player interaction while the player is on this toxic field tile.
     *
     * <p>Behaviour:
     * - Applies player-over behaviour for any stacked entities on this tile.
     * - If the field is eligible for building (toxic and no stacked entities) and the left mouse button is pressed,
     * attempts to place a {@link Pump} on this tile via the machine system.
     * - If pump placement succeeds, the pump is stacked on this tile; otherwise, tile state is unchanged.
     *
     * @param state current engine input/state.
     * @param game  current game state.
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        for (PlayerOverHook playerOverHook : getStackedEntitiesWithPlayerOverHook()) {
            playerOverHook.playerOver(state, game);
        }

        final boolean tileBuildable = getStackedEntities().isEmpty();
        final boolean leftPressed = state.getMouse().isLeftPressed();
        if (tileBuildable && leftPressed) {
            attemptSpawnPump(game.getMachines());
        }
    }

}