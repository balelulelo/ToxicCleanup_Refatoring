package toxiccleanup.builder.player;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Direction;
import toxiccleanup.engine.game.Positionable;

/**
 * The visual player entity rendered on screen. A {@link Cleaner} holds the player's pixel
 * position and current facing sprite. It is created and controlled entirely by
 * {@link PlayerManager}, which reads keyboard input and calls {@link #move} each tick to
 * update the player's position and direction sprite.
 *
 * <p>The cleaner has four directional sprites ('up', 'down', 'left', 'right') and a 'dead'
 * sprite shown when the player has no HP remaining. All sprites are loaded from
 * {@link SpriteGallery#cleaner}.
 *
 * @provided
 */
public class Cleaner extends GameEntity {
    private static final SpriteGroup art = SpriteGallery.cleaner;

    /**
     * Constructs a cleaner instance at the given coordinates.
     *
     * @param position the position we wish to spawn the Cleaner at.
     * @requires position.getX() >= 0, position.getX() is less than the window width
     * @requires position.getY() >= 0, position.getY() is less than the window height
     */
    public Cleaner(Positionable position) {
        super(position);
        assert position.getX() >= 0 && position.getY() >= 0;
        setSprite(art.getSprite("down"));
    }

    /**
     * Move the player by the given amount in the given direction.
     *
     * <p>Update the player's x or y position according to the following table.
     *
     * <table>
     *     <tr><th>Direction</th><th>x</th><th>y</th></tr>
     *     <tr><td>NORTH</td><td>0</td><td>-amount</td></tr>
     *     <tr><td>SOUTH</td><td>0</td><td>amount</td></tr>
     *     <tr><td>EAST</td><td>amount</td><td>0</td></tr>
     *     <tr><td>WEST</td><td>-amount</td><td>0</td></tr>
     *     <caption>&nbsp;</caption>
     * </table>
     *
     * <p>The player's sprite should also be updated based on the move.
     * If the player moves north, the sprite should be set to 'up'. If the player moves south,
     * the sprite should be set to 'down'.
     * If the player moves either east or west, the sprite should be set to 'left' or 'right'
     * respectively.</p>
     *
     * <p>Note: Moving to a negative position is unspecified and won't be tested.</p>
     *
     * @param direction The direction to move in.
     * @param amount    How many pixels to move the player.
     * @requires amount > 0
     */
    public void move(Direction direction, int amount) {
        switch (direction) {
            case NORTH -> {
                setY(getY() - amount);
                setSprite(art.getSprite("up"));
            }
            case SOUTH -> {
                setY(getY() + amount);
                setSprite(art.getSprite("down"));
            }
            case EAST -> {
                setX(getX() + amount);
                setSprite(art.getSprite("right"));
            }
            case WEST -> {
                setX(getX() - amount);
                setSprite(art.getSprite("left"));
            }
            default -> setSprite(art.getSprite("down"));

        }
    }


    /**
     * Switches the cleaner's displayed sprite to the 'dead' state. Called by
     * {@link PlayerManager} when the player's HP reaches 0. Once set, the sprite remains
     * in the 'dead' state for the remainder of the game.
     *
     */
    public void setDeadSprite() {
        setSprite(art.getSprite("dead"));
    }

    /**
     * Called once per game frame to advance the cleaner's per-frame state.
     * This method is required for interface compliance and does not need to be implemented.
     * The body should remain empty.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, included for interface compliance; not needed here.
     */
    @Override
    public void tick(EngineState state, GameState game) {

    }
}
