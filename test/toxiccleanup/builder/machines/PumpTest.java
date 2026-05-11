package toxiccleanup.builder.machines;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.player.Player;
import toxiccleanup.builder.weather.Weather;
import toxiccleanup.builder.weather.WeatherSpawnPoint;
import toxiccleanup.builder.world.World;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.input.KeyState;
import toxiccleanup.engine.input.MouseState;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.renderer.TileGrid;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for {@link Pump}.
 *
 * <p>Uses the staff-provided helper infrastructure (makeEngineState, makeGameState)
 * rather than MockGameState or MockEngineState, per assignment clarification.
 *
 * <p>The test suite is designed to distinguish between correct implementations
 * and faulty versions by covering the following behavioral contracts:
 * <ul>
 * <li><b>Rendering & Animation:</b> Validating that the sprite correctly cycles
 * through animation frames every 4 ticks when powered, and remains static
 * under power deficiency or damaged states.</li>
 * <li><b>Functional Logic:</b> Ensuring the pumping mechanism (target adjustment)
 * occurs strictly every 100 ticks, provided the machine is functional and
 * adequately powered.</li>
 * <li><b>Maintenance:</b> Testing the transition to a damaged state
 * upon receiving external damage and verifying the repair protocol initiated
 * by the player (requiring both spatial proximity and specific input).</li>
 * <li><b>Invariant Enforcement:</b> Confirming that a damaged / unpowered pump
 * halts all observable behavior to prevent illegal state transitions.</li>
 * </ul>
 */
public class PumpTest {

    public static final double testWeight = 5.0;

    private static final int ANIM_INTERVAL = 4;
    private static final int PUMP_INTERVAL = 100;

    private static final TileGrid TILE_GRID = new TileGrid(16, 800);
    private static final Position POSITION = new Position(100, 100);

    /*
    =========================================================================
                             Provided Helpers from ED
    =========================================================================
    */

    private static class TestAdjustable implements Adjustable {
        private int callCount = 0;
        private int lastAmount = 0;

        @Override
        public void adjust(int amount) {
            callCount++;
            lastAmount = amount;
        }

        public boolean adjustCalled() { return callCount > 0; }
        public int getLastAmount() { return lastAmount; }
    }

    private static EngineState makeEngineState(boolean eKeyDown) {
        return new EngineState() {
            @Override public Dimensions getDimensions() { return TILE_GRID; }
            @Override public MouseState getMouse() {
                return new MouseState() {
                    @Override public int getMouseX() { return 0; }
                    @Override public int getMouseY() { return 0; }
                    @Override public boolean isLeftPressed() { return false; }
                    @Override public boolean isRightPressed() { return false; }
                    @Override public boolean isMiddlePressed() { return false; }
                };
            }
            @Override public KeyState getKeys() {
                return new KeyState() {
                    @Override public List<Character> getDown() {
                        return eKeyDown ? List.of('e') : List.of();
                    }
                    @Override public boolean isDown(char c) { return eKeyDown && c == 'e'; }
                };
            }
            @Override public int currentTick() { return 0; }
        };
    }

    private static GameState makeGameState(int power, Damage damage) {
        Machines machines = new MachinesManager(power);
        return new GameState() {
            @Override public World getWorld() {
                return new World() {
                    @Override public List<Tile> tilesAtPosition(Positionable p, Dimensions d) { return List.of(); }
                    @Override public List<Tile> allTiles() { return List.of(); }
                    @Override public void place(Tile tile) {}
                };
            }
            @Override public Player getPlayer() {
                return new Player() {
                    @Override public Positionable getPosition() { return new Position(0, 0); }
                    @Override public void setPosition(Positionable p) {}
                    @Override public int getHp() { return 5; }
                    @Override public int getMaxHp() { return 10; }
                    @Override public void adjust(int amount) {}
                    @Override public void tick(EngineState s, GameState g) {}
                    @Override public List<Renderable> render() { return List.of(); }
                };
            }
            @Override public Machines getMachines() { return machines; }
            @Override public Weather getWeather() {
                return new Weather() {
                    @Override public void addSpawnPoint(WeatherSpawnPoint sp) {}
                    @Override public void addWeather(GameEntity w) {}
                    @Override public boolean isObscuring(Dimensions d, Positionable p) { return false; }
                    @Override public boolean isDamaging(Dimensions d, Positionable p) { return damage != null; }
                    @Override public void applyLightningRod(Positionable p) {}
                    @Override public Damage getDamage(Dimensions d, Positionable p) { return damage; }
                    @Override public Damage getDamage() { return damage; }
                    @Override public void tick(EngineState s, GameState g) {}
                    @Override public List<Renderable> render() { return List.of(); }
                };
            }
        };
    }

