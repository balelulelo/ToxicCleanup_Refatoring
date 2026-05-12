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
    protected int currentArtFrame = 1;
    protected int maxFrames = 5;
    protected final TickTimer animTimer = new RepeatingTimer(12);

    /**
     * Constructs a new {@link Cloud} at the given position using the given movement timer.
     *
     * <p>Requires: {@code position} and {@code timer} must not be {@code null}.</p>
     * <p>Ensures: the cloud is initialized at the given position and displays
     * the first animation frame.</p>
     *
     * @param position the initial position of this cloud.
     * @param timer    the timer controlling how frequently this cloud moves.
     */
    public Cloud(Positionable position, TickTimer timer) {
        super(position);
        this.timer = timer;
        this.maxFrames = art.getSprites().size();
        setSprite(art.getSprite(currentArtFrame + ""));
    }
    /**
     * Constructs a new {@link Cloud} at the given position using the default movement timer.
     *
     * <p>Requires: {@code position} must not be {@code null}.</p>
     * <p>Ensures: the cloud is initialized at the given position and displays
     * the first animation frame.</p>
     *
     * @param position the initial position of this cloud.
     */
    public Cloud(Positionable position) {
        super(position);
        this.timer = new RepeatingTimer(Cloud.MOVEMENT_TIME);
        this.maxFrames = art.getSprites().size();
        setSprite(art.getSprite(currentArtFrame + ""));
    }
    /**
     * Advance a looping sprite animation by one tick using the given sprite group.
     *
     * <p>Ticks the shared {@code animTimer}. If the timer has finished, advances
     * {@code currentArtFrame} by one. If {@code currentArtFrame} exceeds {@code maxFrames},
     * it will wrap back to 1. After that, updates the displayed sprite to the current frame.</p>
     *
     * <p>This method is provided for cloud's subclasses such as {@link RainCloud} and {@link AcidCloud}
     * that need a looping animation rather than the clamping animation used by this class.</p>
     *
     * <p>Requires: {@code art} must not be {@code null} and must contain at least
     * {@code maxFrames} sprites.</p>
     * <p>Ensures: {@code currentArtFrame} is always in the range [1, maxFrames] after
     * this method returns.</p>
     *
     * @param art the sprite group from which frames are retrieved.
     */
    protected void tickLoopingAnimation(SpriteGroup art) {
        this.animTimer.tick();
        if (this.animTimer.isFinished()) {
            currentArtFrame += 1;
            if (currentArtFrame > maxFrames) {
                currentArtFrame = 1;
            }
        }
        setSprite(art.getSprite(currentArtFrame + ""));
    }

    /**
     * Advances this cloud by one game tick.
     *
     * <p>Handles movement only by moving the cloud left by {@value SPEED} pixels when the
     * movement timer starts. If the cloud moves off the left edge of the screen, it marks
     * itself for removal.</p>
     *
     * <p>Cloud subclasses that need looping animation should call{@link #tickLoopingAnimation(SpriteGroup)}
     * after calling {@code super.tick(state, game)}, which will update the sprite with their own looping frame.</p>
     *
     * <p>Requires: {@code state} and {@code game} must not be {@code null}.</p>
     * <p>Ensures: if the cloud's x position drops below 0, it is marked for removal.</p>
     *
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
