package test_utils.mocks;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.core.Core;
import toxiccleanup.engine.core.Debug;
import toxiccleanup.engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.List;

/**
 * The Headless Core is a version of {@link Core} that allows us to run our application without a
 * keyboard, or screen for interaction. Used to allow us to run tests on the Gradescope servers
 * without having to implement a headless version of Processing.
 */
public class HeadlessCore extends Core {

    private final List<List<Renderable>> renderablesHistory = new ArrayList<>();
    private final List<MockEngineState> states = new ArrayList<>();

    public HeadlessCore(int size, int tilesPerRow) {
        super(new Debug(false));
    }

    /**
     * This method is here to provide a consistent interface that matches up with the one used by
     * {@link toxiccleanup.engine.core.p4.ProcessingCore}.
     *
     * @param renderables {@link List} of {@link Renderable} the list of renderables that would be
     *                    rendered if this was the {@link toxiccleanup.engine.core.p4.ProcessingCore}.
     */
    @Override
    public void draw(List<Renderable> renderables) {
        renderablesHistory.add(renderables);
    }

    public List<List<Renderable>> getRenderHistory() {
        return renderablesHistory;
    }

    /**
     * Takes a given {@link MockEngineState} to use for our next engine state!
     *
     * @param engineState - engine state we wish to feed the system
     */
    public void addEngineState(MockEngineState engineState) {
        this.states.add(engineState);
    }

    /**
     * Progresses the Core by one frame, pulling frame data from the given script.
     */
    // TODO (BW): does this need to do something
    public void update(EngineState state) {
        // so our other core methods can pull the relevant scene for our play etc
    }

    /**
     * Returns the x coordinate for where the mouse is currently.
     */
    @Override
    public int getMouseX() {
        return this.states.getLast().getMouse().getMouseX();
    }

    /**
     * Returns the y coordinate for where the mouse is currently.
     */
    @Override
    public int getMouseY() {
        return this.states.getLast().getMouse().getMouseY();
    }

    /**
     * Returns if the left mouse was being held down for this frame.
     */
    @Override
    public boolean isLeftPressed() {
        return this.states.getLast().getMouse().isLeftPressed();
    }

    /**
     * Returns if the right mouse was being held down for this frame.
     */
    @Override
    public boolean isRightPressed() {
        return this.states.getLast().getMouse().isRightPressed();
    }

    /**
     * Returns if the middle mouse was being held down for this frame.
     */
    @Override
    public boolean isMiddlePressed() {
        return this.states.getLast().getMouse().isMiddlePressed();
    }

    /**
     * Returns which keys were down for this frame.
     */
    @Override
    public List<Character> getDown() {
        return this.states.getLast().getKeys().getDown();
    }

    @Override
    public boolean isDown(char character) {
        return this.states.getLast().getKeys().isDown(character);
    }
}
