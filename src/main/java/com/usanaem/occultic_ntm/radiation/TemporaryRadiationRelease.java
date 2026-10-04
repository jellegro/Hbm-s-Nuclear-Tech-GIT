package com.usanaem.occultic_ntm.radiation;

import java.util.ArrayList;
import java.util.List;

import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/** Saved finite emission budgets, not chunk contamination. No optional-mod references. */
public final class TemporaryRadiationRelease extends WorldSavedData {
    public static final String KEY = "occultic_ntm_radiation_releases";
    public static final int DURATION_TICKS = 200;
    public static final double RADIUS = 4D;
    private final List<NBTTagCompound> sources = new ArrayList<NBTTagCompound>();

    public TemporaryRadiationRelease(String name) { super(name); }

    public static TemporaryRadiationRelease get(World world) {
        TemporaryRadiationRelease data = find(world);
        if (data == null) {
            data = new TemporaryRadiationRelease(KEY);
            world.perWorldStorage.setData(KEY, data);
        }
        return data;
    }

    private static TemporaryRadiationRelease find(World world) {
        return (TemporaryRadiationRelease) world.perWorldStorage.loadData(TemporaryRadiationRelease.class, KEY);
    }

    public static void add(World world, double x, double y, double z, double radiation) {
        if (world.isRemote || !(radiation > 0) || !finite(radiation) || !finite(x) || !finite(y) || !finite(z)) {
            throw new IllegalArgumentException("A radiation release requires a finite positive server-side budget and position");
        }
        NBTTagCompound source = new NBTTagCompound();
        source.setDouble("x", x);
        source.setDouble("y", y);
        source.setDouble("z", z);
        source.setDouble("remainingRAD", radiation);
        source.setInteger("ticks", DURATION_TICKS);
        TemporaryRadiationRelease data = get(world);
        data.sources.add(source);
        data.markDirty();
    }

    public double getRemainingRadiation() {
        double remaining = 0D;
        for (NBTTagCompound source : sources) remaining += source.getDouble("remainingRAD");
        return remaining;
    }

    /** Account emitted radiation before exposure callbacks can create another release. */
    static double nextPulse(NBTTagCompound source) {
        double before = source.getDouble("remainingRAD");
        int ticks = source.getInteger("ticks");
        double after = ticks <= 1 ? 0D : before - before / ticks;
        source.setDouble("remainingRAD", after);
        source.setInteger("ticks", Math.max(0, ticks - 1));
        return before - after;
    }

    public static void tick(World world) {
        if (world.isRemote) return;
        TemporaryRadiationRelease data = find(world);
        if (data == null) return;
        // New ruptures append during contaminate callbacks; do not emit them recursively this tick.
        for (int index = data.sources.size() - 1; index >= 0; index--) {
            NBTTagCompound source = data.sources.get(index);
            double x = source.getDouble("x"), y = source.getDouble("y"), z = source.getDouble("z");
            if (!world.getChunkProvider().chunkExists(MathHelper.floor_double(x) >> 4, MathHelper.floor_double(z) >> 4)) continue;
            double emitted = nextPulse(source);
            data.markDirty();
            if (source.getInteger("ticks") == 0) data.sources.remove(index);
            List<EntityLivingBase> nearby = world.getEntitiesWithinAABB(EntityLivingBase.class,
                    AxisAlignedBB.getBoundingBox(x - RADIUS, y - RADIUS, z - RADIUS,
                            x + RADIUS, y + RADIUS, z + RADIUS));
            List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
            for (EntityLivingBase target : nearby) {
                if (!target.isDead && target.getDistanceSq(x, y, z) <= RADIUS * RADIUS) targets.add(target);
            }
            if (targets.isEmpty()) continue; // Radiation is emitted into the surroundings even without a recipient.
            double share = emitted / targets.size();
            float amount = (float) Math.min(Float.MAX_VALUE, share);
            if (amount > share) amount = Math.nextAfter(amount, Double.NEGATIVE_INFINITY);
            for (EntityLivingBase target : targets) {
                // Normal HBM immunity, resistance, creative mode and spawn grace still apply.
                ContaminationUtil.contaminate(target, HazardType.RADIATION, ContaminationType.CREATIVE, amount);
            }
        }
    }

    @Override public void readFromNBT(NBTTagCompound nbt) {
        sources.clear();
        NBTTagList list = nbt.getTagList("sources", 10);
        for (int index = 0; index < list.tagCount(); index++) sources.add((NBTTagCompound) list.getCompoundTagAt(index).copy());
    }

    @Override public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (NBTTagCompound source : sources) list.appendTag(source.copy());
        nbt.setTag("sources", list);
    }

    private static boolean finite(double value) { return !Double.isNaN(value) && !Double.isInfinite(value); }
}
