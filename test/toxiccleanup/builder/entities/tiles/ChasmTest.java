//pending review
package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.player.PlayerManager;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.TileGrid;
import toxiccleanup.engine.util.MockEngineState;
import toxiccleanup.engine.util.MockGameState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ChasmTest {
    public static final double testWeight = 5.0;
    private final TileGrid tileGrid = new TileGrid(25, 800);
    private final MockMouse mockMouse = new MockMouse(2, 2, true, false, true);
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());
    private Chasm testChasm;
    final private Positionable position = new Position(20, 20);

    @Before
    public void setup() {
        testChasm = new Chasm(position);
    }

    /**
     * Checks Chasm construction for expected behaviour.
     */
    @Test
    @Deprecated
    public void initialChasmConstruction() {
        final Sprite defaultChasmArt = SpriteGallery.chasm.getSprite("default");
        final Sprite leftChasmArt = SpriteGallery.chasm.getSprite("left");
        final Sprite rightChasmArt = SpriteGallery.chasm.getSprite("right");
        final Sprite leftSlopeChasmArt = SpriteGallery.chasm.getSprite("leftslope");
        final Sprite rightSlopeChasmArt = SpriteGallery.chasm.getSprite("rightslope");
        assertEquals(
                "Initial Chasm sprite should be field 'default'.",
                defaultChasmArt.toString(),
                testChasm.getSprite().toString()
        );
        // LEFT
        testChasm = new Chasm(position, "left");
        assertEquals(
                "if facing requested as left, left sprite should be set",
                leftChasmArt.toString(),
                testChasm.getSprite().toString()
        );
        // RIGHT
        testChasm = new Chasm(position, "right");
        assertEquals(
                "if facing requested as right, right sprite should be set",
                rightChasmArt.toString(),
                testChasm.getSprite().toString()
        );
        // LEFTSLOPE
        testChasm = new Chasm(position, "leftslope");
        assertEquals(
                "if facing requested as leftslope, leftslope sprite should be set",
                leftSlopeChasmArt.toString(),
                testChasm.getSprite().toString()
        );
        // RIGHTSLOPE
        testChasm = new Chasm(position, "rightslope");
        assertEquals(
                "if facing requested as rightslope, rightslope sprite should be set",
                rightSlopeChasmArt.toString(),
                testChasm.getSprite().toString()
        );
    }

    /**
     * Confirms a chasm that is not fallable does not harm the player if over.
     */
    @Test
    public void nonFallableChasmDoesNotHarmPlayerIfPlayerOver() {

        final ArrayList<String> facings = new ArrayList<>();
        facings.add("left");
        facings.add("right");
        facings.add("leftslope");
        facings.add("rightslope");

        final PlayerManager player = new PlayerManager(testChasm.getPosition());
        final int initialHp = player.getHp();
        final MockGameState baseGameState = new MockGameState();
        baseGameState.setPlayer(player);
        final MockEngineState baseEngineState = new MockEngineState(
                tileGrid,
                mockMouse,
                mockKeys
        );

        for (String facing : facings) { //run the no damage test on each facing
            testChasm = new Chasm(position, facing);
            player.setPosition(testChasm); //sync player position to the new chasm position
            testChasm.tick(baseEngineState, baseGameState);
            testChasm.playerOver(baseEngineState, baseGameState); //should have not harmed player
            assertEquals(
                    "hp should not have changed if player stands on top of " +
                            "a chasm with facing:" + facing,
                    initialHp, player.getHp()
            );
        }

    }

    /**
     * Confirms the player takes 1 damage if ontop of a chasm that is 'fallable'
     */
    @Test
    @Deprecated
    public void fallableChasmHarmsPlayerIfPlayerOver() {
        testChasm = new Chasm(position);

        final PlayerManager player = new PlayerManager(testChasm.getPosition());
        final int initialHp = player.getHp();
        final MockGameState baseGameState = new MockGameState();
        baseGameState.setPlayer(player);
        final MockEngineState baseEngineState = new MockEngineState(
                tileGrid,
                mockMouse,
                mockKeys
        );

        testChasm.tick(baseEngineState, baseGameState);
        testChasm.playerOver(baseEngineState, baseGameState); //should have harmed player for 1hp

        assertEquals(
                "hp should have been adjusted downwards by 1",
                initialHp - 1, player.getHp()
        );

        testChasm.tick(baseEngineState, baseGameState);
        testChasm.playerOver(baseEngineState, baseGameState); //should have not harmed player

        assertEquals(
                "hp should have been adjusted downwards by  an additional 1 (down 2 total)",
                initialHp - 2, player.getHp()
        );

        testChasm.tick(baseEngineState, baseGameState);
        testChasm.playerOver(baseEngineState, baseGameState); //should have not harmed player

        assertEquals(
                "hp should have been adjusted downwards by  an additional 1 (down 3 total)",
                initialHp - 3, player.getHp()
        );
    }
}
