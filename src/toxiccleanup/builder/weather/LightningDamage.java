package toxiccleanup.builder.weather;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.builder.Damage;

/**
 * Lightning Type Damage, can not deal damage to lightning rods specifically.
 */
public class LightningDamage extends Damage {
    private int x = 0;
    private int y = 0;
    public static final String TYPE = "lightning";

    public LightningDamage(Positionable position) {
        super(position);
        this.setType(TYPE);
    }
}
