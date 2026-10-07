package com.usanaem.occultic_ntm.anomaly;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class AnomalyTerrain {
    private AnomalyTerrain() { }
    public static boolean outdoorsAtNight(EntityPlayerMP player) {
        long time = player.worldObj.getWorldTime() % 24000L;
        return player.dimension == 0 && player.isEntityAlive() && !player.capabilities.isCreativeMode
                && !player.isPlayerSleeping() && player.ridingEntity == null && time >= 13000 && time <= 23000
                && Math.abs(player.rotationPitch) < 55F && player.worldObj.canBlockSeeTheSky(
                MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY + player.getEyeHeight()), MathHelper.floor_double(player.posZ));
    }
    public static int[] find(EntityPlayerMP player, int minDistance, int range, boolean forced) {
        World world = player.worldObj;
        for (int attempt = 0; attempt < (forced ? 24 : 8); attempt++) {
            double angle = Math.toRadians(player.rotationYaw + (forced && attempt == 0 ? 0 : (15 + world.rand.nextInt(35)) * (world.rand.nextBoolean() ? 1 : -1)));
            double distance = minDistance + world.rand.nextInt(range + 1);
            int x = MathHelper.floor_double(player.posX - Math.sin(angle) * distance);
            int z = MathHelper.floor_double(player.posZ + Math.cos(angle) * distance);
            if (!loaded(world, Math.min(x - 2, MathHelper.floor_double(player.posX)), Math.max(x + 2, MathHelper.floor_double(player.posX)),
                    Math.min(z - 2, MathHelper.floor_double(player.posZ)), Math.max(z + 2, MathHelper.floor_double(player.posZ)))) continue;
            int y = world.getHeightValue(x, z);
            if (y < 5 || y > world.getHeight() - 3 || Math.abs(y - player.posY) > 10
                    || world.getBlock(x, y - 1, z).getMaterial() == Material.leaves
                    || !World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) continue;
            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x + 0.2, y, z + 0.2, x + 0.8, y + 1.8, z + 0.8);
            if (!world.getCollidingBoundingBoxes(player, box).isEmpty() || world.isAnyLiquid(box)) continue;
            Vec3 eyes = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            if (world.rayTraceBlocks(eyes, Vec3.createVectorHelper(x + 0.5, y + 1.5, z + 0.5)) == null) return new int[] {x, y, z};
        }
        return null;
    }
    public static boolean loaded(World world, int minX, int maxX, int minZ, int maxZ) {
        for (int x = minX >> 4; x <= maxX >> 4; x++) for (int z = minZ >> 4; z <= maxZ >> 4; z++)
            if (!world.getChunkProvider().chunkExists(x, z)) return false;
        return true;
    }
}
