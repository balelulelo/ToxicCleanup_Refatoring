package toxiccleanup.builder.machines;

import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.builder.util.MockAdjustable;
import toxiccleanup.engine.timing.TickTimer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for {@link MachinesManager}.
 *
 * <p>Test coverage includes:
 * <ul>
 * <li><b>Resource Integrity:</b> Verifying power state transitions, including
 * clamping logic during construction and the accuracy of power adjustments
 * (e.g., {@code setPower}, {@code adjust})</li>
 * <li><b>Factory Methods:</b> Validating the spawning of game entities
 * ({@code SolarPanel}, {@code LightningRod}, {@code Teleporter}, {@code Pump})
 * under a certain power conditions</li>
 * <li><b>Teleportation:</b> Testing logic for teleporter selection
 * and cooldown mechanisms over time.</li>
 * <li><b>State Progression:</b> Ensuring the {@code tick()} method correctly
 * updates the  state of managed machines</li>
 * </ul>
 */
public class MachinesManagerTest {

    /*
    =========================================================================
                                Shared Tests
    =========================================================================
    */

    /** A simple fixed position used wherever a spawn location is required. */
    private static final Position POS_A = new Position(100, 100);

    /** A second distinct position, used to test teleporter exclusion logic. */
    private static final Position POS_B = new Position(200, 200);

    /** Full-power manager created fresh before each test. */
    private MachinesManager manager;

    @Before
    public void setUp() {
        manager = new MachinesManager();
    }


    /*
    =========================================================================
                                Constructors Tests
    =========================================================================
    */

    /**
     * Default constructor should initialize power to 14.
     */
    @Test
    public void defaultConstructorStartsAtMaxPower() {
        assertEquals("default constructor should start at power 14", 14, manager.getPower());
    }

    /**
     * The int constructor should accept a valid value and store it exactly.
     */
    @Test
    public void intConstructorStoresExactValueWithinRange() {
        MachinesManager m = new MachinesManager(7);
        assertEquals("int constructor should store power 7", 7, m.getPower());
    }

    /**
     * Values below 0 (negative values) passed to the int constructor must be clamped to 0.
     */
    @Test
    public void intConstructorClampsNegativeToZero() {
        MachinesManager m = new MachinesManager(-99);
        assertEquals("negative starting power should be clamped to 0", 0, m.getPower());
    }

    /**
     * Values above 14 passed to the int constructor must be clamped to 14.
     */
    @Test
    public void intConstructorClampsAboveMaxToMax() {
        MachinesManager m = new MachinesManager(999);
        assertEquals("starting power above 14 should be clamped to 14", 14, m.getPower());
    }

    /**
     * The timer-injection constructor does not set power, so it defaults to 0.
     */
    @Test
    public void timerConstructorStartsPowerAtZero() {
        TickTimer alwaysFinished = new TickTimer() {
            @Override
            public void tick() {}

            @Override
            public boolean isFinished() {
                return true;
            }
        };
        MachinesManager m = new MachinesManager(alwaysFinished);
        assertEquals("timer-injection constructor power should default to 0", 0, m.getPower());
    }

    /*
    =========================================================================
                               getMaxPower Tests
    =========================================================================
    */

    /**
     * getMaxPower should always return 14 regardless of current power.
     */
    @Test
    public void getMaxPowerAlwaysReturns14() {
        assertEquals(14, manager.getMaxPower());
        manager.setPower(0); // set the power to 0, to test getMaxPower again
        assertEquals(14, manager.getMaxPower());
    }

    /*
    =========================================================================
                               setPower Tests
    =========================================================================
    */

    /**
     * setPower should store the value when it is within [0, 14].
     */
    @Test
    public void setPowerStoresValidValue() {
        manager.setPower(6);
        assertEquals(6, manager.getPower());
    }

    /**
     * setPower should clamp values below 0 to 0.
     */
    @Test
    public void setPowerClampsNegativeToZero() {
        manager.setPower(-100);
        assertEquals(0, manager.getPower());
    }

