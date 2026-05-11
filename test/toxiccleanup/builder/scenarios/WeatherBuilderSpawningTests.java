package toxiccleanup.builder.scenarios;

import toxiccleanup.engine.Engine;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Game;
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
import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.builder.world.WorldLoadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Runs a game sim focused on confirming that weather events spawn and move
 * as expected.
 * Maps Used:
 * "resources/testmaps/wasteland.map" and
 * "resources/testmaps/wasteland_weather.map".
 *
 *
 * <h5>All Ticks</h5>
 * <ul>
 *     <li>No User Input</li>
 * </ul>
 */
public class WeatherBuilderSpawningTests {
    private static final int SIZE = 800;
    private static final int TILES_PER_ROW = 16;
    private static final int TICKS = 320;
    private static AnalyserManager data;
    private static final Dimensions dimensions = new TileGrid(TILES_PER_ROW, SIZE);

    @Before
    public void setUp() throws IOException, WorldLoadException {
        final Game game =
                new ToxicCleanup(
                        dimensions,
                        new Position(20, 20),
                        "resources/testmaps/wasteland.map",
                        "resources/testmaps/wasteland_weather.map"
                );
        final HeadlessCore core = new HeadlessCore(dimensions.windowSize(), TILES_PER_ROW);
        final Engine engine = new Engine(game, dimensions, core);
        WeatherBuilderSpawningTests.data = new AnalyserManager();
        MockKeys keyState = new MockKeys(new ArrayList<>());
        for (int i = 0; i < TICKS; i += 1) {
            if (i == 3) {
                ArrayList<Character> characters = new ArrayList<>();
                keyState = new MockKeys(characters);
            }
            final MockMouse mouseState = new MockMouse(i, i, false, false, false);
            core.addEngineState(new MockEngineState(dimensions, mouseState, keyState, i));
            engine.tick(); // tick forward one frame of our game!

            for (Renderable renderable :
                    game.render()) { // process all the renderables for the frame
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

    @Test
    public void playerSpawned() {
        RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        Assert.assertEquals(
                "player should have spawned on 0th frame of game",
                0, player.getFirstFrame().getFrame()
        );
    }

    @Test
    public void spawnedCloudsTest() {
        final List<RenderableAnalyser> clouds = data.getBySpriteGroup("cloud");
        Assert.assertEquals(
                "After 320 ticks using testmaps/wasteland_weather.map  +\n" +
                        "we expect to see 8 clouds spawned",
                8,
                clouds.size()
        );
        final RenderableAnalyser firstCloud = data.getFirstSpawnedOfSpriteGroup("cloud");
        Assert.assertTrue("No clouds should have been spawned before the 100th frame", firstCloud.getFirstFrame().getFrame() >= 100);
        Assert.assertTrue("First cloud should have been spawned no later than the 301st frame", firstCloud.getFirstFrame().getFrame() <= 301);
    }

    @Test
    public void cloudsSpawnedOnRightSideLocationTest() {
        final List<RenderableAnalyser> clouds = data.getBySpriteGroup("cloud");
        for (RenderableAnalyser cloud : clouds) {
            final SpatialAnalyser cloudMovement = new SpatialAnalyser(cloud);

            final int screenCenterY = dimensions.windowSize() / 2;
            final int widthOfAreaBeingChecked = dimensions.tileSize() * 2;
            final boolean wasInStartingArea = cloudMovement.visitedRectangularArea(
                    750, screenCenterY,
                    widthOfAreaBeingChecked, dimensions.windowSize()
            );
            Assert.assertTrue("When simulating testmaps/wasteland_weather.map, " +
                    "We expect to see that the spawned clouds started on the " +
                    "far right column of the map", wasInStartingArea);
        }
    }

    @Test
    public void spawnedCloudsMoveTest() {
        final List<RenderableAnalyser> clouds = data.getBySpriteGroup("cloud");
        for (RenderableAnalyser cloud : clouds) {
            final SpatialAnalyser cloudSpatial = new SpatialAnalyser(cloud);
            final boolean movedLeft = cloudSpatial.measureOverallMove().getX() < 0;
            Assert.assertTrue("first cloud should have moved to the left over its lifespan", movedLeft);

            final boolean movedLeftAtLeast5Tiles = cloudSpatial.measureOverallMove().getX() < dimensions.tileSize() * 5;
            Assert.assertTrue("first cloud should have moved to the left at least 5 tiles worth over it's lifespan", movedLeftAtLeast5Tiles);
            Assert.assertEquals("first cloud should not have moved on its y axis", 0, cloudSpatial.measureOverallMove().getY());
        }
    }

    @Test
    public void spawnedAcidCloudsTest() {
        final RenderableAnalyser firstAcidCloud = data.getFirstSpawnedOfSpriteGroup("acidcloud");
        Assert.assertTrue("No clouds should have been spawned before the 100th frame", firstAcidCloud.getFirstFrame().getFrame() >= 100);
        Assert.assertTrue("First cloud should have been spawned no later than the 301st frame", firstAcidCloud.getFirstFrame().getFrame() <= 301);
    }

    @Test
    public void spawnedAcidCloudsMoveTest() {
        final RenderableAnalyser firstAcidCloud = data.getFirstSpawnedOfSpriteGroup("acidcloud");
        final SpatialAnalyser firstAcidCloudMove = new SpatialAnalyser((firstAcidCloud));
        //first cloud should have moved to the left, decreasing its .getX()
        final boolean movedLeft = firstAcidCloudMove.measureOverallMove().getX() < 0;
        Assert.assertTrue("first cloud should have moved to the left over its lifespan", movedLeft);
        Assert.assertEquals("first cloud should not have moved on its y axis", 0, firstAcidCloudMove.measureOverallMove().getY());

        final boolean movedLeftAtLeast5Tiles = firstAcidCloudMove.measureOverallMove().getX() < dimensions.tileSize() * 5;
        Assert.assertTrue("first cloud should have moved to the left at least 5 tiles worth over it's lifespan", movedLeftAtLeast5Tiles);
        Assert.assertEquals("first cloud should not have moved on its y axis", 0, firstAcidCloudMove.measureOverallMove().getY());
    }

    @Test
    public void acidCloudsSpawnedOnRightSideLocationTest() {
        final List<RenderableAnalyser> acidclouds = data.getBySpriteGroup("acidcloud");
        for (RenderableAnalyser acidcloud : acidclouds) {
            final SpatialAnalyser cloudMovement = new SpatialAnalyser(acidcloud);

            final int screenCenterY = dimensions.windowSize() / 2;
            final int widthOfAreaBeingChecked = (int) (dimensions.tileSize() * 4);
            //we check against a broad area because there is a good chance they've ticked
            // their movement forward after spawning but before rendering.
            final boolean wasOnRightSide = cloudMovement.visitedRectangularArea(
                    750, screenCenterY, widthOfAreaBeingChecked, dimensions.windowSize()
            );
            Assert.assertTrue(
                    "When simulating testmaps/wasteland_weather.map, " +
                            "We expect to see that the spawned acidclouds started on the " +
                            "far right column of the map", wasOnRightSide
            );
        }
    }

    @Test
    public void spawnedRainCloudsTest() {
        RenderableAnalyser firstRainCloud = data.getFirstSpawnedOfSpriteGroup("raincloud");
        Assert.assertTrue(
                "No clouds should have been spawned before the 100th frame",
                firstRainCloud.getFirstFrame().getFrame() >= 100
        );
        Assert.assertTrue(
                "First cloud should have been spawned no later than the 301st frame",
                firstRainCloud.getFirstFrame().getFrame() <= 301
        );
    }

    @Test
    public void spawnedRainCloudsMoveTest() {
        final RenderableAnalyser firstRainCloud = data.getFirstSpawnedOfSpriteGroup("raincloud");
        final SpatialAnalyser firstRainCloudMove = new SpatialAnalyser((firstRainCloud));
        final boolean movedLeft = firstRainCloudMove.measureOverallMove().getX() < 0;
        Assert.assertTrue("first cloud should have moved to the left over its lifespan",
                movedLeft);
        Assert.assertEquals("first cloud should not have moved on its y axis",
                0, firstRainCloudMove.measureOverallMove().getY()
        );

        final int overallXMove = firstRainCloudMove.measureOverallMove().getX();
        boolean movedLeftAtLeast5Tiles = overallXMove < dimensions.tileSize() * 5;
        Assert.assertTrue("first cloud should have moved to the left at least 5 tiles worth over it's lifespan", movedLeftAtLeast5Tiles);
        Assert.assertEquals("first cloud should not have moved on its y axis", 0, firstRainCloudMove.measureOverallMove().getY());
    }

    @Test
    public void spawnedLightningTest() {
        final List<RenderableAnalyser> lightnings = data.getBySpriteGroup("lightning");
        final int expectedLightningCount = 10;
        Assert.assertEquals(
                "After 320 ticks using testmaps/wasteland_weather.map " +
                        "we expect to see" + expectedLightningCount + "lightning spawned (all 'l' " +
                        "lightning on map should have spawned twice)",
                expectedLightningCount,
                lightnings.size()
        );
    }

    @Test
    public void spawnedLightningDidNotMoveWhenNoRodsPresentTest() {
        final List<RenderableAnalyser> lightnings = data.getBySpriteGroup("lightning");

        for (RenderableAnalyser lightning : lightnings) {
            final SpatialAnalyser lightningMove = new SpatialAnalyser((lightning));
            final int xAxisMovement = lightningMove.measureOverallMove().getX();
            final int yAxisMovement = lightningMove.measureOverallMove().getY();
            Assert.assertEquals(
                    "lightning total movement on X axis should be 0 when no " +
                            "lightning rods present",
                    0,
                    xAxisMovement
            );
            Assert.assertEquals(
                    "lightning total movement on Y axis should be 0 when no " +
                            "lightning rods present",
                    0,
                    yAxisMovement
            );
        }
    }

    @Test
    public void noOverlappingLightningSpawnedInFirstSpawnWave() {
        final List<RenderableAnalyser> lightnings = data.getBySpriteGroup("lightning");
        final List<Integer> verticalPositions = new ArrayList<>();
        final int firstWaveOfLightningSpawns = lightnings.getFirst().getFirstFrame().getFrame();
        for (RenderableAnalyser lightning : lightnings) {
            if (lightning.getFirstFrame().getFrame() == firstWaveOfLightningSpawns) {
                verticalPositions.add(lightning.getFirstFrame().getY());
            }
        }
        final Set<Integer> uniqueVerticalPositions = new HashSet<>(verticalPositions);
        Assert.assertEquals(
                "When simulating testmaps/wasteland_weather.map, " +
                        "no spawned lightning should share the same y coordinate " +
                        "if this is failing, check you aren't spawning you aren't spawning " +
                        "multiple lightning on top of each other at once",
                0,
                verticalPositions.size() - uniqueVerticalPositions.size()
        );
    }

    @Test
    public void spawnedLightningAroundCorrectLocationsTest() {
        final List<RenderableAnalyser> lightnings = data.getBySpriteGroup("lightning");

        for (RenderableAnalyser lightning : lightnings) {
            final SpatialAnalyser lightningMovement = new SpatialAnalyser((lightning));
            //check against a broad column in the center
            final int screenCenterX = dimensions.windowSize() / 2;
            final int screenCenterY = dimensions.windowSize() / 2;
            final int widthOfAreaBeingChecked = (int) (dimensions.tileSize() * 6);
            final boolean wasWithinWideColumnInCenter = lightningMovement.visitedRectangularArea(
                    screenCenterX, screenCenterY,
                    widthOfAreaBeingChecked, dimensions.windowSize()
            );
            Assert.assertTrue(
                    "When simulating testmaps/wasteland_weather.map, " +
                            "We expect to see that the spawned lightning spawned around " +
                            "the middle of the screen horizontally and in a column vertically",
                    wasWithinWideColumnInCenter
            );
        }
    }
}
