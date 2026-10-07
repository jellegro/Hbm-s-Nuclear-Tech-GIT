package com.usanaem.occultic_ntm.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.lang.ref.WeakReference;
import net.minecraft.block.Block;
import net.minecraft.world.World;

/** Narrow public Circle.addGlyph query adapter. No optional-mod class is loaded here.
 * Actual chunk storage, saves, rendering and all ordinary world reads retain the fork block.
 */
public final class GlyphCircleQueries {
    private static Block radiant, white;
    private static final ThreadLocal<List<Query>> QUERIES = new ThreadLocal<List<Query>>();
    private GlyphCircleQueries() { }
    public static void configure(Block radiantBlock, Block nativeWhite) { radiant = radiantBlock; white = nativeWhite; }
    public static void enter(World world, int x, int y, int z) {
        if (radiant == null) return;
        List<Query> queries = QUERIES.get();
        if (queries == null) { queries = new ArrayList<Query>(); QUERIES.set(queries); }
        // Reconcile with live public API frames, including re-entry after a thrown call.
        int depth = depth();
        while (queries.size() >= depth && !queries.isEmpty()) queries.remove(queries.size() - 1);
        if (depth > 0) queries.add(new Query(world, x, y, z));
    }
    public static void leave() {
        List<Query> queries = QUERIES.get();
        if (queries == null) return;
        if (!queries.isEmpty()) queries.remove(queries.size() - 1);
        if (queries.isEmpty()) QUERIES.remove();
    }
    public static Block resolve(World world, int x, int y, int z, Block actual) {
        if (actual != radiant || radiant == null) return actual;
        List<Query> queries = QUERIES.get();
        if (queries == null) return actual;
        // RETURN callbacks cannot run on exceptions. A live-frame check prevents leaked
        // scope from ever aliasing subsequent normal reads; only rare glyph queries pay it.
        int depth = depth();
        while (queries.size() > depth) queries.remove(queries.size() - 1);
        if (queries.isEmpty()) { QUERIES.remove(); return actual; }
        Query query = queries.get(queries.size() - 1);
        return query.world.get() == world && query.x == x && query.y == y && query.z == z ? white : actual;
    }
    private static int depth() {
        int count = 0;
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            if ("com.emoniph.witchery.ritual.Circle".equals(frame.getClassName()) && "addGlyph".equals(frame.getMethodName())) count++;
        }
        return count;
    }
    private static final class Query {
        final WeakReference<World> world; final int x, y, z;
        Query(World world, int x, int y, int z) { this.world = new WeakReference<World>(world); this.x = x; this.y = y; this.z = z; }
    }
}
