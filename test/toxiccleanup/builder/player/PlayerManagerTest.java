package toxiccleanup.builder.player;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.TileGrid;
import toxiccleanup.engine.util.MockEngineState;
import toxiccleanup.engine.util.MockGameState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PlayerManagerTest {
    public static final double testWeight = 15.0;
    private final int SIZE = 800;
    private final int TILES_PER_ROW = 16;
    private final MockMouse mockMouse = new MockMouse(
            2, 2,
            false, false, false
    );
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());

    private final Dimensions tileGrid = new TileGrid(TILES_PER_ROW, SIZE);
    private final MockEngineState baseEngineState =
            new MockEngineState(tileGrid, mockMouse, mockKeys);
    private final MockGameState baseGameState = new MockGameState();
    private PlayerManager playerManager;

    @Before
    public void setup() {
        playerManager = new PlayerManager(new Position(50, 50));
    }

    @Test
    public void playerCanNotMoveOffScreenUp() {
        playerManager = new PlayerManager(new Position(50, 50));
        playerManager.setPosition(new Position(2, 10));
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('w');
        MockKeys mockKeys = new MockKeys(keys);

        final MockEngineState baseEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys);

        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);

        final boolean playerOnScreenVertically = playerManager.getPosition().getY() >= 0;
        assertTrue(
                "player should not be able to  move offscreen up",
                playerOnScreenVertically
        );
        assertNotEquals("player must have moved from original y axis",
                10, playerManager.getPosition().getY()
        );
    }

    @Test
    public void playerCanNotMoveOffScreenDown() {
        playerManager = new PlayerManager(new Position(50, 50));
        playerManager.setPosition(new Position(2, tileGrid.windowSize() - 10));
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('s');
        MockKeys mockKeys = new MockKeys(keys);

        final MockEngineState baseEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys);

        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);

        final boolean playerOnScreenVertically = playerManager.getPosition().getY()
                <= tileGrid.windowSize();
        assertTrue(
                "player should not be able to move offscreen down",
                playerOnScreenVertically
        );
        assertNotEquals("player must have moved from original y axis",
                10, playerManager.getPosition().getY()
        );
    }

    @Test
    public void playerCanNotMoveOffScreenLeft() {
        playerManager = new PlayerManager(new Position(50, 50));
        playerManager.setPosition(new Position(10, tileGrid.windowSize() - 10));
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('a');
        MockKeys mockKeys = new MockKeys(keys);

        final MockEngineState baseEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys);

        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);

        final int x = playerManager.getPosition().getX();
        final boolean playerOnScreenHorizontally = x >= 0 && x <= tileGrid.windowSize();
        assertTrue(
                "player should not be able to move offscreen left",
                playerOnScreenHorizontally
        );
        assertNotEquals("player must have moved from original x axis",
                10, playerManager.getPosition().getX()
        );
    }

    @Test
    public void playerCanNotMoveOffScreenRight() {
        playerManager = new PlayerManager(new Position(50, 50));
        playerManager.setPosition(new Position(tileGrid.windowSize() - 10, tileGrid.windowSize() - 10));
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('d');
        MockKeys mockKeys = new MockKeys(keys);

        final MockEngineState baseEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys);

        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);
        playerManager.tick(baseEngineState, baseGameState);

        final int x = playerManager.getPosition().getX();
        final boolean playerOnScreenHorizontally = x >= 0 && x <= tileGrid.windowSize();
        assertTrue(
                "player should not be able to move offscreen on the right",
                playerOnScreenHorizontally
        );
        assertNotEquals("player must have moved from original x axis",
                tileGrid.windowSize() - 10, playerManager.getPosition().getX()
        );
    }

    @Test
    public void playerSpriteMovesUp5OnW() {
        final MockMouse mockMouse = new MockMouse(
                2, 2,
                false, false, false
        );
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('w');
        MockKeys mockKeys = new MockKeys(keys);

        playerManager = new PlayerManager(new Position(50, 100));
        for (int i = 0; i < 15; i += 1) {
            //jb:we simulate 15 ticks rather than exactly 10 to
            //allow for differences in when they tick their timer
            MockEngineState tempEngineState =
                    new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(i);
            playerManager.tick(tempEngineState, baseGameState);
        }
        assertEquals(
                "x should not have been changed by just w",
                50, playerManager.getPosition().getX()
        );
        assertEquals(
                "player should have moved upwards one tileSize (50)" +
                        " within 10 ticks if w is held down",
                50, playerManager.getPosition().getY()
        );
        final Sprite playerSprite = playerManager.render().getFirst().getSprite();
        final Sprite leftSprite = SpriteGallery.cleaner.getSprite("up");
        assertEquals(
                "player should be rendering the up sprite when moving up",
                playerSprite.toString(),
                leftSprite.toString()
        );
    }

    @Test
    public void playerSpriteMovesDown5OnS() {
        final MockMouse mockMouse = new MockMouse(
                2, 2,
                false, false, false
        );
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('s');
        MockKeys mockKeys = new MockKeys(keys);

        playerManager = new PlayerManager(new Position(50, 50));
        for (int i = 0; i < 15; i += 1) {
            //jb:we simulate 15 ticks rather than exactly 10 to
            //allow for differences in when they tick their timer
            MockEngineState tempEngineState =
                    new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(i);
            playerManager.tick(tempEngineState, baseGameState);
        }

        assertEquals(
                "x should NOT have been changed by 1 tileSize unit " +
                        "within 10 ticks while holding s down (start at 50, add 50 for tilesize)",
                50, playerManager.getPosition().getX()
        );
        assertEquals(
                "player should have moved downwards one tilesize unit within 10 ticks " +
                        "while holding s down (start at 50, add 50 for tilesize)",
                100, playerManager.getPosition().getY()
        );
        final Sprite playerSprite = playerManager.render().getFirst().getSprite();
        final Sprite leftSprite = SpriteGallery.cleaner.getSprite("down");
        assertEquals(
                "player should be rendering the down sprite when moving down",
                playerSprite.toString(),
                leftSprite.toString()
        );
    }

    @Test
    public void playerSpriteMovesRightOnD() {
        final MockMouse mockMouse = new MockMouse(
                2, 2,
                false, false, false
        );
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('d');
        MockKeys mockKeys = new MockKeys(keys);

        final Dimensions tileGrid = new TileGrid(TILES_PER_ROW, SIZE);

        playerManager = new PlayerManager(new Position(50, 50));
        for (int i = 0; i < 15; i += 1) {
            //jb:we simulate 15 ticks rather than exactly 10 to
            //allow for differences in when they tick their timer
            MockEngineState tempEngineState =
                    new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(i);
            playerManager.tick(tempEngineState, baseGameState);
        }
        assertEquals(
                "y should not have been changed by just d",
                50, playerManager.getPosition().getY()
        );
        assertEquals(
                "player should have move rightwards one tileSize (50)" +
                        " within 10 ticks if d is held down",
                100, playerManager.getPosition().getX()
        );
        final Sprite playerSprite = playerManager.render().getFirst().getSprite();
        final Sprite leftSprite = SpriteGallery.cleaner.getSprite("right");
        assertEquals(
                "player should be rendering the right sprite when moving right",
                playerSprite.toString(),
                leftSprite.toString()
        );
    }

    @Test
    public void playerSpriteMovesLeftOnA() {
        final MockMouse mockMouse = new MockMouse(
                2, 2,
                false, false, false
        );
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('a');
        MockKeys mockKeys = new MockKeys(keys);

        final Dimensions tileGrid = new TileGrid(TILES_PER_ROW, SIZE);

        playerManager = new PlayerManager(new Position(100, 50));
        for (int i = 0; i < 15; i += 1) {
            //jb:we simulate 15 ticks rather than exactly 10 to
            //allow for differences in when they tick their timer
            MockEngineState tempEngineState =
                    new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(i);
            playerManager.tick(tempEngineState, baseGameState);
        }
        assertEquals(
                "y should not have been changed by just a",
                50, playerManager.getPosition().getY()
        );
        assertEquals(
                "player should have move leftwards one tileSize (50)" +
                        " within 10 ticks if a is held down",
                50, playerManager.getPosition().getX()
        );
        final Sprite playerSprite = playerManager.render().getFirst().getSprite();
        final Sprite leftSprite = SpriteGallery.cleaner.getSprite("left");
        assertEquals(
                "player should be rendering the left sprite when moving left",
                playerSprite.toString(),
                leftSprite.toString()
        );
    }
}