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
import test_utils.analysers.LetterAnalyser;
import test_utils.analysers.RenderableAnalyser;
import test_utils.analysers.SpatialAnalyser;
import test_utils.mocks.HeadlessCore;
import test_utils.mocks.MockEngineState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.builder.world.WorldLoadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic sim Tests that confirm that the player can pump a toxic field clear and win.
 * Player will move to the right, place a pump and continue moving right
 * while the pump clears the toxic field and wins the game.
 * <p>
 * Some standard weather events will occur during the game,
 * so we can test that the game stops correctly when the player wins.
 * <h4>
 * Maps:
 * </h4>
 * <ul>
 *  <li>"resources/testmaps/microtests/win.map",</li>
 *  <li>"resources/testmaps/microtests/win_weather.map"</li>
 * </ul>
 *
 * <h4> User Inputs for Sim </h4>
 * <p>All Ticks </p>
 * <ul>
 *     <li>Left Mouse Down</li>
 *     <li>D key down</li>
 * </ul>
 */
public class WinTests {
    private static final int SIZE = 800;
    private static final int TILES_PER_ROW = 5;
    private static final int TICKS = 950;
    private static AnalyserManager data;
    private static final Dimensions dimensions = new TileGrid(TILES_PER_ROW, SIZE);
    private static int farLeftTileColumnX;

    @Before
    public void setUp() throws IOException, WorldLoadException {
        farLeftTileColumnX = dimensions.tileSize() / 2;
        final ToxicCleanup game =
                new ToxicCleanup(
                        dimensions,
                        new Position(20, 20),
                        "resources/testmaps/microtests/win.map",
                        "resources/testmaps/microtests/win_weather.map"
                );
        final Position position = new Position(
                dimensions.tileSize() / 2,
                dimensions.windowSize() / 2
        );
        game.movePlayer(position);
        final HeadlessCore core = new HeadlessCore(dimensions.windowSize(), TILES_PER_ROW);
        final Engine engine = new Engine(game, dimensions, core);
        data = new AnalyserManager();
        MockKeys keyState = new MockKeys(new ArrayList<>());
        for (int i = 0; i < TICKS; i += 1) {
            //place solar panels as move down
            ArrayList<Character> characters = new ArrayList<>();
            MockMouse mouseState = new MockMouse(
                    0, 0,
                    true, false, false
            );
            characters.add('d');
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

        final int centerY = dimensions.windowSize() / 2;

        final boolean visitedCenterTileOfFarLeftCol = playerMovement.visitedRectangularArea(
                farLeftTileColumnX, centerY,
                dimensions.windowSize(), dimensions.tileSize()
        );
        Assert.assertTrue(
                "player have at some point been in the far left col of tiles in the center tile ",
                visitedCenterTileOfFarLeftCol
        );
    }

    @Test
    public void playerDidMoveHorizontally() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerMovement = new SpatialAnalyser(player);
        final int totalXMovement = playerMovement.measureOverallMove().getX();
        final int minXDistance = dimensions.tileSize() * 2;
        final boolean movementThresholdPassed = minXDistance < totalXMovement;
        Assert.assertTrue(
                "player should have moved significantly on the x axis when 'd' is down, " +
                        "at *least* 2 tiles worth",
                movementThresholdPassed
        );
    }

