package com.usanaem.occultic_ntm.haunting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/** Small deterministic walks, not a pathfinder or teleport: slip behind cover, or retreat on foot. */
public final class HauntingCover {
    private HauntingCover() { }
    public static boolean concealed(EntityHerobrine figure, double x, double z) {
        return concealed(figure.worldObj, figure.posY, x, z);
    }
    public static boolean concealed(World world, double y, double x, double z) {
        for (Object object : world.playerEntities) {
            EntityPlayer player = (EntityPlayer) object;
            if (!player.isEntityAlive() || player.getDistanceSq(x, y, z) > 128 * 128) continue;
            if (HauntingPerception.line(world, HauntingPerception.eyes(player), Vec3.createVectorHelper(x, y + 1.6, z)).clear
                    || HauntingPerception.line(world, HauntingPerception.eyes(player), Vec3.createVectorHelper(x, y + .8, z)).clear) return false;
        }
        return true;
    }
    public static double[] find(EntityHerobrine figure, EntityPlayer viewer) {
        return route(figure.worldObj, figure.posX, figure.posY, figure.posZ, viewer);
    }
    /** A hidden body can physically step out, rather than materialize in a player's view. */
    static double[] emerge(World world, double x, double y, double z, EntityPlayer viewer) {
        if (!concealed(world, y, x, z)) return null;
        double toward = Math.atan2(viewer.posZ - z, viewer.posX - x);
        for (int turn : new int[] {90, -90, 45, -45, 0, 135, -135, 180}) {
            double angle = toward + Math.toRadians(turn), sx = Math.cos(angle) * .08, sz = Math.sin(angle) * .08;
            for (int step = 1; step <= 30; step++) {
                double tx = x + sx * step, tz = z + sz * step;
                if (!HauntingPlacement.deliverable(viewer, tx, tz) || !HauntingPlacement.valid(world, tx, y, tz)) break;
                if (step >= 5 && HauntingPerception.line(world, HauntingPerception.eyes(viewer),
                        Vec3.createVectorHelper(tx, y + 1.6, tz)).clear) return new double[] {sx, sz, step};
            }
        }
        return null;
    }
    /** Continue a verified entrance across a visible gap into a second occluder, at most 7.2 blocks. */
    static int crossing(World world, double x, double y, double z, double dx, double dz, int entrance, EntityPlayer viewer) {
        boolean revealed = false;
        for (int step = 1; step <= 90; step++) {
            double tx = x + dx * step, tz = z + dz * step;
            if (!HauntingPlacement.deliverable(viewer, tx, tz) || !HauntingPlacement.valid(world, tx, y, tz)) return 0;
            if (HauntingPerception.line(world, HauntingPerception.eyes(viewer), Vec3.createVectorHelper(tx, y + 1.6, tz)).clear) revealed = true;
            if (revealed && step >= entrance + 12 && concealed(world, y, tx, tz)) return step;
        }
        return 0;
    }
    /** Lateral route {stepX, stepZ, steps} to a spot hidden from every player, or null. Usable before a figure exists. */
    public static double[] route(World world, double fx, double fy, double fz, EntityPlayer viewer) {
        double dx = viewer.posX - fx, dz = viewer.posZ - fz;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < .01) return null;
        for (int sign : new int[] {-1, 1}) {
            double sx = -dz / length * .1 * sign, sz = dx / length * .1 * sign;
            for (int steps : new int[] {18, 24}) {
                boolean clear = true;
                for (int i = 1; i <= steps; i++) if (!HauntingPlacement.valid(world, fx + sx * i, fy, fz + sz * i)) { clear = false; break; }
                if (clear && concealed(world, fy, fx + sx * steps, fz + sz * steps)) return new double[] {sx, sz, steps};
            }
        }
        return null;
    }
    /**
     * Unhurried walk directly away from the viewer, used when there is no cover to step behind.
     * Returns {stepX, stepZ, steps} for at least 12 and at most 40 valid 0.07-block steps, or null.
     */
    public static double[] retreat(EntityHerobrine figure, EntityPlayer viewer) {
        double dx = figure.posX - viewer.posX, dz = figure.posZ - viewer.posZ;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < .01) return null;
        double base = Math.atan2(dz, dx);
        double lean = Math.toRadians(25) * (figure.worldObj.rand.nextBoolean() ? 1 : -1);
        for (double turn : new double[] {lean, 0, -lean}) {
            double sx = Math.cos(base + turn) * .07, sz = Math.sin(base + turn) * .07;
            int steps = 0;
            while (steps < 40 && HauntingPlacement.valid(figure.worldObj, figure.posX + sx * (steps + 1), figure.posY, figure.posZ + sz * (steps + 1))) steps++;
            if (steps >= 12) return new double[] {sx, sz, steps};
        }
        return null;
    }
}
