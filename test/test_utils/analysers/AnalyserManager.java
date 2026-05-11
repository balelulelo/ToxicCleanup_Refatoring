package test_utils.analysers;

import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;

import java.util.*;
import java.util.function.Predicate;

/**
 * Responsible for holding all the generated {@link RenderableAnalyser}s identifiable by their
 * stringified UUIDS. Holds several useful predicate driven methods like .every, .count, .filter to
 * help with common checks when interrogating overall game state in our tests.
 */
public class AnalyserManager {

    private final Map<String, RenderableAnalyser> data = new HashMap<>();

    /**
     * Constructs a new empty AnalyserManager.
     */
    public AnalyserManager() {
    }

    /**
     * Record the state of the given renderable during the given frame. If the renderable has not
     * previously been tracked, it will be added to the internal tracking.
     *
     * @param frame      The current frame number according to {@link EngineState#currentTick()}.
     * @param renderable The renderable we want to begin tracking or update the state of one we are
     *                   currently tracking.
     */
    public void add(int frame, Renderable renderable) {
        if (!data.containsKey(renderable.getID())) {
            data.put(renderable.getID(), new RenderableAnalyser(renderable.getID()));
        }
        data.get(renderable.getID()).addFrameData(frame, renderable);
    }

    /**
     * Return a {@link RenderableAnalyser} that matches the given id.
     *
     * @param id id we are filtering by.
     * @return a {@link RenderableAnalyser} that matches the given id or null.
     */
    public RenderableAnalyser get(String id) {
        return data.get(id);
    }

    /**
     * Return the first {@link RenderableAnalyser} spawned that belongs to the given spriteGroup.
     *
     * @param label label we wish to filter for spriteGroup by.
     * @return the first {@link RenderableAnalyser} spawned that belongs to the given spriteGroup.
     */
    public RenderableAnalyser getFirstSpawnedOfSpriteGroup(String label) {
        int spawnTime = Integer.MAX_VALUE;
        RenderableAnalyser renderable = null;
        for (final RenderableAnalyser entry : this.getBySpriteGroup(label)) {
            if (entry.getFirstFrame().getFrame() < spawnTime) {
                spawnTime = entry.getFirstFrame().getFrame();
                renderable = entry;
            }
        }
        return renderable;
    }

    /**
     * Returns an {@link ArrayList} of {@link RenderableAnalyser}s filtered by the given label
     * against each {@link RenderableAnalyser}s spriteGroup.
     *
     * @param label spriteGroup label we wish to filter for.
     * @return an *unsorted* list of {@link RenderableAnalyser}s filtered by the given label against
     * each {@link RenderableAnalyser}s spriteGroup.
     */
    public List<RenderableAnalyser> getBySpriteGroup(String label) {
        final List<RenderableAnalyser> result = new ArrayList<>();
        for (final RenderableAnalyser analyser : this.getAll()) {
            if (Objects.equals(analyser.spriteGroup(), label)) {
                result.add(analyser);
            }
        }
        return result;
    }

    /**
     * Returns every {@link RenderableAnalyser} stored in this {@link AnalyserManager}.
     *
     * @return every {@link RenderableAnalyser} stored in this {@link AnalyserManager}.
     */
    public List<RenderableAnalyser> getAll() {
        return new ArrayList<>(data.values());
    }

