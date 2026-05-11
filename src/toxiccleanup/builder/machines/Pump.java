package toxiccleanup.builder.machines;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.weather.Weather;

/**
 * A {@link Pump} is a machine that removes toxicity from a {@link toxiccleanup.builder.entities.tiles.ToxicField}
 * over time. Every 100 game ticks it calls {@link Adjustable#adjust(int)} on
 * its target with {@code 1}, reducing the field's toxicity by 1. The pump only operates when
 * the shared power system has at least 2 power units available - if power drops below 2, both
 * the animation and the pumping pause until power is restored.
 *
 * <p>The pump stops and is removed from the field once the field's toxicity reaches 0 (i.e.
 * when the field is fully cleaned up).
 *
 * <p>Costs {@value COST} power units to build. The pump cycles through a sprite animation every
 * 4 ticks while powered. Rendered using {@link SpriteGallery#pump}.
 *
 * @provided
 */
public class Pump extends GameEntity implements Powered, PlayerOverHook {
    /**
     * The number of power units required to place this pump.
     */
    public static final int COST = 5;
    private static final SpriteGroup art = SpriteGallery.pump;
    private static final int POWER_REQUIRED = 2;
    private static final int ANIM_TICK_INTERVAL = 4;
    private static final char USE_KEY = 'e';
    private static final int PUMP_TIMER_INTERVAL = 100;
    private final TickTimer animTimer;
    private final TickTimer pumpTimer;
    private final Adjustable pumpTarget;
    //    private final Damageable health;
    private final int finalAnimIndex;
    private int animIndex = 1;
    private final DamageHandler damageHandler;

    /**
     * Constructs a new Pump at the given position. Initializes an animation timer that fires
     * every 4 ticks and a pump timer that fires every 100 ticks. The given {@link Adjustable}
     * is the target whose value is reduced by 1 each time the pump timer fires (provided
     * sufficient power is available).
     *
     * @param position   the position we wish to spawn this Pump at.
     * @param pumpTarget the object whose adjustable value (e.g. toxicity) will be reduced
     *                   each time the pump fires.
     */
    public Pump(Positionable position, Adjustable pumpTarget) {
        super(position);
        setSprite(art.getSprite("1"));
        finalAnimIndex = art.getSprites().size() - 1;
        animTimer = new RepeatingTimer(Pump.ANIM_TICK_INTERVAL);
        pumpTimer = new RepeatingTimer(Pump.PUMP_TIMER_INTERVAL);
        this.pumpTarget = pumpTarget;
        this.damageHandler = new DamageHandler();
    }

    /**
     * Handles updating the anim to the next sprite,
     * adjusting our internal index and resetting it to the start if we go past the final index.
     *
     */
    private void updateArt() {
        animIndex += 1;
        if (animIndex > finalAnimIndex) { //reset our animation back to the start
            animIndex = 1;
        }
        this.setSprite(art.getSprite(animIndex + ""));
    }

    /**
     * Called every game tick to advance the pump's internal timers. Both the animation timer
     * (every 4 ticks) and the pump timer (every 100 ticks) are ticked unconditionally, but
     * their effects only apply when the shared power system has at least 2 power units:
     *
     * <ul>
     *   <li>When the animation timer fires and power &ge; 2: the displayed sprite advances to
     *       the next animation frame, looping back to frame 1 after the last frame.</li>
     *   <li>When the pump timer fires and power &ge; 2: {@link Adjustable#adjust(int)} is called
     *       on the pump's target with {@code 1}, reducing its toxicity by 1. This may cause
     *       the target field to mark the pump for removal if toxicity reaches 0.</li>
     * </ul>
     *
     * <p>If power drops below 2, the animation freezes and no pumping occurs until power
     * is restored.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, providing access to the machine power system.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state);

        final Weather weather = game.getWeather();
        Damage dmg = weather.getDamage(state.getDimensions(), this.getPosition());
        if (dmg != null) {
            this.damageHandler.setDamage(dmg);
        }
        if (this.damageHandler.isDamaged()) {
            setSprite(art.getSprite("damaged"));
            return; //exit early the solar panel is damaged!
        }

        animTimer.tick();
        pumpTimer.tick();

        if (animTimer.isFinished() && game.getMachines().hasRequiredPower(getPowerRequirement())) {
            updateArt();
        }
        if (pumpTimer.isFinished() && game.getMachines().hasRequiredPower(getPowerRequirement())) {
            int amountToPump = 1;
            pumpTarget.adjust(amountToPump);
        }
    }

    @Override
    public void playerOver(EngineState state, GameState game) {
        if (!state.getKeys().isDown(Pump.USE_KEY)) {
            return; //we can exit early if no use happening
        }
        if (this.damageHandler.isDamaged()) {
            this.damageHandler.repairDamage();
        }
    }

    /**
     * Returns the minimum power level required for this pump to operate. The pump's animation
     * and pumping action only proceed when the shared power system meets this threshold.
     *
     * @return {@code 2}, the number of power units required for the pump to function.
     */
    @Override
    public int getPowerRequirement() {
        return POWER_REQUIRED;
    }
}
