package toxiccleanup.builder.machines;

/**
 * Indicates the implementing machine has a minimum power requirement to operate. Machines
 * such as {@link Pump} and {@link Teleporter} implement this interface so that the
 * {@link Machines#hasRequiredPower(int)} check can be performed before
 * allowing the machine to animate or perform its action.
 *
 * @provided
 */
public interface Powered {
    /**
     * Returns the minimum number of power units that must be available in the shared power
     * system for this machine to operate. If {@link Machines#hasRequiredPower(int)}
     * returns {@code false} for this value, the machine should pause its activity.
     *
     * @return the power level required for this machine to function.
     */
    int getPowerRequirement();
}
