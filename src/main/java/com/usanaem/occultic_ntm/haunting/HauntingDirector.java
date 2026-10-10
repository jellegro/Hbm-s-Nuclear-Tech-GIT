package com.usanaem.occultic_ntm.haunting;

import com.usanaem.occultic_ntm.config.IntegrationConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerEvent;

/** Coordinated scheduler independent of Anomalies and its attention/enable switches. */
public final class HauntingDirector {
    private static HauntingDirector instance;
    private final IntegrationConfig config;
    private final EnvironmentalHauntings environment;
    private final HauntingInvocation invocation = new HauntingInvocation();
    private final HauntingPresenceSounds presenceSounds = new HauntingPresenceSounds();
    private final java.util.Map<EntityPlayerMP, World> sessions = new java.util.WeakHashMap<EntityPlayerMP, World>();
    private final java.util.Map<EntityPlayerMP, EntityHerobrine> manifestations = new java.util.WeakHashMap<EntityPlayerMP, EntityHerobrine>();
    private final java.util.Map<net.minecraft.entity.player.EntityPlayer, Boolean> sleepers = new java.util.WeakHashMap<net.minecraft.entity.player.EntityPlayer, Boolean>();
    public HauntingDirector(IntegrationConfig config) { this.config = config; environment = new EnvironmentalHauntings(config); instance = this; }
    public static boolean enabled() { return instance != null && instance.config.isHerobrineHauntingEnabled(); }
    public static boolean terrainEnabled() { return enabled() && instance.config.areEnvironmentalHauntingsEnabled() && instance.config.areHauntingTerrainChangesAllowed(); }
    public static long age(World world) {
        World overworld = DimensionManager.getWorld(0);
        return Math.max(0, (overworld == null ? world : overworld).getTotalWorldTime());
    }
    public boolean active(World world) {
        if (!config.isHerobrineHauntingEnabled()) return false;
        World overworld = DimensionManager.getWorld(0);
        return age(world) >= config.getHauntingGraceDays() * 24000L || HauntingWorldState.get(overworld == null ? world : overworld).invoked;
    }
    @SubscribeEvent public void clonePlayer(PlayerEvent.Clone event) {
        if (config.isHerobrineHauntingEnabled()) HauntingPlayerState.copy(event.original, event.entityPlayer);
    }
    @SubscribeEvent public void tick(TickEvent.PlayerTickEvent event) {
        if (!config.isHerobrineHauntingEnabled() || event.side != Side.SERVER || event.phase != TickEvent.Phase.END
                || !(event.player instanceof EntityPlayerMP) || event.player.ticksExisted % 20 != 0) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (!player.isEntityAlive() || player.capabilities.isCreativeMode || player.ridingEntity != null || !active(player.worldObj)) {
            sessions.remove(player); presenceSounds.logout(player); return;
        }
        if (player.isPlayerSleeping()) {
            sessions.remove(player); presenceSounds.logout(player);
            if (!sleepers.containsKey(player)) {
                sleepers.put(player, Boolean.TRUE);
                // Pity: every sleep without a Nightmare makes the next one a little likelier (never below 1/4 of base).
                int base = config.getHauntingNightmareOdds();
                int odds = Math.max(Math.max(2, base / 4), base - 5 * HauntingPlayerState.sleepsWithoutNightmare(player));
                boolean haunted = player.worldObj.rand.nextInt(odds) == 0 && spawn(player, Sighting.NIGHTMARE, false);
                HauntingPlayerState.slept(player, haunted);
            }
            return;
        }
        sleepers.remove(player);
        if (player.ticksExisted % 1200 == 0) environment.rememberForest(player);
        long pace = countdown(player);
        HauntingPresence presence = HauntingPresence.read(player);
        presence.sample(player, pace, sessions.put(player, player.worldObj) != player.worldObj);
        EntityHerobrine current = manifestations.get(player);
        if (current != null && !current.isDead && current.worldObj == player.worldObj) {
            // A figure whose chunk unloaded never ticks or retires itself; do not let it hold the slot forever.
            if (age(player.worldObj) - current.bornAt() <= 3000) return;
            HauntingStats.record(current.sighting(), HauntingStats.Outcome.STALE);
            current.setDead();
        }
        if (current != null) {
            manifestations.remove(player);
            if (!current.isDead) current.setDead();
        }
        // Reload: a stale manifestation may have changed memory through its retirement callback.
        HauntingPresence.read(player).act(player, config, environment, presenceSounds, pace);
    }
    /** Where the last perceptible beat seemed to come from, or null; the next placement leans that way. */
    public static double[] cue(net.minecraft.entity.player.EntityPlayer player) {
        if (!enabled()) return null;
        return HauntingPresence.read(player).cue(player);
    }
    static void setCue(net.minecraft.entity.player.EntityPlayer player, double x, double z) {
        if (!enabled()) return;
        HauntingPresence.cue(player, x, player.posY, z);
        HauntingStats.cue();
    }
    long countdown(EntityPlayerMP player) {
        int min = config.getHauntingMinimumSeconds() * 20, max = config.getHauntingMaximumSeconds() * 20;
        long delay = min + player.worldObj.rand.nextInt(max - min + 1);
        if (!player.worldObj.isDaytime()) delay /= 2;
        return Math.max(20L, (long) (delay / (activity(player.worldObj).activity * surge(player.worldObj))));
    }
    public static HauntingActivity activity(World world) {
        return !enabled() ? HauntingActivity.NORMAL : HauntingActivity.saved(instance.profile(world));
    }
    public static double surge(World world) {
        if (!enabled()) return 1;
        World overworld = DimensionManager.getWorld(0);
        HauntingWorldState state = HauntingWorldState.get(overworld == null ? world : overworld);
        long now = age(world);
        if (state.surgeUntil > now) return 6;
        if (state.surgeDecayUntil <= now || state.surgeDecayUntil <= state.surgeUntil) return 1;
        return 1 + 5D * (state.surgeDecayUntil - now) / (state.surgeDecayUntil - state.surgeUntil);
    }
    static void retired(EntityHerobrine figure, boolean arrived) {
        if (!enabled() || figure.worldObj == null || figure.worldObj.isRemote) return;
        for (java.util.Iterator<java.util.Map.Entry<EntityPlayerMP, EntityHerobrine>> it = instance.manifestations.entrySet().iterator(); it.hasNext();) {
            java.util.Map.Entry<EntityPlayerMP, EntityHerobrine> entry = it.next();
            if (entry.getValue() != figure) continue;
            EntityPlayerMP player = entry.getKey(); it.remove();
            if (player.worldObj != figure.worldObj || !player.isEntityAlive()
                    || player.worldObj.func_152378_a(player.getUniqueID()) != player) break;
            if (arrived) {
                HauntingPlayerState.cleared(player, figure.sighting());
                HauntingWorldState state = HauntingWorldState.get(player.worldObj);
                state.nextEvidence = Math.max(state.nextEvidence, age(player.worldObj) + 200); state.markDirty();
            }
            HauntingPresence.retired(player, figure, arrived, instance.countdown(player));
            break;
        }
    }
    public static boolean evidenceAllowed(EntityPlayerMP player) {
        if (!enabled() || !instance.active(player.worldObj) || !player.isEntityAlive() || player.capabilities.isCreativeMode
                || player.ridingEntity != null || player.isPlayerSleeping() || nearby(player)) return false;
        long now = age(player.worldObj);
        HauntingPresence presence = HauntingPresence.read(player);
        return presence.clock >= presence.restUntil && now >= HauntingPlayerState.evidenceUntil(player)
                && now >= HauntingWorldState.get(player.worldObj).nextEvidence;
    }
    /**
     * Perceptible beats (heard footsteps, a nearby door, a growling wolf) take the player's slot and may set up a
     * payoff sighting shortly after, leaning toward the cue. Latent ones (a torch or grove changed out of sight)
     * only space other evidence: the player noticed nothing, so their next sighting is not delayed.
     */
    static void evidenceCompleted(EntityPlayerMP player, String kind, boolean perceptible, boolean payoff) {
        long now = age(player.worldObj);
        HauntingPlayerState.evidence(player, kind, now, perceptible);
        HauntingWorldState state = HauntingWorldState.get(player.worldObj); state.nextEvidence = now + (perceptible ? 600 : 200); state.markDirty();
        HauntingStats.evidence(perceptible);
        HauntingPresence.evidence(player, perceptible, payoff);
    }
    public static boolean nearby(EntityPlayerMP player) {
        for (Object object : player.worldObj.getEntitiesWithinAABB(EntityHerobrine.class, player.boundingBox.expand(128, 64, 128)))
            if (!((EntityHerobrine) object).isDead) return true;
        return false;
    }
    public static boolean spawn(EntityPlayerMP player, Sighting kind, boolean forced) {
        if (!enabled() || player.worldObj.isRemote || !player.isEntityAlive() || !forced && !instance.active(player.worldObj)
                || nearby(player) || !HerobrineSkins.bundledAvailable()) return false;
        boolean escalated = HauntingPlayerState.observed(player);
        if (kind == Sighting.NIGHTMARE && !player.isPlayerSleeping()) return false;
        double[] point = kind == Sighting.NIGHTMARE ? HauntingPlacement.bedWatcher(player)
                : HauntingPlacement.find(player, kind, forced, escalated);
        if (point == null) return false;
        boolean natural = !forced && kind != Sighting.NIGHTMARE;
        boolean spawned = spawnAt(player, kind, point[0], point[1], point[2], natural);
        if (spawned && natural && point.length >= 6) {
            EntityHerobrine figure = instance.manifestations.get(player);
            int crossing = player.worldObj.rand.nextInt(3) == 0
                    ? HauntingCover.crossing(player.worldObj, point[0], point[1], point[2], point[3], point[4], (int) point[5], player) : 0;
            figure.emerge(point[3], point[4], crossing > 0 ? crossing : (int) point[5], crossing > 0);
        }
        return spawned;
    }
    public static boolean spawnAt(EntityPlayerMP player, Sighting kind, double x, double y, double z) {
        return spawnAt(player, kind, x, y, z, false);
    }
    private static boolean spawnAt(EntityPlayerMP player, Sighting kind, double x, double y, double z, boolean natural) {
        if (!enabled() || player.worldObj.isRemote || !player.isEntityAlive() || nearby(player) || !HerobrineSkins.bundledAvailable()
                || !HauntingPlacement.deliverable(player, x, z) || !HauntingPlacement.valid(player.worldObj, x, y, z)
                || natural && HauntingPlacement.exposed(player.worldObj, x, y, z)) return false;
        EntityHerobrine figure = new EntityHerobrine(player.worldObj);
        figure.manifest(player, x, y, z, kind, HauntingPlayerState.observed(player));
        figure.presenceDriven(natural);
        boolean result = player.worldObj.spawnEntityInWorld(figure);
        if (result) {
            instance.manifestations.put(player, figure); HauntingPlayerState.begin(player);
            HauntingPresence.published(player, x, y, z); instance.presenceSounds.logout(player);
            HauntingStats.record(kind, HauntingStats.Outcome.PUBLISHED);
        }
        return result;
    }
    public static String debug(EntityPlayerMP player, String name) {
        if (!enabled()) return "Herobrine haunting is disabled. Enable haunting.enableHerobrineHaunting and restart first.";
        if ("stats".equals(name)) return HauntingStats.report();
        if ("invoke".equals(name)) return HauntingInvocation.activate(player) ? "The invitation was accepted." : "Invocation failed.";
        if ("ghost_miner".equals(name)) return GhostMiner.create(player) ? "A sealed gallery was formed beside your mine." : "No safe stone carving path with stable support, protection veto, terrain disabled, or an active manifestation nearby.";
        if ("status".equals(name)) return "Haunting " + (instance.active(player.worldObj) ? "active" : "in grace")
                + "; observation=" + HauntingPlayerState.observed(player) + "; next=" + HauntingPlayerState.next(player)
                + "; profile=" + activity(player.worldObj).label + "; frequency=" + instance.config.getHauntingFrequency()
                + "; activity=" + (activity(player.worldObj).activity * surge(player.worldObj))
                + "; present=" + instance.manifestations.containsKey(player) + "; placement retries=" + HauntingPlayerState.failures(player)
                + "; tracked sites=" + HauntingWorldState.get(player.worldObj).sites.size() + HauntingPresence.describe(player);
        if ("footsteps".equals(name) || "door".equals(name) || "donation".equals(name) || "light".equals(name) || "leafless_grove".equals(name))
            return instance.environment.force(player, name) ? "Started " + name + "." : "No eligible tracked, loaded, unobserved site; environmental/terrain switches and safety still apply.";
        Sighting kind = Sighting.named(name);
        if (kind == null) return "Unknown haunting encounter: " + name;
        return spawn(player, kind, true) ? "Manifested " + name + "." : "No safe loaded position, missing window/bed context, or a manifestation is already nearby.";
    }
    @SubscribeEvent public void worldTick(TickEvent.WorldTickEvent event) {
        if (!config.isHerobrineHauntingEnabled() || event.world.isRemote || event.phase != TickEvent.Phase.END) return;
        environment.tick(event.world);
        if (config.areEnvironmentalHauntingsEnabled()) presenceSounds.tick(event.world);
        invocation.tick(event.world);
        long now = age(event.world);
        if (now % 20 != 0 || !active(event.world)) return;
        HauntingActivity activity = activity(event.world);
        if (!config.areEnvironmentalHauntingsEnabled()) return;
        HauntingWorldState state = HauntingWorldState.get(event.world);
        if (state.nextAudit == 0) { state.nextAudit = now + 6000; state.markDirty(); return; }
        if (now >= state.nextAudit) { state.nextAudit = now + 6000; state.markDirty(); environment.audit(event.world, state, activity); }
    }
    @SubscribeEvent(priority = cpw.mods.fml.common.eventhandler.EventPriority.LOWEST) public void placed(net.minecraftforge.event.world.BlockEvent.PlaceEvent event) {
        if (!config.isHerobrineHauntingEnabled() || event.isCanceled()) return;
        environment.remember(event.world, event.player, event.x, event.y, event.z);
    }
    @SubscribeEvent(priority = cpw.mods.fml.common.eventhandler.EventPriority.LOWEST) public void interacted(net.minecraftforge.event.entity.player.PlayerInteractEvent event) {
        if (!config.isHerobrineHauntingEnabled() || event.world.isRemote || event.isCanceled()
                || event.action != net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        environment.remember(event.world, event.entityPlayer, event.x, event.y, event.z);
        if (active(event.world)) {
            HauntingPresence.working(event.entityPlayer, false);
            net.minecraft.block.Block block = event.world.getBlock(event.x, event.y, event.z);
            if (block == net.minecraft.init.Blocks.bed || block == net.minecraft.init.Blocks.chest)
                HauntingPresence.familiar(event.entityPlayer, event.x, event.y, event.z);
        }
        invocation.interacted(event);
    }
    @SubscribeEvent(priority = cpw.mods.fml.common.eventhandler.EventPriority.LOWEST)
    public void mining(net.minecraftforge.event.world.BlockEvent.BreakEvent event) {
        if (!config.isHerobrineHauntingEnabled() || event.world.isRemote || event.isCanceled() || HauntingSafety.probing() || !active(event.world)
                || !(event.getPlayer() instanceof EntityPlayerMP) || event.getPlayer().capabilities.isCreativeMode) return;
        // Player activity only. The mutation safety probe must not feed back into mining intent.
        if (event.getPlayer().getDistanceSq(event.x + .5, event.y + .5, event.z + .5) <= 6 * 6
                && event.block.getMaterial() == net.minecraft.block.material.Material.rock)
            HauntingPresence.working(event.getPlayer(), true);
    }
    @SubscribeEvent public void unload(net.minecraftforge.event.world.WorldEvent.Unload event) {
        environment.unload(event.world);
        presenceSounds.unload(event.world);
        for (java.util.Iterator<java.util.Map.Entry<EntityPlayerMP, World>> it = sessions.entrySet().iterator(); it.hasNext();)
            if (it.next().getValue() == event.world) it.remove();
        invocation.unload(event.world);
        for (java.util.Iterator<net.minecraft.entity.player.EntityPlayer> it = sleepers.keySet().iterator(); it.hasNext();)
            if (it.next().worldObj == event.world) it.remove();
        for (java.util.Iterator<EntityHerobrine> it = manifestations.values().iterator(); it.hasNext();) if (it.next().worldObj == event.world) it.remove();
    }
    @SubscribeEvent public void logout(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        sleepers.remove(event.player);
        sessions.remove(event.player); presenceSounds.logout(event.player);
        EntityHerobrine figure = manifestations.remove(event.player);
        // Remove ownership before retiring so an already-saved player receives no
        // new deadline or history write from the entity's cleanup callback.
        if (figure != null) figure.setDead();
    }
    private int profile(World world) {
        if (!config.doesHerobrineHaveControl()) return -1;
        World overworld = DimensionManager.getWorld(0);
        World clock = overworld == null ? world : overworld;
        HauntingWorldState state = HauntingWorldState.get(clock);
        long day = clock.getWorldTime() / 24000L;
        if (state.profileDay != day) {
            state.profile = state.profileDay < 0 ? clock.rand.nextInt(4) : (state.profile + 1 + clock.rand.nextInt(3)) % 4;
            state.profileDay = day; state.markDirty();
        }
        return state.profile;
    }
}
