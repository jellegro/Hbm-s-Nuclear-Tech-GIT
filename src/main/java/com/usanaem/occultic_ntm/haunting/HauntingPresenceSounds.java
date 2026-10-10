package com.usanaem.occultic_ntm.haunting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/** Short reactive audio trails near the current player. Jobs contain UUIDs, never player/world ownership. */
final class HauntingPresenceSounds {
    private final Map<World, List<Trail>> trails = new WeakHashMap<World, List<Trail>>();
    private static final class Trail {
        UUID owner;
        int dimension, age, next, emitted, limit, stoppedTicks;
        double x, y, z, lastX, lastZ, dx, dz;
        float yaw;
        boolean digging, moving, reported;
        String step;
    }
    boolean start(EntityPlayerMP player, HauntingPresence presence) {
        if (!HauntingDirector.enabled() || !HauntingDirector.evidenceAllowed(player)) return false;
        List<Trail> jobs = trails.get(player.worldObj);
        if (jobs == null) { jobs = new ArrayList<Trail>(); trails.put(player.worldObj, jobs); }
        if (jobs.size() >= 8) return false;
        for (Trail job : jobs) if (job.owner.equals(player.getUniqueID())
                || player.getDistanceSq(job.x, job.y, job.z) < 24 * 24) return false;
        Trail job = new Trail(); job.owner = player.getUniqueID(); job.dimension = player.dimension;
        job.digging = presence.context == HauntingPresence.Context.MINE && presence.workingUntil > presence.clock;
        job.moving = presence.context == HauntingPresence.Context.TRAVEL;
        double angle = Math.toRadians(player.rotationYaw + 130 + player.worldObj.rand.nextInt(101));
        double bx = presence.anchored ? presence.bearingX : -Math.sin(angle);
        double bz = presence.anchored ? presence.bearingZ : Math.cos(angle);
        if (job.moving) { bx = -presence.headingX; bz = -presence.headingZ; }
        double distance = job.digging ? 6 : 4 + player.worldObj.rand.nextInt(5);
        job.x = player.posX + bx * distance; job.y = player.posY; job.z = player.posZ + bz * distance;
        job.dx = bx; job.dz = bz;
        if (!HauntingPlacement.loaded(player.worldObj, job.x - 1, job.z - 1, job.x + 1, job.z + 1)
                || !HauntingPlacement.deliverable(player, job.x, job.z)) return false;
        // Digging can originate through a wall. Footfalls require a real local floor, not the air above a ravine.
        if (!job.digging && !footing(player.worldObj, job)) return false;
        job.lastX = player.posX; job.lastZ = player.posZ; job.yaw = player.rotationYaw;
        job.limit = job.digging ? 5 + player.worldObj.rand.nextInt(5) : 4 + player.worldObj.rand.nextInt(5);
        job.step = player.worldObj.getBlock(MathHelper.floor_double(job.x), MathHelper.floor_double(job.y) - 1,
                MathHelper.floor_double(job.z)).stepSound.getStepResourcePath();
        jobs.add(job); return true;
    }
    private static boolean footing(World world, Trail job) {
        int x = MathHelper.floor_double(job.x), z = MathHelper.floor_double(job.z), y = MathHelper.floor_double(job.y);
        for (int at = y + 1; at >= Math.max(1, y - 2); at--) {
            Block floor = world.getBlock(x, at - 1, z);
            if (World.doesBlockHaveSolidTopSurface(world, x, at - 1, z) && !floor.getMaterial().isLiquid()
                    && world.isAirBlock(x, at, z)) { job.y = at; return true; }
        }
        return false;
    }
    void tick(World world) {
        List<Trail> jobs = trails.get(world); if (jobs == null) return;
        for (Iterator<Trail> it = jobs.iterator(); it.hasNext();) if (!tick(world, it.next())) it.remove();
        if (jobs.isEmpty()) trails.remove(world);
    }
    private boolean tick(World world, Trail job) {
        EntityPlayer found = world.func_152378_a(job.owner);
        if (!(found instanceof EntityPlayerMP)) return false;
        EntityPlayerMP player = (EntityPlayerMP) found;
        if (!HauntingDirector.enabled() || !player.isEntityAlive() || player.isPlayerSleeping() || player.capabilities.isCreativeMode
                || player.ridingEntity != null || player.dimension != job.dimension || ++job.age > 240
                || player.getDistanceSq(job.x, job.y, job.z) > 20 * 20 || HauntingDirector.nearby(player)) return false;
        if (job.age < job.next) return true;
        double moved = Math.sqrt((player.posX - job.lastX) * (player.posX - job.lastX) + (player.posZ - job.lastZ) * (player.posZ - job.lastZ));
        float turn = Math.abs(MathHelper.wrapAngleTo180_float(player.rotationYaw - job.yaw));
        // It listens when you listen, and falls silent when you spin around to catch it.
        if (job.emitted > 0 && (turn > 55 || job.moving && moved < .15)) {
            job.stoppedTicks += 10; job.next = job.age + 10;
            if (job.stoppedTicks > 70 || turn > 90) return false;
            return true;
        }
        if (job.digging && job.emitted > 0) {
            HauntingPresence p = HauntingPresence.read(player);
            if (p.workingUntil <= p.clock) return false;
        }
        if (job.moving) {
            // Continue in world space behind the travelling player; never snap the source when they turn.
            double dx = player.posX - job.lastX, dz = player.posZ - job.lastZ;
            if (moved > 0 && moved < 10) { job.x += dx * .7; job.z += dz * .7; }
        } else if (job.emitted > 0 && !job.digging) { job.x -= job.dx * .45; job.z -= job.dz * .45; }
        if (!HauntingPlacement.loaded(world, job.x - 1, job.z - 1, job.x + 1, job.z + 1)
                || !HauntingPlacement.deliverable(player, job.x, job.z) || !job.digging && !footing(world, job)) return false;
        world.playSoundEffect(job.x, job.y + (job.digging ? 1 : 0), job.z, job.digging ? "dig.stone" : job.step,
                job.digging ? .28F : .22F, .83F + world.rand.nextFloat() * .13F);
        HauntingDirector.setCue(player, job.x, job.z);
        if (!job.reported) {
            job.reported = true; HauntingPresence.heard(player, 160);
            HauntingStats.evidence(true); HauntingStats.presenceSound(job.digging);
            HauntingWorldState state = HauntingWorldState.get(world);
            state.nextEvidence = Math.max(state.nextEvidence, HauntingDirector.age(world) + 400); state.markDirty();
        }
        job.lastX = player.posX; job.lastZ = player.posZ; job.yaw = player.rotationYaw; job.stoppedTicks = 0;
        job.emitted++; job.next = job.age + (job.digging ? 12 + world.rand.nextInt(21) : 8 + world.rand.nextInt(9));
        return job.emitted < job.limit;
    }
    void unload(World world) { trails.remove(world); }
    void logout(EntityPlayer player) {
        for (List<Trail> jobs : trails.values()) for (Iterator<Trail> it = jobs.iterator(); it.hasNext();)
            if (it.next().owner.equals(player.getUniqueID())) it.remove();
    }
    int active(World world) { List<Trail> jobs = trails.get(world); return jobs == null ? 0 : jobs.size(); }
}
