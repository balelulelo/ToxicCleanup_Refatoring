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
 * A {@link SolarPanel} is a machine that passively generates power for the game's shared power
 * system. Once placed on a paved {@link toxiccleanup.builder.entities.tiles.Dirt} tile,
 * it increments the power in the {@link MachinesManager}
 * by 1 every 120 game ticks (approximately every 2 seconds at 60 ticks per second).
 * Power is capped at the machine manager's maximum (14 by default).
 *
 * <p>Costs {@value COST} power units to build. Rendered using {@link SpriteGallery#solarPanel}.
 *
 * @provided
 */
public class SolarPanel extends GameEntity implements PlayerOverHook {
    /**
     * The number of power units required to place this solar panel.
     */
    public static final int COST = 3;
    private static final SpriteGroup solarPanelArt = SpriteGallery.solarPanel;
    private static final int POWER_GAIN = 1;
    private static final int TICK_TARGET = 120;
    private static final char USE_KEY = 'e';
    private final TickTimer timer;
    private final DamageHandler damageHandler;

    /**
     * Constructs a new SolarPanel at the given position using a default internal
     * {@link TickTimer} set to fire every (120) frames.
     *
     * @param position the position we wish to spawn the {@link SolarPanel} at.
     */
    public SolarPanel(Positionable position) {
        super(position);
        setSprite(solarPanelArt.getSprite("default"));
        timer = new RepeatingTimer(SolarPanel.TICK_TARGET);
        damageHandler = new DamageHandler();
    }

    /**
     * Constructs a new {@link SolarPanel} at the given position using the given
     * {@link DamageHandler}.
     *
     * @param position      the position we wish to spawn the {@link SolarPanel} at.
     * @param damageHandler the damage handler to use for this solar panel.
     */
    public SolarPanel(Positionable position, DamageHandler damageHandler) {
        super(position);
        setSprite(solarPanelArt.getSprite("default"));
        timer = new RepeatingTimer(SolarPanel.TICK_TARGET);
        this.damageHandler = damageHandler;
    }

    /**
     * Called every game tick to advance the solar panel's internal timer. When the timer fires
     * (every 120 ticks), adds 1 power to the shared machine power system via
     * {@link Machines#adjust(int)}.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, providing access to the machine power system.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state);

        final Weather weather = game.getWeather();
        if(damageHandler.checkAndApplyDamage(weather, state.getDimensions(), this.getPosition())){
            setSprite(solarPanelArt.getSprite("damaged"));
            return; //exit early the solar panel is damaged!
        }

        if (weather.isObscuring(state.getDimensions(), this.getPosition())) {
            setSprite(solarPanelArt.getSprite("off"));
            return; //exit early the solar panel is obscured!
        }
        setSprite(solarPanelArt.getSprite("default"));

        timer.tick();
        if (timer.isFinished()) {
            game.getMachines().adjust(SolarPanel.POWER_GAIN);
        }
    }

    /**
     * Called when the player is on top of this object. Intended for handling
     * any interaction that occurs while the player overlaps the
     * corresponding tile or entity.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        if (!state.getKeys().isDown(SolarPanel.USE_KEY)) {
            return; //we can exit early if no use happening
        }
        if (this.damageHandler.isDamaged()) {
            this.damageHandler.repairDamage();
        }
    }
}
