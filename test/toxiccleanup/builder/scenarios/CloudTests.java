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
 * These tests focused around cloud functionality sim a 5 x 5 world
 * with the player starting around the top left corner.
 * <p>[_P_][___][___][___][_r_]</p>
 * <p>[_↓_][___][___][___][_a_]</p>
 * <p>[___][___][___][___][_c_]</p>
 * <p>[___][___][___][___][_r_]</p>
 * <p>[___][___][___][___][_c_]</p>
 * The player will move down the screen paving and placing solar panels.
 * 2 clouds, 2 rain clouds and 1 acid cloud will be spawned on the far right column.
 * They should proceed to move left before despawning at some point after leaving the screen.
 * <p>[___][___][___][_<_][_r_]</p>
 * <p>[_S_][___][___][_<_][_a_]</p>
 * <p>[_S_][___][___][_<_][_c_]</p>
 * <p>[_S_][___][___][_<_][_r_]</p>
 * <p>[sP_][___][___][_<_][_c_]</p>
 * <p>
 * Maps used:
 * "resources/testmaps/microtests/clouds.map",
 * "resources/testmaps/microtests/clouds_weather.map"
 *
 * <h4> User Inputs for Sim </h3>
 * <p>All Ticks:</p>
 * <ul>
 * <li>F key down</li>
 * </ul>
 * <hr/>
 * <p>Between ticks ~50 -> ~700</p>
 *      <ul>
 *          <li>leftMouse:down</li>
 *          <li>S key down</li>
 *      </ul>
 * <hr/>
 * <p>Between Ticks ~700 -> ~712</p>
 *      <ul>
 *          <li>D key down</li>
 *      </ul>
 * <hr/>
 * <p>Ticks ~712+</p>
 *      <ul>
 *          <li>W key down</li>
 *          <li>R key down</li>
 *      </ul>
 */
public class CloudTests {
    private static final int SIZE = 800;
    private static final int TILES_PER_ROW = 5;
    private static final int TICKS = 850;
    private static AnalyserManager data;
    private static final Dimensions dimensions = new TileGrid(TILES_PER_ROW, SIZE);
    private static int farRightTileColumnX;
    private static int farLeftTileColumnX;

