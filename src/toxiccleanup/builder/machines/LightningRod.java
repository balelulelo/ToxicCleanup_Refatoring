package toxiccleanup.builder.machines;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.weather.Lightning;
import toxiccleanup.builder.weather.LightningDamage;
import toxiccleanup.builder.weather.Weather;

/**
 * A {@link LightningRod} is a machine that passively attracts
 * nearby {@link Lightning} to its position, the effective radius is defined in {@value RADIUS}.
 * <p>Can only be placed on a paved {@link toxiccleanup.builder.entities.tiles.Dirt} tile.</p>
 *
 * <p>Costs {@value COST} power units to build.</p>
 * <p>Rendered using {@link SpriteGallery#chasm}.</p>
 *
 * @provided
 */
public class LightningRod extends GameEntity implements PlayerOverHook, Damageable {
    public static final int RADIUS = 300;
    public static final int COST = 1;
    private static final SpriteGroup art = SpriteGallery.lightningrod;
    private final DamageHandler damageHandler;
    private static final char USE_KEY = 'e';

    public LightningRod(Positionable position) {
        super(position);
        this.setSprite(art.getSprite("default"));
        this.damageHandler = new DamageHandler();
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

        final Weather weather = game.getWeather();
        final Damage dmg = weather.getDamage(state.getDimensions(), this.getPosition());
        if (dmg != null && !dmg.getType().equals(LightningDamage.TYPE)) {
            this.damageHandler.setDamage(dmg);
        }
        if (this.isDamaged()) {
            setSprite(art.getSprite("damaged"));
            return; //exit early the machine is damaged!
        }
        setSprite(art.getSprite("default"));
        weather.applyLightningRod(this.getPosition());
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
        if (!state.getKeys().isDown(USE_KEY)) {
            return; //we can exit early if no use happening
        }
        if (this.damageHandler.isDamaged()) {
            this.damageHandler.repairDamage();
        }
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
