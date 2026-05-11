package toxiccleanup.engine.util;

import toxiccleanup.builder.Damage;
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.machines.*;
import toxiccleanup.builder.player.Player;
import toxiccleanup.builder.weather.Weather;
import toxiccleanup.builder.weather.WeatherSpawnPoint;
import toxiccleanup.builder.world.World;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;

import java.util.List;

public class MockGameState implements GameState {
    private World world;
    private Player player;
    private Machines machines;

    public MockGameState() {
        this.world = new World() {
            @Override
            public List<Tile> tilesAtPosition(Positionable position, Dimensions dimensions) {
                return List.of();
            }

            @Override
            public List<Tile> allTiles() {
                return List.of();
            }

            @Override
            public void place(Tile tile) {
            }
        };
        this.player = new Player() {
            private int hp = 5;
            private final int maxHp = 10;
            private int x = 20;
            private int y = 20;

            @Override
            public List<Renderable> render() {
                return List.of();
            }

            @Override
            public void adjust(int amount) {
                hp -= amount;
                hp = Math.clamp(hp, 0, maxHp);
            }

            @Override
            public int getHp() {
                return hp;
            }

            @Override
            public int getMaxHp() {
                return maxHp;
            }

            @Override
            public void tick(EngineState state, GameState game) {
            }

            @Override
            public Positionable getPosition() {
                return new Position(x, y);
            }

            @Override
            public void setPosition(Positionable mockPosition) {
                this.x = mockPosition.getX();
                this.y = mockPosition.getY();
            }
        };
        this.machines = new Machines() {
            /**
             * Advances component state by one game tick using engine and game context.
             *
             * @param state The state of the engine, including the mouse, keyboard information and
             *              dimension. Useful for processing keyboard presses or mouse movement.
             * @param game  The state of the game, including the player and world. Can be used to query or
             *              update the game state.
             */
            @Override
            public void tick(EngineState state, GameState game) {

            }

            private int power = 12;
            private final int max = 14;

            @Override
            public void setPower(int value) {
                this.power = value;
            }

            @Override
            public int getPower() {
                return this.power;
            }

            @Override
            public int getMaxPower() {
                return this.max;
            }

            @Override
            public boolean hasRequiredPower(int powerRequirement) {
                return true;
            }

            @Override
            public SolarPanel spawnSolarPanel(Positionable position) {
                return null;
            }

            @Override
            public LightningRod spawnLightningRod(Positionable position) {
                return null;
            }

            @Override
            public Teleporter spawnTeleporter(Positionable position) {
                return null;
            }

            @Override
            public Pump spawnPump(Positionable position, Adjustable adjustable) {
                return null;
            }

            @Override
            public Positionable getNextTeleporterPosition(Positionable excludedPosition) {
                return null;
            }

            @Override
            public void adjust(int amount) {
            }
        };
    }

    public MockGameState(Machines machines) {
        this();
        this.machines = machines;
    }

    public void setMachines(Machines machines) {
        this.machines = machines;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Machines getMachines() {
        return this.machines;
    }

    /**
     * Returns the current state of the weather system and access to methods for add Weather phenomena.
     *
     * @return the current state of the weather system and access to methods for add Weather phenomena.
     */
    @Override
    public Weather getWeather() {
        return new Weather() {
            @Override
            public void addSpawnPoint(WeatherSpawnPoint spawnPoint) {

            }

            @Override
            public void addWeather(GameEntity weather) {

            }

            @Override
            public boolean isObscuring(Dimensions dimensions, Positionable position) {
                return false;
            }

            @Override
            public boolean isDamaging(Dimensions dimensions, Positionable position) {
                return false;
            }

            @Override
            public void applyLightningRod(Positionable position) {

            }

            @Override
            public void tick(EngineState state, GameState game) {

            }

            @Override
            public List<Renderable> render() {
                return List.of();
            }

            @Override
            public Damage getDamage(Dimensions dimensions, Positionable position) {
                return null;
            }

            @Override
            public Damage getDamage() {
                return null;
            }
        };
    }
}