    @Before
    public void setUp() throws IOException, WorldLoadException {
        farLeftTileColumnX = dimensions.tileSize() / 2;
        farRightTileColumnX = dimensions.windowSize() - dimensions.tileSize() / 2;
        final ToxicCleanup game =
                new ToxicCleanup(
                        dimensions,
                        new Position(20, 20),
                        "resources/testmaps/microtests/clouds.map",
                        "resources/testmaps/microtests/clouds_weather.map"
                );
        final Position position = new Position(
                dimensions.tileSize() / 2,
                dimensions.tileSize() / 2
        );
        game.movePlayer(position);
        final HeadlessCore core = new HeadlessCore(dimensions.windowSize(), TILES_PER_ROW);
        final Engine engine = new Engine(game, dimensions, core);
        CloudTests.data = new AnalyserManager();
        MockKeys keyState = new MockKeys(new ArrayList<>());
        for (int i = 0; i < TICKS; i += 1) {
            //place solar panels as move down
            ArrayList<Character> characters = new ArrayList<>();
            MockMouse mouseState = new MockMouse(
                    0, 0,
                    false, false, false
            );
            if (i > 50 && i < 700) {
                mouseState = new MockMouse(
                        0, 0,
                        true, false, false
                );
                characters.add('s'); //move down if frame is more then the 0th or 1st
            }
            characters.add('f'); //pave
            if (i >= 700 && i < 712) {
                characters.add('d'); //now go to the right
            }
            if (i > 712) {
                characters.add('w'); //now go up
                characters.add('r'); //place lightning rods
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

    @Test
    public void playerSpawned() {
        RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        Assert.assertEquals(
                "player should have spawned on 0th frame of game",
                0, player.getFirstFrame().getFrame()
        );
        final SpatialAnalyser playerMovement = new SpatialAnalyser(player);

        final int centerXOfScreen = dimensions.windowSize() / 2;
        final int centerYOfTopRow = dimensions.tileSize() / 2;

        final boolean visitedFirstRow = playerMovement.visitedRectangularArea(
                centerXOfScreen, centerYOfTopRow,
                dimensions.windowSize(), dimensions.tileSize()
        );
        Assert.assertTrue(
                "player should spawned within the top row of tiles",
                visitedFirstRow
        );
    }

    @Test
    public void playerDidNotMoveHorizontallyForFirst500Frames() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerMovement = new SpatialAnalyser(player);
        final int totalXMovement = playerMovement.measureOverallMoveBetween(0, 500).getX();
        Assert.assertEquals(
                "player should not have moved on the x axis at all when only 's' is down",
                0,
                totalXMovement
        );
    }

    @Test
    public void playerMovedToBottomOfScreen() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerMovement = new SpatialAnalyser(player);

        final int centerXOfScreen = dimensions.windowSize() / 2;
        final int centerYOfBottomRow = dimensions.windowSize() - dimensions.tileSize() / 2;

        final boolean visitedLastRow = playerMovement.visitedRectangularArea(
                centerXOfScreen, centerYOfBottomRow,
                dimensions.windowSize(), dimensions.tileSize()
        );
        Assert.assertTrue(
                "player should moved to bottom tile row by end of sim",
                visitedLastRow
        );
    }

    /**
     * Should have no fallback sprites rendering if we do a sprite isn't being set in a constructor!
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
    public void playerBuiltSolarPanels() {
        final List<RenderableAnalyser> panels = data.getBySpriteGroup("solarPanel");
        final int tileSize = dimensions.tileSize();
        Assert.assertEquals(
                "we expect 3 solar panels to have been placed by the player",
                3, panels.size()
        );
        final int panelsThatStayedInExpectedArea = data.count("solarPanel", (panel) -> {
            SpatialAnalyser spatial = new SpatialAnalyser(panel);
            final int x = tileSize / 2;
            final int y = dimensions.windowSize() / 2;
            final boolean inLeftColumn = spatial.stayedInRectangularArea(
                    x, y,
                    tileSize, dimensions.windowSize()
            );
            return inLeftColumn;
        });
        Assert.assertEquals(
                "We expect " + panels.size() + " panels to be placed " +
                        "on the leftmost column of tiles on the screen",
                panels.size(),
                panelsThatStayedInExpectedArea
        );
    }

    @Test
    public void cloudsSpawned() {
        final List<RenderableAnalyser> clouds = data.getBySpriteGroup("cloud");
        Assert.assertEquals("4 clouds should have been spawned", 4, clouds.size());
        final int cloudsSpawnedOnFarRight = data.count("cloud", (cloud) -> {
            final SpatialAnalyser cloudMovement = new SpatialAnalyser(cloud);
            final int screenCenterY = dimensions.windowSize() / 2;
            final boolean spawnedOnFarRight = cloudMovement.visitedRectangularArea(
                    farRightTileColumnX,
                    screenCenterY,
                    dimensions.tileSize(),
                    dimensions.windowSize()
            );
            return spawnedOnFarRight;
        });
        Assert.assertEquals(
                "Should have been " + clouds.size() + " clouds" +
                        " spawned on the far right column of tiles",
                clouds.size(), cloudsSpawnedOnFarRight
        );
    }

    /**
     * Confirm that sometime before the end of the sim the 2 clouds spawned stopped being rendered!
     * We will take the lack of rendering as a sign they were probably successfully despawned.s
     */
    @Test
    public void firstWaveCloudsDespawned() {
        final int countOfFirstWaveCloudsThatDespawned = data.count("cloud", (cloud) -> {
            if (!cloud.wasWithinFrames(0, 500)) {
                return false;
            }
            final int finalFrameCount = cloud.getLastFrame().getFrame();
            return finalFrameCount < TICKS - 1;
        });

        Assert.assertEquals(
                "We expect both clouds that were in the first wave to have been removed " +
                        "before the end of the sim",
                2, countOfFirstWaveCloudsThatDespawned
        );
    }

    @Test
    public void rainCloudsSpawned() {
        final List<RenderableAnalyser> clouds = data.getBySpriteGroup("raincloud");
        final int expectedRainCloudsCount = 4;
        Assert.assertEquals("4 Rain Clouds should have spawned " +
                        "over the lifespan of this sim",
                expectedRainCloudsCount, clouds.size()
        );
        final int rainCloudsSpawnedOnRight = data.count("cloud", (cloud) -> {
            final SpatialAnalyser cloudMovement = new SpatialAnalyser(cloud);
            final int farRightTileColumnX = dimensions.windowSize() - dimensions.tileSize() / 2;
            final int screenCenterY = dimensions.windowSize() / 2;
            final boolean spawnedOnFarRight = cloudMovement.visitedRectangularArea(
                    farRightTileColumnX,
                    screenCenterY,
                    dimensions.tileSize(),
                    dimensions.windowSize()
            );
            return spawnedOnFarRight;
        });
        Assert.assertEquals("We expect all " + expectedRainCloudsCount + " rain clouds " +
                        "to have been spawned on the far right tile column",
                expectedRainCloudsCount, rainCloudsSpawnedOnRight
        );
    }

    @Test
    public void firstWaveRainCloudsDespawned() {
        final int countOfFirstWaveCloudsThatDespawned = data.count("raincloud", (cloud) -> {
            if (!cloud.wasWithinFrames(0, 500)) {
                return false;
            }
            final int finalFrameCount = cloud.getLastFrame().getFrame();
            return finalFrameCount < TICKS - 1;
        });

        Assert.assertEquals(
                "We expect both rainclouds that were in the first wave" +
                        " to have been removed before the end of the sim",
                2, countOfFirstWaveCloudsThatDespawned
        );
    }

    @Test
    public void firstWaveCloudsReachedLeftTileColumn() {
        final int screenYCenter = dimensions.windowSize() / 2;
        final int tileSize = dimensions.tileSize();
        final int windowSize = dimensions.windowSize();
        final int firstWaveCloudsThatReachedFarLeft = data.count("cloud", (cloud) -> {
            if (!cloud.wasWithinFrames(0, 500)) {
                return false;
            }
            SpatialAnalyser cloudMovement = new SpatialAnalyser(cloud);
            final boolean firstWaveCloudsReachedFarLeftColumn = cloudMovement.visitedRectangularArea(
                    farLeftTileColumnX, screenYCenter,
                    tileSize, windowSize
            );
            return firstWaveCloudsReachedFarLeftColumn;
        });
        Assert.assertEquals(
                "2 clouds from the first spawned wave should have reached " +
                        "the far left column of tiles in their lifespan",
                2, firstWaveCloudsThatReachedFarLeft
        );
    }

    @Test
    public void firstWaveRainCloudsReachedLeftTileColumn() {
        final int screenYCenter = dimensions.windowSize() / 2;
        final int tileSize = dimensions.tileSize();
        final int windowSize = dimensions.windowSize();
        final int firstWaveRainCloudsThatReachedFarLeft = data.count("raincloud", (cloud) -> {
            if (!cloud.wasWithinFrames(0, 500)) {
                return false;
            }
            SpatialAnalyser cloudMovement = new SpatialAnalyser(cloud);
            final boolean firstWaveRainCloudsReachedFarLeftColumn =
                    cloudMovement.visitedRectangularArea(
                            farLeftTileColumnX, screenYCenter,
                            tileSize, windowSize
                    );
            return firstWaveRainCloudsReachedFarLeftColumn;
        });
        Assert.assertEquals(
                "2 rain clouds from the first spawned wave should have reached " +
                        "the far left column of tiles within their lifespan",
                2, firstWaveRainCloudsThatReachedFarLeft
        );
    }


    @Test
    public void firstWaveAcidCloudsReachedLeftTileColumn() {
        final int screenYCenter = dimensions.windowSize() / 2;
        final int tileSize = dimensions.tileSize();
        final int windowSize = dimensions.windowSize();
        final int firstWaveAcidCloudsThatReachedFarLeft = data.count("acidcloud", (cloud) -> {
            if (!cloud.wasWithinFrames(0, 500)) {
                return false;
            }
            SpatialAnalyser cloudMovement = new SpatialAnalyser(cloud);
            final boolean firstWaveAcidCloudsReachedFarLeftColumn =
                    cloudMovement.visitedRectangularArea(
                            farLeftTileColumnX, screenYCenter,
                            tileSize, windowSize
                    );
            return firstWaveAcidCloudsReachedFarLeftColumn;
        });
        Assert.assertEquals(
                "the 1 acid cloud from the first spawned wave should have reached " +
                        "the far left column of tiles within its lifespan",
                1, firstWaveAcidCloudsThatReachedFarLeft
        );
    }

    @Test
    public void acidCloudSpawned() {
        final List<RenderableAnalyser> clouds = data.getBySpriteGroup("acidcloud");
        Assert.assertEquals(
                "Expect 2 acid clouds to have been spawned across the entire sim lifespan",
                2, clouds.size()
        );
        final int acidCloudsThatSpawnedOnFarRight = data.count("acidcloud", (cloud) -> {
            final SpatialAnalyser cloudMovement = new SpatialAnalyser(cloud);
            final int farRightTileColumnX = dimensions.windowSize() - dimensions.tileSize() / 2;
            final int screenCenterY = dimensions.windowSize() / 2;
            final boolean spawnedOnFarRight = cloudMovement.visitedRectangularArea(
                    farRightTileColumnX,
                    screenCenterY,
                    dimensions.tileSize(),
                    dimensions.windowSize()
            );
            return spawnedOnFarRight;
        });
        Assert.assertEquals(
                "expect 2 acid clouds to have been spawned on the far right side " +
                        "over the lifespan of this sim",
                2, acidCloudsThatSpawnedOnFarRight
        );
    }

    @Test
    public void acidCloudsDespawned() {
        final int countOfFirstWaveCloudsThatDespawned = data.count("acidcloud", (cloud) -> {
            if (!cloud.wasWithinFrames(0, 500)) {
                return false;
            }
            final int finalFrameCount = cloud.getLastFrame().getFrame();
            return finalFrameCount < TICKS - 1;
        });

        Assert.assertEquals(
                "We expect the acidcloud that was in the first wave" +
                        " to have been removed before the end of the sim",
                1,
                countOfFirstWaveCloudsThatDespawned
        );
    }

    @Test
    public void solarPanelsObscured() {
        final Sprite off = SpriteGallery.solarPanel.getSprite("off");
        final int panelsThatHaveBeenOff = data.count("solarPanel", (panel) -> {
            return panel.hasSprite(off);
        });
        Assert.assertEquals(
                "2 panels should have at some point during this sim been " +
                        "obscured and flipped to their 'off' sprite state when clouds went over",
                2, panelsThatHaveBeenOff
        );
    }

    @Test
    public void solarPanelsDamaged() {
        final Sprite damaged = SpriteGallery.solarPanel.getSprite("damaged");
        final int panelsThatHaveBeenDamaged = data.count("solarPanel", (panel) -> {
            return panel.hasSprite(damaged);
        });
        Assert.assertEquals(
                "1 panel should have at some point " +
                        "during the sim been damaged and flipped to its damaged state when " +
                        "acidcloud went over",
                1, panelsThatHaveBeenDamaged
        );
    }

    @Test
    public void lightningRodBuilt() {
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        Assert.assertEquals(
                "5 lightning rods should have been built during this sim",
                5, rods.size()
        );
    }

    @Test
    public void lightningRodBuiltInSecondLeftColumn() {
        final int secondLeftTileColumnX = (dimensions.tileSize() / 2) * 2;
        final int screenCenterY = dimensions.windowSize() / 2;
        final int rodsBuiltInSecondLeftColumn = data.count("lightningrod", (rod) -> {
            final SpatialAnalyser rodSpatial = new SpatialAnalyser(rod);
            final boolean rodIsInSecondLeftTileColumn = rodSpatial.stayedInRectangularArea(
                    secondLeftTileColumnX,
                    screenCenterY,
                    dimensions.tileSize(),
                    dimensions.windowSize()
            );
            return rodIsInSecondLeftTileColumn;
        });
        Assert.assertEquals(
                "5 rods should have been built in second left column",
                5, rodsBuiltInSecondLeftColumn
        );
    }

    @Test
    public void cloudsRenderAllExpectedSprites() {
        final List<Sprite> cloudSprites = SpriteGallery.cloud.getSprites();
        final int cloudCount = data.getBySpriteGroup("cloud").size();
        final int cloudsWithAllExpectedAcidCloudSprites = data.count("cloud",
                (cloud) -> {
                    //Confirm that this specific rain cloud rendered
                    //each of the expected sprites in its lifespan
                    for (Sprite sprite : cloudSprites) {
                        if (!cloud.hasSprite(sprite)) {
                            return false;
                        }
                    }
                    return true;
                });
        Assert.assertEquals(
                cloudCount + "rain clouds should have rendered each of the sprites " +
                        "from their sprite gallery in this sim.",
                cloudCount,
                cloudsWithAllExpectedAcidCloudSprites
        );
    }

    @Test
    public void rainCloudsRenderAllExpectedSprites() {
        final List<Sprite> rainCloudSprites = SpriteGallery.raincloud.getSprites();
        final int rainCloudCount = data.getBySpriteGroup("raincloud").size();
        final int rainCloudsWithAllExpectedRainCloudSprites = data.count("raincloud",
                (raincloud) -> {
                    //Confirm that this specific rain cloud rendered
                    //each of the expected sprites in its lifespan
                    for (Sprite sprite : rainCloudSprites) {
                        if (!raincloud.hasSprite(sprite)) {
                            return false;
                        }
                    }
                    return true;
                });
        Assert.assertEquals(
                rainCloudCount + "rain clouds should have rendered each of the sprites " +
                        "from their sprite gallery in this sim.",
                rainCloudCount,
                rainCloudsWithAllExpectedRainCloudSprites
        );
    }

    @Test
    public void acidCloudsRenderAllExpectedSprites() {
        final List<Sprite> acidCloudSprites = SpriteGallery.acidcloud.getSprites();
        final int acidCloudCount = data.getBySpriteGroup("acidcloud").size();
        final int acidCloudsWithAllExpectedAcidCloudSprites = data.count("acidcloud",
                (acidcloud) -> {
                    //Confirm that this specific acid cloud rendered each of the expected sprites
                    // in its lifespan
                    for (Sprite sprite : acidCloudSprites) {
                        if (!acidcloud.hasSprite(sprite)) {
                            return false;
                        }
                    }
                    return true;
                });
        Assert.assertEquals(
                acidCloudCount + " acid clouds should have rendered each of the sprites " +
                        "from their sprite gallery in this sim.",
                acidCloudCount,
                acidCloudsWithAllExpectedAcidCloudSprites
        );
    }
}