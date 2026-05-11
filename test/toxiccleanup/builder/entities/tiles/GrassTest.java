package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.game.Position;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GrassTest {
    public static final double testWeight = 2.0;
    /**
     * Construct a `Grass` instance at new Position(x=0, y=0).
     * Ensure that the sprite is a 'default' grass sprite.
     */
    @Test
    @Deprecated
    public void initialGrassConstruction() {
        Grass grass = new Grass(new Position(0, 0));
        assertEquals(
                "Initial Grass sprite should be grass 'default'.",
                SpriteGallery.grass.getSprite("default"),
                grass.getSprite()
        );
    }


}
