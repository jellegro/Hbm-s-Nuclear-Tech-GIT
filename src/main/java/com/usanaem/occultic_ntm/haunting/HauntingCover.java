package com.usanaem.occultic_ntm.haunting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

/** Small deterministic lateral walk, not a pathfinder or teleport. */
public final class HauntingCover {
    private HauntingCover() { }
    public static boolean concealed(EntityHerobrine figure, double x, double z) {
        for (Object object : figure.worldObj.playerEntities) {
            EntityPlayer player = (EntityPlayer) object;
            if (!player.isEntityAlive() || player.getDistanceSq(x, figure.posY, z) > 128 * 128) continue;
            if (HauntingPerception.line(figure.worldObj, HauntingPerception.eyes(player), Vec3.createVectorHelper(x, figure.posY + 1.6, z)).clear
                    || HauntingPerception.line(figure.worldObj, HauntingPerception.eyes(player), Vec3.createVectorHelper(x, figure.posY + .8, z)).clear) return false;
        }
        return true;
    }
    public static double[] find(EntityHerobrine figure, EntityPlayer viewer) {
        double dx = viewer.posX - figure.posX, dz = viewer.posZ - figure.posZ;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < .01) return null;
        for (int sign : new int[] {-1, 1}) {
            double sx = -dz / length * .1 * sign, sz = dx / length * .1 * sign;
            for (int steps : new int[] {18, 24}) {
                boolean clear = true;
                for (int i = 1; i <= steps; i++) if (!HauntingPlacement.valid(figure.worldObj, figure.posX + sx * i, figure.posY, figure.posZ + sz * i)) { clear = false; break; }
                if (clear && concealed(figure, figure.posX + sx * steps, figure.posZ + sz * steps)) return new double[] {sx, sz, steps};
            }
        }
        return null;
    }
}
