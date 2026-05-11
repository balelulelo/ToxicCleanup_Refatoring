package toxiccleanup.builder.machines;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.builder.Tickable;

/**
 * Interface for managing the various machines that exist in this game
 * + the associated power system.
 *
 * @provided
 */
public interface Machines extends Adjustable, Tickable {

    /**
     * Used to adjust the internal power resource used by all machines.
     * Adds the given value to the current power. Add a negative value to subtract.
     * Note: Power is clamped between 0 and 14.
     *
     * @param amount - the amount of power to add to the current total.
     */
    void adjust(int amount);

    /**
     * Sets the current power level to the given value, clamped to [0, {@link #getMaxPower()}].
     * Values below 0 are clamped to 0; values above the maximum are clamped to the maximum.
     *
     * @param value the power level to set.
     */
    void setPower(int value);

    /**
     * Returns the amount of power currently available.
     *
     * @return the amount of power currently available.
     */
    int getPower();

    /**
     * Returns the maximum amount of power the implementing object can store.
     *
     * @return the maximum amount of power the implementing object can store.
     */
    int getMaxPower();

    /**
     * Returns whether the current power level is at least {@code powerRequirement}. Used by
     * machines such as {@link Pump} and {@link Teleporter} to decide whether they can animate
     * or perform their action this tick.
     *
     * @param powerRequirement the minimum number of power units needed.
     * @return {@code true} if current power &ge; {@code powerRequirement}; {@code false} otherwise.
     */
    boolean hasRequiredPower(int powerRequirement);

    /**
     * Attempts to create a {@link SolarPanel} at the given location and return it.
     * Should only create if there is enough power to pay the {@link SolarPanel} COST (3).
     * If the current power is at least 3, this method deducts 3 power and returns the new SolarPanel.
     * Otherwise, this method returns null.
     *
     * @param position the position we wish to spawn the {@link SolarPanel} at.
     * @return created {@link SolarPanel} or null.
     */
    SolarPanel spawnSolarPanel(Positionable position);

    LightningRod spawnLightningRod(Positionable position);

    /**
     * Attempts to create a {@link Teleporter} at the given location and return it.
     * Should only create if there is enough power to pay the {@link Teleporter} COST (2).
     * Otherwise, should return null
     *
     * @param position the position we wish to spawn the {@link Teleporter} at.
     * @return created {@link Teleporter} or null.
     */
    Teleporter spawnTeleporter(Positionable position);

    /**
     * Spawns a pump at the given position, holding a reference to the given {@link Adjustable}.
     * A Pump is only created if there is enough power to pay the {@link Pump} COST (5).
     * If creation succeeds, the new Pump is returned.
     * Otherwise, this method returns null.
     *
     * @param position   position we wish to spawn the pump at.
     * @param adjustable the adjustable we wish the pump to hold a reference to.
     * @return newly created {@link Pump}, or null if there is insufficient power
     */
    Pump spawnPump(Positionable position, Adjustable adjustable);

    /**
     * Returns the position of a registered {@link Teleporter} other than the one at
     * {@code excludedPosition}, chosen randomly from all known teleporter locations.
     * If only one teleporter exists, its own position is returned. This is called by
     * {@link Teleporter#playerOver} to determine where to send the player.
     *
     * @param excludedPosition the position of the teleporter the player is currently on;
     *                         excluded from the result when other options exist.
     * @return a {@link Positionable} representing the destination teleporter's position.
     */
    Positionable getNextTeleporterPosition(Positionable excludedPosition);
}