    /**
     * setPower should clamp values above 14 to 14.
     */
    @Test
    public void setPowerClampsAboveMaxToMax() {
        manager.setPower(100);
        assertEquals(14, manager.getPower());
    }

    /*
    =========================================================================
                                 adjust Tests
    =========================================================================
    */

    /**
     * adjust given a positive amount should increase power.
     */
    @Test
    public void adjustPositiveIncreasesPower() {
        manager.setPower(0);
        manager.adjust(5);
        assertEquals(5, manager.getPower());
    }

    /**
     * adjust given a negative amount should decrease power.
     */
    @Test
    public void adjustNegativeDecreasesPower() {
        manager.setPower(10);
        manager.adjust(-5);
        assertEquals(5, manager.getPower());
    }

    /**
     * adjust should clamp power to 0 if the result will be negative.
     */
    @Test
    public void adjustClampsToZeroOnUnderflow() {
        manager.setPower(1);
        manager.adjust(-100);
        assertEquals(0, manager.getPower());
    }

    /**
     * adjust should clamp power to 14 if the result would exceed the maximum.
     */
    @Test
    public void adjustClampsToMaxOnOverflow() {
        manager.setPower(13);
        manager.adjust(100);
        assertEquals(14, manager.getPower());
    }

    /*
    =========================================================================
                             hasRequiredPower Tests
    =========================================================================
    */

    /**
     * hasRequiredPower should return true when power equals the requirement exactly.
     */
    @Test
    public void hasRequiredPowerReturnsTrueAtRequirement() {
        manager.setPower(3);
        assertTrue(manager.hasRequiredPower(3));
    }

    /**
     * hasRequiredPower should return true when power exceeds the requirement.
     */
    @Test
    public void hasRequiredPowerReturnsTrueWhenAboveRequirement() {
        manager.setPower(10);
        assertTrue(manager.hasRequiredPower(3));
    }

    /**
     * hasRequiredPower should return false when power is below the requirement.
     */
    @Test
    public void hasRequiredPowerReturnsFalseWhenBelowRequirement() {
        manager.setPower(2);
        assertFalse(manager.hasRequiredPower(3));
    }

     /*
    =========================================================================
                              spawnSolarPanel Tests
    =========================================================================
    */

    /**
     * spawnSolarPanel should return a SolarPanel when power is sufficient.
     */
    @Test
    public void spawnSolarPanelReturnsNonNullWhenPowerSufficient() {
        manager.setPower(SolarPanel.COST);
        assertNotNull(manager.spawnSolarPanel(POS_A));
    }

    /**
     * spawnSolarPanel should deduct exactly COST power on successful spawn.
     */
    @Test
    public void spawnSolarPanelDeductsCostFromPower() {
        manager.setPower(14);
        manager.spawnSolarPanel(POS_A);
        assertEquals(14 - SolarPanel.COST, manager.getPower());
    }

    /**
     * spawnSolarPanel should return null when power is less than COST.
     */
    @Test
    public void spawnSolarPanelReturnsNullWhenPowerInsufficient() {
        manager.setPower(SolarPanel.COST - 1);
        assertNull(manager.spawnSolarPanel(POS_A));
    }

    /**
     * spawnSolarPanel should not change power when it failed to spawn.
     */
    @Test
    public void spawnSolarPanelDoesNotChangePowerOnFailure() {
        int startPower = SolarPanel.COST - 1;
        manager.setPower(startPower);
        manager.spawnSolarPanel(POS_A);
        assertEquals(startPower, manager.getPower());
    }

    /*
    =========================================================================
                             spawnLightningRod Tests
    =========================================================================
    */

    /**
     * spawnLightningRod should return a LightningRod when power is sufficient.
     */
    @Test
    public void spawnLightningRodReturnsNonNullWhenPowerSufficient() {
        manager.setPower(LightningRod.COST);
        assertNotNull(manager.spawnLightningRod(POS_A));
    }

    /**
     * spawnLightningRod should deduct exactly COST power on success.
     */
    @Test
    public void spawnLightningRodDeductsCostFromPower() {
        manager.setPower(5);
        manager.spawnLightningRod(POS_A);
        assertEquals(5 - LightningRod.COST, manager.getPower());
    }

