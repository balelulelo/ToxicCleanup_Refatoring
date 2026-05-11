package toxiccleanup.builder.entities;

import toxiccleanup.builder.GameState;
import toxiccleanup.engine.EngineState;

/**
 * A game component that has intended behaviour when the player is on top of them. The hook
 * (calling of {@link #playerOver}) is triggered when the player occupies the same grid square
 * as this component. The hook should continue to be called for each tick as long
 * as the player occupies the space.
 *
 * @provided
 */
public interface PlayerOverHook {

    /**
     * Called when the player is on top of this object. Intended for handling
     * any interaction that occurs while the player overlaps the
     * corresponding tile or entity.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    void playerOver(EngineState state, GameState game);
}
