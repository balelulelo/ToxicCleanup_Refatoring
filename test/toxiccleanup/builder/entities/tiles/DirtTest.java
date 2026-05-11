package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.machines.MachinesManager;
import toxiccleanup.builder.machines.MachinesManagerTest;
import toxiccleanup.builder.machines.SolarPanel;
import toxiccleanup.builder.machines.Teleporter;
import toxiccleanup.engine.art.sprites.Sprite;
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

public class DirtTest {
    public static final double testWeight = 8.0;
    private final TileGrid tileGrid = new TileGrid(25, 800);
    private final MockMouse mockMouse = new MockMouse(2, 2, true, false, true);
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());
    private final MockEngineState baseEngineState =
            new MockEngineState(tileGrid, mockMouse, mockKeys);
    private MockGameState baseGameState = new MockGameState();

    private Dirt dirt;
    final private Position position = new Position(20, 20);

    @Before
    public void setup() {
        baseGameState = new MockGameState();
        baseGameState.getPlayer().setPosition(position);
        dirt = new Dirt(position);
    }

    /**
     * Checks Dirt construction for expected behaviour.
     */
    @Test
    @Deprecated
    public void initialDirtConstruction() {
        final Sprite defaultDirtArt = SpriteGallery.dirt.getSprite("default");
        assertEquals(
                "Initial Dirt sprite should be field 'default'.",
                defaultDirtArt.toString(),
                dirt.getSprite().toString());
        assertFalse("Dirt tile should not be paved initially.", dirt.isPaved());
        assertTrue(
                "Dirt should have nothing placed on it originally.",
                dirt.getStackedEntities().isEmpty());
    }

    /**
     * Checks that Dirt can be paved.
     */
    @Test
    @Deprecated
    public void canPave() {
        dirt.pave();
        assertTrue("Dirt tile should have become paved.", dirt.isPaved());
    }

    /**
     * Checks if the player can spawn a Teleporter on a dirt tile if
     * all expected conditions are met.
     * note: requires {@link MachinesManagerTest} implementation
     */
    @Test
    @Deprecated
    public void canSpawnTeleporterViaPlayerInput() {
        dirt.pave();

        baseGameState = new MockGameState(new MachinesManager());
        baseGameState.getPlayer().setPosition(position);

        MockMouse mockMouse = new MockMouse(2, 2, false, true, false);
        MockKeys mockKeys = new MockKeys(new ArrayList<>());
        MockEngineState baseEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys);
        dirt.playerOver(baseEngineState, baseGameState);
        assertEquals(
                "If:" +
                        "player is on dirt tile," +
                        "it is paved, " +
                        "there is sufficient Power in machineManager " +
                        "and the player is holding down right mouse. " +
                        "Should have placed a Teleporter on the dirt tile as a child.",
                1, dirt.getStackedEntities().size()
        );
        System.out.println(dirt.getStackedEntities().getFirst().getClass());
        assertTrue(
                "First stacked entity should be a Teleporter",
                dirt.getStackedEntities().getFirst() instanceof Teleporter
        );
    }

    /**
     * Checks if the player can spawn a SolarPanel on a dirt tile if
     * all expected conditions are met.
     * note: requires {@link MachinesManagerTest} implementation
     */
    @Test
    @Deprecated
    public void canSpawnSolarPanelViaPlayerInput() {
        dirt.pave();

        baseGameState = new MockGameState(new MachinesManager());
        baseGameState.getPlayer().setPosition(position);
        dirt.playerOver(baseEngineState, baseGameState);
        assertEquals(
                "If:" +
                        "player is on dirt tile," +
                        "it is paved, " +
                        "there is sufficient Power in machineManager " +
                        "and the player is holding down right mouse. " +
                        "Should have placed a Teleporter on the dirt tile as a child.",
                1, dirt.getStackedEntities().size()
        );
        assertTrue(
                "First Stacked entity should be a SolarPanel",
                dirt.getStackedEntities().getFirst() instanceof SolarPanel
        );
    }
}
