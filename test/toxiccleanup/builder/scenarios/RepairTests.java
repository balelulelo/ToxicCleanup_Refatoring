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
 * Tests repairing our various machines,
 * Spawns a map with a column of machines that will get hit by lightning.
 * Player spawns, player preps machines,
 * wait for long enough enough for machines to take lightning hits,
 * then place rod at bottom that has acid cloud, turn left, then up placing rods.
 * at top 1 tile right then down repairing.
 * <p>
 * Check for damaged states of non rods on final frames, none should be damaged after repair
 * <p>
 * Player will then walk down and place lightning rods to protect them, then step to the right.
 * Then go up the damaged column repairing said machines, that should now be protected
 * from further lightning strikes.
 * Should be able to find undamaged initial sprites for machines,
 * damaged sprites in the middle,
 * undamaged sprites again at the end
 *
 * <h4> User Inputs for Sim </h4>
 * <hr/>
 * <p>Ticks 0 - 899</p>
 * <ul>
 *     <li>S key down</li>
 *     <li>F key down</li>
 * </ul>
 * <hr/>
 * <p>Ticks 0 - 49 and 51 - 199</p>
 * <ul>
 *     <li>Left mouse down</li>
 * </ul>
 * <hr/>
 * <p>Ticks 201+</p>
 * <ul>
 *     <li>R key down</li>
 * </ul>
 * <hr/>
 * <p>Ticks 1001 - 1199</p>
 * <ul>
 *     <li>Left mouse down</li>
 * </ul>
 * <hr/>
 * <p>Ticks 1200+</p>
 * <ul>
 *     <li>W key down</li>
 *     <li>E key down</li>
 * </ul>
 * <p>
 * maps used:
 * "resources/testmaps/microtests/repair.map",
 * "resources/testmaps/microtests/repair_weather.map"
 */
public class RepairTests {
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
                        "resources/testmaps/microtests/repair.map",
                        "resources/testmaps/microtests/repair_weather.map"
                );

        game.movePlayer(new Position(halfTile * 3, halfTile));
        final HeadlessCore core = new HeadlessCore(dimensions.windowSize(), TILES_PER_ROW);
        final Engine engine = new Engine(game, dimensions, core);
        RepairTests.data = new AnalyserManager();
        MockKeys keyState = new MockKeys(new ArrayList<>());
        for (int i = 0; i < TICKS; i += 1) {
            //place solar panels as move down
            ArrayList<Character> characters = new ArrayList<>();
            MockMouse mouseState = new MockMouse(
                    0, 0,
                    false, false, false
            );
            if (i < 50) {
                mouseState = new MockMouse(
                        0, 0,
                        true, false, false
                );
            }
            if (i > 50 && i < 200) {
                mouseState = new MockMouse(
                        0, 0,
                        true, false, false
                );
            }
            if (i < 900) { //go down and pave as go
                characters.add('s');
                characters.add('f');
            }
            if (i >= 1200) {
                characters.add('w'); //go back up
                characters.add('e'); //go back up
            }
            if (i > 1000 && i < 1200) {
                mouseState = new MockMouse(
                        0, 0,
                        true, false, false
                );
            }

            if (i > 200) {
                characters.add('r');
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
     * Should have no default sprites!
     */
    @Test
    public void noDefaultSprites() {
        Assert.assertEquals(
                "0 default sprites should have been spawned!",
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
    public void playerDidNotMoveHorizontally() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerMovement = new SpatialAnalyser(player);
        final int totalXMovement = playerMovement.measureOverallMove().getX();
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

    @Test
    public void builtTeleporter() {
        final String target = "teleporter";
        final List<RenderableAnalyser> machine = data.getBySpriteGroup(target);
        Assert.assertEquals(
                "should have built 1 " + target,
                1,
                machine.size()
        );
    }

    @Test
    public void builtSolar() {
        final String target = "solarPanel";
        final List<RenderableAnalyser> machine = data.getBySpriteGroup(target);
        Assert.assertEquals(
                "should have built 1 " + target,
                2,
                machine.size()
        );
    }

    @Test
    public void builtRod() {
        final String target = "lightningrod";
        final List<RenderableAnalyser> machine = data.getBySpriteGroup(target);
        Assert.assertEquals(
                "should have built 1 " + target,
                1,
                machine.size()
        );
    }

    @Test
    public void builtPump() {
        final String target = "pump";
        final List<RenderableAnalyser> machine = data.getBySpriteGroup(target);
        Assert.assertEquals(
                "should have built 1 " + target,
                1,
                machine.size()
        );
    }

    @Test
    public void pumpDamaged() {
        final Sprite damaged = SpriteGallery.pump.getSprite("damaged");
        final int damagedPumpsCount = data.count("pump", pump -> {
            return pump.hasSprite(damaged);
        });
        Assert.assertEquals("Expect 1 pump to have been damaged", 1, damagedPumpsCount);
    }


    @Test
    public void teleporterDamaged() {
        final Sprite damaged = SpriteGallery.teleporter.getSprite("damaged");
        final int teleportersDamaged = data.count("teleporter", teleporter -> {
            return teleporter.hasSprite(damaged);
        });
        Assert.assertEquals("Expect 1 teleporter to have been damaged", 1, teleportersDamaged);
    }

    @Test
    public void teleporterRepair() {
        final Sprite damaged = SpriteGallery.teleporter.getSprite("damaged");
        final int damagedCount = data.count("teleporter", (teleporter) -> {
            return teleporter.hasSpriteBetween(damaged, TICKS - 2, TICKS - 1);
        });
        Assert.assertEquals(
                "Should have 0 teleporters in a damaged state in final frames",
                0, damagedCount
        );
    }

    @Test
    public void pumpRepair() {
        final Sprite damaged = SpriteGallery.pump.getSprite("damaged");
        final int damagedCount = data.count("pump", (pump) -> {
            return pump.hasSpriteBetween(damaged, TICKS - 2, TICKS - 1);
        });
        Assert.assertEquals(
                "Should have 0 pumps in a damaged state in final frames",
                0, damagedCount
        );
    }

    @Test
    public void solarRepair() {
        final Sprite damaged = SpriteGallery.solarPanel.getSprite("damaged");
        final int damagedCount = data.count("solarPanel", (panel) -> {
            return panel.hasSpriteBetween(damaged, TICKS - 2, TICKS - 1);
        });
        Assert.assertEquals(
                "Should have no solarPanels in a damaged state in final frames",
                0, damagedCount
        );
    }
}
