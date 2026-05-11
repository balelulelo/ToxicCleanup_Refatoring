package toxiccleanup.builder.weather;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;

/**
 * <p> A {@link Cloud} is a weather phenomena that will move to the left, over time. </p>
 * <p> It obscures {@link toxiccleanup.builder.machines.SolarPanel}s that
 * it is sharing a tile with. </p>
 * <p> When it reaches the leftmost edge of the screen, it will mark itself for removal. </p>
 *
 * <p> Rendered using {@link SpriteGallery#cloud}. </p>
 *
 * @provided
 */
public class Cloud extends GameEntity implements Obscuring {
    private static final SpriteGroup art = SpriteGallery.cloud;
    final public static int SPAWN_TIME = 300;
    final public static int MOVEMENT_TIME = 1;
    final private static int SPEED = 2;
    final private TickTimer timer;
    private int currentArtFrame = 1;
    private int maxFrames = 5;
    private final TickTimer animTimer = new RepeatingTimer(12);

    public Cloud(Positionable position, TickTimer timer) {
        super(position);
        this.timer = timer;
        this.maxFrames = art.getSprites().size();
        setSprite(art.getSprite(currentArtFrame + ""));
    }

    public Cloud(Positionable position) {
        super(position);
        this.timer = new RepeatingTimer(Cloud.MOVEMENT_TIME);
        this.maxFrames = art.getSprites().size();
        setSprite(art.getSprite(currentArtFrame + ""));
    }

    /**
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state, game);
        this.animTimer.tick();

        if (this.animTimer.isFinished()) {
            currentArtFrame += 1;
            if (currentArtFrame > maxFrames) {
                currentArtFrame = maxFrames;
            }
        }
        setSprite(art.getSprite(currentArtFrame + ""));

        this.timer.tick();
        if (this.timer.isFinished()) {
            final int movement = this.getX() - Cloud.SPEED;
            this.setX(movement);
            if (this.getX() < 0) {
                this.markForRemoval();
            } else {
                //do nothing
            }
        } else {
            //do nothing
        }
    }
}
