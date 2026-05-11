package toxiccleanup.builder.util;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.world.World;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;

import java.util.ArrayList;
import java.util.List;

public class MockWorld implements World {
    private final List<Tile> tiles = new ArrayList<>();

    @Override
    public List<Tile> tilesAtPosition(Positionable position, Dimensions worldDimensions) {
        final int x = position.getX();
        final int y = position.getY();
        return tiles.stream()
                .filter(
                        t ->
                                worldDimensions.pixelToTile(t.getX())
                                        == worldDimensions.pixelToTile(x)
                                        && worldDimensions.pixelToTile(t.getY())
                                        == worldDimensions.pixelToTile(y))
                .toList();
    }


    @Override
    public List<Tile> allTiles() {
        return new ArrayList<>(tiles);
    }

    @Override
    public void place(Tile tile) {
        this.tiles.add(tile);
    }

    /**
     * Resets world state to empty.
     */
    public void reset() {
        this.tiles.clear();
    }
}
