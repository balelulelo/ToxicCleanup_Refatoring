package toxiccleanup;

import toxiccleanup.builder.ToxicCleanup;
import toxiccleanup.builder.world.WorldLoadException;
import toxiccleanup.engine.Engine;
import toxiccleanup.engine.game.Game;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.TileGrid;

import java.io.IOException;

/**
 * A main class to execute the Toxic Cleanup game.
 *
 * @provided
 */
public class Main {
    private static final int SIZE = 800;
    private static final int TILES_PER_ROW = 16;

    /**
     * Start the game.
     *
     * @param args Command line arguments, unused in this method.
     * @throws IOException        If the map file cannot be found or read from.
     * @throws WorldLoadException If the map file is invalid in some way.
     */
    public static void main(String[] args) throws IOException, WorldLoadException {
        final Dimensions dimensions = new TileGrid(TILES_PER_ROW, SIZE);
        final Game game = new ToxicCleanup(dimensions, new Position(dimensions.tileSize() * 13,
                dimensions.tileSize() * 13));
        final Engine engine = new Engine(game, dimensions);

        // Optionally uncomment this line to turn on debug mode
        // engine.debug().on();

        run(engine);
    }

    /**
     * Helper method to run the game loop.
     *
     * @param engine The {@link Engine} instance to execute.
     */
    private static void run(Engine engine) {
        while (engine.isRunning()) {
            if (engine.isTimeForNextTick()) {
                engine.tick();
            }
        }
    }
}
