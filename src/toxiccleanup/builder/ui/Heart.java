package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;


/**
 * A HUD icon representing one unit of the player's HP. The {@link GuiManager} creates one
 * {@link Heart} per remaining HP point each tick, arranged in a vertical column on the
 * right side of the screen. Rendered using the default sprite from
 * {@link SpriteGallery#heart}.
 *
 */
public class Heart extends GameEntity {
    private static final SpriteGroup art = SpriteGallery.heart;

    /**
     * Constructs a new {@link Heart} instance.
     *
     * @param position position we wish the heart to be spawned at.
     */
    public Heart(Positionable position) {
        super(position);
        setSprite(art.getSprite("default"));
    }
}
