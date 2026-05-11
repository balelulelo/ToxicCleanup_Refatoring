package toxiccleanup.builder.player;

import toxiccleanup.builder.Tickable;
import toxiccleanup.builder.ui.RenderableGroup;
import toxiccleanup.engine.game.Positionable;

/**
 * An interface to query information about the player in the game including position, hp,
 * render any sprites etc.
 *
 * @provided
 */
public interface Player extends Tickable, RenderableGroup, Harmable {
    /**
     *
     * Returns the current pixel position of the player.
     * @return a Positionable containing the player's current x and y coordinates
     * @ensures result.getX() >= 0, result.getX() is less than the window width, result.getY() >= 0, result.getY() is less than the window height
     *
     */
    Positionable getPosition();

    /**
     * Sets the horizontal (x-axis) and vertical (y-axis) coordinate of the player entity.
     *
     * @param mockPosition the position
     */
    void setPosition(Positionable mockPosition);
}
