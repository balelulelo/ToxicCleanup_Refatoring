package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;

/**
 * A decorative HUD icon displayed in the top-left corner of the screen to label the power bar.
 * Rendered using the 'icon' sprite from {@link SpriteGallery#power}. The
 * {@link GuiManager} places one {@link PowerIcon} above the column of {@link PowerBar} segments
 * each tick.
 */
public class PowerIcon extends GameEntity {
    private static final SpriteGroup art = SpriteGallery.power;

    /**
     * Constructs a new {@link PowerIcon} instance.
     *
     * @param position position we wish the PowerIcon to be spawned at.
     */
    public PowerIcon(Positionable position) {
        super(position);
        setSprite(art.getSprite("icon"));
    }

}
