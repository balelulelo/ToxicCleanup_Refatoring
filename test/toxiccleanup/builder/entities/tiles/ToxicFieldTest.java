package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.machines.MachinesManager;
import toxiccleanup.builder.machines.Pump;
import toxiccleanup.builder.util.MockPlayerManager;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.TileGrid;
import toxiccleanup.engine.util.MockEngineState;
import toxiccleanup.engine.util.MockGameState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ToxicFieldTest {
    public static final double testWeight = 8.0;
    private final TileGrid tileGrid = new TileGrid(25, 800);
    private final MockMouse mockMouse = new MockMouse(
            2,
            2,
            true,
            false,
            true
    );
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());
    private final MockEngineState baseEngineState =
            new MockEngineState(tileGrid, mockMouse, mockKeys);
    private MockPlayerManager mockPlayerManager;
    private ToxicField testField;
    final private Position position = new Position(20, 20);
    private MockGameState baseGameState = new MockGameState();

    @Before
    public void setup() {
        mockPlayerManager = new MockPlayerManager();
        testField = new ToxicField(position);
    }

    @Test
    @Deprecated
    public void ToxicFieldsStartToxic() {
        assertTrue("Toxic Fields should spawn in a toxic state.", testField.isToxic());
    }

    @Test
    @Deprecated
    public void ToxicFieldCanBeDrained() {
        testField.adjust(1000000);
        assertFalse("Toxic Fields should be drainable to no longer be toxic.", testField.isToxic());
    }

    @Test
    public void ToxicFieldStartsWithNoStackedEntities() {
        assertEquals("", 0, testField.getStackedEntities().size());
    }

    @Test
    public void PlayerCanBuildOnToxicField() {
        baseGameState = new MockGameState(new MachinesManager());
        baseGameState.getPlayer().setPosition(position);
        mockPlayerManager.setPosition(testField);
        testField.playerOver(baseEngineState, baseGameState);
        assertEquals(
                "Player should be able to build on a toxic field if machineManager " +
                        "has enough power, left mouse is pressed and the player is over the tile",
                1, testField.getStackedEntities().size()
        );
        assertTrue(
                "Pump should have been built on the test toxic Field",
                testField.getStackedEntities().getFirst() instanceof Pump
        );
    }
}
