package toxiccleanup.builder.machines;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.util.RandomNumberGenerator;
import toxiccleanup.builder.GameState;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;

import java.util.ArrayList;

/**
 * The concrete implementation of {@link Machines} for the
 * {@link toxiccleanup.builder.ToxicCleanup} game. {@link MachinesManager} is responsible for:
 *
 * <ul>
 *   <li>Tracking the current power level (starts at 14 by default; capped at 14).</li>
 *   <li>Spending power when a machine is built; each machine type has a fixed {@code COST}
 *       constant on its class (e.g. {@link SolarPanel#COST}, {@link Teleporter#COST},
 *       {@link Pump#COST}), {@link LightningRod#COST}.</li>
 *   <li>Constructing and returning new machine instances when there is sufficient power,
 *       or returning {@code null} if the power cost cannot be met.</li>
 *   <li>Tracking all teleporter positions so that the {@link Teleporter} can retrieve a
 *       destination when the player activates one.</li>
 * </ul>
 */
public class MachinesManager implements Machines {
    private static final int DEFAULT_POWER = 14;
    private static final int maxPower = MachinesManager.DEFAULT_POWER;
    private final ArrayList<Positionable> teleporterPositions = new ArrayList<>();
    private TickTimer teleporterCooldown = new RepeatingTimer(TELEPORTER_COOLDOWN);
    private static final int TELEPORTER_COOLDOWN = 20;
    private final RandomNumberGenerator random = new RandomNumberGenerator();
    private int power;

    /**
     * Constructs a new {@link MachinesManager} starting at full power (14). Use this
     * constructor when the game should begin with maximum power available.
     */
    public MachinesManager() {
        power = MachinesManager.DEFAULT_POWER;
    }


    /**
     * Here for testability purposes. Allows us to inject an alternative {@link TickTimer}
     * to override the default internal timer used for the teleportation systems cooldown.
     *
     * @param teleporterCooldownTimer the new {@link TickTimer} we want our {@link MachinesManager}
     *                                to use instead of the default internal {@link TickTimer}.
     */
    public MachinesManager(TickTimer teleporterCooldownTimer) {
        this.teleporterCooldown = teleporterCooldownTimer;
    }


    /**
     * Constructs a new {@link MachinesManager} with the given amount of starting power.
     * Maximum power is fixed at the default (14).
     *
     * @param power the starting power level; clamped to [0, 14] if out of range.
     */
    public MachinesManager(int power) {
        this.power = Math.clamp(power, 0, maxPower);
    }

    /**
     * Returns whether the current power level is sufficient for a machine's operation.
     *
     * @param powerRequirement the minimum number of power units needed.
     * @return {@code true} if the current power is greater than or equal to
     * {@code powerRequirement}; {@code false} otherwise.
     */
    @Override
    public boolean hasRequiredPower(int powerRequirement) {
        return power >= powerRequirement;
    }

    /**
     * Returns the current power level of this machine manager.
     *
     * @return the current power, in the range [0, {@link #getMaxPower()}].
     */
    @Override
    public int getPower() {
        return power;
    }

    /**
     * Sets the power to the given value, clamped to [0, maxPower (14 by default)].
     *
     * @param value the power level to set.
     */
    @Override
    public void setPower(int value) {
        power = Math.clamp(value, 0, maxPower);
    }

    /**
     * Returns the maximum power capacity of this {@link MachinesManager}.
     *
     * @return the upper bound for power, fixed at 14 by default.
     */
    @Override
    public int getMaxPower() {
        return maxPower;
    }

    /**
     * Adds {@code amount} to the current power level, then clamps the result to [0, 14].
     * Pass a positive value to gain power (e.g. from a {@link SolarPanel}) or a negative
     * value to spend power. This is the primary method called by machines to change power.
     *
     * @param amount the amount of power to add; use a negative value to subtract.
     */
    @Override
    public void adjust(int amount) {
        this.gainPower(amount);
    }

    /**
     * Increase power available by the given amount.
     * Note: Power is capped at a maximum of 14, if it would go above 14,
     * it will instead round back down to 14.
     *
     * @param amount amount to increase the power by.
     */
    private void gainPower(int amount) {
        power += amount;
        power = Math.clamp(power, 0, maxPower);
    }

