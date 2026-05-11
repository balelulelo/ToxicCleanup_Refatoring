package toxiccleanup.builder.util;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.builder.player.Harmable;
import toxiccleanup.builder.player.Player;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Renderable;

import java.util.List;

public class MockPlayerManager implements Player {
    private int x = 0;
    private int y = 0;
    private final int hp = 0;
    private final int maxHp = 20;
    final private MockAdjustable adjustable = new MockAdjustable();

    public MockPlayerManager() {
    }

    public MockAdjustable getMockAdjustable() {
        return adjustable;
    }

    /**
     * Returns the horizontal (x-axis) and vertical (y-axis) coordinate of the player entity.
     *
     * @return The horizontal (x-axis) and vertical (y-axis) coordinate.
     * @ensures \result >= 0
     * @ensures \result is less than the window width
     */
    @Override
    public Positionable getPosition() {
        return new Position(x, y) {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }

            @Override
            public void setX(int value) {
                x = value;
            }

            @Override
            public void setY(int value) {
                y = value;
            }
        };
    }

    @Override
    public void setPosition(Positionable mockPosition) {
        x = mockPosition.getX();
        y = mockPosition.getY();
    }

    /**
     * The tick method is called on most components on the game each time the tick event is
     * dispatched by the game engine (i.e. {@link ToxicCleanup#tick(EngineState)} is called).
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void tick(EngineState state, GameState game) {

    }

    /**
     * Returns how much hp this {@link Harmable} currently has.
     *
     * @return how much hp this {@link Harmable} currently has.
     */
    @Override
    public int getHp() {
        return hp;
    }

    /**
     * Returns how much max hp this {@link Harmable} currently has.
     *
     * @return how much max hp this {@link Harmable} currently has.
     */
    @Override
    public int getMaxHp() {
        return maxHp;
    }

    @Override
    public void adjust(int amount) {
        this.adjustable.adjust(amount);
    }

    /**
     * A collection of renderables that should each be displayed.
     *
     * @return A collection of renderables to display.
     */
    @Override
    public List<Renderable> render() {
        return List.of();
    }
}
