package com.usanaem.occultic_ntm.haunting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/** Herobrine-only history. Reads never allocate persistent tags. */
public final class HauntingPlayerState {
    public static final String KEY = "occultic_ntm:haunting";
    private HauntingPlayerState() { }
    private static NBTTagCompound read(EntityPlayer player) {
        return player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(KEY);
    }
    private static NBTTagCompound write(EntityPlayer player) {
        NBTTagCompound root = player.getEntityData();
        if (!root.hasKey(EntityPlayer.PERSISTED_NBT_TAG, 10)) root.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        NBTTagCompound persisted = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (!persisted.hasKey(KEY, 10)) persisted.setTag(KEY, new NBTTagCompound());
        return persisted.getCompoundTag(KEY);
    }
    public static boolean observed(EntityPlayer player) { return read(player).getBoolean("observed"); }
    public static boolean markObserved(EntityPlayer player) {
        if (player.worldObj.isRemote || observed(player)) return false;
        write(player).setBoolean("observed", true);
        return true;
    }
    public static long next(EntityPlayer player) { return Math.max(0, read(player).getLong("nextEncounter")); }
    public static void defer(EntityPlayer player, long time) { write(player).setLong("nextEncounter", Math.max(0, time)); }
    public static boolean currentCadence(EntityPlayer player) { return read(player).getInteger("cadenceVersion") >= 2; }
    public static int failures(EntityPlayer player) { return Math.max(0, Math.min(6, read(player).getInteger("placementFailures"))); }
    public static int lastKind(EntityPlayer player) { return read(player).hasKey("lastKind") ? read(player).getInteger("lastKind") : -1; }
    public static long evidenceUntil(EntityPlayer player) { return Math.max(0, read(player).getLong("evidenceUntil")); }
    public static String lastEvidence(EntityPlayer player) { return read(player).getString("lastEvidence"); }
    public static void schedule(EntityPlayer player, long time) {
        NBTTagCompound tag = write(player); tag.setInteger("cadenceVersion", 2);
        tag.setInteger("placementFailures", 0); defer(player, time);
    }
    public static void begin(EntityPlayer player) {
        write(player).setInteger("cadenceVersion", 2);
        // Publication is not successful delivery: keep backoff until an arrived encounter retires.
        defer(player, 0);
    }
    public static void retry(EntityPlayer player, long time) {
        write(player).setInteger("placementFailures", Math.min(6, failures(player) + 1)); defer(player, time);
    }
    public static void cleared(EntityPlayer player, Sighting kind) { write(player).setInteger("lastKind", kind.ordinal()); }
    public static void evidence(EntityPlayer player, String kind, long now) {
        NBTTagCompound tag = write(player); tag.setString("lastEvidence", kind); tag.setLong("evidenceUntil", now + 600);
    }
    public static void copy(EntityPlayer original, EntityPlayer replacement) {
        NBTTagCompound persisted = original.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (persisted.hasKey(KEY, 10)) {
            write(replacement);
            replacement.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setTag(KEY, persisted.getCompoundTag(KEY).copy());
        }
    }
}
