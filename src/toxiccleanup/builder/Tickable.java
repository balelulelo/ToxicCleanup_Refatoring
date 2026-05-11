package toxiccleanup.builder;

import toxiccleanup.engine.EngineState;

/**
 * A game component that performs some progression during each game tick.
 *
 * @provided
 */
public interface Tickable {

    /**
     * Advances component state by one game tick using engine and game context.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    void tick(EngineState state, GameState game);
}