    /**
     * Checks if every {@link RenderableAnalyser} in the target spriteGroup matches against the
     * given conditional function.
     *
     * @param label label
     * @param func  conditional function
     * @return if every {@link RenderableAnalyser} in the target spriteGroup matches against the
     * given conditional function.
     */
    public boolean every(String label, Predicate<RenderableAnalyser> func) {
        for (final RenderableAnalyser analyser : getBySpriteGroup(label)) {
            if (!func.test(analyser)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks the given function against the target spriteGroup and returns how many of the {@link
     * RenderableAnalyser} fulfill that conditional function
     */
    public int count(String label, Predicate<RenderableAnalyser> func) {
        int count = 0;
        for (final RenderableAnalyser analyser : getBySpriteGroup(label)) {
            if (func.test(analyser)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks the given function against the target spriteGroup and returns any of the {@link
     * RenderableAnalyser}s fulfill the conditional function condition.
     *
     * @param label spriteGroup label we use to help filter to what we want to filter further
     * @param func  - conditional func we use to assess whether we wish the time being checked by the
     *              func should be a part of the returned {@link ArrayList}
     * @return returns any of the {@link RenderableAnalyser}s fulfill the conditional function
     * condition.
     */
    public List<RenderableAnalyser> filter(String label, Predicate<RenderableAnalyser> func) {
        final List<RenderableAnalyser> result = new ArrayList<>();
        for (final RenderableAnalyser analyser : getBySpriteGroup(label)) {
            if (func.test(analyser)) {
                result.add(analyser);
            }
        }
        return result;
    }

    public String getStringMiniMapForFrame(int frameTarget, Dimensions dimensions) {
        final List<RenderableAnalyser> records = this.getFrame(frameTarget);
        final HashMap<String, String> map = new HashMap<>();
        for (final RenderableAnalyser entry : records) {
            final FrameRecord frame = entry.getFrame(frameTarget);
            if (frame.getY() > dimensions.windowSize() || frame.getX() > dimensions.windowSize()) {
                continue;
            }
            final int gridX = dimensions.pixelToTile(frame.getX());
            final int gridY = dimensions.pixelToTile(frame.getY());
            if (entry.spriteGroup().equals("cleaner")) {
                map.put(new Position(gridX, gridY).toString(), " C ");
            } else if (entry.spriteGroup().equals("pump")) {
                map.put(new Position(gridX, gridY).toString(), " P ");
            } else if (entry.spriteGroup().equals("solarPanel")) {
                map.put(new Position(gridX, gridY).toString(), " $ ");
            } else if (entry.spriteGroup().equals("teleporter")) {
                map.put(new Position(gridX, gridY).toString(), " t ");
            } else if (entry.spriteGroup().equals("cloud")) {
                map.put(new Position(gridX, gridY).toString(), " o ");
            } else if (entry.spriteGroup().equals("toxicField")) {
                map.put(new Position(gridX, gridY).toString(), " x ");
            } else if (entry.spriteGroup().equals("power")) {
                map.put(new Position(gridX, gridY).toString(), " p ");
            } else if (entry.spriteGroup().equals("acidcloud")) {
                map.put(new Position(gridX, gridY).toString(), " a ");
            } else if (entry.spriteGroup().equals("lightning")) {
                map.put(new Position(gridX, gridY).toString(), " l ");
            } else if (entry.spriteGroup().equals("heart")) {
                map.put(new Position(gridX, gridY).toString(), " h ");
            } else if (entry.spriteGroup().equals("lightningrod")) {
                map.put(new Position(gridX, gridY).toString(), " r ");
            } else if (entry.spriteGroup().equals("dirt")) {
                map.put(new Position(gridX, gridY).toString(), " - ");
            } else {
                map.put(new Position(gridX, gridY).toString(), frame.getSprite().utf8BlockSymbol());
            }
        }
        final int tilesPerRow = dimensions.windowSize() / dimensions.tileSize();
        final StringBuilder sb = new StringBuilder();
        sb.append("fr: " + frameTarget);
        sb.append("\n");
        sb.append("/");
        sb.append("=".repeat((tilesPerRow * 3) + 2));
        sb.append("\\");
        sb.append("\n");
        for (int y = 0; y < tilesPerRow; y += 1) {
            sb.append(" |");
            for (int x = 0; x < tilesPerRow; x += 1) {
                final String key = new Position(x, y).toString();
                final String symbol = map.get(key);
                sb.append(symbol);
            }
            sb.append("|\n");
        }
        sb.append("\\");
        sb.append("=".repeat((tilesPerRow * 3) + 2));
        sb.append("/");
        sb.append("\n");
        return sb.toString();
    }

    public List<RenderableAnalyser> getFrame(int targetFrame) {
        final List<RenderableAnalyser> result = new ArrayList<>();
        for (final RenderableAnalyser analyser : this.getAll()) {
            if (analyser.wasInFrame(targetFrame)) {
                final RenderableAnalyser frame = analyser;
                result.add(frame);
            }
        }
        return result;
    }
}
