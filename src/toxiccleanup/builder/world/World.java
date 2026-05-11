package toxiccleanup.builder.world;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;

import java.util.List;

/**
 * An interface to query and modify the state of the world.
 *
 * <p>A world consists of a grid of tiles. The tiles at a given position can be queried via
 * {@link #tilesAtPosition(Positionable, Dimensions)}. New tiles can be placed on the world (at the
 * position contained within the tile instance) using {@link #place(Tile)}.
 *
 * @provided
 */
public interface World {

    /**
     * Returns all tiles whose grid cell matches the tile index containing the given pixel position.
     *
     * <p>A tile is at a matching position if its x and y position occupy the same tile index as the given x
     * and y position (according to {@link Dimensions#pixelToTile(int)}).
     *
     * <p>The order of the tiles is unspecified.
     *
     * @param position   position we wish to check against for tiles in the world that overlap.
     * @param dimensions The dimensions of the world.
     * @return A list of all tiles occupying the given x, y position.
     */
    List<Tile> tilesAtPosition(Positionable position, Dimensions dimensions);

    /**
     * Return all tiles in the world.
     *
     * <p>Modifying the returned list must not modify the state of the world (although modifying the
     * tiles within the list will).
     *
     * <p>The order of the tiles is unspecified, any ordering is suitable.
     *
     * @return Returns all tiles in the world.
     */
    List<Tile> allTiles();

    /**
     * Place a new tile into the world.
     *
     * <p>The tile will be placed at the position specified by its {@link Tile#getX()} and {@link
     * Tile#getY()} position.
     *
     * @param tile The tile to place into the world.
     * @ensures Any calls to {@link #tilesAtPosition} will reflect the existence of this new tile in
     * the world.
     */
    void place(Tile tile);
}
