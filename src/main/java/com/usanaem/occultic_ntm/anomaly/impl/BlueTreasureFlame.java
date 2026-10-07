package com.usanaem.occultic_ntm.anomaly.impl;

import com.hbm.items.ModItems;
import com.usanaem.occultic_ntm.anomaly.*;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public final class BlueTreasureFlame implements Anomaly {
    public String id() { return "blue_treasure_flame"; }
    public Ownership ownership() { return Ownership.SERVER; }
    public boolean enabled(IntegrationConfig config) { return config.areBlueTreasureFlamesEnabled(); }
    public boolean eligible(EntityPlayerMP player) { return AnomalyTerrain.outdoorsAtNight(player); }
    public int minimumAttention() { return 0; }
    public int chanceDenominator(EntityPlayerMP player, IntegrationConfig config) { return 120 - AnomalyAttention.get(player) / 5; }
    public int weight() { return 1; }
    public long cooldownTicks(EntityPlayerMP player, IntegrationConfig config) { return 1200L * 120; }

    public boolean execute(EntityPlayerMP player, boolean forced) {
        World world = player.worldObj;
        if (!world.getEntitiesWithinAABB(EntityBlueFlame.class, player.boundingBox.expand(64, 32, 64)).isEmpty()) return false;
        int[] point = AnomalyTerrain.find(player, 12, 14, forced);
        if (point == null) return false;
        int x = point[0], y = point[1] - 3, z = point[2];
        Regions regions = Regions.get(world);
        String region = (x >> 7) + ":" + (z >> 7);
        if (!forced && regions.times.getLong(region) > world.getTotalWorldTime()) return false;
        // Diggable untouched surface soil only; never replace a structure or inventory.
        for (int checkY = y; checkY < point[1]; checkY++) {
            Block soil = world.getBlock(x, checkY, z);
            if (soil != Blocks.dirt && soil != Blocks.grass && soil != Blocks.sand && soil != Blocks.gravel) return false;
            if (world.getTileEntity(x, checkY, z) != null) return false;
        }
        if (world.getBlock(x - 1, y, z) == Blocks.chest || world.getBlock(x + 1, y, z) == Blocks.chest
                || world.getBlock(x, y, z - 1) == Blocks.chest || world.getBlock(x, y, z + 1) == Blocks.chest) return false;
        Block original = world.getBlock(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        if (!world.setBlock(x, y, z, Blocks.chest, 0, 3)) return false;
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof TileEntityChest)) { world.setBlock(x, y, z, original, metadata, 3); return false; }
        WeightedRandomChestContent[] loot = {
            new WeightedRandomChestContent(new net.minecraft.item.ItemStack(Items.iron_ingot), 1, 3, 15),
            new WeightedRandomChestContent(new net.minecraft.item.ItemStack(Items.gold_nugget), 2, 6, 12),
            new WeightedRandomChestContent(new net.minecraft.item.ItemStack(Items.bone), 1, 4, 10),
            new WeightedRandomChestContent(new net.minecraft.item.ItemStack(ModItems.ingot_lead), 1, 2, 8),
            new WeightedRandomChestContent(new net.minecraft.item.ItemStack(Items.emerald), 1, 1, 1)
        };
        WeightedRandomChestContent.generateChestContents(world.rand, loot, (TileEntityChest) tile, 2 + world.rand.nextInt(3));
        tile.markDirty();
        EntityBlueFlame flame = new EntityBlueFlame(world);
        flame.setPosition(x + 0.5, point[1] + 0.1, z + 0.5);
        if (!world.spawnEntityInWorld(flame)) {
            TileEntityChest chest = (TileEntityChest) tile;
            for (int slot = 0; slot < chest.getSizeInventory(); slot++) chest.setInventorySlotContents(slot, null);
            world.setBlock(x, y, z, original, metadata, 3);
            return false;
        }
        regions.times.setLong(region, world.getTotalWorldTime() + 168000L);
        regions.markDirty();
        return true;
    }

    /** Persistent spatial cooldown prevents neighboring players multiplying natural rewards. */
    public static final class Regions extends WorldSavedData {
        private static final String KEY = "occultic_ntm_flame_regions";
        private NBTTagCompound times = new NBTTagCompound();
        public Regions(String name) { super(name); }
        public static Regions get(World world) {
            Regions data = (Regions) world.perWorldStorage.loadData(Regions.class, KEY);
            if (data == null) { data = new Regions(KEY); world.perWorldStorage.setData(KEY, data); }
            return data;
        }
        public void readFromNBT(NBTTagCompound tag) { times = tag.getCompoundTag("regions"); }
        public void writeToNBT(NBTTagCompound tag) { tag.setTag("regions", times); }
    }
}