    /**
     * Attempts to create a {@link SolarPanel} at the given location. If the current power is
     * at least 3 (the solar panel's cost), deducts 3 power and returns the new instance.
     * Returns {@code null} if there is insufficient power.
     *
     * @param position the position we wish to spawn the {@link SolarPanel} at.
     * @return the newly created {@link SolarPanel}, or {@code null} if power &lt; 3.
     */
    @Override
    public SolarPanel spawnSolarPanel(Positionable position) {
        if (power >= SolarPanel.COST) {
            power -= SolarPanel.COST;
            return new SolarPanel(position);
        }
        return null;
    }

    /**
     * Attempts to create a {@link LightningRod} at the given location. If the current power is
     * at least 1 (the lightning rod's cost), deducts 1 power and returns the new instance.
     * Returns {@code null} if there is insufficient power.
     *
     * @param position the position we wish to spawn the {@link LightningRod} at.
     * @return the newly created {@link LightningRod}, or {@code null} if power &lt; 1.
     */
    @Override
    public LightningRod spawnLightningRod(Positionable position) {
        if (power >= LightningRod.COST) {
            power -= LightningRod.COST;
            return new LightningRod(position);
        }
        return null;
    }


    /**
     * Attempts to create a {@link Teleporter} at the given location. If the current power is
     * at least 2 (the teleporter's cost), deducts 2 power, records the teleporter's position
     * for future {@link #getNextTeleporterPosition} calls, and returns the new instance.
     * Returns {@code null} if there is insufficient power.
     *
     * @param position the position we wish to spawn the {@link Teleporter} at.
     * @return the newly created {@link Teleporter}, or {@code null} if power &lt; 2.
     */
    @Override
    public Teleporter spawnTeleporter(Positionable position) {
        if (power >= Teleporter.COST) {
            power -= Teleporter.COST;
            teleporterPositions.add(new Position(position.getX(), position.getY()));
            return new Teleporter(position);
        }
        return null;
    }

    /**
     * Returns the position of a teleporter other than the one at {@code excludedPosition},
     * chosen at random from all registered teleporter locations. This is used by the
     * {@link Teleporter} to determine where to send the player. If only one teleporter exists,
     * its own position is returned (the player teleports in place).
     *
     * @param excludedPosition the position of the teleporter the player is currently standing on;
     *                         will be excluded from the random selection when possible.
     * @return the next position ({@link Positionable}) from stored {@link Teleporter} positions.
     */
    @Override
    public Positionable getNextTeleporterPosition(Positionable excludedPosition) {
        //there is only one possible response so send that back
        if (teleporterPositions.size() == 1) {
            return teleporterPositions.getFirst();
        }
        if (!teleporterCooldown.isFinished()) { //teleports are on cooldown
            return excludedPosition;
        }
        final ArrayList<Positionable> validPositions = new ArrayList<>();
        for (Positionable position : teleporterPositions) {
            final boolean notOverlappingExcludedPosition = (position.getX() != excludedPosition.getX()
                    && position.getY() != excludedPosition.getY());
            if (notOverlappingExcludedPosition) {
                validPositions.add(position);
            }
        }
        if (validPositions.isEmpty()) {
            return excludedPosition;
        }
        int randomIndex = random.nextInt(validPositions.size());
        return validPositions.get(randomIndex);
    }

    /**
     * Attempts to create a {@link Pump} at the given position targeting the given
     * {@link Adjustable}. If the current power is at least 5 (the pump's cost), deducts 5
     * power and returns a new {@link Pump} that will call {@link Adjustable#adjust(int)} on
     * {@code adjustable} every 100 ticks. Returns {@code null} if there is insufficient power.
     *
     * @param position   the position we wish to spawn the {@link Pump} at.
     * @param adjustable the object (e.g. a {@link toxiccleanup.builder.entities.tiles.ToxicField}) whose
     *                   adjustable value will be reduced each time the pump fires.
     * @return the newly created {@link Pump}, or {@code null} if power &lt; 5.
     */
    @Override
    public Pump spawnPump(Positionable position, Adjustable adjustable) {
        if (power >= Pump.COST) {
            power -= Pump.COST;
            return new Pump(position, adjustable);
        }
        return null;
    }

    /**
     * Advances component state by one game tick using engine and game context.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.wd
     */
    @Override
    public void tick(EngineState state, GameState game) {
        teleporterCooldown.tick();
    }
}
