package toxiccleanup.builder.ui;

import toxiccleanup.builder.Tickable;

/**
 * A screen-space HUD overlay that is both renderable and tickable. An {@link Overlay} combines
 * {@link RenderableGroup} (providing a list of {@link toxiccleanup.engine.renderer.Renderable}s to draw each
 * frame) with {@link Tickable} (allowing the overlay to update its contents based on the current
 * game state each tick).
 *
 * <p>Implement this interface for any HUD component that needs to read game state (e.g. current
 * HP, power level) and produce matching renderables for display. {@link GuiManager} is the
 * concrete implementation used in {@link toxiccleanup.builder.ToxicCleanup}.</p>
 *
 * @provided
 */
public interface Overlay extends RenderableGroup, Tickable {
}
