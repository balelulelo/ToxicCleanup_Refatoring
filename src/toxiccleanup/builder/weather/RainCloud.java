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
    private int currentArtFrame = 1;
    private int maxFrames = 5;
    private final TickTimer animTimer = new RepeatingTimer(12);


    public RainCloud(Positionable position) {
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
        }
    }
}
