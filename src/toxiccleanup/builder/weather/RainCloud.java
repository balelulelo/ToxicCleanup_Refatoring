package toxiccleanup.builder.weather;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;


/**
 * <p> A {@link RainCloud} is a weather phenomena that will move to the left, over time. </p>
 * <p> It obscures {@link toxiccleanup.builder.machines.SolarPanel}s that
 * it is sharing a tile with. </p>
 * <p> Plays an animation loop endlessly using sprites from {@link SpriteGallery#raincloud}. </p>
 * <p> When it reaches the leftmost edge of the screen, it will mark itself for removal. </p>
 *
 * <p> Rendered using {@link SpriteGallery#raincloud}. </p>
 *
 * @provided
 */
public class RainCloud extends Cloud {
    private static final SpriteGroup art = SpriteGallery.raincloud;

    /**
     * Constructs a new {@link RainCloud} at the given position.
     *
     * @param position the initial position of this rain cloud.
     */
    public RainCloud(Positionable position) {
        super(position);
        this.maxFrames = art.getSprites().size();
        this.currentArtFrame = 1;
        setSprite(art.getSprite(currentArtFrame + ""));
    }

    /**
     * Advances this rain cloud by one game tick.
     *
     * <p>Delegates the movement logic to {@link Cloud#tick(EngineState, GameState)}, then advances
     * the looping sprite animation via {@link Cloud#tickLoopingAnimation(SpriteGroup)}.
     * If the cloud moves off either horizontal edge of the screen, it marks itself
     * for removal.</p>
     *
     * <p>Requires: {@code state} and {@code game} must not be {@code null}.</p>
     * <p>Ensures: if the cloud's x position is outside the screen bounds, it is marked
     * for removal.</p>
     *
     * @param state the current engine state, including input and display dimensions.
     * @param game  the current game state, including the player and world.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state, game);
        this.animTimer.tick();

        if (this.animTimer.isFinished()) {
            currentArtFrame += 1;
            if (currentArtFrame > maxFrames) {
                currentArtFrame = 1;
            }
        }
        setSprite(art.getSprite(currentArtFrame + ""));
        if (getX() < 0 || getX() > state.getDimensions().windowSize()) {
            markForRemoval();
        }
    }
}
