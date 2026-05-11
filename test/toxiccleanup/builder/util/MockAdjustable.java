package toxiccleanup.builder.util;

import toxiccleanup.builder.machines.Adjustable;

public class MockAdjustable implements Adjustable {
    private boolean called = false;
    private int numOfTimesCalled = 0;
    private int lastAmount = 0;

    @Override
    public void adjust(int amount) {
        called = true;
        lastAmount = amount;
        numOfTimesCalled += 1;
    }

    public boolean adjustCalled() {
        return called;
    }

    public int getNumOfTimesCalled() {
        return numOfTimesCalled;
    }

    public int getLastAmount() {
        return lastAmount;
    }
}

