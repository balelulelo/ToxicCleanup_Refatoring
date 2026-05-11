package toxiccleanup.builder.weather;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;

/**
 * <p>Handles creating {@link WeatherSpawnPoint}s at a given position,
 * the kind of {@link WeatherSpawnPoint} constructed varies based on the symbol given.</p>
 * <ul>
 * <li>c and C are for clouds</li>
 * <li>a and A are for acidclouds</li>
 * <li>r and R are for rainclouds</li>
 * <li>l and L are for lightning</li>
 * </ul>
 * <p>
 * lower case letters use the static SPAWN_TIME for the relevant class
 * as the duration to give a {@link RepeatingTimer}
 * </p>
 * <p>
 *  upper case letters use the static SPAWN_TIME * 5.5 for the relevant class as the duration
 *  to give a {@link RepeatingTimer}
 * </p>
 */
public class SpawnerFactory {
    public static WeatherSpawnPoint fromSymbol(Positionable position, char symbol) {
        WeatherSpawnPoint result = null;
        if (symbol == 'c') {
            result = new WeatherSpawnPoint(
                    position,
                    new RepeatingTimer(Cloud.SPAWN_TIME),
                    (Positionable pos) -> new Cloud(pos)
            );
        } else if (symbol == 'C') {
            result = new WeatherSpawnPoint(
                    position,
                    new RepeatingTimer((int) (Cloud.SPAWN_TIME * 5.5)),
                    (Positionable pos) -> new Cloud(pos)
            );
        } else if (symbol == 'r') {
            result = new WeatherSpawnPoint(
                    position,
                    new RepeatingTimer(RainCloud.SPAWN_TIME),
                    (Positionable pos) -> new RainCloud(pos)
            );
        } else if (symbol == 'R') {
            result = new WeatherSpawnPoint(
                    position,
                    new RepeatingTimer((int) (RainCloud.SPAWN_TIME * 5.5)),
                    (Positionable pos) -> new RainCloud(pos)
            );
        } else if (symbol == 'a') {
            result = new WeatherSpawnPoint(
                    position,
                    new RepeatingTimer(Cloud.SPAWN_TIME),
                    (Positionable pos) -> new AcidCloud(pos)
            );
        } else if (symbol == 'A') {
            result = new WeatherSpawnPoint(
                    position,
                    new RepeatingTimer((int) (AcidCloud.SPAWN_TIME * 5.5)),
                    (Positionable pos) -> new AcidCloud(pos)
            );
        } else if (symbol == 'l') {
            result = new WeatherSpawnPoint(
                    position,
                    new RepeatingTimer(Lightning.SPAWN_TIME),
                    (Positionable pos) -> new Lightning(pos)
            );
        } else if (symbol == 'L') {
            result = new WeatherSpawnPoint(
                    position,
                    new RepeatingTimer((int) (Lightning.SPAWN_TIME * 5.5)),
                    (Positionable pos) -> new Lightning(pos)
            );
        } else if (symbol == '_') {
            return null;
        }
        if (result == null) {
            throw new IllegalArgumentException("Symbol does not represent a tile.");
        }
        return result;

    }


}

