package toxiccleanup.builder.machines;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.weather.Weather;

/**
 * A {@link Teleporter} is a machine that allows the player to instantly travel between teleporter
 * locations on the map. When the player stands on a teleporter and presses the use key ('e'),
 * they are moved to a randomly chosen other teleporter's position - provided the shared power
 * system has at least {@value COST} power units available. Power is NOT consumed on use;
 * it is only required to be present.
 *
 * <p>Costs {@value COST} power units to build. When powered, cycles through a sprite animation
 * every 12 ticks. If power drops below the requirement, the animation pauses. Rendered using
 * {@link SpriteGallery#teleporter}.
 *
 * @provided
 */
public class Teleporter extends GameEntity implements PlayerOverHook, Powered, Damageable {
    /**
     * The number of power units required to place this teleporter.
     */
    public static final int COST = 2;
    private static final SpriteGroup art = SpriteGallery.teleporter;
    private static final char USE_KEY = 'e';
    private static final int ANIM_TICK_INTERVAL = 12;
    private final int finalAnimFrameIndex;
    private final TickTimer animTimer;
    private int animFrame = 1;
    final private DamageHandler damageHandler;
    
    /**
     * Constructs a new Teleporter at the given position.
     *
     * @param position the position we wish to spawn this Teleporter at.
     */
    public Teleporter(Positionable position) {
        super(position);
        setSprite(art.getSprite("1"));
        animTimer = new RepeatingTimer(ANIM_TICK_INTERVAL);
        finalAnimFrameIndex = art.getSprites().size() - 1;
        this.damageHandler = new DamageHandler();
    }

    /**
     * Called every game tick to advance the teleporter's animation timer. When the timer fires
     * and the machine system has at least {@value COST} power units, the displayed sprite advances
     * to the next animation frame. If power is insufficient, the animation pauses.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The current state of the game, providing access to the machine power system.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state);

        final Weather weather = game.getWeather();
        if(damageHandler.checkAndApplyDamage(weather, state.getDimensions(), this.getPosition())){
            setSprite(art.getSprite("damaged"));
            return; //exit early the teleporter is damaged!
        }

        animTimer.tick();
        if (animTimer.isFinished() && game.getMachines().hasRequiredPower(getPowerRequirement())) {
            updateArt();
        }
    }

    /**
     * Returns the minimum power level required for this teleporter to animate and be used.
     * Both the animation and the {@link #playerOver} teleportation check use this value
     * via {@link Machines#hasRequiredPower(int)}.
     *
     * @return {@code 2}, the number of power units required for this teleporter to operate.
     */
    @Override
    public int getPowerRequirement() {
        return Teleporter.COST;
    }

    /**
     * Called each tick the player occupies this teleporter's grid cell. If the use key ('e')
     * is held and the machine system has at least {@value COST} power units available, the player
     * is teleported to a randomly selected other teleporter's position via
     * {@link Machines#getNextTeleporterPosition(Positionable)}.
     * Power is checked but NOT deducted on use.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        if (!state.getKeys().isDown(Teleporter.USE_KEY)) {
            return; //we can exit early if no use happening
        }
        if (this.damageHandler.isDamaged()) {
            this.damageHandler.repairDamage();
            return; //return on the frame we repair
        }

        if (game.getMachines().hasRequiredPower(getPowerRequirement())) {
            //jb: sync positions of player to position of tile
            final Positionable position = game.getMachines().getNextTeleporterPosition(this);
            game.getPlayer().setPosition(position);
        }
    }

    /**
     * Handles updating the anim to the next sprite,
     * adjusting our internal index and resetting it to the start if we go past the final index.
     */
    private void updateArt() {
        animFrame += 1;
        if (animFrame > finalAnimFrameIndex) { //reset our animation back to the start
            animFrame = 1;
        }
        setSprite(art.getSprite(animFrame + ""));
    }

    /**
     * Returns if this damageable Object is or is not in its damaged state.
     *
     * @return if this damageable Object is or is not in its damaged state.
     */
    @Override
    public boolean isDamaged() {
        return this.damageHandler.isDamaged();
    }

    /**
     * Sets the Damageable Object to it's damaged state.
     */
    @Override
    public void setDamage(Damage dmg) {
        this.damageHandler.setDamage(dmg);
    }

    /**
     * Sets the Damageable Object to it's undamaged
     */
    @Override
    public void repairDamage() {
        this.damageHandler.repairDamage();
    }
}