    private static final EngineState BASE_STATE = makeEngineState(false);
    private static final EngineState E_KEY_STATE = makeEngineState(true);

    // -----------------------------------------------------------------------
    // Test fixtures
    // -----------------------------------------------------------------------

    private TestAdjustable pumpTarget;
    private Pump pump;

    @Before
    public void setUp() {
        pumpTarget = new TestAdjustable();
        pump = new Pump(POSITION, pumpTarget);
    }

    // -----------------------------------------------------------------------
    // Helper: tick pump n times
    // -----------------------------------------------------------------------

    private void tickN(int n, EngineState state, GameState game) {
        for (int i = 0; i < n; i++) {
            pump.tick(state, game);
        }
    }

    /*
    =========================================================================
                              Constructor - Tests
    =========================================================================
    */

    /**
     * A newly constructed Pump should display sprite '1'.
     */
    @Test
    public void initialSpriteIsFrame1() {
        Sprite expected = SpriteGallery.pump.getSprite("1");
        assertEquals("initial Pump sprite should be '1'",
                expected.toString(), pump.getSprite().toString());
    }

    /*
    =========================================================================
                            Powered Animation - Tests
    =========================================================================
    */

    /**
     * The Pump animation should advance after enough ticks when power >= 2.
     * Frame 1 and 2 share identical pixel data in the provided art, so 8 ticks
     * (two animation cycles) are needed to reach the first visually distinct frame.
     */
    @Test
    public void animationAdvancesWhenPowered() {
        GameState game = makeGameState(14, null);
        Sprite initialSprite = pump.getSprite();

        tickN(8, BASE_STATE, game);

        assertNotEquals("animation should advance when powered",
                initialSprite.toString(), pump.getSprite().toString());
    }

    /**
     * The animation should NOT advance before the timer starts (3 ticks < interval of 4 ticks per animation).
     */
    @Test
    public void animationDoesNotAdvanceBefore4Ticks() {
        GameState game = makeGameState(14, null);
        Sprite initialSprite = pump.getSprite();

        tickN(3, BASE_STATE, game);

        assertEquals("animation should NOT advance before 4 ticks",
                initialSprite.toString(), pump.getSprite().toString());
    }

    /*
    =========================================================================
                       Not Powered Animation - Test
    =========================================================================
    */

    /**
     * The Pump animation must NOT advance when power is below the requirement
     * (Pump power requirement = 2).
     */
    @Test
    public void animationDoesNotAdvanceWhenPowerInsufficient() {
        GameState game = makeGameState(1, null);
        Sprite initialSprite = pump.getSprite();

        tickN(8, BASE_STATE, game);

        assertEquals("animation must not change when power below 2",
                initialSprite.toString(), pump.getSprite().toString());
    }

    /*
    =========================================================================
                          Pump Behavior - Tests
    =========================================================================
    */

    /**
     * After exactly 100 ticks with sufficient power, the pump target should have
     * been adjusted exactly once with a value of 1.
     */
    @Test
    public void pumpTargetAdjustedAfter100TicksWhenPowered() {
        GameState game = makeGameState(14, null);

        tickN(PUMP_INTERVAL, BASE_STATE, game);

        assertTrue("pump target adjust() should be called after 100 ticks",
                pumpTarget.adjustCalled());
        assertEquals("pump should call adjust with 1", 1, pumpTarget.getLastAmount());
    }

    /**
     * Before exactly 100 ticks (99 Ticks), the pump should not adjust the target yet.
     */
    @Test
    public void pumpTargetNotAdjustedBefore100Ticks() {
        GameState game = makeGameState(14, null);

        tickN(PUMP_INTERVAL - 1, BASE_STATE, game);

        assertFalse("pump target should NOT be adjusted before 100 ticks",
                pumpTarget.adjustCalled());
    }

