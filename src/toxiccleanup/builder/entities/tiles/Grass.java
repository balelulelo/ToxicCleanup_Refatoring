package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.game.Positionable;

/**
 * A {@link Grass} tile is a purely decorative ground tile with no game mechanics. The player
 * can walk over it freely without triggering any interaction. It is rendered using the default
 * sprite of {@link SpriteGallery#grass} and does not accept any stacked machines.
 *
 *
 *
 */
public class Grass extends Tile {
    /**
     * Constructs a new grass tile at the given position.
     *
     * @param position The position we wish to place this newly constructed tile at.
     * @requires position.getX() >= 0, position.getX() is less than the window width
     * @requires position.getY() >= 0, position.getY() is less than the window height
     */
    public Grass(Positionable position) {
        super(position, SpriteGallery.grass);
        setSprite(SpriteGallery.grass.getSprite("default"));
    }
}