    /**
     * spawnLightningRod should return null when power is below the required
     * minimum power (Lightning rod COST = 1).
     */
    @Test
    public void spawnLightningRodReturnsNullWhenPowerInsufficient() {
        manager.setPower(0);
        assertNull(manager.spawnLightningRod(POS_A));
    }

    /*
    =========================================================================
                             spawnTeleporter - Tests
    =========================================================================
    */

    /**
     * spawnTeleporter should return a Teleporter when power is sufficient.
     */
    @Test
    public void spawnTeleporterReturnsNonNullWhenPowerSufficient() {
        manager.setPower(Teleporter.COST);
        assertNotNull(manager.spawnTeleporter(POS_A));
    }

    /**
     * spawnTeleporter should deduct exactly COST power on success.
     */
    @Test
    public void spawnTeleporterDeductsCostFromPower() {
        manager.setPower(14);
        manager.spawnTeleporter(POS_A);
        assertEquals(14 - Teleporter.COST, manager.getPower());
    }

    /**
     * spawnTeleporter should return null when power is less than COST
     * (Teleporter COST = 2).
     */
    @Test
    public void spawnTeleporterReturnsNullWhenPowerInsufficient() {
        manager.setPower(Teleporter.COST - 1);
        assertNull(manager.spawnTeleporter(POS_A));
    }

    /**
     * spawnTeleporter should not change power when it failed to spawn.
     */
    @Test
    public void spawnTeleporterDoesNotChangePowerOnFailure() {
        int startPower = Teleporter.COST - 1;
        manager.setPower(startPower);
        manager.spawnTeleporter(POS_A);
        assertEquals(startPower, manager.getPower());
    }

    /*
    =========================================================================
                               spawnPump - Tests
    =========================================================================
    */

    /**
     * spawnPump should return a Pump when power is sufficient.
     */
    @Test
    public void spawnPumpReturnsNonNullWhenPowerSufficient() {
        manager.setPower(Pump.COST);
        assertNotNull(manager.spawnPump(POS_A, new MockAdjustable()));
    }

    /**
     * spawnPump should deduct exactly COST power on success.
     */
    @Test
    public void spawnPumpDeductsCostFromPower() {
        manager.setPower(14);
        manager.spawnPump(POS_A, new MockAdjustable());
        assertEquals(14 - Pump.COST, manager.getPower());
    }

    /**
     * spawnPump should return null when power is less than COST
     * (Pump COST = 5).
     */
    @Test
    public void spawnPumpReturnsNullWhenPowerInsufficient() {
        manager.setPower(Pump.COST - 1);
        assertNull(manager.spawnPump(POS_A, new MockAdjustable()));
    }

    /**
     * spawnPump should not change power when it failed to spawn.
     */
    @Test
    public void spawnPumpDoesNotChangePowerOnFailure() {
        int startPower = Pump.COST - 1;
        manager.setPower(startPower);
        manager.spawnPump(POS_A, new MockAdjustable());
        assertEquals(startPower, manager.getPower());
    }

    /*
    =========================================================================
               getNextTeleporterPosition - only 1 teleporter exists
    =========================================================================
    */

    /**
     * When only one teleporter exists, getNextTeleporterPosition must return
     *        that teleporter's own position (the player teleports in place).
     */
    @Test
    public void getNextTeleporterPositionWithOneTeleporterReturnsThatPosition() {
        manager.setPower(14);
        manager.spawnTeleporter(POS_A);
        Positionable result = manager.getNextTeleporterPosition(POS_A);

        assertEquals("X should match the single teleporter", POS_A.getX(), result.getX());
        assertEquals("Y should match the single teleporter", POS_A.getY(), result.getY());
    }

    /*
    =========================================================================
                  getNextTeleporterPosition - cooldown tests
    =========================================================================
    */

