package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.core.headless.MockKeys;
import toxiccleanup.engine.core.headless.MockMouse;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.renderer.TileGrid;
import toxiccleanup.engine.util.MockEngineState;
import toxiccleanup.engine.util.MockGameState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GuiManagerTest {
    public static final double testWeight = 10.0;
    private final int SIZE = 800;
    private final int TILES_PER_ROW = 16;
    private final MockMouse mockMouse = new MockMouse(
            2, 2,
            false, false, false
    );
    private final MockKeys mockKeys = new MockKeys(new ArrayList<>());

    private final Dimensions tileGrid = new TileGrid(TILES_PER_ROW, SIZE);
    private final MockEngineState baseEngineState =
            new MockEngineState(tileGrid, mockMouse, mockKeys);
    private final MockGameState baseGameState = new MockGameState();
    private GuiManager guiManager;

    @Before
    public void setup() {
        guiManager = new GuiManager();
    }

    @Test
    @Deprecated
    public void rendersPowerIcon() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();

        boolean found = false;
        Sprite powerIcon = SpriteGallery.power.getSprite("icon");
        for (Renderable renderable : renderables) {
            if (powerIcon.toString().equals(renderable.getSprite().toString())) {
                found = true;
            }
        }
        assertTrue(
                "gui should have rendered a power icon sprite when .render() called",
                found
        );
    }

    @Test
    @Deprecated
    public void rendersCorrectNumberOfHearts() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        Sprite heartIcon = SpriteGallery.heart.getSprite("default");
        final List<Renderable> hearts = renderables.stream().filter(
                r -> r.getSprite().toString().equals(heartIcon.toString())
        ).toList();
        assertEquals(
                "Should have rendered one heart sprite per player hp",
                baseGameState.getPlayer().getHp(),
                hearts.size()
        );
    }

    /**
     * Confirm all hearts are aligned on the x-axis.
     */
    @Test
    public void rendersHeartsAlignedOnX() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        Sprite heartIcon = SpriteGallery.heart.getSprite("default");
        final List<Renderable> hearts = renderables.stream().filter(
                r -> r.getSprite().toString().equals(heartIcon.toString())
        ).toList();
        alignedOnX(hearts);
    }


    /**
     * Confirm all hearts are at different positions on their y-axis
     * as a proxy for vertical placement.
     */
    @Test
    public void rendersHeartsWithDifYs() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        Sprite heartIcon = SpriteGallery.heart.getSprite("default");
        final List<Renderable> hearts = renderables.stream().filter(
                r -> r.getSprite().toString().equals(heartIcon.toString())
        ).toList();
        for (Renderable heart : hearts) {
            for (Renderable otherHeart : hearts) {
                if (!heart.equals(otherHeart)) {
                    assertNotEquals(heart.getY(), otherHeart.getY());
                }
            }
        }
    }

    /**
     * Confirm the correct number of charged power bars are being created
     */
    @Test
    public void rendersCorrectNumberOfChargedPowerBars() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        final Sprite chargedBar = SpriteGallery.power.getSprite("chargedbar");
        final List<Renderable> chargedBars = renderables.stream().filter(
                r -> chargedBar.toString().equals(r.getSprite().toString())
        ).toList();
        final int power = baseGameState.getMachines().getPower();
        assertEquals(
                "Should have a charged power bar for each point of power in machine system",
                power,
                chargedBars.size()
        );
    }

    @Test
    public void rendersChargedPowerBarsAlignedOnX() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        final Sprite chargedBar = SpriteGallery.power.getSprite("chargedbar");
        final List<Renderable> chargedBars = renderables.stream().filter(
                r -> chargedBar.toString().equals(r.getSprite().toString())
        ).toList();
        int targetX = chargedBars.getFirst().getX();
        for (Renderable bar : chargedBars) {
            assertEquals(
                    "all bars should have the same x axis",
                    targetX, bar.getX()
            );
        }
    }

    /**
     * Confirm all hearts are at different positions on their y-axis
     * as a proxy for vertical placement.
     */
    @Test
    public void rendersChargedPowerBarsWithDifYs() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        final Sprite chargedBar = SpriteGallery.power.getSprite("chargedbar");
        final List<Renderable> chargedBars = renderables.stream().filter(
                r -> chargedBar.toString().equals(r.getSprite().toString())
        ).toList();
        for (Renderable bar : chargedBars) {
            for (Renderable otherBar : chargedBars) {
                if (!bar.equals(otherBar)) {
                    assertNotEquals(
                            "no 2 power bars should share the same y coordinate",
                            bar.getY(), otherBar.getY()
                    );
                }
            }
        }
    }

    /**
     * Gui should render a countdown timer starting at 5 minutes (300 seconds),
     * decreasing as ticks progress.
     */
    @Test
    @Deprecated
    public void rendersCountdownFromFiveMinutes() {
        guiManager = new GuiManager();

        // At tick 0: remaining = 18000 ticks = 300s = "5 00" → expect "5" sprite
        MockEngineState initialEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(0);
        guiManager.tick(initialEngineState, baseGameState);

        final Sprite fiveSprite = SpriteGallery.letters.getSprite("5");
        Assert.assertTrue(
                "Gui should render a '5' sprite at tick 0 (countdown starts at 5 minutes)",
                hasSprite(guiManager.render(), fiveSprite)
        );

        // At tick 1800 (30s elapsed): remaining = 270s = "4 30" → expect "4" sprite
        MockEngineState thirtySecEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(1800);
        guiManager.tick(thirtySecEngineState, baseGameState);

        final Sprite fourSprite = SpriteGallery.letters.getSprite("4");
        Assert.assertTrue(
                "Gui should render a '4' sprite at tick 1800 (4 minutes 30 seconds remaining)",
                hasSprite(guiManager.render(), fourSprite)
        );

        // At tick 10800 (3 min elapsed): remaining = 120s = "2 00" → expect "2" sprite
        MockEngineState threeMinEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(10800);
        guiManager.tick(threeMinEngineState, baseGameState);

        final Sprite twoSprite = SpriteGallery.letters.getSprite("2");
        Assert.assertTrue(
                "Gui should render a '2' sprite at tick 10800 (2 minutes remaining)",
                hasSprite(guiManager.render(), twoSprite)
        );
    }

    @Test
    public void scoreIsCorrectyLaidOut() {
        guiManager = new GuiManager();
        // At tick 655: remaining = 17345 ticks = 289s → 4 minutes 49 seconds = "4 49"
        MockEngineState tempEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(655);
        guiManager.tick(tempEngineState, baseGameState);

        // "4 49" contains digits '4', ' ', '4', '9' — get the first '4', ' ' space gap, second '4', and '9'
        // We collect all renderables with the '4' sprite (there should be two), and '9'
        final List<Renderable> allRenderables = guiManager.render();
        final Sprite fourSprite = SpriteGallery.letters.getSprite("4");
        final Sprite nineSprite = SpriteGallery.letters.getSprite("9");

        final List<Renderable> fours = allRenderables.stream()
                .filter(r -> fourSprite.toString().equals(r.getSprite().toString()))
                .toList();
        final List<Renderable> nines = allRenderables.stream()
                .filter(r -> nineSprite.toString().equals(r.getSprite().toString()))
                .toList();

        Assert.assertEquals(
                "When rendering '4 49', there should be two '4' digit sprites",
                2, fours.size()
        );
        Assert.assertEquals(
                "When rendering '4 49', there should be one '9' digit sprite",
                1, nines.size()
        );

        // All digits should share the same y-coordinate (horizontal layout)
        final int targetY = fours.getFirst().getY();
        for (Renderable r : fours) {
            Assert.assertEquals(
                    "Countdown digits should be aligned on the y-axis", targetY, r.getY()
            );
        }
        Assert.assertEquals(
                "Countdown digits should be aligned on the y-axis", targetY, nines.getFirst().getY()
        );

        // The two '4' sprites must occupy different x positions
        Assert.assertNotEquals(
                "The two '4' sprites in '4 49' should have different x coordinates",
                fours.get(0).getX(), fours.get(1).getX()
        );
    }


    /**
     * Gui should render the countdown as digits (minutes and seconds),
     * not as the old 'pts' label.
     */
    @Test
    public void rendersCountdownDigits() {
        guiManager = new GuiManager();
        // At tick 0: remaining = 18000 ticks = 300s = "5 00" → digits '5' and '0'
        MockEngineState initialEngineState =
                new MockEngineState(tileGrid, mockMouse, mockKeys).withFrame(0);
        guiManager.tick(initialEngineState, baseGameState);

        final Sprite fiveSprite = SpriteGallery.letters.getSprite("5");
        final Sprite zeroSprite = SpriteGallery.letters.getSprite("0");
        Assert.assertTrue(
                "Gui should render a '5' sprite at tick 0 (5 minutes remaining)",
                hasSprite(guiManager.render(), fiveSprite)
        );
        Assert.assertTrue(
                "Gui should render '0' sprites at tick 0 (seconds portion of 5:00)",
                hasSprite(guiManager.render(), zeroSprite)
        );

        // The old 'pts' label should no longer appear
        final Sprite p = SpriteGallery.letters.getSprite("P");
        final Sprite t = SpriteGallery.letters.getSprite("T");
        final Sprite s = SpriteGallery.letters.getSprite("S");
        Assert.assertFalse(
                "Gui should NOT render 'P' sprite — 'pts' label is replaced by countdown",
                hasSprite(guiManager.render(), p)
        );
        Assert.assertFalse(
                "Gui should NOT render 'T' sprite — 'pts' label is replaced by countdown",
                hasSprite(guiManager.render(), t)
        );
        Assert.assertFalse(
                "Gui should NOT render 'S' sprite — 'pts' label is replaced by countdown",
                hasSprite(guiManager.render(), s)
        );
    }

    @Test
    public void rendersCorrectNumberOfUnchargedPowerBars() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        Sprite powerBar = SpriteGallery.power.getSprite("bar");
        final List<Renderable> unchargedBars = renderables.stream().filter(
                r -> r.getSprite().toString().equals(powerBar.toString())
        ).toList();

        final int maxPower = baseGameState.getMachines().getMaxPower();
        final int power = baseGameState.getMachines().getPower();
        assertEquals(
                "Should have rendered ",
                unchargedBars.size(),
                maxPower - power
        );
    }

    @Test
    public void rendersUnchargedPowerBarsAlignedOnX() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        Sprite powerBar = SpriteGallery.power.getSprite("bar");
        final List<Renderable> unchargedBars = renderables.stream().filter(
                r -> r.getSprite().toString().equals(powerBar.toString())
        ).toList();

        int targetX = unchargedBars.getFirst().getX();
        for (Renderable bar : unchargedBars) {
            assertEquals(
                    "all bars should have the same x axis",
                    targetX, bar.getX()
            );
        }
    }

    /**
     * Confirm all uncharged power bars are at different positions on their y-axis
     * as a proxy for vertical placement.
     */
    @Test
    public void rendersUnchargedPowerWithDifYs() {
        guiManager.tick(baseEngineState, baseGameState);
        final List<Renderable> renderables = guiManager.render();
        Sprite powerBar = SpriteGallery.power.getSprite("bar");
        final List<Renderable> unchargedBars = renderables.stream().filter(
                r -> r.getSprite().toString().equals(powerBar.toString())
        ).toList();
        for (Renderable bar : unchargedBars) {
            for (Renderable otherBar : unchargedBars) {
                if (!bar.equals(otherBar)) {
                    assertNotEquals(
                            "no 2 power bars should share the same y coordinate",
                            bar.getY(), otherBar.getY()
                    );
                }
            }
        }
    }

    /**
     * Returns if the given list of renderables includes at least one instance of the given Sprite.
     *
     * @param list         of renderables to check
     * @param spriteTarget sprite we are checking for.
     * @return if the given list of renderables includes at least one instance of the given Sprite.
     */
    public boolean hasSprite(List<Renderable> list, Sprite spriteTarget) {
        for (Renderable r : list) {
            if (r.getSprite().toString().equals(spriteTarget.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns if the given list of renderables includes at least one instance of the given Sprite.
     *
     * @param list         of renderables to check
     * @param spriteTarget sprite we are checking for.
     * @return if the given list of renderables includes at least one instance of the given Sprite.
     */
    public Renderable getRenderableWithSprite(List<Renderable> list, Sprite spriteTarget) {
        for (Renderable r : list) {
            if (r.getSprite().toString().equals(spriteTarget.toString())) {
                return r;
            }
        }
        return null;
    }


    /**
     * Returns if the given list of Renderables is aligned on the x-axis.
     *
     * @param list list of Renderables we wish to confirm are aligned on the x-axis
     */
    public void alignedOnX(List<Renderable> list) {
        int targetX = list.getFirst().getX();
        for (Renderable r : list) {
            assertEquals("all renderables should have the same x-axis", targetX, r.getX());
        }
    }

}