    @Test
    public void playerDidNotMoveVertically() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerMovement = new SpatialAnalyser(player);
        final int totalYMovement = playerMovement.measureOverallMove().getY();
        Assert.assertEquals(
                "player should not have moved on the y axis at all when only 'd' is down",
                0,
                totalYMovement
        );
    }

    @Test
    public void simStartedWithExpectedHearts() {
        final int initialHeartCount = data.count("heart", (heart) -> {
            return heart.wasInFrame(0);
        });

        Assert.assertEquals(
                "player should start with 10 hearts",
                10, initialHeartCount
        );
    }

    @Test
    public void builtPump() {
        final List<RenderableAnalyser> pumps = data.getBySpriteGroup("pump");
        Assert.assertEquals("player should have built 1 pump in sim", 1, pumps.size());
    }

    @Test
    public void pumpShouldHaveBeenRemovedBeforeEnd() {
        final int pumpsAtEnd = data.count("pump", (pump) -> {
            return pump.getLastFrame().getFrame() == TICKS - 1;
        });
        Assert.assertEquals(
                "no pumps should still have been around by the final frame",
                0, pumpsAtEnd
        );
    }

    @Test
    public void shouldHaveOneToxicField() {
        final List<RenderableAnalyser> toxicFields = data.getBySpriteGroup("toxicField");
        Assert.assertEquals(
                "Expect there to have been exactly one toxic in this test",
                1, toxicFields.size()
        );
    }

    @Test
    public void allToxicFieldSpriteStatesShouldHaveBeenRendered() {
        final List<Sprite> sprites = SpriteGallery.toxicField.getSprites();

        for (Sprite sprite : sprites) {
            final int toxicFieldsThatHaveHadSprite = data.count("toxicField", (toxicField) -> {
                return toxicField.hasSprite(sprite);
            });
            Assert.assertEquals(
                    "Toxic field should have rendered sprite state:'"
                            + sprite.getLabel() + "' for toxic field",
                    1, toxicFieldsThatHaveHadSprite
            );
        }
    }

    @Test
    public void toxicFieldShouldHaveFlowered() {
        final Sprite cleansed = SpriteGallery.toxicField.getSprite("cleanupdone");
        final int floweredToxicFields = data.count("toxicField", (toxicField) -> {
            return toxicField.hasSprite(cleansed);
        });
        Assert.assertEquals(
                "expect the 1 toxic field to have reached it's flowered state",
                1, floweredToxicFields
        );
    }

    @Test
    public void cloudSpawningShouldHaveFrozenAfterWin() {
        final RenderableAnalyser firstW = data.filter("letter", (rawLetter) -> {
            final LetterAnalyser letter = new LetterAnalyser(rawLetter);
            return letter.is("W");
        }).getFirst();

        //this is the frame we will assume that the win state occurred
        final int firstWFrame = firstW.getFirstFrame().getFrame();

        final int cloudsSpawnedAfterW = data.count("cloud", (cloud) -> {
            return cloud.getFirstFrame().getFrame() > firstWFrame;
        });
        Assert.assertEquals(
                "no clouds should have been spawned after W state",
                0, cloudsSpawnedAfterW
        );
    }

    @Test
    public void lightningSpawningShouldHaveFrozenAfterWin() {
        final RenderableAnalyser firstW = data.filter("letter", (rawLetter) -> {
            final LetterAnalyser letter = new LetterAnalyser(rawLetter);
            return letter.is("W");
        }).getFirst();

        //this is the frame we will assume that the win state occurred
        final int firstWFrame = firstW.getFirstFrame().getFrame();

        final int cloudsSpawnedAfterW = data.count("lightning", (bolt) -> {
            return bolt.getFirstFrame().getFrame() > firstWFrame;
        });
        Assert.assertEquals(
                "no lightning should have been spawned after W state",
                0, cloudsSpawnedAfterW
        );
    }

    @Test
    public void raincloudSpawningShouldHaveFrozenAfterWin() {
        final RenderableAnalyser firstW = data.filter("letter", (rawLetter) -> {
            final LetterAnalyser letter = new LetterAnalyser(rawLetter);
            return letter.is("W");
        }).getFirst();

        //this is the frame we will assume that the win state occurred
        final int firstWFrame = firstW.getFirstFrame().getFrame();

        final int cloudsSpawnedAfterW = data.count("raincloud", (cloud) -> {
            return cloud.getFirstFrame().getFrame() > firstWFrame;
        });
        Assert.assertEquals(
                "no raincloud should have been spawned after W state",
                0, cloudsSpawnedAfterW
        );
    }


    @Test
    public void acidcloudSpawningShouldHaveFrozenAfterWin() {
        final RenderableAnalyser firstW = data.filter("letter", (rawLetter) -> {
            final LetterAnalyser letter = new LetterAnalyser(rawLetter);
            return letter.is("W");
        }).getFirst();

        //this is the frame we will assume that the win state occurred
        final int firstWFrame = firstW.getFirstFrame().getFrame();

        final int cloudsSpawnedAfterW = data.count("acidcloud", (cloud) -> {
            return cloud.getFirstFrame().getFrame() > firstWFrame;
        });
        Assert.assertEquals(
                "no acidclouds should have been spawned after W state",
                0, cloudsSpawnedAfterW
        );
    }

    @Test
    public void weatherMovementShouldHaveFrozenAfterWin() {
        final RenderableAnalyser firstW = data.filter("letter", (rawLetter) -> {
            final LetterAnalyser letter = new LetterAnalyser(rawLetter);
            return letter.is("W");
        }).getFirst();

        //this is the frame we will assume that the win state occurred
        final int firstWFrame = firstW.getFirstFrame().getFrame();

        final int cloudCountThatMovedPastWFrame = data.count("cloud", (cloud) -> {
            final SpatialAnalyser cloudSpatial = new SpatialAnalyser(cloud);
            final boolean inFrameRange = cloud.wasWithinFrames(firstWFrame, TICKS - 1);
            final int xMove = cloudSpatial.measureOverallMoveBetween(firstWFrame, TICKS - 1).getX();
            final int yMove = cloudSpatial.measureOverallMoveBetween(firstWFrame, TICKS - 1).getY();
            return inFrameRange && (xMove != 0 || yMove != 0);
        });

        Assert.assertEquals(
                "no clouds should have any movement after win state has occured",
                0,
                cloudCountThatMovedPastWFrame
        );
    }

    @Test
    public void youWinShouldHaveBeenOnScreen() {
        final List<RenderableAnalyser> rawLetters = data.getBySpriteGroup("letter");
        final List<LetterAnalyser> finalAlphabeticLetters = new ArrayList<>();
        for (RenderableAnalyser raw : rawLetters) {
            LetterAnalyser letter = new LetterAnalyser(raw);
            final boolean onLastFrame = letter.getLastFrame().getFrame() == TICKS - 1;
            if (onLastFrame && letter.isAlphabetic()) {
                finalAlphabeticLetters.add(letter);
            }
        }

        Assert.assertEquals(
                "should have exactly 6 alphabetic characters on the screen" +
                        " on final frame if won (you win) is 6 alphabetic letters",
                6,
                finalAlphabeticLetters.size()
        );

        for (LetterAnalyser letter : finalAlphabeticLetters) {
            final boolean isY = letter.is("Y");
            final boolean isO = letter.is("O");
            final boolean isU = letter.is("U");
            final boolean isW = letter.is("W");
            final boolean isI = letter.is("I");
            final boolean isN = letter.is("N");

            final boolean isValidLetter = (isY || isO || isU || isW || isI || isN);
            if (!isValidLetter) {
                Assert.fail("Invalid Alphabetic letter printed on screen in final frame");
            }
        }
    }
}