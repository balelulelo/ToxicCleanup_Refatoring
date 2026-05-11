package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.machines.Teleporter;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.renderer.TileGrid;
import toxiccleanup.engine.util.MockEngineState;
import toxiccleanup.engine.util.MockGameState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TileTest {
    public static final double testWeight = 5.0;
    private final TileGrid tileGrid = new TileGrid(16, 800);
    private final MockMouse mockMouse = new MockMouse(2, 2, false, false, false);
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());
    private final MockEngineState baseEngineState = new MockEngineState(tileGrid, mockMouse, mockKeys);
    private final MockGameState baseGameState = new MockGameState();
    private final Position position = new Position(100, 100);
    private Grass tile;

    @Before
    public void setup() {
        tile = new Grass(position);
    }

    /**
     * Confirms the tile itself is the first element in render(), before any stacked entities.
     */
    @Test
    @Deprecated
    public void tileRendersItselfFirst() {
        List<Renderable> renderables = tile.render();
        assertFalse("render list should not be empty", renderables.isEmpty());
        assertEquals("first renderable should be the tile itself", tile, renderables.get(0));
    }

    /**
     * Confirms placeOn adds an entity to the tile's stacked entity list.
     */
    @Test
    @Deprecated
    public void placeOnAddsToStackedEntities() {
        Teleporter entity = new Teleporter(position);
        tile.placeOn(entity);
        assertTrue("stacked entities should contain the placed entity",
                tile.getStackedEntities().contains(entity));
    }

    /**
     * Confirms getStackedEntities returns a copy — mutating it does not affect the tile.
     */
    @Test
    public void getStackedEntitiesReturnsCopy() {
        Teleporter entity = new Teleporter(position);
        tile.placeOn(entity);
        List<GameEntity> copy = tile.getStackedEntities();
        copy.clear();
        assertEquals("modifying the returned list should not affect the tile's internal state",
                1, tile.getStackedEntities().size());
    }

    /**
     * Confirms render() includes both the tile and any stacked entities.
     */
    @Test
    @Deprecated
    public void renderIncludesStackedEntities() {
        Teleporter entity = new Teleporter(position);
        tile.placeOn(entity);
        List<Renderable> renderables = tile.render();
        assertEquals("render list should include the tile and the stacked entity",
                2, renderables.size());
        assertTrue("render list should contain the stacked entity", renderables.contains(entity));
    }

    /**
     * Confirms that tick() removes stacked entities that have been marked for removal.
     */
    @Test
    @Deprecated
    public void tickRemovesMarkedForRemovalEntities() {
        Teleporter entity = new Teleporter(position);
        tile.placeOn(entity);
        assertEquals("should have 1 stacked entity before marking for removal",
                1, tile.getStackedEntities().size());
        entity.markForRemoval();
        tile.tick(baseEngineState, baseGameState);
        assertEquals("entity marked for removal should be gone after tick",
                0, tile.getStackedEntities().size());
    }

    /**
     * Confirms getStackedEntitiesWithPlayerOverHook returns only entities implementing
     * PlayerOverHook.
     */
    @Test
    public void getStackedEntitiesWithPlayerOverHookFiltersCorrectly() {
        Teleporter hookEntity = new Teleporter(position);
        tile.placeOn(hookEntity);
        List<PlayerOverHook> hooks = tile.getStackedEntitiesWithPlayerOverHook();
        assertEquals("should have exactly 1 PlayerOverHook entity", 1, hooks.size());
        assertTrue("filtered list should contain the Teleporter", hooks.contains(hookEntity));
    }

    /**
     * Confirms playerOver forwards the event to stacked entities that implement PlayerOverHook.
     * Uses a damaged Teleporter: if playerOver is forwarded with 'e' held, it repairs — proving
     * the forwarding occurred.
     */
    @Test
    @Deprecated
    public void playerOverForwardsToStackedPlayerOverHooks() {
        Teleporter teleporter = new Teleporter(position);
        teleporter.setDamage(new Damage(position));
        tile.placeOn(teleporter);

        ArrayList<Character> keys = new ArrayList<>();
        keys.add('e');
        MockEngineState eState = new MockEngineState(tileGrid, mockMouse, new MockKeys(keys));
        tile.playerOver(eState, baseGameState);
        assertFalse("playerOver should have been forwarded to the Teleporter and repaired it",
                teleporter.isDamaged());
    }
}
