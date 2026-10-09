package com.usanaem.occultic_ntm.haunting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;

/** A twelve-block sealed parallel gallery; never overwrites ores, liquids or mod blocks. */
public final class GhostMiner {
    private GhostMiner() { }
    public static boolean create(EntityPlayerMP player) {
        if (!HauntingDirector.enabled() || !HauntingDirector.terrainEnabled() || !HauntingPlacement.underground(player)
                || HauntingDirector.nearby(player) || !HerobrineSkins.bundledAvailable()) return false;
        World world = player.worldObj;
        int px = MathHelper.floor_double(player.posX), y = MathHelper.floor_double(player.posY), pz = MathHelper.floor_double(player.posZ);
        int direction = MathHelper.floor_double(player.rotationYaw / 90 + .5) & 3;
        int[] vx = {0, -1, 0, 1}, vz = {1, 0, -1, 0};
        int dx = vx[direction], dz = vz[direction], sx = dz, sz = -dx;
        if (y < 5 || y > 120 || !HauntingPlacement.loaded(world, px - 18, pz - 18, px + 18, pz + 18)) return false;
        // Require a real narrow rock corridor on both sides of the miner.
        for (int side : new int[] {-1, 1}) for (int dy = 0; dy <= 1; dy++)
            if (world.getBlock(px + sx * side, y + dy, pz + sz * side) != Blocks.stone) return false;
        for (int side : new int[] {-1, 1}) {
            int x = px + sx * side * 4, z = pz + sz * side * 4;
            if (!preflight(player, x, y, z, dx, dz, sx, sz)) continue;
            List<BlockSnapshot> snapshots = new ArrayList<BlockSnapshot>();
            boolean success = true;
            for (int step = 0; step < 12; step++) for (int dy = 0; dy < 2; dy++) {
                int bx = x + dx * step, bz = z + dz * step;
                snapshots.add(BlockSnapshot.getBlockSnapshot(world, bx, y + dy, bz));
                if (!world.setBlock(bx, y + dy, bz, Blocks.air, 0, 2)) success = false;
            }
            if (success) for (int step : new int[] {3, 7}) {
                // These blocks are part of the same protected preflighted footprint.
                if (!world.setBlock(x + dx * step, y, z + dz * step, Blocks.redstone_torch, 5, 2)) success = false;
            }
            if (success) success = HauntingDirector.spawnAt(player, Sighting.GHOST_MINER, x + dx * 11 + .5, y, z + dz * 11 + .5);
            if (!success) { for (int i = snapshots.size() - 1; i >= 0; i--) snapshots.get(i).restore(true, false); return false; }
            // Only normal block updates; no drops, holes, fluid plugs or bridge blocks.
            for (int step = 0; step < 12; step++) for (int dy = 0; dy < 2; dy++) world.markBlockForUpdate(x + dx * step, y + dy, z + dz * step);
            return true;
        }
        return false;
    }
    private static boolean preflight(EntityPlayerMP player, int x, int y, int z, int dx, int dz, int sx, int sz) {
        World world = player.worldObj;
        // Only the 24 carved cells must be plain stone. The untouched floor,
        // ceiling, sides and ends can contain stable full-cube ores/support.
        for (int step = -1; step <= 12; step++) for (int width = -1; width <= 1; width++) for (int dy = -1; dy <= 2; dy++) {
            int bx = x + dx * step + sx * width, bz = z + dz * step + sz * width;
            Block block = world.getBlock(bx, y + dy, bz);
            boolean carved = step >= 0 && step < 12 && width == 0 && dy >= 0 && dy < 2;
            if (world.getTileEntity(bx, y + dy, bz) != null || block.getMaterial().isLiquid()
                    || block instanceof BlockFalling || !block.isNormalCube(world, bx, y + dy, bz)
                    || carved && (block != Blocks.stone || world.getBlockMetadata(bx, y + dy, bz) != 0)) return false;
        }
        for (int step = 0; step < 12; step++) for (int dy = 0; dy < 2; dy++) {
            int bx = x + dx * step, bz = z + dz * step;
            if (!HauntingSafety.unobserved(world, bx, y + dy, bz, 2)
                    || !HauntingSafety.permitted(world, player, "ghost_miner", bx, y + dy, bz)) return false;
        }
        return true;
    }
}