    /**
     * With two teleporters and the cooldown not yet finished, getNextTeleporterPosition
     *        must return the excluded position (teleport is blocked).
     */
    @Test
    public void getNextTeleporterPositionReturnsExcludedPositionWhenOnCooldown() {
        TickTimer neverFinished = new TickTimer() {
            @Override
            public void tick() {}

            @Override
            public boolean isFinished() {
                return false;
            }
        };
        MachinesManager machine = new MachinesManager(neverFinished);
        machine.setPower(14);
        machine.spawnTeleporter(POS_A);
        machine.spawnTeleporter(POS_B);

        Positionable result = machine.getNextTeleporterPosition(POS_A);

        assertEquals("on cooldown X should equal excluded position", POS_A.getX(), result.getX());
        assertEquals("on cooldown Y should equal excluded position", POS_A.getY(), result.getY());
    }

    /**
     * With two teleporters and the cooldown finished, getNextTeleporterPosition must
     *        return the other teleporter's position, not the excluded one.
     */
    @Test
    public void getNextTeleporterPositionExcludesCurrentPositionWhenCooldownDone() {
        TickTimer alwaysFinished = new TickTimer() {
            @Override
            public void tick() {}

            @Override
            public boolean isFinished() {
                return true;
            }
        };
        MachinesManager machine = new MachinesManager(alwaysFinished);
        machine.setPower(14);
        machine.spawnTeleporter(POS_A);
        machine.spawnTeleporter(POS_B);

        Positionable result = machine.getNextTeleporterPosition(POS_A);

        assertEquals("should return the other teleporter X", POS_B.getX(), result.getX());
        assertEquals("should return the other teleporter Y", POS_B.getY(), result.getY());
    }

    /*
    =========================================================================
          getNextTeleporterPosition — AND/OR overlap-check correctness
    =========================================================================
    */


    /**
     * Two teleporters that share the same X coordinate but differ in Y must
     *        not both be excluded. Only the teleporter at the EXACT same (x, y) as the
     *        excluded position should be filtered out.
     *
     *        <p>If the teleporter implementation uses {@code &&} (AND) instead of full
     *        equality (both X AND Y must match to be the same tile), it would incorrectly
     *        exclude the second teleporter and return the excluded position instead.
     */
    @Test
    public void getNextTeleporterPositionNotExcludedWhenSameXButDifferentY() {
        TickTimer alwaysFinished = new TickTimer() {
            @Override
            public void tick() {}

            @Override
            public boolean isFinished() {
                return true;
            }
        };
        MachinesManager machine = new MachinesManager(alwaysFinished);
        machine.setPower(14);

        final Position posA = new Position(100, 100);
        final Position posSameXDiffY = new Position(100, 200);

        machine.spawnTeleporter(posA);
        machine.spawnTeleporter(posSameXDiffY);

        Positionable result = machine.getNextTeleporterPosition(posA);

        assertEquals("same-X-only teleporter should be a valid destination",
                posSameXDiffY.getX(), result.getX());
        assertEquals("same-X-only teleporter should be a valid destination",
                posSameXDiffY.getY(), result.getY());
    }

    /*
    =========================================================================
                                Tick - Tests
    =========================================================================
    */

    /**
     * Each call to tick should advance the internal cooldown timer exactly once.
     */
    @Test
    public void tickAdvancesCooldownTimerOncePerCall() {
        final int[] tickCount = {0};
        TickTimer countingTimer = new TickTimer() {
            @Override
            public void tick() {
                tickCount[0]++;
            }

            @Override
            public boolean isFinished() {
                return false;
            }
        };

        MachinesManager machine = new MachinesManager(countingTimer);
        toxiccleanup.engine.util.MockGameState game =
                new toxiccleanup.engine.util.MockGameState();
        toxiccleanup.engine.util.MockEngineState state =
                new toxiccleanup.engine.util.MockEngineState(
                        new toxiccleanup.engine.renderer.TileGrid(16, 800),
                        new toxiccleanup.engine.core.headless.MockMouse(2, 2, false, false, false),
                        new toxiccleanup.engine.core.headless.MockKeys(new java.util.ArrayList<>()));

        machine.tick(state, game);
        machine.tick(state, game);
        machine.tick(state, game); // at this point, tickCount should be (3) after 3 calls

        assertEquals("tick should call timer.tick() once per invocation", 3, tickCount[0]);
    }
}