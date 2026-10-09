package com.usanaem.occultic_ntm.compat.witchery;

import java.util.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/** Sparse source enumeration plus legacy migration records; block identity owns appearance and eligibility. */
public final class RadiantGlyphData extends WorldSavedData {
    private static final String KEY = "occultic_ntm_radiant_glyphs";
    private final Map<Long, Map<String, int[]>> chunks = new HashMap<Long, Map<String, int[]>>();
    public RadiantGlyphData(String name) { super(name); }
    public static RadiantGlyphData find(World world) { return (RadiantGlyphData) world.perWorldStorage.loadData(RadiantGlyphData.class, KEY); }
    public static RadiantGlyphData get(World world) {
        RadiantGlyphData data = find(world);
        if (data == null) { data = new RadiantGlyphData(KEY); world.perWorldStorage.setData(KEY, data); }
        return data;
    }
    private static long chunk(int x, int z) { return ((long) (x >> 4) << 32) ^ ((z >> 4) & 0xffffffffL); }
    private static String position(int x, int y, int z) { return x + ":" + y + ":" + z; }
    public void add(int x, int y, int z) {
        long key = chunk(x, z);
        Map<String, int[]> glyphs = chunks.get(key);
        if (glyphs == null) { glyphs = new HashMap<String, int[]>(); chunks.put(key, glyphs); }
        glyphs.put(position(x, y, z), new int[] {x, y, z}); markDirty();
    }
    public boolean contains(int x, int y, int z) {
        Map<String, int[]> glyphs = chunks.get(chunk(x, z));
        return glyphs != null && glyphs.containsKey(position(x, y, z));
    }
    public void remove(int x, int y, int z) {
        long key = chunk(x, z);
        Map<String, int[]> glyphs = chunks.get(key);
        if (glyphs == null || glyphs.remove(position(x, y, z)) == null) return;
        if (glyphs.isEmpty()) chunks.remove(key); markDirty();
    }
    public Map<Long, List<int[]>> snapshot() {
        Map<Long, List<int[]>> copy = new HashMap<Long, List<int[]>>();
        for (Map.Entry<Long, Map<String, int[]>> entry : chunks.entrySet()) {
            List<int[]> positions = new ArrayList<int[]>();
            for (int[] pos : entry.getValue().values()) positions.add(pos.clone());
            copy.put(entry.getKey(), positions);
        }
        return copy;
    }
    public List<int[]> inChunk(int x, int z) {
        List<int[]> result = new ArrayList<int[]>();
        Map<String, int[]> glyphs = chunks.get(((long) x << 32) ^ (z & 0xffffffffL));
        if (glyphs != null) for (int[] pos : glyphs.values()) result.add(pos.clone());
        return result;
    }
    public void readFromNBT(NBTTagCompound tag) {
        chunks.clear();
        NBTTagList list = tag.getTagList("glyphs", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound pos = list.getCompoundTagAt(i);
            int y = pos.getInteger("y");
            if (y >= 0 && y < 256) add(pos.getInteger("x"), y, pos.getInteger("z"));
        }
    }
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (Map<String, int[]> glyphs : chunks.values()) for (int[] pos : glyphs.values()) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger("x", pos[0]); entry.setInteger("y", pos[1]); entry.setInteger("z", pos[2]); list.appendTag(entry);
        }
        tag.setTag("glyphs", list);
    }
}
