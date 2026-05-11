package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.game.Position;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HeartTest {
    public static final double testWeight = 2.0;

    /**
     * Construct a `Heart` instance at new Position(x=0, y=0).
     * Ensure that the sprite is a 'default' heart sprite.
     */
    @Test
    @Deprecated
    public void initialHeartConstruction() {
        Heart heart = new Heart(new Position(0, 0));
        assertEquals(
                "Initial heart sprite should be heart 'default'.",
                SpriteGallery.heart.getSprite("default").toString(),
                heart.getSprite().toString()
        );
    }
}