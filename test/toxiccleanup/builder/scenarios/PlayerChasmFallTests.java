package toxiccleanup.builder.scenarios;

import toxiccleanup.engine.Engine;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.renderer.TileGrid;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import test_utils.analysers.AnalyserManager;
import test_utils.analysers.RenderableAnalyser;
import test_utils.analysers.SpatialAnalyser;
import test_utils.mocks.HeadlessCore;
import test_utils.mocks.MockEngineState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.builder.world.WorldLoadException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Tests that confirm that the player is taking falling damage
 * and dying when they walk on top of a chasm
 *
 *
 * <h4> User Inputs for Sim </h4>
 * <p>All Ticks </p>
 * <ul>
 *     <li>D key down</li>
 * </ul>
 *
 * maps used:
 * "resources/testmaps/microtests/player_chasm.map",
 * "resources/testmaps/microtests/player_chasm_weather.map"
 */
public class PlayerChasmFallTests {
    private static final int SIZE = 800;
    private static final int TILES_PER_ROW = 5;
    private static final int TICKS = 850;
    private static AnalyserManager data;
    private static final Dimensions dimensions = new TileGrid(TILES_PER_ROW, SIZE);
    private static int farLeftTileColumnX;
    private static int farRightTileColumnX;

    @Before
    public void setUp() throws IOException, WorldLoadException {
        farLeftTileColumnX = dimensions.tileSize() / 2;
        farRightTileColumnX = dimensions.windowSize() - dimensions.tileSize() / 2;
        final ToxicCleanup game =
                new ToxicCleanup(
                        dimensions,
                        new Position(20, 20),
                        "resources/testmaps/microtests/player_chasm.map",
                        "resources/testmaps/microtests/player_chasm_weather.map"
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
                    false, false, false
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
    public void simEndedWithExpectedLackOfHearts() {
        final int finalHeartCount = data.count("heart", (heart) -> {
            return heart.wasInFrame(TICKS - 1);
        });
        Assert.assertEquals(
                "player should have 0 hearts by the end of the sim",
                0, finalHeartCount
        );
    }

    @Test
    public void playerCrossedLeftEdgeChasmIntoChasm() {
        final Sprite leftEdgeArt = SpriteGallery.chasm.getSprite("left");

        final RenderableAnalyser leftEdgeTile = data.filter("chasm", (chasm) -> {
            return chasm.hasSprite(leftEdgeArt);
        }).getFirst();

        final Positionable leftChasmEdgeTilePosition = new Position(
                leftEdgeTile.getFirstFrame().getX(),
                leftEdgeTile.getFirstFrame().getY()
        );

        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerSpatial = new SpatialAnalyser(player);

        //confirm they were ever in it
        final boolean playerVisitedChasmEdge = playerSpatial.visitedRectangularArea(
                leftChasmEdgeTilePosition.getX(), leftChasmEdgeTilePosition.getY(),
                dimensions.tileSize(), dimensions.tileSize()
        );
        Assert.assertTrue(
                "We expect player to have cross over the left chasm edge during sim",
                playerVisitedChasmEdge
        );
    }

    @Test
    public void playerSteppedIntoChasm() {
        final Sprite chasmArt = SpriteGallery.chasm.getSprite("default");

        final RenderableAnalyser chasmTile = data.filter("chasm", (chasm) -> {
            return chasm.hasSprite(chasmArt);
        }).getFirst();

        final Positionable chasmTilePosition = new Position(
                chasmTile.getFirstFrame().getX(),
                chasmTile.getFirstFrame().getY()
        );

        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerSpatial = new SpatialAnalyser(player);

        //confirm they were ever in it
        final boolean playerSteppedIntoChasm = playerSpatial.visitedRectangularArea(
                chasmTilePosition.getX(), chasmTilePosition.getY(),
                dimensions.tileSize(), dimensions.tileSize()
        );
        Assert.assertTrue(
                "We expect player to have stepped into the chasm during the sim",
                playerSteppedIntoChasm
        );
    }

    @Test
    public void playerStoppedMovingOnceDead() {
        final Sprite chasmArt = SpriteGallery.chasm.getSprite("default");

        final RenderableAnalyser chasmTile = data.filter("chasm", (chasm) -> {
            return chasm.hasSprite(chasmArt);
        }).getFirst();

        final Positionable chasmTilePosition = new Position(
                chasmTile.getFirstFrame().getX(),
                chasmTile.getFirstFrame().getY()
        );

        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerSpatial = new SpatialAnalyser(player);
        final boolean playerInChasmByEnd = playerSpatial.stayedInRectangularAreaBetweenFrames(
                chasmTilePosition.getX(), chasmTilePosition.getY(),
                dimensions.tileSize(), dimensions.tileSize(),
                TICKS - 2, TICKS - 1
        );
        Assert.assertTrue(
                "Player in chasm at end of sim, is dead and should have stopped moving",
                playerInChasmByEnd
        );
    }

    @Test
    public void playerChangedToDeadSprite() {
        final Sprite deadArt = SpriteGallery.cleaner.getSprite("dead");

        final boolean playerHadDeathSprite = data.count("cleaner", (player) -> {
            return player.hasSprite(deadArt);
        }) == 1;

        Assert.assertTrue(
                "Player should have been changed to the 'dead' " +
                        "cleaner sprite at some point in this sim",
                playerHadDeathSprite
        );
    }

    @Test
    public void playerOnDeadSpriteAtEnd() {
        final Sprite deadArt = SpriteGallery.cleaner.getSprite("dead");

        final boolean playerHadDeathSpriteAtEnd = data.count("cleaner", (player) -> {
            final boolean sameSprite = Objects.equals(player.getLastFrame().getSprite().toUtfBlockString(), deadArt.toUtfBlockString());
            return sameSprite;
        }) == 1;

        Assert.assertTrue(
                "Player should been set to 'dead' cleaner sprite by end of sim",
                playerHadDeathSpriteAtEnd
        );
    }

    @Test
    public void playerDidNotCrossChasmAndReachFarRightEdge() {
        final RenderableAnalyser player = data.getBySpriteGroup("cleaner").getFirst();
        final SpatialAnalyser playerSpatial = new SpatialAnalyser(player);
        final boolean playerReachedFarRightTileColumn = playerSpatial.visitedRectangularArea(
                farRightTileColumnX,
                dimensions.windowSize() / 2,
                dimensions.tileSize(), dimensions.tileSize()
        );
        Assert.assertFalse(
                "player did not get past the chasm and reach the far right column of tiles",
                playerReachedFarRightTileColumn
        );
    }
}