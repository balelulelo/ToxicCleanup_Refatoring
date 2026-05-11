package toxiccleanup.builder.util;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.player.Player;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Renderable;

import java.util.List;

public class MockPlayer implements Player {
    private final MockPositionable mockPosition = new MockPositionable();
    private final MockAdjustable mockAdjust = new MockAdjustable();

    public MockAdjustable getMockAdjust() {
        return mockAdjust;
    }

    public MockPositionable getMockPosition() {
        return mockPosition;
    }

    @Override
    public Positionable getPosition() {
        return mockPosition;
    }

    public void setPosition(Positionable mockPosition) {
        this.mockPosition.setX(mockPosition.getX());
        this.mockPosition.setY(mockPosition.getY());
    }

    @Override
    public void tick(EngineState state, GameState game) {

    }

    @Override
    public int getHp() {
        return 5;
    }

    @Override
    public int getMaxHp() {
        return 10;
    }

    @Override
    public void adjust(int amount) {
        mockAdjust.adjust(amount);
    }

    @Override
    public List<Renderable> render() {
        return List.of();
    }
}
