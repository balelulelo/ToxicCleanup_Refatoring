package toxiccleanup.builder.machines;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.weather.Weather;
import toxiccleanup.builder.weather.WeatherSpawnPoint;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.renderer.TileGrid;
import toxiccleanup.engine.util.MockEngineState;
import toxiccleanup.engine.util.MockGameState;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SolarPanelTest {
    public static final double testWeight = 5.0;
    private final TileGrid tileGrid = new TileGrid(16, 800);
    private final MockMouse mockMouse = new MockMouse(2, 2, false, false, false);
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());
    private final MockEngineState baseEngineState = new MockEngineState(tileGrid, mockMouse, mockKeys);
    private final MockGameState baseGameState = new MockGameState();
    private final Position position = new Position(100, 100);
    private SolarPanel panel;

    @Before
    public void setup() {
        panel = new SolarPanel(position);
    }

    private MockGameState makeGameStateWithWeather(final Weather weather) {
        return new MockGameState() {
            @Override
            public Weather getWeather() {
                return weather;
            }
        };
    }

    private Weather makeWeather(boolean obscuring, Damage damage) {
        return new Weather() {
            @Override public void addSpawnPoint(WeatherSpawnPoint sp) {}
            @Override public void addWeather(GameEntity w) {}
            @Override public boolean isObscuring(Dimensions d, Positionable p) { return obscuring; }
            @Override public boolean isDamaging(Dimensions d, Positionable p) { return damage != null; }
            @Override public void applyLightningRod(Positionable p) {}
            @Override public Damage getDamage(Dimensions d, Positionable p) { return damage; }
            @Override public Damage getDamage() { return damage; }
            @Override public void tick(EngineState state, GameState game) {}
            @Override public List<Renderable> render() { return List.of(); }
        };
    }

    /**
     * Confirms a newly constructed SolarPanel renders the 'default' sprite.
     */
    @Test
    @Deprecated
    public void initialSpriteIsDefault() {
        final Sprite expected = SpriteGallery.solarPanel.getSprite("default");
        assertEquals("initial SolarPanel sprite should be 'default'",
                expected.toString(), panel.getSprite().toString());
    }

    /**
     * Confirms the SolarPanel shows the 'off' sprite when weather is obscuring its position.
     */
    @Test
    @Deprecated
    public void spriteBecomesOffWhenObscured() {
        MockGameState obscuredState = makeGameStateWithWeather(makeWeather(true, null));
        panel.tick(baseEngineState, obscuredState);
        final Sprite expected = SpriteGallery.solarPanel.getSprite("off");
        assertEquals("SolarPanel sprite should be 'off' when obscured by weather",
                expected.toString(), panel.getSprite().toString());
    }

    /**
     * Confirms the SolarPanel returns to the 'default' sprite once obscuring is cleared.
     */
    @Test
    public void spriteReturnsToDefaultWhenNoLongerObscured() {
        panel.tick(baseEngineState, makeGameStateWithWeather(makeWeather(true, null)));
        panel.tick(baseEngineState, makeGameStateWithWeather(makeWeather(false, null)));
        final Sprite expected = SpriteGallery.solarPanel.getSprite("default");
        assertEquals("SolarPanel sprite should return to 'default' once obscuring clears",
                expected.toString(), panel.getSprite().toString());
    }

    /**
     * Confirms the SolarPanel shows the 'damaged' sprite when its damage handler is set.
     */
    @Test
    @Deprecated
    public void spriteIsDamagedWhenDamaged() {
        DamageHandler handler = new DamageHandler();
        panel = new SolarPanel(position, handler);
        handler.setDamage(new Damage(position));
        panel.tick(baseEngineState, baseGameState);
        final Sprite expected = SpriteGallery.solarPanel.getSprite("damaged");
        assertEquals("SolarPanel sprite should be 'damaged' when damage handler reports damage",
                expected.toString(), panel.getSprite().toString());
    }

    /**
     * Confirms pressing 'e' over a damaged SolarPanel repairs it.
     */
    @Test
    @Deprecated
    public void repairWithEKeyWhenDamaged() {
        DamageHandler handler = new DamageHandler();
        panel = new SolarPanel(position, handler);
        handler.setDamage(new Damage(position));

        ArrayList<Character> keys = new ArrayList<>();
        keys.add('e');
        MockEngineState eState = new MockEngineState(tileGrid, mockMouse, new MockKeys(keys));
        panel.playerOver(eState, baseGameState);

        panel.tick(eState, baseGameState);
        final Sprite expected = SpriteGallery.solarPanel.getSprite("default");
        assertEquals("SolarPanel should show 'default' sprite after being repaired with 'e'",
                expected.toString(), panel.getSprite().toString());
    }

    /**
     * Confirms pressing 'e' on an undamaged SolarPanel does not cause errors or change state.
     */
    @Test
    public void pressEKeyWhenNotDamagedDoesNothing() {
        ArrayList<Character> keys = new ArrayList<>();
        keys.add('e');
        MockEngineState eState = new MockEngineState(tileGrid, mockMouse, new MockKeys(keys));
        panel.playerOver(eState, baseGameState);
        panel.tick(eState, baseGameState);
        final Sprite expected = SpriteGallery.solarPanel.getSprite("default");
        assertEquals("undamaged SolarPanel should still show 'default' sprite after 'e' press",
                expected.toString(), panel.getSprite().toString());
    }

    /**
     * Confirms the SolarPanel adds power to the machine system after 120 ticks.
     */
    @Test
    @Deprecated
    public void powerGeneratedAfter120Ticks() {
        MachinesManager machines = new MachinesManager(0); // start at 0 so gain is visible
        MockGameState gameState = new MockGameState(machines);
        final int initialPower = machines.getPower();
        for (int i = 0; i < 125; i++) {
            panel.tick(baseEngineState, gameState);
        }
        assertTrue("SolarPanel should have added at least 1 power after 120+ ticks",
                machines.getPower() > initialPower);
    }
}
