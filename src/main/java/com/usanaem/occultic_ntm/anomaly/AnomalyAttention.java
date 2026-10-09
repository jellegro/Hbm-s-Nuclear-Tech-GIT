package com.usanaem.occultic_ntm.anomaly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/** Hidden, bounded server state. Forge's persisted compound survives saves and death. */
public final class AnomalyAttention {
    public static final String KEY = "occultic_ntm:anomalies";
    private AnomalyAttention() { }

    private static NBTTagCompound state(EntityPlayer player) {
        NBTTagCompound root = player.getEntityData();
        if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) root.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey(KEY, 10)) persisted.setTag(KEY, new NBTTagCompound());
        return persisted.getCompoundTag(KEY);
    }

    public static int get(EntityPlayer player) { return Math.max(0, Math.min(100, state(player).getInteger("attention"))); }
    public static void add(EntityPlayer player, int amount) {
        if (player == null || player.worldObj.isRemote || amount <= 0) return;
        state(player).setInteger("attention", (int) Math.min(100L, (long) get(player) + amount));
    }
    public static boolean observed(EntityPlayer player) { return state(player).getBoolean("impossibleObservation"); }
    public static boolean markObserved(EntityPlayer player) {
        if (player == null || player.worldObj.isRemote || observed(player)) return false;
        state(player).setBoolean("impossibleObservation", true);
        add(player, 20);
        return true;
    }
    /** Explicit hook for successful unusual nuclear rites; ordinary placement calls nothing. */
    public static void radiantRitePerformed(EntityPlayer player) { add(player, 1); }

    public static long clock(EntityPlayer player) { return Math.max(0L, state(player).getLong("playTicks")); }
    public static void advance(EntityPlayer player) {
        long now = clock(player);
        state(player).setLong("playTicks", now == Long.MAX_VALUE ? now : now + 1L);
    }
    public static long next(EntityPlayer player, String id) { return state(player).getLong("next:" + id); }
    public static void defer(EntityPlayer player, String id, long ticks) {
        long now = clock(player);
        state(player).setLong("next:" + id, now + Math.min(Math.max(1L, ticks), Long.MAX_VALUE - now));
    }
    public static void copy(EntityPlayer original, EntityPlayer replacement) {
        NBTTagCompound root = original.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (root.hasKey(KEY, 10)) {
            // Preserve other mods' persisted state on the replacement.
            state(replacement);
            replacement.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setTag(KEY, root.getCompoundTag(KEY).copy());
        }
    }
}
