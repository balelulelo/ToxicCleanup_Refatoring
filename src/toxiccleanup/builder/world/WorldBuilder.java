package toxiccleanup.builder.world;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.entities.tiles.TileFactory;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.util.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Load an instance of a world from a string representation.
 *
 * <p>Each line of the file, separated by new line characters, corresponds to a row of tiles in the
 * world. Each character represents a tile according
 * to {@link TileFactory#fromSymbol(engine.game.Positionable, char)}.
 *
 */
public class WorldBuilder {

    /**
     * Read the encoded world text and construct the corresponding list of tiles.
     *
     * <p>Each line in the text corresponds to one row of tiles. Each character corresponds to one tile.
     * A character at row 3, column 10 in the text corresponds to a tile at y = 2 and x = 9. You have
     * to use
     * {@link Dimensions#tileToPixel(int)} to convert these tile coordinates to pixel coordinates when creating the tile instances.
     *
     * <p>The tile type for each character is determined based on {@link
     * TileFactory#fromSymbol(engine.game.Positionable, char)}.
     *
     * <p>The world is square. Let {@code n = dimensions.windowSize() / dimensions.tileSize()}.
     * The encoding must contain exactly {@code n} lines, and each line must contain exactly
     * {@code n} characters.
     *
     * <p>If the number of rows, any row length, or any tile symbol is invalid, a
     * {@link WorldLoadException} is thrown.
     *
     * @param dimensions The dimensions of the world. The tile encoding must correspond to these
     *                   dimensions.
     * @param text       The text encoding of a world.
     * @return A list of tiles loaded from the given string.
     * @throws WorldLoadException If the number of lines doesn't match the required amount according
     *                            to the dimensions.
     * @throws WorldLoadException If the length of any line doesn't match the required amount
     *                            according to the dimensions.
     * @throws WorldLoadException If any character doesn't correspond to a tile according to {@link
     *                            TileFactory#fromSymbol(engine.game.Positionable, char)}.
     * @requires dimensions.windowSize() % dimensions.tileSize() == 0
     */
    public static List<Tile> fromString(Dimensions dimensions, String text)
            throws WorldLoadException {
        int numberOfTiles = dimensions.windowSize() / dimensions.tileSize();
        String[] lines = text.split("\n");

        if (lines.length != numberOfTiles) {
            throw new WorldLoadException("Expected " + numberOfTiles
                    + " lines to match the given dimensions but got " + lines.length);
        }

        List<Tile> tiles = new ArrayList<>();
        for (int row = 0; row < numberOfTiles; row++) {
            char[] currentRow = lines[row].strip().toCharArray();

            if (currentRow.length != numberOfTiles) {
                throw new WorldLoadException("Expected " + numberOfTiles
                        + " characters to match the given dimensions but got "
                        + currentRow.length, row);
            }

            for (int col = 0; col < numberOfTiles; col++) {
                final int tileX = dimensions.tileToPixel(col);
                final int tileY = dimensions.tileToPixel(row);
                char symbol = currentRow[col];
                final Position position = new Position(tileX, tileY);
                Tile tile;
                try {
                    tile = TileFactory.fromSymbol(position, symbol);
                } catch (IllegalArgumentException e) {
                    throw new WorldLoadException("Unknown symbol: '" + symbol + "'", row, col);
                }
                tiles.add(tile);
            }
        }
        return tiles;
    }

    /**
     * Read the provided file and attempt to create a new world based on the tile encoding in the
     * file.
     *
     * <p>See {@link #fromString(Dimensions, String)} for a description of how the tile encoding is
     * read.
     *
     * @param dimensions The dimensions of the world. The tile encoding must correspond to these
     *                   dimensions.
     * @param filepath   The path to a file containing a tile encoding.
     * @return A new world containing all tiles in the specified file.
     * @throws IOException        If the file path doesn't exist or otherwise can't be read,
     *                            as thrown by {@link FileManager#readFile(String)}.
     * @throws WorldLoadException If the tile encoding is invalid (according to {@link
     *                            #fromString(Dimensions, String)}).
     */
    public static ToxicWorld fromFile(Dimensions dimensions, String filepath)
            throws IOException, WorldLoadException {
        final String text = new FileManager().readFile(filepath);
        return fromTiles(fromString(dimensions, text));
    }

    /**
     * Constructs a new {@link ToxicWorld} pre-populated with the given tiles. Tiles are added
     * to the world in reverse order so that no test inadvertently depends on the insertion
     * order of tiles; the world's tile ordering is unspecified.
     *
     * @param tiles the list of tiles to place into the new world.
     * @return a new {@link ToxicWorld} containing all given tiles.
     */
    public static ToxicWorld fromTiles(List<Tile> tiles) {
        ToxicWorld world = new ToxicWorld();
        for (Tile tile : tiles.reversed()) { // reverse so tests don't implicitly rely on order
            world.place(tile);
        }
        return world;
    }
}
