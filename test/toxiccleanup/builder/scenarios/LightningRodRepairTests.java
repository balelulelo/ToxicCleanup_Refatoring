package toxiccleanup.builder.scenarios;

import toxiccleanup.engine.Engine;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.renderer.TileGrid;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import test_utils.analysers.AnalyserManager;
import test_utils.analysers.SpatialAnalyser;
import test_utils.analysers.RenderableAnalyser;
import test_utils.mocks.HeadlessCore;
import test_utils.mocks.MockEngineState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.builder.world.WorldLoadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple sim test specifically for LightningRod.
 *
 * <h4> User Inputs for Sim </h4>
 * <p>Ticks 0 - 1</p>
 * <ul>
 *     <li>F key down</li>
 * </ul>
 * <hr/>
 *
 * <p>Ticks 2 - 399</p>
 * <ul>
 *     <li>R key down</li>
 * </ul>
 * <hr/>
 *
 * <p>Ticks 400 - 649</p>
 * <ul>
 *     <li>No player input</li>
 * </ul>
 * <hr/>
 * <p>Ticks 650 - 1299</p>
 * <ul>
 *     <li>E key down</li>
 * </ul>
 *
 * maps used:
 * "resources/testmaps/microtests/lightningrod.map",
 * "resources/testmaps/microtests/lightningrod_weather.map"
 *
 *
 */
public class LightningRodRepairTests {
    private static final int SIZE = 800;
    private static final int TILES_PER_ROW = 5;
    private static final int TICKS = 1300;
    private static AnalyserManager data;
    private static final Dimensions dimensions = new TileGrid(TILES_PER_ROW, SIZE);

    @Before
    public void setUp() throws IOException, WorldLoadException {
        final int halfTile = dimensions.tileSize() / 2;
        final ToxicCleanup game =
                new ToxicCleanup(
                        dimensions,
                        new Position(halfTile * 3, halfTile),
                        "resources/testmaps/microtests/lightningrod.map",
                        "resources/testmaps/microtests/lightningrod_weather.map"
                );

        game.movePlayer(new Position(halfTile * 5, halfTile * 5));
        final HeadlessCore core = new HeadlessCore(dimensions.windowSize(), TILES_PER_ROW);
        final Engine engine = new Engine(game, dimensions, core);
        LightningRodRepairTests.data = new AnalyserManager();
        MockKeys keyState = new MockKeys(new ArrayList<>());
        for (int i = 0; i < TICKS; i += 1) {
            //place solar panels as move down
            ArrayList<Character> characters = new ArrayList<>();
            MockMouse mouseState = new MockMouse(
                    0, 0,
                    false, false, false
            );
            if (i < 2) {
                characters.add('f');  //pave
            } else if (i < 400) {
                characters.add('r'); //build the rod
            } else if (i < 650) {

            } else {
                characters.add('e'); //repair
            }

            keyState = new MockKeys(characters);

            core.addEngineState(new MockEngineState(dimensions, mouseState, keyState, i));
            engine.tick(); // tick forward one frame of our game!

            // process all the renderables for the frame
            for (Renderable renderable : game.render()) {
                data.add(i, renderable);
            }
        }
    }

    /**
     * Should have no fallback sprites!
     */
    @Test
    public void noFallbackSpritesRendered() {
        Assert.assertEquals(
                "0 fallback sprites should have been seen! " +
                        "hint:this likely means you forgot to set a sprite in " +
                        "a constructor somewhere.",
                0,
                data.getBySpriteGroup("default").size());
    }

    @Test
    public void playerSpawned() {
        RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        Assert.assertEquals(
                "player should have spawned on 0th frame of game",
                0, player.getFirstFrame().getFrame()
        );
    }

    @Test
    public void lightingRodBuilt() {
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        Assert.assertEquals("1 rod should have been built", 1, rods.size());
    }

    @Test
    public void lightningRodDamagedAtSomePoint() {
        final Sprite damaged = SpriteGallery.lightningrod.getSprite("damaged");
        int rodsThatHaveBeenDamaged = data.count("lightningrod", rod -> {
            return rod.hasSpriteBetween(damaged, 0, TICKS - 1);
        });
        final int amountOfRodsThatShouldBeDamaged = 1;
        Assert.assertEquals(
                amountOfRodsThatShouldBeDamaged + " rod(s) should have at some point " +
                        "during the sim been damaged and flipped to its damaged state when " +
                        "acidcloud went over",
                amountOfRodsThatShouldBeDamaged, rodsThatHaveBeenDamaged
        );
    }

