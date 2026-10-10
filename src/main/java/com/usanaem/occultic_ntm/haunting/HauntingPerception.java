package com.usanaem.occultic_ntm.haunting;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class HauntingPerception {
    public enum State { UNSEEN, PERIPHERAL, OBSERVED, FIXATED, OBSERVED_THROUGH_GLASS }
    public static final class SightLine {
        public final boolean clear, glass;
        SightLine(boolean clear, boolean glass) { this.clear = clear; this.glass = glass; }
    }
    private HauntingPerception() { }
    public static Vec3 eyes(EntityPlayer player) { return Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ); }
    public static State classify(double dot, boolean clear, boolean glass) { return classify(dot, clear, glass, .82); }
    /** cone is the archetype's direct-observation threshold; .5 remains the outer peripheral edge. */
    public static State classify(double dot, boolean clear, boolean glass, double cone) {
        if (!clear || dot < .5) return State.UNSEEN;
        if (dot < cone) return State.PERIPHERAL;
        if (glass) return State.OBSERVED_THROUGH_GLASS;
        return dot >= .9986 ? State.FIXATED : State.OBSERVED;
    }
    public static State observe(EntityPlayer player, double x, double y, double z) { return observe(player, x, y, z, .82); }
    public static State observe(EntityPlayer player, double x, double y, double z, double cone) {
        Vec3 origin = eyes(player), target = Vec3.createVectorHelper(x, y, z);
        Vec3 direction = Vec3.createVectorHelper(x - origin.xCoord, y - origin.yCoord, z - origin.zCoord);
        if (direction.lengthVector() > 128) return State.UNSEEN;
        double dot = player.getLookVec().dotProduct(direction.normalize());
        if (dot < .5) return State.UNSEEN;
        SightLine line = line(player.worldObj, origin, target);
        return classify(dot, line.clear, line.glass, cone);
    }
    public static boolean watching(EntityPlayer player, double x, double y, double z) {
        return observe(player, x, y, z) != State.UNSEEN;
    }
    /** Native collision ray tracing, advancing beyond at most eight glass blocks/panes. */
    public static SightLine line(World world, Vec3 origin, Vec3 target) {
        if (!HauntingPlacement.loaded(world, origin.xCoord, origin.zCoord, target.xCoord, target.zCoord)) return new SightLine(false, false);
        Vec3 direction = Vec3.createVectorHelper(target.xCoord - origin.xCoord, target.yCoord - origin.yCoord, target.zCoord - origin.zCoord).normalize();
        Vec3 cursor = Vec3.createVectorHelper(origin.xCoord, origin.yCoord, origin.zCoord);
        boolean glass = false;
        for (int i = 0; i < 9; i++) {
            MovingObjectPosition hit = world.func_147447_a(cursor, target, false, true, false);
            if (hit == null) return new SightLine(true, glass);
            // A world mutation observes a block's visible face; hitting that target
            // block is visibility, not cover in front of it. Entity endpoints are air.
            if (hit.blockX == MathHelper.floor_double(target.xCoord) && hit.blockY == MathHelper.floor_double(target.yCoord)
                    && hit.blockZ == MathHelper.floor_double(target.zCoord)) return new SightLine(true, glass);
            Block block = world.getBlock(hit.blockX, hit.blockY, hit.blockZ);
            if (block != Blocks.glass && block != Blocks.glass_pane && block != Blocks.stained_glass && block != Blocks.stained_glass_pane) return new SightLine(false, glass);
            glass = true;
            // Move to the exit face of this block, rather than repeatedly hitting its entry.
            double exit = Double.POSITIVE_INFINITY;
            double[] at = {hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord};
            double[] delta = {direction.xCoord, direction.yCoord, direction.zCoord};
            int[] cell = {hit.blockX, hit.blockY, hit.blockZ};
            for (int axis = 0; axis < 3; axis++) if (Math.abs(delta[axis]) > 1e-8) {
                double t = ((delta[axis] > 0 ? cell[axis] + 1 : cell[axis]) - at[axis]) / delta[axis];
                if (t >= -1e-7) exit = Math.min(exit, Math.max(0, t));
            }
            cursor = Vec3.createVectorHelper(hit.hitVec.xCoord + direction.xCoord * (exit + .002),
                    hit.hitVec.yCoord + direction.yCoord * (exit + .002), hit.hitVec.zCoord + direction.zCoord * (exit + .002));
            if (cursor.distanceTo(target) < .01) return new SightLine(true, glass);
        }
        return new SightLine(false, glass);
    }
}