    /**
     * Pump target must NOT be adjusted when power is below the requirement,
     * even after 100 ticks.
     */
    @Test
    public void pumpTargetNotAdjustedWhenPowerInsufficient() {
        GameState game = makeGameState(1, null); // below POWER_REQUIRED of 2

        tickN(PUMP_INTERVAL, BASE_STATE, game);

        assertFalse("pump target should NOT be adjusted when power below 2",
                pumpTarget.adjustCalled());
    }

    /*
    =========================================================================
                                Damage - Tests
    =========================================================================
    */

    /**
     * When weather deals damage at the pump's position, the pump should display
     * the "damaged" sprite on the next tick.
     */
    @Test
    public void spriteIsDamagedWhenWeatherDealsDamage() {
        GameState damagedGame = makeGameState(14, new Damage(POSITION));
        pump.tick(BASE_STATE, damagedGame);

        Sprite expected = SpriteGallery.pump.getSprite("damaged");
        assertEquals("pump should show 'damaged' sprite when the weather deals a damage",
                expected.toString(), pump.getSprite().toString());
    }

    /**
     * While damaged, the pump must not adjust the pump target, even after 100+ ticks.
     */
    @Test
    public void damagedPumpDoesNotPump() {
        GameState damagedGame = makeGameState(14, new Damage(POSITION));
        pump.tick(BASE_STATE, damagedGame);
        // tick with continued damage
        tickN(PUMP_INTERVAL, BASE_STATE, damagedGame);

        assertFalse("damaged pump should not call adjust on target",
                pumpTarget.adjustCalled());
    }

    /**
     * While damaged the animation must not advance and should remain in "damaged".
     */
    @Test
    public void damagedPumpDoesNotAnimate() {
        GameState damagedGame = makeGameState(14, new Damage(POSITION));
        pump.tick(BASE_STATE, damagedGame);
        // get the damaged sprite for pump
        Sprite damagedSprite = pump.getSprite();
        tickN(8, BASE_STATE, damagedGame);

        assertEquals("damaged pump sprite should remain 'damaged'",
                damagedSprite.toString(), pump.getSprite().toString());
    }

    /*
    =========================================================================
                          playerOver Repair - Tests
    =========================================================================
    */

    /**
     * Pressing 'e' while the player is over a damaged Pump, the pump should be repaired.
     * The sprite updates when animTimer fires, so 4 ticks are needed after repair.
     */
    @Test
    public void pressEKeyRepairsDamagedPump() {
        GameState damagedGame = makeGameState(14, new Damage(POSITION));
        pump.tick(BASE_STATE, damagedGame);
        pump.playerOver(E_KEY_STATE, makeGameState(14, null));

        // Tick 4 times with no damage. animTimer fires and sprite re-evaluates
        tickN(ANIM_INTERVAL, BASE_STATE, makeGameState(14, null));

        assertNotEquals("repaired pump should no longer show 'damaged' sprite",
                SpriteGallery.pump.getSprite("damaged").toString(),
                pump.getSprite().toString());
    }

    /**
     * playerOver without 'e' held should not repair a damaged pump.
     */
    @Test
    public void playerOverWithoutEKeyDoesNotRepair() {
        GameState damagedGame = makeGameState(14, new Damage(POSITION));
        pump.tick(BASE_STATE, damagedGame);
        pump.playerOver(BASE_STATE, makeGameState(14, null));

        pump.tick(BASE_STATE, damagedGame);

        assertEquals("pump should still be damaged if 'e' was not pressed",
                SpriteGallery.pump.getSprite("damaged").toString(),
                pump.getSprite().toString());
    }

    /**
     * playerOver with 'e' on an undamaged pump should have no visible effect.
     */
    @Test
    public void pressEKeyOnUndamagedPumpDoesNothing() {
        GameState game = makeGameState(14, null);

        pump.playerOver(E_KEY_STATE, game);
        pump.tick(BASE_STATE, game);

        assertNotEquals("undamaged pump should not show 'damaged' sprite after 'e' press",
                SpriteGallery.pump.getSprite("damaged").toString(),
                pump.getSprite().toString());
    }
}