package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.game.Position;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PowerBarTest {
    public static final double testWeight = 3.0;

    /**
     * Construct a `PowerBar` instance at new Position(x=0, y=0).
     * Ensure that the sprite is a 'bar' power sprite.
     */
    @Test
    @Deprecated
    public void powerBarConstruction() {
        PowerBar powerbar = new PowerBar(new Position(0, 0));
        assertEquals(
                "Initial power sprite should be power 'bar' if not set to charged.",
                SpriteGallery.power.getSprite("bar").toString(),
                powerbar.getSprite().toString()
        );
    }

    /**
     * Construct a `PowerBar` instance at new Position(x=0, y=0) with charged state set to true
     * Ensure that the sprite is a 'chargedbar' power sprite.
     */
    @Test
    public void chargedPowerBarConstruction() {
        PowerBar powerbar = new PowerBar(new Position(0, 0), true);
        assertEquals(
                "Initial power sprite should be power 'chargedbar' if set to charged " +
                        "at construction.",
                SpriteGallery.power.getSprite("chargedbar").toString(),
                powerbar.getSprite().toString()
        );
    }
}
