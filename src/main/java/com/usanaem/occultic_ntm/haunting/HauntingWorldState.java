package com.usanaem.occultic_ntm.haunting;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/** Small per-dimension history; transient entities/jobs never save. */
public final class HauntingWorldState extends WorldSavedData {
    public static final String KEY = "occultic_ntm_haunting";
    public boolean invoked;
    public long surgeUntil;
    public long surgeDecayUntil;
    public long nextEvidence;
    public long nextAudit;
    public long profileDay = -1;
    public int profile;
    public String lastAuditOwner = "";
    public final List<NBTTagCompound> sites = new ArrayList<NBTTagCompound>();
    public HauntingWorldState(String name) { super(name); }
    public static HauntingWorldState get(World world) {
        HauntingWorldState state = (HauntingWorldState) world.perWorldStorage.loadData(HauntingWorldState.class, KEY);
        if (state == null) {
            state = new HauntingWorldState(KEY);
            world.perWorldStorage.setData(KEY, state);
        }
        return state;
    }
    public void remember(String type, int x, int y, int z, String owner) {
        for (NBTTagCompound site : sites) {
            if (site.getInteger("x") == x && site.getInteger("y") == y && site.getInteger("z") == z && type.equals(site.getString("type"))) return;
        }
        int sameType = 0;
        for (NBTTagCompound site : sites) if (type.equals(site.getString("type"))) sameType++;
        if (sameType >= 32) evict(type, owner);
        if (sites.size() >= 128) evict(null, owner);
        NBTTagCompound site = new NBTTagCompound();
        site.setString("type", type); site.setString("owner", owner);
        site.setInteger("x", x); site.setInteger("y", y); site.setInteger("z", z);
        sites.add(site); markDirty();
    }
    private void evict(String type, String incomingOwner) {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        counts.put(incomingOwner, 1); // Include the incoming record in fair-share accounting.
        for (NBTTagCompound site : sites) if (type == null || type.equals(site.getString("type"))) {
            String owner = site.getString("owner");
            Integer count = counts.get(owner); counts.put(owner, count == null ? 1 : count + 1);
        }
        int victim = -1, largest = -1;
        for (int i = 0; i < sites.size(); i++) {
            NBTTagCompound site = sites.get(i);
            if (type != null && !type.equals(site.getString("type"))) continue;
            String owner = site.getString("owner"); int count = counts.get(owner);
            if (count > largest || count == largest && owner.equals(incomingOwner)
                    && !sites.get(victim).getString("owner").equals(incomingOwner)) {
                victim = i; largest = count;
            }
        }
        if (victim >= 0) sites.remove(victim);
    }
    @Override public void readFromNBT(NBTTagCompound tag) {
        invoked = tag.getBoolean("invoked"); surgeUntil = tag.getLong("surgeUntil");
        surgeDecayUntil = tag.getLong("surgeDecayUntil"); nextEvidence = Math.max(0, tag.getLong("nextEvidence"));
        nextAudit = tag.getLong("nextAudit"); profileDay = tag.hasKey("profileDay") ? tag.getLong("profileDay") : -1;
        profile = Math.max(0, Math.min(3, tag.getInteger("profile")));
        lastAuditOwner = tag.getString("lastAuditOwner");
        sites.clear(); NBTTagList list = tag.getTagList("sites", 10);
        for (int i = 0; i < Math.min(128, list.tagCount()); i++) sites.add(list.getCompoundTagAt(i));
    }
    @Override public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("version", 3); tag.setBoolean("invoked", invoked); tag.setLong("surgeUntil", surgeUntil);
        tag.setLong("surgeDecayUntil", surgeDecayUntil); tag.setLong("nextEvidence", nextEvidence);
        tag.setLong("nextAudit", nextAudit); tag.setLong("profileDay", profileDay); tag.setInteger("profile", profile);
        tag.setString("lastAuditOwner", lastAuditOwner);
        NBTTagList list = new NBTTagList(); for (NBTTagCompound site : sites) list.appendTag(site.copy()); tag.setTag("sites", list);
    }
}
