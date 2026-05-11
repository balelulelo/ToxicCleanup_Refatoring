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
 * Tests damaging of our various machines,
 * including:
 * <ul>
 * <li>Lightning Rod to protect a specific solar panel from a lightning strike</li>
 * <li>AcidCloud hitting a different Lightning Rod <- confirm lightning near it does
 * NOT get moved after this is damaged</li>
 * <li>AcidCloud hitting a different solar panel <- confirm power changes visually</li>
 * <li>AcidCloud hitting a toxic field with a pump <- confirm pump stops</li>
 * <li>Lightning hitting a pump <- confirm pump stops</li>
 * <li>Lightning hitting a panel <- confirm panel stops working</li>
 * </ul>
 * <p>
 * To do this our play will spawn and then move down the screen placing solar panels,
 * at the bottom of the screen they will take one step to the right and then
 * move up the screen placing lightning rods for roughly 50 ticks.
 * Maps used:
 * "resources/testmaps/microtests/damage.map",
 * "resources/testmaps/microtests/damage_weather.map"
 *
 * <h4> User Inputs for Sim </h4>
 * <p>All Ticks:</p>
 * <ul>
 * <li>F key down</li>
 * </ul>
 * <hr/>
 * <p>Ticks 51 - 699</p>
 * <ul>
 *     <li>S key down</li>
 *     <li>F key down</li>
 *     <li>Left mouse down</li>
 * </ul>
 *
 * <p>Ticks 701 - 711</p>
 * <ul>
 *     <li>F key down</li>
 *     <li>D key down</li>
 * </ul>
 *
 * <hr/>
 *
 * <p>Ticks 712 - 724</p>
 * <ul>
 *     <li>Left mouse down</li>
 * </ul>
 *
 * <hr/>
 *
 * <p>Ticks 806 - 859</p>
 * <ul>
 *     <li>W key down</li>
 *     <li>F key down</li>
 *     <li>R key down</li>
 * </ul>

 */
public class DamageTests {
    private static final int SIZE = 800;
    private static final int TILES_PER_ROW = 5;
    private static final int TICKS = 1050;
    private static AnalyserManager data;
    private static final Dimensions dimensions = new TileGrid(TILES_PER_ROW, SIZE);

