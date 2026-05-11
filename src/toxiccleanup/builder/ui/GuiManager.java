package toxiccleanup.builder.ui;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.ui.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all HUD (heads-up display) elements shown during gameplay. Each tick, the
 * {@link GuiManager} rebuilds the following on-screen elements from the current game state:
 *
 * <ul>
 *   <li><b>Power icon</b>: a lightning-bolt icon in the top-left corner.</li>
 *   <li><b>Power bar</b>: a vertical column of {@link PowerBar} segments below the power icon,
 *       showing how much of the maximum power (14) is currently available; filled segments
 *       represent available power, empty segments represent spent power.</li>
 *   <li><b>Hearts</b>: a vertical column of {@link Heart} icons in the top-right corner,
 *       one per remaining HP point.</li>
 *   <li><b>Countdown timer</b>: a text display at the bottom-left showing the remaining game
 *       time in minutes and seconds.</li>
 * </ul>
 *
 * <p>When the game ends, {@link #win} or {@link #lose} is called to overlay a win/lose message
 * in the centre of the screen. Once set, this message persists until the game restarts.
 */
public class GuiManager implements Overlay {
    private static final int TIME_LIMIT_TICKS = 18000; // 5 minutes * 60 ticks/s
    private final List<GameEntity> items = new ArrayList<>();
    private int tileSize; //we need to track this internally so the gui knows where to place things
    private Text score;
    private Text winText;

    /**
     * Constructs a new instance of our {@link GuiManager}.
     */
    public GuiManager() {
    }

    /**
     * <p>Called each game tick to rebuild all HUD elements from the current game state. This method
     * recalculates and recreates the power icon, power bars, heart icons, and countdown timer
     * text on every tick so they always reflect up-to-date values.</p>
     *
     * <p> Notes:</p> <p>This method must be called before {@link #render()} is invoked, as render depends on state set here.</p>
     * <p>The countdown is based on a 5-minute game duration, i.e. 18000 ticks at 60 ticks per second.</p>
     * <p> Assume the game runs at 60 ticks per second when converting ticks to remaining time. </p>
     * <p> Format the countdown as "{minutes} {seconds}", with seconds padded to 2 digits. </p>
     * <p>The power icon is centred in the top-left tile, the power bars are placed below
     * it in a vertical column extending downward, and the hearts are likewise arranged downward
     * from the top-right.</p>
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void tick(EngineState state, GameState game) {
        final int remainingTicks = Math.max(0, TIME_LIMIT_TICKS - state.currentTick());
        final int remainingSeconds = remainingTicks / 60;
        final int minutes = remainingSeconds / 60;
        final int seconds = remainingSeconds % 60;
        final String countdownText = minutes + " " + String.format("%02d", seconds);
        final int windowSize = state.getDimensions().windowSize();
        final int currentTileSize = state.getDimensions().tileSize();
        this.score = new Text(countdownText, currentTileSize / 2, windowSize
                - (currentTileSize / 2), currentTileSize);
        items.clear();
        tileSize = state.getDimensions().tileSize();

        PowerIcon powerIcon = new PowerIcon(new Position(tileSize / 2, tileSize / 2));
        items.add(powerIcon);

        final int leftSide = tileSize / 2;
        final int rightSide = state.getDimensions().windowSize() - tileSize / 2;
        final int hpBarY = (int) (tileSize * 1.5);
        final int maxPower = game.getMachines().getMaxPower();

        final List<PowerBar> powerBars = buildPowerBars(new Position(leftSide, hpBarY),
                maxPower, game.getMachines().getPower());
        items.addAll(powerBars);

        final List<Heart> hearts = buildHpBars(new Position(rightSide, tileSize / 2),
                game.getPlayer().getHp());
        items.addAll(hearts);
    }


    /**
     * Switches the GUI to display a centred "YOU WIN" message. Called by
     * {@link toxiccleanup.builder.ToxicCleanup#tick} when all toxic fields have been cleared.
     * Once set, the win message is rendered on every subsequent call to {@link #render()}.
     *
     * @param state the current engine state, used to determine window dimensions for
     *              positioning the text in the centre of the screen.
     */
    public void win(EngineState state) {
        final int windowsize = state.getDimensions().windowSize();
        this.winText = new Text("YOU WIN", windowsize / 2,
                windowsize / 2, state.getDimensions().tileSize());
    }


    /**
     * Switches the GUI to display a centred "GAME OVER" message. Called by
     * {@link toxiccleanup.builder.ToxicCleanup#tick} when the player's HP reaches 0.
     * Once set, the game-over message is rendered on every subsequent call to {@link #render()}.
     *
     * @param state The state of the engine, used to determine window dimensions for
     *              positioning the text in the centre of the screen.
     */
    public void lose(EngineState state) {
        final int windowsize = state.getDimensions().windowSize();
        this.winText = new Text("GAME OVER", windowsize / 2,
                windowsize / 2, state.getDimensions().tileSize());
    }

    /**
     * Creates a vertical column of {@link Heart} icons starting at {@code position}, spaced
     * one tile apart downward. One heart is created per remaining HP point. Called each tick
     * by {@link #tick} with the player's current HP to rebuild the HP display.
     *
     * @param position    the screen position of the first (topmost) heart icon.
     * @param numOfHearts the number of hearts to create, equal to the player's current HP.
     * @return a list of {@link Heart} instances positioned vertically down the screen.
     */
    private List<Heart> buildHpBars(Positionable position, int numOfHearts) {
        final int y = position.getY();
        List<Heart> hearts = new ArrayList<>();
        for (int i = 0; i < numOfHearts; i += 1) {
            final int yOffset = i * tileSize;
            position.setY(y + yOffset);
            hearts.add(new Heart(position));
        }
        return hearts;
    }

    /**
     * Creates a vertical column of {@link PowerBar} segments starting at {@code position},
     * spaced one tile apart downward. The first {@code powerThreshold} segments are rendered
     * as charged (power available); the remaining segments are uncharged (power spent).
     * Called each tick by {@link #tick} to rebuild the power display.
     *
     * @param position       the screen position of the first (topmost) power bar segment.
     * @param numOfPowerBars the total number of segments to create, equal to max power (14).
     * @param powerThreshold the number of charged segments, equal to the current power level.
     * @return a list of {@link PowerBar} instances positioned vertically down the screen.
     */
    private List<PowerBar> buildPowerBars(Positionable position,
                                          int numOfPowerBars, int powerThreshold) {
        final int y = position.getY();
        final List<PowerBar> powerBars = new ArrayList<>();
        for (int i = 0; i < numOfPowerBars; i += 1) {
            final int yOffset = i * tileSize;
            position.setY(y + yOffset);
            if (powerThreshold > 0) {
                powerBars.add(new PowerBar(position, true));
            } else {
                powerBars.add(new PowerBar(position));
            }
            powerThreshold -= 1;
        }
        return powerBars;
    }

    /**
     * Returns all HUD {@link Renderable}s for the current frame. Includes the power icon, power bar
     * segments, and heart icons (rebuilt each tick by {@link #tick}), plus the countdown timer
     * text. If a win or lose condition has been triggered, the overlay message is also included.
     *
     * @return a list of all {@link Renderable} HUD elements to display this frame.
     */
    @Override
    public List<Renderable> render() {
        List<Renderable> renderables = new ArrayList<>();
        renderables.addAll(items);
        renderables.addAll(this.score.render());
        if (this.winText != null) {
            renderables.addAll(this.winText.render());
        }
        return renderables;
    }
}
