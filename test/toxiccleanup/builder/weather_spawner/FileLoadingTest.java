package toxiccleanup.builder.weather_spawner;

import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.TileGrid;
import org.junit.Test;
import toxiccleanup.builder.world.WeatherBuilder;
import toxiccleanup.builder.world.WorldLoadException;

public class FileLoadingTest {
    final private Dimensions dimensionsA = new TileGrid(4, 400);
    final private Dimensions dimensionsB = new TileGrid(16, 800);
    final private String filepathA = "";
    final private String filepathB = "";
    final private String fileContentsA =  "____\n____\n____\n____\n";
    final private String fileContentsB =  "_______________C\n" +
            "_______________C\n" +
            "_______________C\n" +
            "_____________r_C\n" +
            "_____________R_c\n" +
            "_____________r_C\n" +
            "_____________R_c\n" +
            "_____________r_C\n" +
            "_____________R_c\n" +
            "_____________r_C\n" +
            "_____l____L__A_c\n" +
            "_____L____l__a_C\n" +
            "_____l____L__A_c\n" +
            "_____L____l__a_c\n" +
            "_____l____l__a_c\n" +
            "_______________c";

    @Test
    public void fromString() throws WorldLoadException {
        WeatherBuilder.fromString(dimensionsA, fileContentsA);
        WeatherBuilder.fromString(dimensionsB, fileContentsB);
    }
}
