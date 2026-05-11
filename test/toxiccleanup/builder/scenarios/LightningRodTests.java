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
import test_utils.analysers.FrameRecord;
import test_utils.analysers.SpatialAnalyser;
import test_utils.analysers.RenderableAnalyser;
import test_utils.mocks.HeadlessCore;
import test_utils.mocks.MockEngineState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.builder.world.WorldLoadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * These tests focused around lightning rod functionality sim a 5 x 5 world
 * with the player starting around the top left corner.
 * <p>[_P_][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * The player will move down the screen paving as they go.
 * On the 0th frame and 1st frame they will attempt place a solar panel.
 * From the 2nd frame to the 15th frame they will input 'down' and 'pave'
 * 2 to 15
 * <p>[_P_][___][___][___][___]</p>
 * <p>[_↓_][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * On roughly the 60th frame (exact frame will differ based on tick ordering etc.)
 * lightning will be spawned at the following locations.
 * <p>[___][___][___][___][_l_]</p>
 * <p>[___][_l_][___][___][___]</p>
 * <p>[_P_][_l_][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * On the 80th frame they will attempt to place a LightningRod with 'r'
 * <p>[_S_][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[rP_][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * On roughly the 120th frame (exact frame will differ based on tick ordering etc.)
 * lightning will be spawned at the following locations.
 * <p>[___][___][___][___][_l_]</p>
 * <p>[___][_l_][___][___][___]</p>
 * <p>[rP_][_l_][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * If lightning rod is working correctly the 2  lightning within the range of lightning rod
 * should have then been moved onto its position
 * <p>[___][___][___][___][_l_]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[rPl][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 * <p>[___][___][___][___][___]</p>
 *
 * <h4> User Inputs for Sim </h4>
 * <p>Ticks 0 - 1</p>
 * <ul>
 *     <li>F key down</li>
 *     <li>Left mouse down</li>
 * </ul>
 * <hr/>
 * <p>Ticks 2 - 14</p>
 * <ul>
 *     <li>S key down</li>
 *     <li>F key down</li>
 * </ul>

 * <hr/>
 * <p>Ticks 15 - 79</p>
 * <ul>
 *     <li>No User Input</li>
 * </ul>

 * <hr/>
 * <p>Ticks 80 - 100</p>
 * <ul>
 *     <li>R key down</li>
 * </ul>

 * <hr/>
 * <p>Ticks 101+</p>
 * <ul>
 *     <li>No User Input</li>
 * </ul>
 *
 * maps:
 * "resources/testmaps/microtests/lightning_and_rod.map",
 * "resources/testmaps/microtests/lightning_and_rod_weather.map"
 */
public class LightningRodTests {
    private static final int SIZE = 800;
    private static final int TILES_PER_ROW = 5;
    private static final int TICKS = 265;
    private static AnalyserManager data;
    private static final Dimensions dimensions = new TileGrid(TILES_PER_ROW, SIZE);

    @Before
    public void setUp() throws IOException, WorldLoadException {
        final ToxicCleanup game =
                new ToxicCleanup(
                        dimensions,
                        new Position(20, 20),
                        "resources/testmaps/microtests/lightning_and_rod.map",
                        "resources/testmaps/microtests/lightning_and_rod_weather.map"
                );
        game.movePlayer(new Position(dimensions.tileSize(), dimensions.tileSize()));
        final HeadlessCore core = new HeadlessCore(dimensions.windowSize(), TILES_PER_ROW);
        final Engine engine = new Engine(game, dimensions, core);
        LightningRodTests.data = new AnalyserManager();
        MockKeys keyState = new MockKeys(new ArrayList<>());
        for (int i = 0; i < TICKS; i += 1) {
            ArrayList<Character> characters = new ArrayList<>();
            MockMouse mouseState = new MockMouse(i, i, false, false, false);
            if (i < 2) { //pave and spawn a solar panel!
                mouseState = new MockMouse(i, i, true, false, false);
                characters.add('f'); //pave
            } else if (i < 15) { //travel down screen paving as we go
                characters.add('s'); //move down
                characters.add('f'); //pave
            } else if (i >= 80 && i <= 100) { //player should have reached destination by now
                characters.add('r'); //attempt to place rod
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
                        "hint:this likely means you forgot to set a initial sprite in " +
                        "a constructor somewhere.",
                0,
                data.getBySpriteGroup("default").size());
    }

    /**
     * Sanity test to confirm the player is being spawned on the 0th frame of the game.
     */
    @Test
    public void playerSpawned() {
        RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        Assert.assertEquals(
                "player should have spawned on 0th frame of game",
                0, player.getFirstFrame().getFrame()
        );
    }

    /**
     * Confirm the player has moved down and stopped at the middle tile vertically speaking.
     */
    @Test
    public void playerMovedToVerticalMiddle() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final FrameRecord closeToFinalFrame = player.getLastFrame();
        final int middleVerticalTile = 2;
        Assert.assertEquals(middleVerticalTile, dimensions.pixelToTile(closeToFinalFrame.getY()));
    }


    @Test
    public void playerStayedOnLeft() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerMovement = new SpatialAnalyser(player);
        final int tileSize = dimensions.tileSize();
        final int centerScreenY = dimensions.windowSize() / 2;
        final boolean playerStayedInRectangle = playerMovement.stayedInRectangularArea(
                tileSize,
                centerScreenY,
                tileSize * 2,
                dimensions.windowSize()
        );
        Assert.assertTrue(
                "player should have stayed within the left most column of tiles for " +
                        "entire scenario",
                playerStayedInRectangle
        );
    }

    @Test
    public void playerBuiltRodInMiddle() {
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final FrameRecord lastFrame = player.getLastFrame();
        final FrameRecord lastRodFrame = rods.getFirst().getLastFrame();
        final int middleVerticalTile = 2;
        Assert.assertEquals(middleVerticalTile, dimensions.pixelToTile(lastFrame.getY()));
        Assert.assertEquals(middleVerticalTile, dimensions.pixelToTile(lastRodFrame.getY()));
    }

    @Test
    public void solarSpawned() {
        final List<RenderableAnalyser> solarpanels = data.getBySpriteGroup("solarPanel");
        Assert.assertEquals(
                "Player moving down the screen should placed 1 solarpanel on frame 0.",
                1, solarpanels.size()
        );
    }

    @Test
    public void rodSpawned() {
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        Assert.assertEquals(
                "Player should have managed to place exactly 1 rod",
                1,
                rods.size()
        );
    }

    @Test
    public void rodSpawnedInLeftColumn() {
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        RenderableAnalyser rod = rods.getFirst();

        final SpatialAnalyser rodMovement = new SpatialAnalyser(rod);
        final int tileSize = dimensions.tileSize();
        final int centerScreenY = dimensions.windowSize() / 2;
        final boolean rodStayedInRectangle = rodMovement.stayedInRectangularArea(
                tileSize,
                centerScreenY,
                tileSize * 2,
                dimensions.windowSize()
        );
        Assert.assertTrue(
                "rod should have stayed within the left most column of tiles for " +
                        "entire scenario",
                rodStayedInRectangle
        );

        final int tileRodIsOn = dimensions.pixelToTile(rod.getFirstFrame().getY());
        Assert.assertEquals(
                "Rod should have been placed " +
                        "on the middle tile vertically on the leftmost column",
                2, tileRodIsOn
        );
    }

    /**
     *
     */
    @Test
    public void rodPersistsAfterCreation() {
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        RenderableAnalyser rod = rods.getFirst();
        final int lifespan = rod.frameLifespan();
        final int start = rod.getFirstFrame().getFrame();
        final int lastFrameCount = TICKS - 1;
        final int actualFrameDif = start - lastFrameCount;

        Assert.assertTrue(
                "Rod should persist from frame spawned until end of sim",
                lifespan >= actualFrameDif
        );
    }

    /**
     * We confirm the LightningRod never has its sprite changed as a proxy for ensuring it is never
     * set to it's damaged visual state by being hit by lightning
     */
    @Test
    public void noDamageIsDoneByLightningToLightningRods() {
        final Sprite damaged = SpriteGallery.lightningrod.getSprite("damaged");
        final int rodsThatHaveBeenDamaged = data.count("lightningrod", rod -> {
            return rod.hasSprite(damaged);
        });
        final int amountOfRodsThatShouldBeDamaged = 0;
        Assert.assertEquals(
                "LightningRods should not take damage from lightning hits," +
                        "rods damaged by lightning:" + rodsThatHaveBeenDamaged,
                amountOfRodsThatShouldBeDamaged, rodsThatHaveBeenDamaged
        );
    }

    @Test
    public void correctNumberOfLightningSpawned() {
        final List<RenderableAnalyser> bolts = data.getBySpriteGroup("lightning");
        Assert.assertEquals("Lightning spawned ", 6, bolts.size());
    }

    @Test
    public void firstLightningWaveNotPlacedOnEventualRodPosition() {
        final List<RenderableAnalyser> lightnings = data.getBySpriteGroup("lightning");
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        final FrameRecord rod = rods.getFirst().getLastFrame();
        final List<RenderableAnalyser> firstWaveOfLightning = data.filter("lightning",
                (bolt) -> {
                    return bolt.wasWithinFrames(0, 100);
                }
        );

        final List<String> firstWaveOfLightningFirstFramePositions = new ArrayList<>();
        final Position rodPosition = new Position(rod.getX(), rod.getY());

        for (RenderableAnalyser lightning : firstWaveOfLightning) {
            //Get lightning from only the first spawned wave of lightning that
            //should not have been effected by a lightning rod
            final Position position = new Position(
                    lightning.getFirstFrame().getX(),
                    lightning.getFirstFrame().getY()
            );
            firstWaveOfLightningFirstFramePositions.add(position.toString());
            Assert.assertNotEquals(
                    "No lightning in first spawning wave should share a position with where the lightning rod is eventually placed ",
                    position.getX(),
                    rodPosition.getX()
            );
            Assert.assertNotEquals(
                    "No lightning in first spawning wave should share a position with where the lightning rod is eventually placed ",
                    position.getY(),
                    rodPosition.getY()
            );

        }
    }

    @Test
    public void firstLightningWaveSpawnsAtVariousLocations() {
        final List<String> firstWaveOfLightningFirstFramePositions = new ArrayList<>();
        final List<RenderableAnalyser> firstWaveOfLightning = data.filter("lightning",
                (bolt) -> {
                    return bolt.wasWithinFrames(0, 100);
                }
        );
        for (RenderableAnalyser bolt : firstWaveOfLightning) {
            //Get lightning from only the first spawned wave of lightning that
            //should not have been effected by a lightning rod
            final Position position = new Position(
                    bolt.getFirstFrame().getX(),
                    bolt.getFirstFrame().getY()
            );
            firstWaveOfLightningFirstFramePositions.add(position.toString());

        }
        final Set<String> uniquePositions = new HashSet<>(firstWaveOfLightningFirstFramePositions);
        Assert.assertEquals(
                "Expected there to be"
                        + firstWaveOfLightningFirstFramePositions.size()
                        + " unique positions in the first wave of spawned lightning",
                firstWaveOfLightningFirstFramePositions.size(),
                uniquePositions.size()
        );
    }

    @Test
    public void secondLightningWavePartiallyMovedToLightningRodPosition() {
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        RenderableAnalyser rod = rods.getFirst();

        final int secondWaveBoltCount = data.count("lightning", (bolt) -> {
            return bolt.wasWithinFrames(220, TICKS - 1);
        });

        Assert.assertEquals(
                "Expect 3 bolts to have been spawned in the 2nd wave",
                3, secondWaveBoltCount
        );
        final Position position = new Position(
                rod.getLastFrame().getX(),
                rod.getLastFrame().getY()
        );

        final int boltsMovedCount = data.count("lightning", (bolt) -> {
            if (!bolt.wasWithinFrames(220, TICKS - 1)) {
                return false;
            }

            final FrameRecord frame = bolt.getLastFrame();
            return frame.getX() == position.getX() && frame.getY() == position.getY();
        });

        Assert.assertNotEquals(
                "only 2 of 3 lightning bolts should have been teleported " +
                        "onto the lightning rod position" +
                        "check if you have altered your LightningRod.RADIUS from 300 by accident",
                3,
                boltsMovedCount
        );
        Assert.assertEquals(
                "2 lightning bolts should have been teleported " +
                        "onto the Lightning Rod in the 2nd wave of Lightning spawns.",
                2,
                boltsMovedCount
        );
    }
}
