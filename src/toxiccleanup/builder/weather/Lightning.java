package toxiccleanup.builder.weather;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.timing.FixedTimer;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;

/**
 * <p> A {@link Lightning} is a weather phenomena that will spawn at a given location.</p>
 * <p> It exists for a set lifespan (see {@value #LIFESPAN} then will mark itself for removal. </p>
 * <p> When it reaches the leftmost edge of the screen, it will mark itself for removal. </p>
 * <p> Lightning only deals {@link LightningDamage} during frames 5 and 6 of it's animation cycle.</p>
 * <p> See also: {@link toxiccleanup.builder.machines.LightningRod} </p>
 *
 * @provided
 */
public class Lightning extends GameEntity implements Damaging {
    public static final int SPAWN_TIME = 120;
    private static final int LIFESPAN = 60;
    private final TickTimer lifespanTimer = new FixedTimer(LIFESPAN);

    private final int finalAnimFrameIndex;
    private final TickTimer animTimer;
    private int animFrame = 1;

    private static final SpriteGroup art = SpriteGallery.lightning;

    /**
     * Constructs {@link Lightning} at the given position.
     *
     * @param position the position we wish to construct the lightning instance at.
     */
    public Lightning(Positionable position) {
        super(position);

        setSprite(art.getSprite("1"));

        finalAnimFrameIndex = art.getSprites().size() - 1;
        int ANIM_TICK_INTERVAL = ((int) (double) (LIFESPAN / finalAnimFrameIndex));
        animTimer = new RepeatingTimer(ANIM_TICK_INTERVAL);
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
        lifespanTimer.tick();
        animTimer.tick();
        if (lifespanTimer.isFinished()) {
            markForRemoval();
        }
        if (animTimer.isFinished()) {
            this.updateArt();
        }
    }


    @Override
    public Damage getDamage(Dimensions dimensions, Positionable position) {
        return new LightningDamage(this.getPosition());
    }

    /**
     * Returns an instance of the damage Lightning can do
     * if it is currently in a damage dealing state.
     *
     * @return instance of {@link Damage}
     */
    public Damage getDamage() {
        if (this.isDamaging()) {
            return new LightningDamage(this.getPosition());
        }
        return null;
    }

    /**
     * Returns if the {@link Lightning} is currently in its state that would deal {@link Damage}
     *
     * @return if the {@link Lightning} is currently in its state that would deal {@link Damage}
     */
    public boolean isDamaging() {
        final boolean isActiveHitFrames = animFrame == 5 || animFrame == 6;
        return isActiveHitFrames;
    }


    /**
     * Handles updating the anim to the next sprite,
     * adjusting our internal index and resetting it to the start if we go past the final index.
     */
    private void updateArt() {
        animFrame += 1;
        if (animFrame > finalAnimFrameIndex) { //reset our animation back to the start
            animFrame = finalAnimFrameIndex;
        }
        setSprite(art.getSprite(animFrame + ""));
    }
}
