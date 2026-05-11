package toxiccleanup.builder.weather_spawner;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.weather.*;
import toxiccleanup.builder.world.WeatherBuilder;
import toxiccleanup.builder.world.WorldLoadException;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.renderer.TileGrid;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.util.MockEngineState;
import toxiccleanup.engine.util.MockGameState;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SpawningTest {
    public static final double testWeight = 5.0;
    private final TileGrid tileGrid = new TileGrid(4, 400);
    private final MockMouse mockMouse = new MockMouse(2, 2, false, false, false);
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());
    private final MockEngineState baseEngineState = new MockEngineState(tileGrid, mockMouse, mockKeys);
    private final Position spawnPosition = new Position(100, 100);

    /**
     * Confirms SpawnerFactory creates a non-null WeatherSpawnPoint for lowercase 'c' (Cloud).
     */
    @Test
    @Deprecated
    public void spawnerFactoryCreatesCloudFromLowerC() {
        assertNotNull("SpawnerFactory should return a WeatherSpawnPoint for 'c'",
                SpawnerFactory.fromSymbol(spawnPosition, 'c'));
    }

    /**
     * Confirms SpawnerFactory creates a non-null WeatherSpawnPoint for uppercase 'C' (slow Cloud).
     */
    @Test
    @Deprecated
    public void spawnerFactoryCreatesCloudFromUpperC() {
        assertNotNull("SpawnerFactory should return a WeatherSpawnPoint for 'C'",
                SpawnerFactory.fromSymbol(spawnPosition, 'C'));
    }

    /**
     * Confirms SpawnerFactory creates a non-null WeatherSpawnPoint for lowercase 'r' (RainCloud).
     */
    @Test
    @Deprecated
    public void spawnerFactoryCreatesRainCloudFromLowerR() {
        assertNotNull("SpawnerFactory should return a WeatherSpawnPoint for 'r'",
                SpawnerFactory.fromSymbol(spawnPosition, 'r'));
    }

    /**
     * Confirms SpawnerFactory creates a non-null WeatherSpawnPoint for uppercase 'R' (slow RainCloud).
     */
    @Test
    public void spawnerFactoryCreatesRainCloudFromUpperR() {
        assertNotNull("SpawnerFactory should return a WeatherSpawnPoint for 'R'",
                SpawnerFactory.fromSymbol(spawnPosition, 'R'));
    }

    /**
     * Confirms SpawnerFactory creates a non-null WeatherSpawnPoint for lowercase 'a' (AcidCloud).
     */
    @Test
    @Deprecated
    public void spawnerFactoryCreatesAcidCloudFromLowerA() {
        assertNotNull("SpawnerFactory should return a WeatherSpawnPoint for 'a'",
                SpawnerFactory.fromSymbol(spawnPosition, 'a'));
    }

    /**
     * Confirms SpawnerFactory creates a non-null WeatherSpawnPoint for lowercase 'l' (Lightning).
     */
    @Test
    @Deprecated
    public void spawnerFactoryCreatesLightningFromLowerL() {
        assertNotNull("SpawnerFactory should return a WeatherSpawnPoint for 'l'",
                SpawnerFactory.fromSymbol(spawnPosition, 'l'));
    }

    /**
     * Confirms SpawnerFactory returns null for the blank tile symbol '_'.
     */
    @Test
    @Deprecated
    public void spawnerFactoryReturnsNullForUnderscore() {
        assertNull("SpawnerFactory should return null for '_' (blank tile)",
                SpawnerFactory.fromSymbol(spawnPosition, '_'));
    }

    /**
     * Confirms SpawnerFactory throws IllegalArgumentException for an unrecognised symbol.
     */
    @Test(expected = IllegalArgumentException.class)
    @Deprecated
    public void spawnerFactoryThrowsForUnknownSymbol() {
        SpawnerFactory.fromSymbol(spawnPosition, 'X');
    }

    /**
     * Confirms WeatherSpawnPoint.getPosition() returns the x and y given at construction.
     */
    @Test
    @Deprecated
    public void weatherSpawnPointGetPositionReturnsCorrectXY() {
        Position pos = new Position(150, 250);
        WeatherSpawnPoint spawnPoint = new WeatherSpawnPoint(pos, new RepeatingTimer(1),
                Cloud::new);
        assertEquals("getPosition() x should match construction position",
                150, spawnPoint.getPosition().getX());
        assertEquals("getPosition() y should match construction position",
                250, spawnPoint.getPosition().getY());
    }

    /**
     * Confirms WeatherSpawnPoint calls weather.addWeather when its timer fires.
     */
    @Test
    @Deprecated
    public void spawnPointSpawnsEntityWhenTimerFires() {
        final int[] addWeatherCallCount = {0};
        WeatherSpawnPoint spawnPoint = new WeatherSpawnPoint(
                spawnPosition,
                new RepeatingTimer(1), // fires after 1 tick
                Cloud::new
        );
        MockGameState trackingState = new MockGameState() {
            @Override
            public Weather getWeather() {
                return new Weather() {
                    @Override public void addSpawnPoint(WeatherSpawnPoint sp) {}
                    @Override public void addWeather(GameEntity w) { addWeatherCallCount[0]++; }
                    @Override public boolean isObscuring(Dimensions d, Positionable p) { return false; }
                    @Override public boolean isDamaging(Dimensions d, Positionable p) { return false; }
                    @Override public void applyLightningRod(Positionable p) {}
                    @Override public Damage getDamage(Dimensions d, Positionable p) { return null; }
                    @Override public Damage getDamage() { return null; }
                    @Override public void tick(EngineState state, GameState game) {}
                    @Override public List<Renderable> render() { return List.of(); }
                };
            }
        };
        spawnPoint.tick(baseEngineState, trackingState);
        assertEquals("addWeather should have been called once after the timer fires",
                1, addWeatherCallCount[0]);
    }

    /**
     * Confirms WeatherBuilder.fromString parses a valid map string without exceptions.
     */
    @Test
    @Deprecated
    public void weatherBuilderFromStringParsesValidInput() throws WorldLoadException {
        Dimensions dims = new TileGrid(4, 400);
        String contents = "____\n____\n____\n____\n";
        Weather weather = WeatherBuilder.fromString(dims, contents);
        assertNotNull("WeatherBuilder.fromString should return a non-null Weather for valid input",
                weather);
    }

    /**
     * Confirms WeatherBuilder.fromString throws WorldLoadException when line count is wrong.
     */
    @Test(expected = WorldLoadException.class)
    @Deprecated
    public void weatherBuilderFromStringThrowsOnBadLineCount() throws WorldLoadException {
        Dimensions dims = new TileGrid(4, 400);
        String tooFewLines = "____\n____\n____"; // 3 lines instead of 4
        WeatherBuilder.fromString(dims, tooFewLines);
    }
}
