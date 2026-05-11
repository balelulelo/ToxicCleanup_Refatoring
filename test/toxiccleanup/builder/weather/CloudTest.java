package toxiccleanup.builder.weather;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.TileGrid;
import toxiccleanup.engine.util.MockEngineState;
import toxiccleanup.engine.util.MockGameState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CloudTest {
    public static final double testWeight = 5.0;
    private final TileGrid tileGrid = new TileGrid(16, 800);
    private final MockMouse mockMouse = new MockMouse(2, 2, false, false, false);
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());
    private final MockEngineState baseEngineState = new MockEngineState(tileGrid, mockMouse, mockKeys);
    private final MockGameState baseGameState = new MockGameState();
    private final int startX = 200;
    private final int startY = 100;
    private Cloud cloud;

    @Before
    public void setup() {
        cloud = new Cloud(new Position(startX, startY));
    }

    /**
     * Confirms a newly constructed Cloud is placed at the correct position.
     */
    @Test
    @Deprecated
    public void cloudInitialPositionIsCorrect() {
        assertEquals("cloud x should match the position given at construction",
                startX, cloud.getX());
        assertEquals("cloud y should match the position given at construction",
                startY, cloud.getY());
    }

    /**
     * Confirms the Cloud moves left (x decreases) over time when ticked.
     */
    @Test
    @Deprecated
    public void cloudMovesLeftOverTime() {
        cloud.tick(baseEngineState, baseGameState);
        assertTrue("cloud x should have decreased after ticking (cloud moves left)",
                cloud.getX() < startX);
    }

    /**
     * Confirms the Cloud marks itself for removal once its x coordinate drops below zero.
     */
    @Test
    @Deprecated
    public void cloudMarksForRemovalWhenOffScreen() {
        cloud = new Cloud(new Position(1, startY)); // 1 pixel from edge
        cloud.tick(baseEngineState, baseGameState); // SPEED=2, so x becomes -1
        assertTrue("cloud should be marked for removal when x drops below 0",
                cloud.isMarkedForRemoval());
    }

    /**
     * Confirms a newly constructed RainCloud renders the '1' animation frame.
     */
    @Test
    @Deprecated
    public void rainCloudInitialSpriteSet() {
        RainCloud rain = new RainCloud(new Position(startX, startY));
        final Sprite expected = SpriteGallery.raincloud.getSprite("1");
        assertEquals("RainCloud should initially render sprite '1'",
                expected.toString(), rain.getSprite().toString());
    }

    /**
     * Confirms a newly constructed AcidCloud renders the '1' animation frame.
     */
    @Test
    @Deprecated
    public void acidCloudInitialSpriteSet() {
        AcidCloud acid = new AcidCloud(new Position(startX, startY));
        final Sprite expected = SpriteGallery.acidcloud.getSprite("1");
        assertEquals("AcidCloud should initially render sprite '1'",
                expected.toString(), acid.getSprite().toString());
    }

    /**
     * Confirms AcidCloud.getDamage() always returns a non-null Damage object.
     */
    @Test
    @Deprecated
    public void acidCloudGetDamageReturnsNonNull() {
        AcidCloud acid = new AcidCloud(new Position(startX, startY));
        assertNotNull("AcidCloud.getDamage() should return non-null Damage",
                acid.getDamage());
        assertNotNull("AcidCloud.getDamage(dimensions, position) should return non-null Damage",
                acid.getDamage(tileGrid, new Position(startX, startY)));
    }

    /**
     * Confirms Cloud implements the Obscuring interface.
     */
    @Test
    public void cloudIsObscuringInterface() {
        assertTrue("Cloud should implement Obscuring", cloud instanceof Obscuring);
    }

    /**
     * Confirms AcidCloud implements the Damaging interface.
     */
    @Test
    public void acidCloudImplementsDamaging() {
        AcidCloud acid = new AcidCloud(new Position(startX, startY));
        assertTrue("AcidCloud should implement Damaging", acid instanceof Damaging);
    }
}
