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

public class TeleporterTest {
    public static final double testWeight = 5.0;
    private final TileGrid tileGrid = new TileGrid(16, 800);
    private final MockMouse mockMouse = new MockMouse(2, 2, false, false, false);
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());
    private final MockEngineState baseEngineState = new MockEngineState(tileGrid, mockMouse, mockKeys);
    private final MockGameState baseGameState = new MockGameState();
    private final Position position = new Position(100, 100);
    private Teleporter teleporter;

    @Before
    public void setup() {
        teleporter = new Teleporter(position);
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
     * Confirms a newly constructed Teleporter renders frame '1' sprite.
     */
    @Test
    @Deprecated
    public void initialSpriteIsFrame1() {
        final Sprite expected = SpriteGallery.teleporter.getSprite("1");
        assertEquals("initial Teleporter sprite should be '1'",
                expected.toString(), teleporter.getSprite().toString());
    }

    /**
     * Confirms the Teleporter's power requirement is 2.
     */
    @Test
    @Deprecated
    public void getPowerRequirementReturns2() {
        assertEquals("Teleporter power requirement should be 2",
                2, teleporter.getPowerRequirement());
    }

    /**
     * Confirms a newly constructed Teleporter is not in the damaged state.
     */
    @Test
    @Deprecated
    public void isDamagedStartsFalse() {
        assertFalse("newly constructed Teleporter should not be damaged",
                teleporter.isDamaged());
    }

    /**
     * Confirms the Teleporter shows the 'damaged' sprite when weather deals damage at its location.
     */
    @Test
    @Deprecated
    public void spriteIsDamagedWhenDamaged() {
        MockGameState damagedState =
                makeGameStateWithWeather(makeWeather(false, new Damage(position)));
        teleporter.tick(baseEngineState, damagedState);
        final Sprite expected = SpriteGallery.teleporter.getSprite("damaged");
        assertEquals("Teleporter sprite should be 'damaged' when weather deals damage",
                expected.toString(), teleporter.getSprite().toString());
    }

    /**
     * Confirms pressing 'e' over a damaged Teleporter repairs it.
     */
    @Test
    @Deprecated
    public void repairWithEKeyWhenDamaged() {
        teleporter.setDamage(new Damage(position));
        assertTrue("Teleporter should be damaged before repair", teleporter.isDamaged());

        ArrayList<Character> keys = new ArrayList<>();
        keys.add('e');
        MockEngineState eState = new MockEngineState(tileGrid, mockMouse, new MockKeys(keys));
        teleporter.playerOver(eState, baseGameState);
        assertFalse("Teleporter should no longer be damaged after pressing 'e'",
                teleporter.isDamaged());
    }

    /**
     * Confirms the Teleporter animation advances when the timer fires and there is sufficient power.
     */
    @Test
    @Deprecated
    public void animationAdvancesWhenTimerFiresAndHasPower() {
        final Sprite initialSprite = teleporter.getSprite();
        // MockGameState's hasRequiredPower always returns true
        for (int i = 0; i < 13; i++) {
            teleporter.tick(baseEngineState, baseGameState);
        }
        assertNotEquals("Teleporter animation should have advanced after 12+ ticks with sufficient power",
                initialSprite.toString(), teleporter.getSprite().toString());
    }

    /**
     * Confirms the Teleporter animation does NOT advance when power is insufficient.
     */
    @Test
    public void animationDoesNotAdvanceWithoutPower() {
        final Sprite initialSprite = SpriteGallery.teleporter.getSprite("1");
        MachinesManager machines = new MachinesManager();
        machines.setPower(0); // below power requirement of 2
        MockGameState noPowerState = new MockGameState(machines);

        for (int i = 0; i < 13; i++) {
            teleporter.tick(baseEngineState, noPowerState);
        }
        assertEquals("Teleporter animation should NOT advance when power is insufficient",
                initialSprite.toString(), teleporter.getSprite().toString());
    }

    /**
     * Confirms the player is teleported to another teleporter's position when 'e' is pressed
     * and there is sufficient power.
     */
    @Test
    @Deprecated
    public void playerTeleportsWhenEPressedWithPower() {
        final Position targetPosition = new Position(400, 400);
        MockGameState teleportState = new MockGameState() {
            @Override
            public Machines getMachines() {
                return new Machines() {
                    private int power = 12;
                    private final int max = 14;

                    @Override public void tick(EngineState s, GameState g) {}
                    @Override public void setPower(int v) { this.power = v; }
                    @Override public int getPower() { return power; }
                    @Override public int getMaxPower() { return max; }
                    @Override public boolean hasRequiredPower(int req) { return power >= req; }
                    @Override public SolarPanel spawnSolarPanel(Positionable p) { return null; }
                    @Override public LightningRod spawnLightningRod(Positionable p) { return null; }
                    @Override public Teleporter spawnTeleporter(Positionable p) { return null; }
                    @Override public Pump spawnPump(Positionable p, Adjustable a) { return null; }
                    @Override public Positionable getNextTeleporterPosition(Positionable excluded) {
                        return targetPosition;
                    }
                    @Override public void adjust(int amount) {}
                };
            }
        };

        ArrayList<Character> keys = new ArrayList<>();
        keys.add('e');
        MockEngineState eState = new MockEngineState(tileGrid, mockMouse, new MockKeys(keys));
        teleporter.playerOver(eState, teleportState);

        final Positionable playerPos = teleportState.getPlayer().getPosition();
        assertEquals("player x should match the target teleporter x",
                targetPosition.getX(), playerPos.getX());
        assertEquals("player y should match the target teleporter y",
                targetPosition.getY(), playerPos.getY());
    }
}
