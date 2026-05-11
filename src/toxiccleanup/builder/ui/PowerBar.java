package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Positionable;

/**
 * A HUD element representing one unit of the power bar. The power bar is displayed as a
 * vertical column of {@link PowerBar} segments in the top-left area of the screen, one per
 * maximum power unit. Each segment is rendered as either <b>charged</b> (power available) or
 * <b>uncharged</b> (power spent). {@link GuiManager} creates these and determines which are
 * charged based on the current power level relative to the maximum. Rendered using the default sprite from
 * {@link SpriteGallery#power}.
 *
 */
public class PowerBar extends GameEntity {
    private static final SpriteGroup art = SpriteGallery.power;

    /**
     * Constructs a {@link PowerBar} in the uncharged visual state at the given position.
     *
     * @param position position we wish to spawn the uncharged powerbar at.
     */
    public PowerBar(Positionable position) {
        super(position);
        setSprite(art.getSprite("bar"));
    }

    /**
     * Constructs a {@link PowerBar} in either the charged or uncharged visual state at
     * the given position.
     *
     * @param position position we wish to spawn the charged powerbar at.
     * @param charged  a boolean flag indicating if the newly constructed PowerBar should be set
     *                 to it's charged state.
     */
    public PowerBar(Positionable position, boolean charged) {
        super(position);
        if (charged) {
            setSprite(art.getSprite("chargedbar"));
        } else {
            setSprite(art.getSprite("bar"));
        }
    }
}
