package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.game.Position;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PowerIconTest {
    public static final double testWeight = 2.0;
    /**
     * Construct a `PowerIcon` instance at new Position(x=0, y=0).
     * Ensure that the sprite is a 'icon' power sprite.
     */
    @Test
    @Deprecated
    public void initialPowerIconConstruction() {
        PowerIcon powericon = new PowerIcon(new Position(0, 0));
        assertEquals(
                "Initial power sprite should be power 'icon'.",
                SpriteGallery.power.getSprite("icon").toString(),
                powericon.getSprite().toString()
        );
    }
}
