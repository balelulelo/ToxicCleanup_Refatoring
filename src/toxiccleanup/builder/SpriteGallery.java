package toxiccleanup.builder;

import toxiccleanup.engine.art.ArtNotFoundException;
import toxiccleanup.engine.art.loader.ArtLoader;
import toxiccleanup.engine.art.loader.MalformedArtException;
import toxiccleanup.engine.art.sprites.SpriteGroup;

import java.io.IOException;

/**
 * A repository of sprites to use throughout the game.
 *
 * @provided
 */
public class SpriteGallery {
    /**
     * Sprite group for cloud sprites.
     */
    public static final SpriteGroup lightningrod = load("LightningRod", "lightningrod");

    /**
     * Sprite group for cloud sprites.
     */
    public static final SpriteGroup lightning = load("Lightning", "lightning");


    /**
     * Sprite group for cloud sprites.
     */
    public static final SpriteGroup cloud = load("Cloud", "cloud");

    /**
     * Sprite group for acid cloud sprites.
     */
    public static final SpriteGroup acidcloud = load("AcidCloud", "acidcloud");

    /**
     * Sprite group for acid cloud sprites.
     */
    public static final SpriteGroup raincloud = load("RainCloud", "raincloud");

    /**
     * Sprite group for grass tiles.
     */
    public static final SpriteGroup grass = load("Grass", "grass");
    /**
     * Sprite group for dirt tiles.
     */
    public static final SpriteGroup dirt = load("Dirt", "dirt");
    /**
     * Sprite group for paved tiles.
     */
    public static final SpriteGroup paved = load("Paved", "paved");
    /**
     * Sprite group for toxic field tiles.
     */
    public static final SpriteGroup toxicField = load("ToxicField", "toxicField");
    /**
     * Sprite group for chasm tiles.
     */
    public static final SpriteGroup chasm = load("Chasm", "chasm");

    /**
     * Sprite group for the power bar UI element.
     */
    public static final SpriteGroup power = load("Power", "power");
    /**
     * Sprite group for letter/text UI elements.
     */
    public static final SpriteGroup letters = load("Letters", "letter");
    /**
     * Sprite group for the heart/HP UI element.
     */
    public static final SpriteGroup heart = load("Heart", "heart");

    /**
     * Sprite group for the player character (cleaner).
     */
    public static final SpriteGroup cleaner = load("Cleaner", "cleaner");

    /**
     * Sprite group for the solar panel machine.
     */
    public static final SpriteGroup solarPanel = load("SolarPanel", "solarPanel");
    /**
     * Sprite group for the pump machine.
     */
    public static final SpriteGroup pump = load("Pump", "pump");
    /**
     * Sprite group for the teleporter machine.
     */
    public static final SpriteGroup teleporter = load("Teleporter", "teleporter");

    private SpriteGallery() {
    }

    /**
     * Load a sprite image from an art file at resources/art/[spriteFilename].art. The group of
     * assets under groupName are returned.
     *
     * @param spriteFilename The name of the file under resources/art/ to load.
     * @param groupName      The common prefix of sprites within the given file.
     */
    private static SpriteGroup load(String spriteFilename, String groupName) {
        try {
            return ArtLoader.load("resources/art/" + spriteFilename + ".art").lookup(groupName);
        } catch (IOException | ArtNotFoundException | MalformedArtException e) {
            // Cannot throw a checked exception when instantiating a static field.
            // Wrap up any thrown exception as RuntimeException
            // This should crash the JVM when starting up the game
            throw new RuntimeException(e);
        }
    }
}
