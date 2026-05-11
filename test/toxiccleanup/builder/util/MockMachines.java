package toxiccleanup.builder.util;

import toxiccleanup.engine.EngineState;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.machines.*;
import toxiccleanup.engine.game.Positionable;

public class MockMachines implements Machines {
    private boolean adjustCalled = false;
    private int numOfTimesCalled = 0;
    private int lastAmount = 0;
    private boolean requiredPower = true;

    public MockMachines() {

    }

    @Override
    public void adjust(int amount) {
        adjustCalled = true;
        lastAmount = amount;
        numOfTimesCalled += 1;
    }

    public boolean adjustCalled() {
        return adjustCalled;
    }

    public int getNumOfTimesAdjustCalled() {
        return numOfTimesCalled;
    }

    public int getLastAmount() {
        return lastAmount;
    }

    public void setRequiredPower(boolean mockValue) {
        this.requiredPower = mockValue;
    }

    @Override
    public void setPower(int value) {

    }

    @Override
    public int getPower() {
        return 5;
    }

    @Override
    public int getMaxPower() {
        return 10;
    }

    @Override
    public boolean hasRequiredPower(int powerRequirement) {
        return this.requiredPower;
    }

    @Override
    public SolarPanel spawnSolarPanel(Positionable position) {
        return null;
    }

    @Override
    public LightningRod spawnLightningRod(Positionable position) {
        return null;
    }

    @Override
    public Teleporter spawnTeleporter(Positionable position) {
        return null;
    }

    @Override
    public Pump spawnPump(Positionable position, Adjustable adjustable) {
        return null;
    }

    @Override
    public Positionable getNextTeleporterPosition(Positionable excludedPosition) {
        return null;
    }

    /**
     * Advances component state by one game tick using engine and game context.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void tick(EngineState state, GameState game) {

    }
}
