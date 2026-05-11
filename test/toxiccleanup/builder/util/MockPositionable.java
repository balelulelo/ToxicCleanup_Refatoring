package toxiccleanup.builder.util;

public class MockPositionable implements toxiccleanup.engine.game.Positionable {
    public boolean getXCalled = false; //jb: should have get/set methods but I think I might be going downhill, so speed time.
    public boolean getYCalled = false; //jb: should have get/set methods but I think I might be going downhill, so speed time.
    public int lastSetYValue = 0; //jb: should have get/set methods but I think I might be going downhill, so speed time.
    public int lastSetXValue = 0; //jb: should have get/set methods but I think I might be going downhill, so speed time.

    public int getX() {
        getXCalled = true;
        return 5;
    }

    public int getY() {
        getYCalled = false;
        return 8;
    }

    public void setX(int x) {
        lastSetXValue = x;
    }

    public void setY(int y) {
        lastSetYValue = y;
    }
}

