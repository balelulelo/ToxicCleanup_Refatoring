package toxiccleanup.builder.world;

import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.util.FileManager;
import toxiccleanup.builder.weather.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class WeatherBuilder {
    public static Weather fromString(Dimensions dimensions, String text) throws WorldLoadException {
        final Weather weather = new WeatherManager();
        int numberOfTiles = dimensions.windowSize() / dimensions.tileSize();
        String[] lines = text.split("\n");

        if (lines.length != numberOfTiles) {
            throw new WorldLoadException("Expected " + numberOfTiles
                    + " lines to match the given dimensions but got " + lines.length);
        }

        final List<WeatherSpawnPoint> spawnPoints = new ArrayList<>();
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
                WeatherSpawnPoint spawner;
                try {
                    spawner = SpawnerFactory.fromSymbol(position, symbol);
                } catch (IllegalArgumentException e) {
                    throw new WorldLoadException("Unknown symbol: '" + symbol + "'", row, col);
                }
                if (spawner != null) {
                    spawnPoints.add(spawner);
                }
            }
        }
        for (WeatherSpawnPoint spawnPoint : spawnPoints) {
            weather.addSpawnPoint(spawnPoint);

        }
        return weather;
    }

    /**
     * Takes the given dimensions and locations of a file and attempts to process said file for {@link WeatherSpawnPoint} locations for our various weather Phenomena including:
     * <ul>
     *  <li> {@link Cloud} </li>
     *  <li> {@link AcidCloud} </li>
     *  <li> {@link RainCloud} </li>
     *  <li> {@link Lightning} </li>
     * </ul>
     *
     * @param dimensions - world dimensions, used for placing of spawners in relation to
     *                   said dimensions.
     * @param filepath   - path to a file holding the desired x,y locations and type for placing our
     *                   various {@link WeatherSpawnPoint}s
     * @return our newly Weather, with {@link WeatherSpawnPoint} added and ready for use in the
     * game.
     * @throws IOException
     * @throws WorldLoadException
     */
    public static Weather fromFile(Dimensions dimensions, String filepath)
            throws IOException, WorldLoadException {
        final String text = new FileManager().readFile(filepath);
        return fromString(dimensions, text);
    }
}