    @Before
    public void setUp() throws IOException, WorldLoadException {
        final int halfTile = dimensions.tileSize() / 2;
        final ToxicCleanup game =
                new ToxicCleanup(
                        dimensions,
                        new Position(halfTile * 3, halfTile),
                        "resources/testmaps/microtests/damage.map",
                        "resources/testmaps/microtests/damage_weather.map"
                );

        game.movePlayer(new Position(halfTile, halfTile));
        final HeadlessCore core = new HeadlessCore(dimensions.windowSize(), TILES_PER_ROW);
        final Engine engine = new Engine(game, dimensions, core);
        DamageTests.data = new AnalyserManager();
        MockKeys keyState = new MockKeys(new ArrayList<>());
        for (int i = 0; i < TICKS; i += 1) {
            //place solar panels as move down
            ArrayList<Character> characters = new ArrayList<>();
            MockMouse mouseState = new MockMouse(
                    0, 0,
                    false, false, false
            );
            if (i > 50 && i < 700) {
                characters.add('s'); //move down if frame is more then the 0th or 1st
                characters.add('f'); //pave
                mouseState = new MockMouse(
                        0, 0,
                        true, false, false
                );
            }
            if (i > 700 && i < 712) {
                characters.add('f'); //pave
                characters.add('d'); //now go to the right
            }


            if (i >= 712 && i < 725) {
                mouseState = new MockMouse(
                        0, 0,
                        true, false, false
                );
            }
            if (i > 805 && i < 860) {
                characters.add('f'); //pave
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
                data.getBySpriteGroup("default").size()
        );
    }

    @Test
    public void playerSpawned() {
        RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        Assert.assertEquals(
                "player should have spawned on 0th frame of game",
                0, player.getFirstFrame().getFrame()
        );
        final SpatialAnalyser playerSpatial = new SpatialAnalyser(player);

        final int centerXOfScreen = dimensions.windowSize() / 2;
        final int centerYOfTopRow = dimensions.tileSize() / 2;

        final boolean visitedFirstRow = playerSpatial.visitedRectangularArea(
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
    public void playerWalkedToBottomOfScreen() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerSpatial = new SpatialAnalyser(player);

        final int centerXOfScreen = dimensions.windowSize() / 2;
        final int centerYOfBottomRow = dimensions.windowSize() - dimensions.tileSize() / 2;

        final boolean visitedLastRow = playerSpatial.visitedRectangularArea(
                centerXOfScreen, centerYOfBottomRow,
                dimensions.windowSize(), dimensions.tileSize()
        );
        Assert.assertTrue(
                "player should have walked to bottom tile row by end of sim",
                visitedLastRow
        );
    }

    @Test
    public void builtSolarPanels() {
        final List<RenderableAnalyser> panels = data.getBySpriteGroup("solarPanel");
        final int expectedPanelCount = 3;
        Assert.assertEquals(
                "player should have built " + expectedPanelCount + " panels",
                expectedPanelCount,
                panels.size()
        );

        final int tile = dimensions.tileSize();
        final int halfTile = dimensions.tileSize() / 2;
        final int panelsInColumn = data.count("solarPanel", panel -> {
                    final SpatialAnalyser spatial = new SpatialAnalyser(panel);
                    return spatial.stayedInRectangularArea(
                            halfTile, dimensions.windowSize() / 2,
                            tile, dimensions.windowSize()
                    );
                }
        );
        Assert.assertEquals(
                "all " + expectedPanelCount + " panel(s) should have been " +
                        "built in the far left column",
                expectedPanelCount,
                panelsInColumn
        );
    }

    @Test
    public void builtPumps() {
        final List<RenderableAnalyser> pumps = data.getBySpriteGroup("pump");
        final int pumpCount = 1;
        Assert.assertEquals(pumpCount + " pump(s) should have been built by player",
                pumpCount,
                pumps.size()
        );
        final int tile = dimensions.tileSize();
        final int halfTile = dimensions.tileSize() / 2;
        final int secondLeftColumnX = halfTile + tile;
        final int pumpsInColumn = data.count("pump", pump -> {
                    final SpatialAnalyser spatial = new SpatialAnalyser(pump);
                    return spatial.stayedInRectangularArea(
                            secondLeftColumnX, dimensions.windowSize() / 2,
                            tile, dimensions.windowSize()
                    );
                }
        );
        Assert.assertEquals(
                "all " + pumpCount + " pump(s) should have been " +
                        "built in the 2nd from left column",
                pumpCount,
                pumpsInColumn
        );
    }

    @Test
    public void builtRods() {
        final List<RenderableAnalyser> rods = data.getBySpriteGroup("lightningrod");
        final int rodCount = 2;
        Assert.assertEquals(rodCount + " rod(s) should have been built by player",
                rodCount,
                rods.size()
        );
        final int tile = dimensions.tileSize();
        final int halfTile = dimensions.tileSize() / 2;
        final int secondLeftColumnX = halfTile + tile;
        final int rodsInColumn = data.count("lightningrod", rod -> {
                    final SpatialAnalyser spatial = new SpatialAnalyser(rod);
                    return spatial.stayedInRectangularArea(
                            secondLeftColumnX, dimensions.windowSize() / 2,
                            tile, dimensions.windowSize()
                    );
                }
        );
        Assert.assertEquals(
                "all " + rodCount + " rod(s) should have been " +
                        "built in the 2nd from left column",
                rodCount,
                rodsInColumn
        );
    }

    @Test
    public void builtTeleporter() {
        final List<RenderableAnalyser> teleporter = data.getBySpriteGroup("teleporter");
        final int expectedTeleporterCount = 1;
        Assert.assertEquals(
                expectedTeleporterCount + "teleporters should have been built by player",
                expectedTeleporterCount,
                teleporter.size()
        );
    }

    @Test
    public void spawnedAcidClouds() {
        final List<RenderableAnalyser> acidclouds = data.getBySpriteGroup("acidcloud");
        final int expectedAcidCloudCount = 9;
        Assert.assertEquals(
                expectedAcidCloudCount + " acidclouds should have been spawned " +
                        "over the lifespan of this sim",
                expectedAcidCloudCount,
                acidclouds.size()
        );
    }

    @Test
    public void spawnedLightning() {
        final List<RenderableAnalyser> lightning = data.getBySpriteGroup("lightning");
        final int expectedBoltCount = 80;
        Assert.assertEquals(
                "Should have spawned "
                        + expectedBoltCount
                        + "lightning bolts over sim lifespan",
                expectedBoltCount,
                lightning.size()
        );

        final int tile = dimensions.tileSize();
        final int halfTile = dimensions.tileSize() / 2;
        final int secondFromLeftColumnX = halfTile + tile;
        final int secondFromRightColumnX = dimensions.windowSize() - (halfTile + tile);
        final int boltsInValidColumns = data.count("lightning", bolt -> {
                    final SpatialAnalyser spatial = new SpatialAnalyser(bolt);
                    final boolean inSecondFromLeftCol = spatial.stayedInRectangularArea(
                            secondFromLeftColumnX, dimensions.windowSize() / 2,
                            tile, dimensions.windowSize()
                    );
                    final boolean inSecondFromRightCol = spatial.stayedInRectangularArea(
                            secondFromRightColumnX, dimensions.windowSize() / 2,
                            tile, dimensions.windowSize()
                    );
                    return inSecondFromLeftCol || inSecondFromRightCol;
                }
        );
        Assert.assertEquals(
                "all " + expectedBoltCount + " lightning bolt(s) should have been " +
                        "spawned in the (2nd from left column or 2nd from right column)",
                expectedBoltCount,
                boltsInValidColumns
        );
    }

    @Test
    public void builtLightningRodsDamagedByAcidCloud() {
        final Sprite damaged = SpriteGallery.lightningrod.getSprite("damaged");
        int rodsThatHaveBeenDamaged = data.count("lightningrod", rod -> {
            return rod.hasSpriteBetween(damaged, 0, 900);
        });
        final int amountOfRodsThatShouldBeDamaged = 2;
        Assert.assertEquals(
                amountOfRodsThatShouldBeDamaged + " rods should have at some point " +
                        "during the sim been damaged and flipped to its damaged state when " +
                        "acidcloud went over",
                amountOfRodsThatShouldBeDamaged, rodsThatHaveBeenDamaged
        );
    }

    @Test
    public void teleporterDamagedByLightning() {
        final Sprite damaged = SpriteGallery.teleporter.getSprite("damaged");
        final int teleportersDamaged = data.count("teleporter", teleporter -> {
            return teleporter.hasSprite(damaged);
        });
        Assert.assertEquals("Expect 1 teleporter to have been damaged", 1, teleportersDamaged);
    }

    @Test
    public void pumpDamaged() {
        final Sprite damaged = SpriteGallery.pump.getSprite("damaged");
        final int damagedPumpsCount = data.count("pump", pump -> {
            return pump.hasSprite(damaged);
        });
        Assert.assertEquals(
                "Expect 1 pump to have been damaged",
                1, damagedPumpsCount
        );
    }
}
