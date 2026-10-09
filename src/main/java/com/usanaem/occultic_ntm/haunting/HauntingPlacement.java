package com.usanaem.occultic_ntm.haunting;

import com.usanaem.occultic_ntm.anomaly.AnomalyTerrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/** Bounded loaded-block searches; no modern heightmaps or chunk generation. */
public final class HauntingPlacement {
    private HauntingPlacement() { }
    /** Server-loaded terrain may still be queued for this particular recipient. */
    public static boolean deliverable(EntityPlayer player, double x, double z) {
        return player instanceof EntityPlayerMP && player.worldObj instanceof WorldServer
                && ((WorldServer) player.worldObj).getPlayerManager().isPlayerWatchingChunk((EntityPlayerMP) player,
                        MathHelper.floor_double(x) >> 4, MathHelper.floor_double(z) >> 4);
    }
    public static boolean loaded(World world, double x, double z, double tx, double tz) {
        return AnomalyTerrain.loaded(world, MathHelper.floor_double(Math.min(x, tx)) - 1,
                MathHelper.floor_double(Math.max(x, tx)) + 1, MathHelper.floor_double(Math.min(z, tz)) - 1,
                MathHelper.floor_double(Math.max(z, tz)) + 1);
    }
    public static boolean valid(World world, double x, double y, double z) {
        int bx = MathHelper.floor_double(x), by = MathHelper.floor_double(y), bz = MathHelper.floor_double(z);
        if (by < 1 || by >= world.getHeight() - 2 || !loaded(world, x, z, x, z)) return false;
        Material floor = world.getBlock(bx, by - 1, bz).getMaterial();
        if (floor == Material.leaves || floor.isLiquid() || !World.doesBlockHaveSolidTopSurface(world, bx, by - 1, bz)) return false;
        // Air-only clearance deliberately rejects plants/leaves even when noncollidable.
        if (!world.isAirBlock(bx, by, bz) || !world.isAirBlock(bx, by + 1, bz)) return false;
        for (double cx : new double[] {x - .3, x + .3}) for (double cz : new double[] {z - .3, z + .3}) {
            int ix = MathHelper.floor_double(cx), iz = MathHelper.floor_double(cz);
            if (!world.isAirBlock(ix, by, iz) || !world.isAirBlock(ix, by + 1, iz)
                    || !World.doesBlockHaveSolidTopSurface(world, ix, by - 1, iz)) return false;
        }
        return true;
    }
    public static boolean underground(EntityPlayer player) {
        if (player.dimension == -1) return true;
        if (player.dimension == 1) return false;
        World world = player.worldObj;
        int x = MathHelper.floor_double(player.posX), y = MathHelper.floor_double(player.posY + player.getEyeHeight()), z = MathHelper.floor_double(player.posZ);
        if (!loaded(world, x, z, x, z)) return true;
        if (world.canBlockSeeTheSky(x, y, z)) return false;
        int top = Math.min(world.getHeight() - 1, world.getHeightValue(x, z));
        // Tree canopy must not reroute a forest's surface silhouettes into cave encounters.
        for (int ceiling = y; ceiling <= Math.min(top, y + 32); ceiling++) {
            net.minecraft.block.Block block = world.getBlock(x, ceiling, z);
            if (block.getMaterial() == Material.leaves || block == Blocks.log || block == Blocks.log2) continue;
            if (block.getMaterial().blocksMovement()) return true;
        }
        return top > y + 32;
    }
    public static double[] bedWatcher(EntityPlayer player) {
        World world = player.worldObj;
        int px = MathHelper.floor_double(player.posX), py = MathHelper.floor_double(player.posY), pz = MathHelper.floor_double(player.posZ);
        if (!loaded(world, px - 4, pz - 4, px + 4, pz + 4)) return null;
        int[] dx = {0, -1, 0, 1}, dz = {1, 0, -1, 0};
        for (int x = px - 1; x <= px + 1; x++) for (int z = pz - 1; z <= pz + 1; z++) for (int y = py - 1; y <= py + 1; y++) {
            if (world.getBlock(x, y, z) != net.minecraft.init.Blocks.bed) continue;
            int meta = world.getBlockMetadata(x, y, z), direction = meta & 3;
            int headX = x + ((meta & 8) == 0 ? dx[direction] : 0), headZ = z + ((meta & 8) == 0 ? dz[direction] : 0);
            for (int side : new int[] {0, -1, 1}) {
                double tx = headX - dx[direction] * 3 + dz[direction] * side + .5;
                double tz = headZ - dz[direction] * 3 - dx[direction] * side + .5;
                if (deliverable(player, tx, tz) && valid(world, tx, y, tz)) return new double[] {tx, y, tz};
            }
        }
        return null;
    }
    public static double[] find(EntityPlayer player, Sighting kind, boolean forced, boolean escalated) {
        if (kind == Sighting.WINDOW) return window(player, forced);
        World world = player.worldObj;
        boolean cave = underground(player) || kind == Sighting.DWELLING || kind == Sighting.NIGHTMARE || kind == Sighting.CREEPING || kind == Sighting.WINDOW;
        double[] best = null;
        double bestScore = -Double.MAX_VALUE;
        for (int attempt = 0; attempt < (forced ? 48 : 32); attempt++) {
            // Cardinal/eighth-turn samples find real corridors; later samples explore the rest of the horizon.
            double angle = Math.toRadians(player.rotationYaw + (kind == Sighting.CREEPING ? 180
                    : attempt < 8 ? attempt * 45 : forced ? (attempt / 2) * 17 * (attempt % 2 == 0 ? 1 : -1) : world.rand.nextInt(360)));
            double distance = kind.minDistance + (forced ? attempt % (kind.maxDistance - kind.minDistance + 1)
                    : world.rand.nextInt(kind.maxDistance - kind.minDistance + 1));
            if (escalated && kind == Sighting.STALKING) distance *= .8;
            double lateral = kind == Sighting.CREEPING ? (2 + attempt % 2) * (attempt % 4 < 2 ? 1 : -1) : 0;
            int x = MathHelper.floor_double(player.posX - Math.sin(angle) * distance + Math.cos(angle) * lateral);
            int z = MathHelper.floor_double(player.posZ + Math.cos(angle) * distance + Math.sin(angle) * lateral);
            if (!loaded(world, player.posX, player.posZ, x, z) || !deliverable(player, x, z)) continue;
            int anchor = cave ? MathHelper.floor_double(player.posY) + 5 : Math.min(world.getHeight() - 3, world.getHeightValue(x, z));
            int bottom = cave ? Math.max(1, anchor - 16) : Math.max(1, anchor - 32);
            for (int y = anchor; y >= bottom; y--) {
                if (cave && Math.abs(y - player.posY) > 5 || !valid(world, x + .5, y, z + .5)) continue;
                HauntingPerception.SightLine line = HauntingPerception.line(world, HauntingPerception.eyes(player),
                        net.minecraft.util.Vec3.createVectorHelper(x + .5, y + 1.6, z + .5));
                if (!line.clear) continue;
                double score = contextScore(player, kind, x, y, z) + (forced ? 0 : world.rand.nextDouble());
                if (score > bestScore) { bestScore = score; best = new double[] {x + .5, y, z + .5}; }
                break;
            }
        }
        return best;
    }
    /** Preferences compose the shot, while validity/visibility remain hard gates. */
    static int contextScore(EntityPlayer player, Sighting kind, int x, int y, int z) {
        World world = player.worldObj;
        if (!loaded(world, x - 4, z - 4, x + 4, z + 4)) return 0;
        int trees = 0, cover = 0, lower = 0;
        for (int[] direction : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            for (int d = 1; d <= 3; d++) {
                Material side = world.getBlock(x + direction[0] * d, y + 1, z + direction[1] * d).getMaterial();
                if (side == Material.wood || side == Material.leaves) trees++;
                if (d <= 2 && world.getBlock(x + direction[0] * d, y + 1, z + direction[1] * d).isOpaqueCube()) cover++;
            }
            if (kind == Sighting.LURKING && world.getHeightValue(x + direction[0] * 4, z + direction[1] * 4) < y) lower++;
        }
        if (kind == Sighting.LURKING) return lower * 5 + Math.min(trees, 4) * 3;
        if (kind == Sighting.DWELLING) {
            int dark = world.getBlockLightValue(x, y + 1, z) <= 8 ? 10 : 0;
            return dark + Math.min(cover, 4) * 3 + (world.isAirBlock(x, y + 2, z) ? 0 : 5);
        }
        if (kind == Sighting.STALKING) return Math.min(trees, 3) * 4 + Math.min(cover, 3) * 2;
        return Math.min(cover, 3) * 2;
    }
    private static boolean glass(World world, int x, int y, int z) {
        net.minecraft.block.Block block = world.getBlock(x, y, z);
        return block == Blocks.glass || block == Blocks.glass_pane || block == Blocks.stained_glass || block == Blocks.stained_glass_pane;
    }
    private static boolean framed(World world, int x, int y, int z) {
        int solid = 0;
        for (int[] offset : new int[][] {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 2, 0}, {0, -2, 0}, {0, 0, 1}, {0, 0, -1}})
            if (world.getBlock(x + offset[0], y + offset[1], z + offset[2]).isOpaqueCube()) solid++;
        return solid >= 2;
    }
    private static double[] window(EntityPlayer player, boolean forced) {
        World world = player.worldObj;
        int px = MathHelper.floor_double(player.posX), py = MathHelper.floor_double(player.posY + player.getEyeHeight()), pz = MathHelper.floor_double(player.posZ);
        if (!forced && world.canBlockSeeTheSky(px, py, pz)) return null;
        double[] best = null;
        double bestScore = -Double.MAX_VALUE;
        int inspected = 0;
        // Search outward from the owner, bounded to 21x21x5 cells and 24 glass columns.
        for (int radius = 1; radius <= 10; radius++) for (int dx = -radius; dx <= radius; dx++) for (int dz = -radius; dz <= radius; dz++) {
            if (Math.max(Math.abs(dx), Math.abs(dz)) != radius || !loaded(world, px + dx - 2, pz + dz - 2, px + dx + 2, pz + dz + 2)) continue;
            for (int gy = py - 2; gy <= py + 2; gy++) {
                int gx = px + dx, gz = pz + dz;
                if (gy < 2 || gy >= world.getHeight() - 3 || !glass(world, gx, gy, gz) || !forced && !framed(world, gx, gy, gz)) continue;
                if (++inspected > 24) return best;
                double vx = gx + .5 - player.posX, vz = gz + .5 - player.posZ;
                double length = Math.sqrt(vx * vx + vz * vz); if (length < .5) continue;
                for (int beyond = 4; beyond <= 12; beyond += 2) {
                    if (length + beyond < Sighting.WINDOW.minDistance || length + beyond > Sighting.WINDOW.maxDistance) continue;
                    int x = MathHelper.floor_double(gx + .5 + vx / length * beyond), z = MathHelper.floor_double(gz + .5 + vz / length * beyond);
                    if (!loaded(world, player.posX, player.posZ, x, z) || !deliverable(player, x, z)) continue;
                    for (int y = MathHelper.floor_double(player.posY) + 2; y >= Math.max(1, player.posY - 3); y--) {
                        if (!valid(world, x + .5, y, z + .5) || !forced && !world.canBlockSeeTheSky(x, y + 1, z)) continue;
                        HauntingPerception.SightLine line = HauntingPerception.line(world, HauntingPerception.eyes(player),
                                net.minecraft.util.Vec3.createVectorHelper(x + .5, y + 1.6, z + .5));
                        if (!line.clear || !line.glass) continue;
                        double score = 30 - length - beyond * .2 + contextScore(player, Sighting.STALKING, x, y, z);
                        if (score > bestScore) { bestScore = score; best = new double[] {x + .5, y, z + .5}; }
                        break;
                    }
                }
                break;
            }
        }
        return best;
    }
}
