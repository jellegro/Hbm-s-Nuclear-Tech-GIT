package com.usanaem.occultic_ntm.haunting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.WeakHashMap;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

/** Infrequent tracked-site audits and one bounded forest job per dimension. */
public final class EnvironmentalHauntings {
    private final IntegrationConfig config;
    private final Map<World, Runtime> runtimes = new WeakHashMap<World, Runtime>();
    private static final class Runtime {
        final List<Footsteps> sounds = new ArrayList<Footsteps>();
        Grove grove;
    }
    private static final class Footsteps {
        double x, y, z, dx, dz;
        int age, count;
        String sound;
        Footsteps(EntityPlayerMP player) {
            int approach = player.worldObj.rand.nextInt(5), distance = approach < 2 || approach == 4 ? 4 : 6;
            double angle = Math.toRadians(player.rotationYaw + (approach == 4 ? 180 : approach % 2 == 0 ? 90 : -90));
            x = player.posX - Math.sin(angle) * distance; y = player.posY; z = player.posZ + Math.cos(angle) * distance;
            dx = (player.posX - x) / distance; dz = (player.posZ - z) / distance;
            Block floor = player.worldObj.getBlock(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY) - 1, MathHelper.floor_double(player.posZ));
            sound = floor.stepSound.getStepResourcePath();
        }
        boolean tick(World world) {
            if (age++ % 6 != 0) return true;
            double sx = x + dx * count, sz = z + dz * count;
            if (HauntingPlacement.loaded(world, sx, sz, sx, sz)) world.playSoundEffect(sx, y, sz, sound, .22F, .95F);
            return ++count < 4;
        }
    }
    EnvironmentalHauntings(IntegrationConfig config) { this.config = config; }
    private Runtime runtime(World world) {
        Runtime runtime = runtimes.get(world);
        if (runtime == null) { runtime = new Runtime(); runtimes.put(world, runtime); }
        return runtime;
    }
    public void unload(World world) { runtimes.remove(world); }
    public void tick(World world) {
        if (!HauntingDirector.enabled() || !config.areEnvironmentalHauntingsEnabled() || world.isRemote) return;
        Runtime runtime = runtimes.get(world);
        if (runtime == null) return;
        for (Iterator<Footsteps> it = runtime.sounds.iterator(); it.hasNext();) if (!it.next().tick(world)) it.remove();
        if (runtime.grove != null && !runtime.grove.tick(world)) runtime.grove = null;
    }
    public void remember(World world, EntityPlayer player, int x, int y, int z) {
        if (!HauntingDirector.enabled() || !config.areEnvironmentalHauntingsEnabled() || world.isRemote || !HauntingPlacement.loaded(world, x, z, x, z)) return;
        Block block = world.getBlock(x, y, z);
        String kind = block == Blocks.wooden_door ? "door" : block == Blocks.chest ? "donation" : block == Blocks.torch ? "light" : null;
        if (kind == null) return;
        if (kind.equals("door") && (world.getBlockMetadata(x, y, z) & 8) != 0) y--;
        HauntingWorldState.get(world).remember(kind, x, y, z, player.getUniqueID().toString());
    }
    public void rememberForest(EntityPlayerMP player) {
        if (!config.areEnvironmentalHauntingsEnabled() || !config.areHauntingTerrainChangesAllowed() || player.dimension != 0 || HauntingPlacement.underground(player)) return;
        int x = MathHelper.floor_double(player.posX), y = MathHelper.floor_double(player.posY), z = MathHelper.floor_double(player.posZ);
        if (!forest(player.worldObj, x, z)) return;
        HauntingWorldState state = HauntingWorldState.get(player.worldObj);
        for (NBTTagCompound site : state.sites) if ("leafless_grove".equals(site.getString("type"))) {
            double dx = site.getInteger("x") - x, dz = site.getInteger("z") - z;
            if (dx * dx + dz * dz < 64 * 64) return;
        }
        state.remember("leafless_grove", x, y, z, player.getUniqueID().toString());
    }
    private static boolean forest(World world, int x, int z) {
        if (!HauntingPlacement.loaded(world, x, z, x, z)) return false;
        BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        return BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.FOREST);
    }
    public void audit(World world, HauntingWorldState state, HauntingActivity activity) {
        if (!config.areEnvironmentalHauntingsEnabled() || HauntingDirector.age(world) < state.nextEvidence) return;
        // Four distinct records, rotating eligible owners before repeating one.
        for (NBTTagCompound site : auditCandidates(world, state, activity)) {
            state.lastAuditOwner = site.getString("owner"); state.markDirty();
            EntityPlayerMP owner = owner(world, site);
            if (owner == null || !HauntingDirector.evidenceAllowed(owner)) continue;
            String kind = site.getString("type");
            int odds = activity.evidenceOdds(kind, HauntingDirector.surge(world));
            if (kind.equals(HauntingPlayerState.lastEvidence(owner))) odds *= 2;
            if (odds > 0 && world.rand.nextInt(odds) == 0 && atSite(owner, site, false)) {
                // Only a door within earshot is a beat the owner can perceive; torches and groves change silently.
                boolean heard = "door".equals(kind) && owner.getDistanceSq(site.getInteger("x") + .5, site.getInteger("y"), site.getInteger("z") + .5) < 20 * 20;
                if (heard) HauntingDirector.setCue(owner, site.getInteger("x") + .5, site.getInteger("z") + .5);
                HauntingDirector.evidenceCompleted(owner, kind, heard, activity != HauntingActivity.QUIET); return;
            }
        }
        int footstepsOdds = activity.evidenceOdds("footsteps", HauntingDirector.surge(world));
        if (!world.playerEntities.isEmpty() && footstepsOdds > 0 && world.rand.nextInt(footstepsOdds) == 0) {
            Object object = world.playerEntities.get(world.rand.nextInt(world.playerEntities.size()));
            if (object instanceof EntityPlayerMP) subtle((EntityPlayerMP) object, activity);
        }
    }
    List<NBTTagCompound> auditCandidates(World world, HauntingWorldState state, HauntingActivity activity) {
        Map<String, List<NBTTagCompound>> groups = new LinkedHashMap<String, List<NBTTagCompound>>();
        long now = HauntingDirector.age(world);
        for (NBTTagCompound site : state.sites) {
            if (activity.evidenceWeight(site.getString("type")) <= 0 || now < site.getLong("next")
                    || !HauntingPlacement.loaded(world, site.getInteger("x"), site.getInteger("z"), site.getInteger("x"), site.getInteger("z"))) continue;
            EntityPlayerMP player = owner(world, site);
            if (player == null || !HauntingDirector.evidenceAllowed(player)) continue;
            String key = site.getString("owner");
            List<NBTTagCompound> records = groups.get(key);
            if (records == null) { records = new ArrayList<NBTTagCompound>(); groups.put(key, records); }
            records.add(site);
        }
        List<NBTTagCompound> candidates = new ArrayList<NBTTagCompound>();
        List<String> owners = new ArrayList<String>(groups.keySet());
        if (owners.isEmpty()) return candidates;
        int cursor = (owners.indexOf(state.lastAuditOwner) + 1) % owners.size();
        // At most four samples and at most four bounded passes over the owner list.
        for (int visited = 0; visited < owners.size() * 4 && candidates.size() < 4; visited++) {
            List<NBTTagCompound> records = groups.get(owners.get(cursor));
            if (!records.isEmpty()) candidates.add(records.remove(world.rand.nextInt(records.size())));
            cursor = (cursor + 1) % owners.size();
        }
        return candidates;
    }
    /** An atmospheric beat can occupy a cadence slot without publishing another figure. */
    public boolean subtle(EntityPlayerMP player, HauntingActivity activity) {
        if (!config.areEnvironmentalHauntingsEnabled() || !HauntingDirector.evidenceAllowed(player)) return false;
        boolean companions = CompanionAwareness.hint(player, activity);
        boolean sound = footsteps(player);
        if (!sound && companions) {
            double angle = Math.toRadians(player.rotationYaw + 180);
            HauntingDirector.setCue(player, player.posX - Math.sin(angle) * 8, player.posZ + Math.cos(angle) * 8);
        }
        // Quiet days keep their silence: the beat fills the slot but is not followed by a quick sighting.
        if (sound || companions) { HauntingDirector.evidenceCompleted(player, sound ? "footsteps" : "companions", true, activity != HauntingActivity.QUIET); return true; }
        return false;
    }
    /** A nearby familiar door is part of the current stalking, never a destination the player must seek. */
    boolean presenceDoor(EntityPlayerMP player) {
        if (!config.areEnvironmentalHauntingsEnabled() || !HauntingDirector.evidenceAllowed(player)) return false;
        for (NBTTagCompound site : new ArrayList<NBTTagCompound>(HauntingWorldState.get(player.worldObj).sites)) {
            if (!"door".equals(site.getString("type")) || !player.getUniqueID().toString().equals(site.getString("owner"))
                    || player.getDistanceSq(site.getInteger("x") + .5, site.getInteger("y"), site.getInteger("z") + .5) > 14 * 14) continue;
            if (atSite(player, site, false)) {
                HauntingDirector.setCue(player, site.getInteger("x") + .5, site.getInteger("z") + .5);
                HauntingDirector.evidenceCompleted(player, "door", true, true); return true;
            }
        }
        return false;
    }
    private static EntityPlayerMP owner(World world, NBTTagCompound site) {
        for (Object object : world.playerEntities) if (object instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) object;
            if (player.getUniqueID().toString().equals(site.getString("owner")) && player.isEntityAlive()) return player;
        }
        return null;
    }
    public boolean footsteps(EntityPlayerMP player) {
        if (!HauntingDirector.enabled() || !config.areEnvironmentalHauntingsEnabled() || !player.isEntityAlive() || player.isPlayerSleeping()) return false;
        Runtime runtime = runtime(player.worldObj);
        if (!runtime.sounds.isEmpty() || !HauntingPlacement.loaded(player.worldObj, player.posX - 7, player.posZ - 7, player.posX + 7, player.posZ + 7)) return false;
        Footsteps steps = new Footsteps(player);
        runtime.sounds.add(steps); HauntingDirector.setCue(player, steps.x, steps.z); return true;
    }
    public boolean force(EntityPlayerMP player, String kind) {
        if (!HauntingDirector.enabled() || !config.areEnvironmentalHauntingsEnabled()) return false;
        if ("footsteps".equals(kind)) return footsteps(player);
        HauntingWorldState state = HauntingWorldState.get(player.worldObj);
        for (NBTTagCompound site : new ArrayList<NBTTagCompound>(state.sites)) if (kind.equals(site.getString("type"))
                && player.getUniqueID().toString().equals(site.getString("owner")) && atSite(player, site, true)) return true;
        return false;
    }
    private boolean atSite(EntityPlayerMP owner, NBTTagCompound site, boolean forced) {
        World world = owner.worldObj;
        String kind = site.getString("type");
        int x = site.getInteger("x"), y = site.getInteger("y"), z = site.getInteger("z");
        if (HauntingDirector.age(world) < site.getLong("next")) return false;
        if ("leafless_grove".equals(kind)) {
            if (!config.areHauntingTerrainChangesAllowed() || runtime(world).grove != null || !forest(world, x, z)) return false;
            Grove grove = new Grove(owner, x, y, z);
            if (!grove.safeRegion(world)) return false;
            runtime(world).grove = grove;
        } else {
            if (!HauntingSafety.unobserved(world, x, y, z, "door".equals(kind) ? 5 : 25)) return false;
            Block block = world.getBlock(x, y, z);
            if (!("door".equals(kind) && block == Blocks.wooden_door || "light".equals(kind) && block == Blocks.torch
                    || "donation".equals(kind) && block == Blocks.chest)) {
                HauntingWorldState.get(world).sites.remove(site); HauntingWorldState.get(world).markDirty(); return false;
            }
            if (!HauntingSafety.permitted(world, owner, kind, x, y, z)) return false;
            if ("door".equals(kind)) {
                if (world.getBlock(x, y + 1, z) != Blocks.wooden_door || (world.getBlockMetadata(x, y + 1, z) & 8) == 0
                        || !HauntingSafety.permitted(world, owner, kind, x, y + 1, z)
                        || world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z)) return false;
                // Change only the lower half's open bit. Direction, hinge and upper metadata survive.
                int meta = world.getBlockMetadata(x, y, z);
                if ((meta & 8) != 0 || !world.setBlockMetadataWithNotify(x, y, z, meta ^ 4, 3)) return false;
                world.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
            } else if ("light".equals(kind)) {
                if (world.getTileEntity(x, y, z) != null || !world.setBlockToAir(x, y, z)) return false;
            } else {
                if (!(world.getTileEntity(x, y, z) instanceof TileEntityChest)) return false;
                TileEntityChest chest = (TileEntityChest) world.getTileEntity(x, y, z);
                if (chest.numPlayersUsing > 0) return false;
                int slot = -1;
                for (int i = 0; i < chest.getSizeInventory(); i++) if (chest.getStackInSlot(i) == null) { slot = i; break; }
                if (slot < 0) return false;
                ItemStack gift = new ItemStack(Blocks.torch);
                gift.setStackDisplayName("A light for the way back");
                chest.setInventorySlotContents(slot, gift); chest.markDirty();
            }
        }
        site.setLong("next", HauntingDirector.age(world) + ("leafless_grove".equals(kind) ? 7 * 24000L : "donation".equals(kind) ? 24000L : 6000L));
        HauntingWorldState.get(world).markDirty(); return true;
    }
    private static final class Grove {
        final java.util.UUID ownerId;
        final int x, y, z;
        int cursor, removed, elapsed;
        Grove(EntityPlayerMP owner, int x, int y, int z) { ownerId = owner.getUniqueID(); this.x = x - 16; this.y = Math.max(1, Math.min(215, y - 4)); this.z = z - 16; }
        boolean safeRegion(World world) {
            if (!HauntingPlacement.loaded(world, x, z, x + 31, z + 31)) return false;
            for (Object object : world.playerEntities) {
                EntityPlayer player = (EntityPlayer) object;
                // Exclude the entire region, and conservatively pause if it lies in the view cone.
                if (player.getDistanceSq(x + 16, y + 16, z + 16) < 110 * 110) return false;
                net.minecraft.util.Vec3 to = net.minecraft.util.Vec3.createVectorHelper(x + 16 - player.posX, y + 16 - player.posY - player.getEyeHeight(), z + 16 - player.posZ);
                if (to.lengthVector() < 160 && player.getLookVec().dotProduct(to.normalize()) > .3) return false;
            }
            for (NBTTagCompound site : HauntingWorldState.get(world).sites) if (!"leafless_grove".equals(site.getString("type"))
                    && site.getInteger("x") >= x - 8 && site.getInteger("x") <= x + 39 && site.getInteger("z") >= z - 8 && site.getInteger("z") <= z + 39) return false;
            return true;
        }
        boolean tick(World world) {
            if (++elapsed > 2400 || removed >= 2048 || cursor >= 32 * 32 * 40) return false;
            EntityPlayer player = world.func_152378_a(ownerId);
            if (!(player instanceof EntityPlayerMP) || !safeRegion(world)) return true;
            // Strict budget: at most 256 candidates, each with at most 48 neighbour checks.
            for (int budget = 0; budget < 256 && cursor < 32 * 32 * 40 && removed < 2048; budget++, cursor++) {
                int bx = x + cursor % 32, bz = z + cursor / 32 % 32, by = y + cursor / (32 * 32);
                Block block = world.getBlock(bx, by, bz);
                if ((block != Blocks.leaves && block != Blocks.leaves2) || (world.getBlockMetadata(bx, by, bz) & 4) != 0
                        || world.getTileEntity(bx, by, bz) != null || !forest(world, bx, bz) || !naturalNeighbourhood(world, bx, by, bz)) continue;
                if (HauntingSafety.permitted(world, (EntityPlayerMP) player, "leafless_grove", bx, by, bz)
                        && world.setBlock(bx, by, bz, Blocks.air, 0, 2)) removed++;
            }
            return cursor < 32 * 32 * 40 && removed < 2048;
        }
        boolean naturalNeighbourhood(World world, int bx, int by, int bz) {
            if (!HauntingPlacement.loaded(world, bx - 3, bz - 3, bx + 3, bz + 3)) return false;
            boolean trunk = false;
            for (int ox = -1; ox <= 1; ox++) for (int oy = -1; oy <= 1; oy++) for (int oz = -1; oz <= 1; oz++) {
                Block nearby = world.getBlock(bx + ox, by + oy, bz + oz);
                if (world.getTileEntity(bx + ox, by + oy, bz + oz) != null || !natural(nearby)) return false;
            }
            // Small cardinal neighbourhood rejects foreign blocks, tiles and common infrastructure.
            for (int step = -3; step <= 3; step++) for (int axis = 0; axis < 3; axis++) {
                int tx = bx + (axis == 0 ? step : 0), ty = by + (axis == 1 ? step : 0), tz = bz + (axis == 2 ? step : 0);
                Block other = world.getBlock(tx, ty, tz);
                if (world.getTileEntity(tx, ty, tz) != null) return false;
                if (other == Blocks.log || other == Blocks.log2) { if ((world.getBlockMetadata(tx, ty, tz) & 12) == 0) trunk = true; }
                else if (other != Blocks.air && other != Blocks.leaves && other != Blocks.leaves2 && other != Blocks.grass && other != Blocks.dirt && other != Blocks.vine && other != Blocks.tallgrass) return false;
            }
            return trunk;
        }
        private boolean natural(Block block) {
            return block == Blocks.air || block == Blocks.log || block == Blocks.log2 || block == Blocks.leaves || block == Blocks.leaves2
                    || block == Blocks.grass || block == Blocks.dirt || block == Blocks.vine || block == Blocks.tallgrass;
        }
    }
}
