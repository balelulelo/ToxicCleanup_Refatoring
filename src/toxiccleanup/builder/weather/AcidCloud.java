package toxiccleanup.builder.weather;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;

/**
 * <p> A {@link AcidCloud} is a weather phenomena that will move to the left, over time. </p>
 * <p> It damages any machine that it shares a tile with, changing them to their damaged state </p>
 * <p> Plays an animation loop endlessly using sprites from {@link SpriteGallery#acidcloud}. </p>
 * <p> When it reaches the leftmost edge of the screen, it will mark itself for removal. </p>
 *
 * <p> Rendered using {@link SpriteGallery#acidcloud}. </p>
 *
 * @provided
 */
public class AcidCloud extends Cloud implements Damaging {
    final public static int SPAWN_TIME = 300;
    private static final SpriteGroup art = SpriteGallery.acidcloud;

    /**
     * Constructs a new {@link AcidCloud} at the given position.
     *
     * @param position the initial position of this acid cloud.
     */
    public AcidCloud(Positionable position) {
        super(position);
        this.maxFrames = art.getSprites().size();
        this.currentArtFrame = 1;
        setSprite(art.getSprite(currentArtFrame + ""));
    }

    /**
     * Advances this acid cloud by one game tick.
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
        tickLoopingAnimation(art);
        if (getX() < 0 || getX() > state.getDimensions().windowSize()) {
            markForRemoval();
        }
    }

    /**
     * Returns the {@link Damage} the acid cloud deals to machines in the same tile as the acid cloud.
     *
     * <p>Ensures: always returns a non-null {@link Damage} instance at this cloud's
     * current position.</p>
     *
     * @param dimensions the screen and tile dimensions for pixel-to-tile conversion.
     * @param position   the position of the machine being checked for damage.
     * @return a {@link Damage} instance at this cloud's current position.
     */
    @Override
    public Damage getDamage(Dimensions dimensions, Positionable position) {
        return new Damage(this.getPosition());
    }


    /**
     * Returns the {@link Damage} the acid cloud deals.
     *
     * <p>Ensures: always returns a {@link Damage} instance at this cloud's
     * current position.</p>
     *
     * @return a {@link Damage} instance at this cloud's current position.
     */
    @Override
    public Damage getDamage() {
        return new Damage(this.getPosition());
    }
}