    @Test
    public void lightningRodDamagedInExpectedWindow() {
        final Sprite damaged = SpriteGallery.lightningrod.getSprite("damaged");
        final int rodsThatHaveBeenDamaged = data.count("lightningrod", rod -> {
            return rod.hasSpriteBetween(damaged, 450, 600);
        });
        Assert.assertEquals(
                "rod should have been damaged by acid cloud between frames 400 to 600",
                1, rodsThatHaveBeenDamaged
        );
    }

    @Test
    public void lightningNotMovedDueToRodDamageInExpectedWindow() {
        final Sprite damaged = SpriteGallery.lightningrod.getSprite("damaged");
        final int numOfBoltsInCenter = data.count("lightning", (bolt) -> {
            if (!bolt.wasWithinFrames(450, 600)) {
                return false;
            }
            final SpatialAnalyser spatial = new SpatialAnalyser(bolt);
            return spatial.stayedInRectangularArea(
                    dimensions.windowSize() / 2, dimensions.windowSize() / 2,
                    dimensions.tileSize(), dimensions.tileSize()
            );
        });
        final int rodsThatHaveBeenDamaged = data.count("lightningrod", rod -> {
            return rod.hasSpriteBetween(damaged, 400, 600);
        });
        Assert.assertEquals(
                "rod should have been damaged by acid cloud between frames 400 to 600",
                1, rodsThatHaveBeenDamaged
        );
        Assert.assertEquals(
                "no bolts should have been pulled into the center during" +
                        "this window as the rod was damaged",
                0,
                numOfBoltsInCenter
        );
    }

    @Test
    public void lightningRodRepair() {
        final Sprite damaged = SpriteGallery.lightningrod.getSprite("damaged");
        final int damagedCount = data.count("lightningrod", (rod) -> {
            return rod.hasSpriteBetween(damaged, TICKS - 2, TICKS - 1);
        });
        Assert.assertEquals(
                "Should have no rods in a damaged state in final frames",
                0, damagedCount
        );
    }

    @Test
    public void eightLightningSpawnedInFirstWave() {
        final int numOfBoltsInFirstWave = data.count("lightning", (bolt) -> {
            return bolt.wasWithinFrames(0, 200);
        });
        Assert.assertEquals(
                "8 bolts should have been spawned in the first wave",
                8, numOfBoltsInFirstWave
        );
    }

    @Test
    public void lightningFirstWaveSpawnNotMovedAsRodNotBuilt() {
        final int numOfFirstWaveBoltsInCenter = data.count("lightning", (bolt) -> {
            if (!bolt.wasWithinFrames(0, 200)) {
                return false;
            }
            final SpatialAnalyser spatial = new SpatialAnalyser(bolt);
            return spatial.stayedInRectangularArea(
                    dimensions.windowSize() / 2, dimensions.windowSize() / 2,
                    dimensions.tileSize(), dimensions.tileSize()
            );
        });
        Assert.assertEquals(
                "Should be 0 lightning bolts in the center of the screen " +
                        "for the first wave as no rod has been built yet",
                0,
                numOfFirstWaveBoltsInCenter
        );
    }

    @Test
    public void lightningFutureWavesSpawnMovedToRod() {
        final int startFrame = 1020;
        final int endFrame = 1100;
        final int numOfFirstWaveBoltsInCenter = data.count("lightning", (bolt) -> {
            if (!bolt.wasWithinFrames(startFrame, endFrame)) {
                return false;
            }
            final SpatialAnalyser spatial = new SpatialAnalyser(bolt);
            return spatial.visitedRectangularArea(
                    dimensions.windowSize() / 2, dimensions.windowSize() / 2,
                    dimensions.tileSize(), dimensions.tileSize()
            );
        });
        Assert.assertEquals(
                "Should be 8 lightning bolts in the center of the screen " +
                        "for the future waves when rod is built and not damaged",
                8,
                numOfFirstWaveBoltsInCenter
        );
    }

}
