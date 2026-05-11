package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.ui.RenderableGroup;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.art.ArtNotFoundException;
import toxiccleanup.engine.art.sprites.Sprite;
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.Entity;
import toxiccleanup.engine.game.HasTick;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract base class for all ground tiles in the game world. Each tile occupies one cell
 * in the grid and is responsible for:
 *
 * <ul>
 *   <li>Displaying itself using a {@link SpriteGroup} set at construction (or changed later
 *       via {@link #setArt}).</li>
 *   <li>Maintaining a stack of {@link GameEntity}s that sit on top of it (e.g. a
 *       {@link toxiccleanup.builder.machines.SolarPanel} placed on a paved {@link Dirt} tile).</li>
 *   <li>Ticking itself and all stacked entities each frame, and removing any that are marked
 *       for removal.</li>
 *   <li>Implementing {@link PlayerOverHook#playerOver} to react when the player stands on this
 *       cell, and forwarding the event to any stacked entities that also implement the hook.</li>
 *   <li>Collecting all {@link Renderable}s for itself and its stack via {@link #render()}.</li>
 * </ul>
 *
 * <p>Concrete tile types ({@link Dirt}, {@link Grass}, {@link Chasm}, {@link ToxicField})
 * extend this class and implement their specific mechanics.
 *
 * @provided
 */
public abstract class Tile extends GameEntity implements PlayerOverHook, RenderableGroup, HasTick {

    private final List<GameEntity> stackedEntities = new ArrayList<>();
    private SpriteGroup art;

    /**
     * Constructs an instance of {@link Tile} at the given {@link Positionable}.
     *
     * @param position The position we wish to place this newly constructed tile at.
     * @param art      The sprite group art to use for this tile, the tile will initially render
     *                 as the 'default' sprite for this group.
     * @requires position.getX() >= 0, position.getX() is less than the window width
     * @requires position.getY() >= 0, position.getY() is less than the window height
     * @requires The given sprite group must contain a 'default' sprite.
     */
    public Tile(Positionable position, SpriteGroup art) {
        super(position);
        setArt(art);
    }

    /**
     * Replaces this tile's sprite group and immediately switches the displayed sprite to the
     * 'default' sprite within the new group. Used when a tile changes its visual appearance
     * entirely, for example when a {@link Dirt} tile is paved and switches from the dirt
     * sprite group to the paved sprite group.
     *
     * @param art a sprite group to use for this tile's sprites going forward.
     * @requires The given sprite group must contain a 'default' sprite.
     */
    public void setArt(SpriteGroup art) {
        this.art = art;
        updateSprite("default");
    }

    /**
     * Change the current sprite (see {@link #setSprite(Sprite)}) to the given artwork name within
     * the tiles current art (i.e. the sprite group provided to the constructor or set by {@link
     * #setArt(SpriteGroup)}).
     *
     * @param artName The name of the art within the sprite group.
     * @throws ArtNotFoundException If the given name doesn't exist within the sprite group.
     * @hint You don't need to do anything special to throw {@link ArtNotFoundException}, {@link
     * SpriteGroup#getSprite(String)} will do it for you.
     */
    public void updateSprite(String artName) throws ArtNotFoundException {
        setSprite(art.getSprite(artName));
    }

    /**
     * Advances this tile by one game tick, removes any stacked entities marked for removal, then advances each remaining stacked entity by one tick.
     *
     * @param engine The state of the engine, including the mouse, keyboard information and
     *               dimension.
     * @param game   The state of the game, including the player and world.
     * @hint You may have to modify a list while iterating through it, this will throw a {@link
     * java.util.ConcurrentModificationException}. There are a few ways to work around this, see
     * <a href="https://edstem.org/au/courses/31746/discussion/3149206">this Ed post</a> for
     * options.
     */
    public void tick(EngineState engine, GameState game) {
        super.tick(engine);
        cleanup();
        for (GameEntity stackedEntity : this.stackedEntities) {
            stackedEntity.tick(engine, game);
        }
    }

    /**
     * Removes any stacked entities that are marked for removal.
     * Loops through the stackEntities list backwards so as we remove entities
     * we do not affect the position of future entities in the list.
     */
    private void cleanup() {
        for (int i = this.stackedEntities.size() - 1; i >= 0; i -= 1) {
            if (this.stackedEntities.get(i).isMarkedForRemoval()) {
                this.stackedEntities.remove(i);
            }
        }
    }

    /**
     * Returns the list of entities currently stacked on top of this tile
     *
     * @return Any entities stacked onto this tile.
     */
    public List<GameEntity> getStackedEntities() {
        return new ArrayList<>(stackedEntities);
    }

    /**
     * Filters the stacked entity list to return only those that implement
     * {@link PlayerOverHook}. Used by subclasses in
     * their {@link #playerOver} implementations to forward the event to relevant stacked
     * machines without needing to type-check manually.
     *
     * @return a new {@link List} containing only the stacked entities that implement
     * {@link PlayerOverHook}; empty if no such entities exist.
     */
    public List<PlayerOverHook> getStackedEntitiesWithPlayerOverHook() {
        final List<PlayerOverHook> PlayerOverHooks = new ArrayList<>();
        for (Entity stackedEntity : stackedEntities) {
            if (stackedEntity instanceof PlayerOverHook) {
                PlayerOverHooks.add((PlayerOverHook) stackedEntity);
            }
        }
        return PlayerOverHooks;
    }

    /**
     * Adds the given entity to this tile's stacked entity list. Used when a machine is built
     * on top of a tile (e.g. placing a {@link toxiccleanup.builder.machines.SolarPanel} on a paved
     * {@link Dirt} tile). After this call the entity will be ticked and rendered as part of
     * this tile each frame.
     *
     * @param tile the {@link GameEntity} to place on top of this tile.
     * @ensures The entity is contained within {@link #getStackedEntities()}.
     */
    public void placeOn(GameEntity tile) {
        stackedEntities.add(tile);
    }

    /**
     * Called each tick the player occupies this tile's grid cell. The base implementation
     * forwards the event to every stacked entity that also implements {@link PlayerOverHook},
     * allowing machines or other entities on the tile to react to the player's presence.
     * Subclasses should call this (or re-implement the forwarding) and add their own interaction
     * logic (e.g. dealing damage, building machines).
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        for (Entity stackedEntity : stackedEntities) {
            if (stackedEntity instanceof PlayerOverHook usable) {
                usable.playerOver(state, game);
            }
        }
    }

    /**
     * A collection of items to render, including the tile and any entities stacked on it.
     *
     * <p>This tile must be the first renderable in the list so that it is rendered behind each
     * stacked entity. The remaining list must match the order of {@link #getStackedEntities()}.
     *
     * @return The list of renderables required to draw this tile to the screen.
     */
    @Override
    public List<Renderable> render() {
        List<Renderable> result = new ArrayList<>(List.of(this));
        result.addAll(getStackedEntities());
        return result;
    }
}
