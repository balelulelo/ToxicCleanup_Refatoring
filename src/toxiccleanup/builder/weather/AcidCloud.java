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
    private int currentArtFrame = 1;
    private final int maxFrames = 5;
    private final TickTimer animTimer = new RepeatingTimer(12);

    /**
     * @param position
     */
    public AcidCloud(Positionable position) {
        super(position);
        setSprite(art.getSprite(currentArtFrame + ""));
    }

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
        } else {
            //do nothing
        }
    }

    @Override
    public Damage getDamage(Dimensions dimensions, Positionable position) {
        return new Damage(this.getPosition());
    }

    @Override
    public Damage getDamage() {
        return new Damage(this.getPosition());
    }
}