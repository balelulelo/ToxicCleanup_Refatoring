package toxiccleanup.engine.util;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.input.KeyState;
import toxiccleanup.engine.input.MouseState;
import toxiccleanup.engine.renderer.Dimensions;

import java.util.ArrayList;
import java.util.List;

public class MockEngineState implements EngineState {

    public final static int LEFT_CLICK = 0;
    public final static int MIDDLE_CLICK = 1;
    public final static int RIGHT_CLICK = 2;

    private Dimensions dimensions;
    private MouseState mouse;
    private KeyState keys;
    private final int frame;

    public MockEngineState(Dimensions dimensions, MouseState mouse, KeyState keys) {
        this(dimensions, mouse, keys, 0);
    }

    public MockEngineState(Dimensions dimensions, MouseState mouse, KeyState keys, int frame) {
        this.dimensions = dimensions;
        this.mouse = mouse;
        this.keys = keys;
        this.frame = frame;
    }

    public MockEngineState withFrame(int frame) {
        return new MockEngineState(dimensions, mouse, keys, frame);
    }

    public MockEngineState press(char key) {
        List<Character> keys = new ArrayList<>();
        keys.add(key);
        return new MockEngineState(dimensions, mouse, new MockKeys(keys));
    }

    /**
     * Click either the left, middle, or right key
     * (LEFT_CLICK:0, MIDDLE_CLICK:1, RIGHT_CLICK:2 respectively).
     */
    public MockEngineState click(int key) {
        MockMouse mockMouse = new MockMouse(0, 0,
                key == LEFT_CLICK,
                key == MIDDLE_CLICK,
                key == RIGHT_CLICK
        );
        return new MockEngineState(dimensions, mockMouse, keys);
    }

    @Override
    public Dimensions getDimensions() {
        return dimensions;
    }

    @Override
    public MouseState getMouse() {
        return mouse;
    }

    @Override
    public KeyState getKeys() {
        return keys;
    }

    public void setKeys(KeyState keys) {
        this.keys = keys;
    }

    @Override
    public int currentTick() {
        return frame;
    }
}
