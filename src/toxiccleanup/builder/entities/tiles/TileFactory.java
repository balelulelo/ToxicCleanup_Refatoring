package toxiccleanup.builder.entities.tiles;

import toxiccleanup.engine.game.Positionable;

/**
 * A tile factory uses the {@link #fromSymbol(Positionable, char)} method to construct new tile
 * instances from a set encoding.
 *
 */
public class TileFactory {

    /**
     * Constructs a new {@link Tile} based on the symbol encoded at the given position.
     * The following table enumerates the tile encodings.
     *
     * <table>
     *     <tr><th>Character</th><td>Tile</td></tr>
     *     <tr><td>d</td><td>Dirt</td></tr>
     *     <tr><td>t</td><td>ToxicField</td></tr>
     *     <tr><td>g</td><td>Grass</td></tr>
     *     <tr><td>l</td><td>Chasm (set to 'left' facing sprite)</td></tr>
     *     <tr><td>L</td><td>Chasm (set to 'leftslope' facing sprite)</td></tr>
     *     <tr><td>r</td><td>Chasm (set to 'right' facing sprite)</td></tr>
     *     <tr><td>R</td><td>Chasm (set to 'rightslope' facing sprite)</td></tr>
     *     <tr><td>c</td><td>Chasm</td></tr>
     *     <caption>&nbsp;</caption>
     * </table>
     *
     * <p>Any characters not listed above should throw an {@link IllegalArgumentException}.
     *
     * @param position the position we wish to place our next tile at.
     * @param symbol   A symbol to identify the tile type.
     * @return A new tile at the given x,y coordinate of the type specified by the symbol.
     * @throws IllegalArgumentException If symbol does not correspond to a tile.
     * @requires position.getX() >= 0, position.getY() >= 0
     */
    public static Tile fromSymbol(Positionable position, char symbol) {
        assert position.getX() >= 0 && position.getY() >= 0;
        //jb: this isn't great, but I don't want to throw out the entire switch for one edge.
        if (symbol == 'D') {
            final Dirt pavedDirt = new Dirt(position);
            pavedDirt.pave();
            return pavedDirt;
        }
        return switch (symbol) {
            case 'd' -> new Dirt(position);
            case 'g' -> new Grass(position);
            case 'c' -> new Chasm(position);
            case 'l' -> new Chasm(position, "left");
            case 'L' -> new Chasm(position, "leftslope");
            case 'r' -> new Chasm(position, "right");
            case 'R' -> new Chasm(position, "rightslope");
            case 't' -> new ToxicField(position);
            default -> throw new IllegalArgumentException("Symbol does not represent a tile.");
        };
    }
}
