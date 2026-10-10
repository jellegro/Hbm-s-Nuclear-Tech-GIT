package com.usanaem.occultic_ntm.haunting;

import com.usanaem.occultic_ntm.config.IntegrationConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/** Saved stalker intentions outlive their temporary body. No distant entity or chunk tickets.
 * Internal clocks measure eligible online ticks; nextEncounter remains the public world deadline. */
public final class HauntingPresence {
    public enum Intent { SHADOW, APPROACH, INTERCEPT, INTRUDE, WITHDRAW }
    public enum Context { SURFACE, TRAVEL, SHELTER, MINE }
    Intent intent = Intent.SHADOW;
    Context context = Context.SURFACE;
    long clock, cueAt, intentUntil, restUntil, lastNotice, lastBeat, workingUntil, occupiedUntil, remainingFigure, anchorUntil;
    int pressure, discoveries, misses, chain, vigilance, dimension, familiarDimension;
    boolean initialized, positioned, anchored, familiar;
    double x, y, z, yaw, headingX, headingZ = 1, distance, anchorX, anchorY, anchorZ, bearingX, bearingZ;
    double familiarX, familiarY, familiarZ;
    private HauntingPresence() { }
    static HauntingPresence read(EntityPlayer player) {
        NBTTagCompound n = HauntingPlayerState.presence(player);
        HauntingPresence p = new HauntingPresence(); p.initialized = n.getInteger("version") == 1;
        if (!p.initialized) return p;
        p.intent = Intent.values()[bound(n.getInteger("intent"), Intent.values().length - 1)];
        p.context = Context.values()[bound(n.getInteger("context"), Context.values().length - 1)];
        p.clock = time(n, "clock"); p.cueAt = time(n, "cueAt"); p.intentUntil = time(n, "intentUntil");
        p.restUntil = time(n, "restUntil"); p.lastNotice = time(n, "lastNotice"); p.lastBeat = time(n, "lastBeat");
        p.workingUntil = time(n, "workingUntil"); p.occupiedUntil = time(n, "occupiedUntil"); p.anchorUntil = time(n, "anchorUntil");
        p.remainingFigure = Math.min(3456000L, time(n, "remainingFigure"));
        p.pressure = bound(n.getInteger("pressure"), 100); p.discoveries = bound(n.getInteger("discoveries"), 10000);
        p.misses = bound(n.getInteger("misses"), 12); p.chain = bound(n.getInteger("chain"), 8); p.vigilance = bound(n.getInteger("vigilance"), 12);
        p.dimension = n.getInteger("dimension"); p.positioned = n.getBoolean("positioned"); p.anchored = n.getBoolean("anchored");
        p.x = number(n, "x"); p.y = number(n, "y"); p.z = number(n, "z"); p.yaw = number(n, "yaw");
        p.headingX = number(n, "headingX"); p.headingZ = number(n, "headingZ"); p.distance = Math.max(0, number(n, "distance"));
        p.anchorX = number(n, "anchorX"); p.anchorY = number(n, "anchorY"); p.anchorZ = number(n, "anchorZ");
        p.bearingX = number(n, "bearingX"); p.bearingZ = number(n, "bearingZ");
        p.familiar = n.getBoolean("familiar"); p.familiarDimension = n.getInteger("familiarDimension");
        p.familiarX = number(n, "familiarX"); p.familiarY = number(n, "familiarY"); p.familiarZ = number(n, "familiarZ"); return p;
    }
    private static int bound(int v, int max) { return Math.max(0, Math.min(max, v)); }
    private static long time(NBTTagCompound n, String key) { return Math.max(0, Math.min(Long.MAX_VALUE / 4, n.getLong(key))); }
    private static double number(NBTTagCompound n, String key) {
        double v = n.getDouble(key); return Double.isNaN(v) || Double.isInfinite(v) ? 0 : Math.max(-30000000, Math.min(30000000, v));
    }
    void save(EntityPlayer player) {
        NBTTagCompound n = new NBTTagCompound(); n.setInteger("version", 1);
        n.setInteger("intent", intent.ordinal()); n.setInteger("context", context.ordinal());
        n.setLong("clock", clock); n.setLong("cueAt", cueAt); n.setLong("intentUntil", intentUntil); n.setLong("restUntil", restUntil);
        n.setLong("lastNotice", lastNotice); n.setLong("lastBeat", lastBeat); n.setLong("workingUntil", workingUntil);
        n.setLong("occupiedUntil", occupiedUntil); n.setLong("remainingFigure", remainingFigure); n.setLong("anchorUntil", anchorUntil);
        n.setInteger("pressure", pressure); n.setInteger("discoveries", discoveries); n.setInteger("misses", misses);
        n.setInteger("chain", chain); n.setInteger("vigilance", vigilance); n.setInteger("dimension", dimension);
        n.setBoolean("positioned", positioned); n.setBoolean("anchored", anchored);
        n.setDouble("x", x); n.setDouble("y", y); n.setDouble("z", z); n.setDouble("yaw", yaw);
        n.setDouble("headingX", headingX); n.setDouble("headingZ", headingZ); n.setDouble("distance", distance);
        n.setDouble("anchorX", anchorX); n.setDouble("anchorY", anchorY); n.setDouble("anchorZ", anchorZ);
        n.setDouble("bearingX", bearingX); n.setDouble("bearingZ", bearingZ);
        n.setBoolean("familiar", familiar); n.setInteger("familiarDimension", familiarDimension);
        n.setDouble("familiarX", familiarX); n.setDouble("familiarY", familiarY); n.setDouble("familiarZ", familiarZ);
        HauntingPlayerState.presence(player, n);
    }
    void initialize(EntityPlayerMP player, long pace) {
        if (initialized) return;
        initialized = true; dimension = player.dimension;
        cueAt = 100 + player.worldObj.rand.nextInt((int) Math.max(100, Math.min(1200, pace / 2)));
        intentUntil = 400 + player.worldObj.rand.nextInt(800);
        long next = HauntingPlayerState.next(player), now = HauntingDirector.age(player.worldObj);
        if (next == 0 || !HauntingPlayerState.currentCadence(player)) HauntingPlayerState.schedule(player, now + pace);
        remainingFigure = Math.max(0, HauntingPlayerState.next(player) - now);
    }
    /** A reconnect or portal never catches up offline time or becomes a synthetic high-speed chase. */
    void sample(EntityPlayerMP player, long pace, boolean resumed) {
        boolean existing = initialized; initialize(player, pace);
        long now = HauntingDirector.age(player.worldObj);
        if (existing && resumed && HauntingPlayerState.next(player) != 0)
            HauntingPlayerState.defer(player, now + Math.max(100, remainingFigure));
        else if (existing && resumed) HauntingPlayerState.schedule(player, now + Math.max(200, Math.min(1200, pace)));
        clock += 20;
        if (player.openContainer != player.inventoryContainer) occupiedUntil = clock + 80;
        double dx = player.posX - x, dz = player.posZ - z, moved = Math.sqrt(dx * dx + dz * dz);
        boolean relocated = !positioned || dimension != player.dimension || moved > 48 || Math.abs(player.posY - y) > 24;
        if (relocated || resumed) {
            if (relocated) {
                anchored = false;
                headingX = -Math.sin(Math.toRadians(player.rotationYaw)); headingZ = Math.cos(Math.toRadians(player.rotationYaw));
            }
            distance = 0; workingUntil = occupiedUntil = 0;
            if (intent != Intent.WITHDRAW) intentUntil = clock;
        } else {
            distance = distance * .7 + moved;
            if (moved > .7) { headingX = dx / moved; headingZ = dz / moved; }
            double turned = Math.abs(net.minecraft.util.MathHelper.wrapAngleTo180_float((float) (player.rotationYaw - yaw)));
            if (turned > 65) vigilance = Math.min(12, vigilance + 2);
            else if (clock % 100 == 0) vigilance = Math.max(0, vigilance - 1);
        }
        x = player.posX; y = player.posY; z = player.posZ; yaw = player.rotationYaw; dimension = player.dimension; positioned = true;
        boolean home = familiar && familiarDimension == dimension && player.getDistanceSq(familiarX, familiarY, familiarZ) < 32 * 32;
        boolean underground = HauntingPlacement.underground(player);
        Context next = workingUntil > clock && underground ? Context.MINE
                : HauntingPlacement.roofed(player) || home && underground ? Context.SHELTER
                : underground ? Context.MINE : distance > 7 ? Context.TRAVEL : Context.SURFACE;
        if (next != context) { context = next; if (intent != Intent.WITHDRAW) intentUntil = clock; }
        if (clock > restUntil && clock - lastBeat > 200) pressure = Math.min(100, pressure + 1);
        if (clock >= intentUntil) { intent = chooseIntent(player); intentUntil = clock + 300 + player.worldObj.rand.nextInt(901); }
        if (anchored && (clock > anchorUntil || player.getDistanceSq(anchorX, anchorY, anchorZ) > 96 * 96)) anchored = false;
        remainingFigure = Math.max(0, HauntingPlayerState.next(player) - now); save(player);
    }
    private Intent chooseIntent(EntityPlayerMP player) {
        if (clock < restUntil) return Intent.WITHDRAW;
        if (context == Context.SHELTER) return Intent.INTRUDE;
        if (context == Context.TRAVEL) return Intent.INTERCEPT;
        if (workingUntil > clock || occupiedUntil > clock || pressure > 55 || misses > 1) return Intent.APPROACH;
        return player.worldObj.rand.nextBoolean() ? Intent.SHADOW : Intent.APPROACH;
    }
    /** Cue and figure clocks are independent: audible trails cannot cancel an overdue manifestation. */
    void act(EntityPlayerMP player, IntegrationConfig config, EnvironmentalHauntings environment, HauntingPresenceSounds sounds, long pace) {
        long now = HauntingDirector.age(player.worldObj);
        if (HauntingDirector.nearby(player)) {
            HauntingPlayerState.defer(player, Math.max(HauntingPlayerState.next(player), now + 200));
            remainingFigure = Math.max(0, HauntingPlayerState.next(player) - now); save(player); return;
        }
        if (clock < restUntil) return;
        long next = HauntingPlayerState.next(player); boolean figureDue = next == 0 || now >= next;
        if (now < HauntingPlayerState.evidenceUntil(player)) {
            if (figureDue) HauntingPlayerState.defer(player, Math.min(HauntingPlayerState.evidenceUntil(player), now + 200));
            return;
        }
        HauntingActivity activity = HauntingDirector.activity(player.worldObj);
        if (!figureDue && clock >= cueAt && config.areEnvironmentalHauntingsEnabled() && HauntingDirector.evidenceAllowed(player)) {
            boolean domestic = context == Context.SHELTER && player.worldObj.rand.nextInt(3) == 0 && environment.presenceDoor(player);
            if (!domestic) sounds.start(player, this);
            HauntingPresence current = read(player); current.cueAt = clock + cueDelay(player, pace); current.save(player); return;
        }
        if (!figureDue) return;
        Sighting kind = select(player, activity);
        if (HauntingDirector.terrainEnabled() && context == Context.MINE && workingUntil > clock && activity.minerOdds > 0
                && now >= HauntingPlayerState.nextMiner(player) && player.worldObj.rand.nextInt(activity.minerOdds) == 0) {
            boolean formed = GhostMiner.create(player); HauntingPlayerState.minerBudget(player, now + (formed ? 24000L : 3000L));
            if (formed) return;
        }
        if (HauntingDirector.spawn(player, kind, false)) return;
        if (kind == Sighting.WINDOW && HauntingDirector.spawn(player,
                activity == HauntingActivity.QUIET ? Sighting.DWELLING : Sighting.CREEPING, false)) return;
        if (kind == Sighting.LURKING && HauntingDirector.spawn(player, Sighting.STALKING, false)) return;
        if (kind == Sighting.DWELLING && HauntingDirector.spawn(player,
                activity == HauntingActivity.QUIET ? Sighting.WINDOW : Sighting.CREEPING, false)) return;
        HauntingStats.record(kind, HauntingStats.Outcome.PLACEMENT_FAILED);
        HauntingPlayerState.retry(player, now + Math.min(1200, 100L << Math.min(4, HauntingPlayerState.failures(player))));
        remainingFigure = Math.max(0, HauntingPlayerState.next(player) - now);
        if (clock >= cueAt && config.areEnvironmentalHauntingsEnabled() && HauntingDirector.evidenceAllowed(player)) {
            save(player); sounds.start(player, this);
            HauntingPresence current = read(player); current.cueAt = clock + cueDelay(player, pace); current.save(player);
        } else save(player);
    }
    Sighting select(EntityPlayerMP player, HauntingActivity activity) {
        boolean quiet = activity == HauntingActivity.QUIET;
        int last = HauntingPlayerState.lastKind(player);
        if (context == Context.SHELTER) return misses % 3 == 0 && last != Sighting.WINDOW.ordinal() ? Sighting.WINDOW
                : quiet ? Sighting.DWELLING : Sighting.CREEPING;
        if (context == Context.MINE) return !quiet && (misses > 1 || last == Sighting.DWELLING.ordinal()
                && player.worldObj.rand.nextInt(3) == 0) ? Sighting.CREEPING : Sighting.DWELLING;
        if (misses >= 2 && !quiet) return misses % 2 == 0 ? Sighting.CREEPING : Sighting.STALKING;
        if (vigilance > 5 || quiet || activity == HauntingActivity.DISTANT) return Sighting.LURKING;
        if (intent == Intent.INTERCEPT) return last == Sighting.STALKING.ordinal() && player.worldObj.rand.nextBoolean()
                ? Sighting.LURKING : Sighting.STALKING;
        return activity.choose(player.worldObj.rand, false, HauntingPlayerState.lastKind(player), HauntingPlayerState.prevKind(player));
    }
    private long cueDelay(EntityPlayerMP player, long pace) {
        long base = Math.max(300, Math.min(2400, pace / 2));
        return base / 2 + player.worldObj.rand.nextInt((int) base) + (vigilance > 5 ? 200 : 0);
    }
    static void cue(EntityPlayer player, double ax, double ay, double az) {
        HauntingPresence p = read(player);
        // A forced encounter can precede natural activation. Keep its cue without starting an offline clock.
        p.initialized = true; p.dimension = player.dimension;
        p.anchorX = ax; p.anchorY = ay; p.anchorZ = az;
        double dx = ax - player.posX, dz = az - player.posZ, len = Math.sqrt(dx * dx + dz * dz);
        if (len > .01) { p.bearingX = dx / len; p.bearingZ = dz / len; }
        p.anchored = true; p.anchorUntil = p.clock + 2400; p.save(player);
    }
    static void heard(EntityPlayerMP player, long quietTicks) {
        HauntingPresence p = read(player); p.lastBeat = p.clock; p.pressure = Math.max(0, p.pressure - 8); p.save(player);
        HauntingPlayerState.evidence(player, "presence", HauntingDirector.age(player.worldObj), true);
        HauntingPlayerState.quietUntil(player, HauntingDirector.age(player.worldObj) + quietTicks);
    }
    static void noticed(EntityPlayerMP player, EntityHerobrine figure) {
        HauntingPresence p = read(player); p.lastNotice = p.lastBeat = p.clock; p.discoveries = Math.min(10000, p.discoveries + 1);
        p.pressure = Math.max(0, p.pressure - 35); p.misses = 0; p.save(player); cue(player, figure.posX, figure.posY, figure.posZ);
    }
    static void retired(EntityPlayerMP player, EntityHerobrine figure, boolean arrived, long pace) {
        HauntingPresence p = read(player); p.initialize(player, pace); long delay;
        if (!arrived) {
            delay = Math.min(1200, 100L << Math.min(4, HauntingPlayerState.failures(player)));
            HauntingPlayerState.retry(player, HauntingDirector.age(player.worldObj) + delay);
        } else if (!figure.noticed()) {
            p.misses = Math.min(12, p.misses + 1); p.pressure = Math.min(100, p.pressure + 18);
            delay = Math.max(100, Math.min(600, pace / (2 + p.misses)));
            HauntingPlayerState.schedule(player, HauntingDirector.age(player.worldObj) + delay);
            p.intentUntil = p.clock; p.cueAt = Math.min(p.cueAt, p.clock + 100);
        } else {
            p.chain++; boolean relief = p.chain >= 2 + player.worldObj.rand.nextInt(3);
            long pause = relief ? (long) (pace * (1.25 + player.worldObj.rand.nextDouble()))
                    : Math.max(240, Math.min(1200, pace / 3 + player.worldObj.rand.nextInt(301)));
            if (figure.pursued()) { p.vigilance = Math.min(12, p.vigilance + 3); pause += 200; }
            p.restUntil = p.clock + pause; p.intent = Intent.WITHDRAW; p.intentUntil = p.restUntil;
            if (relief) { p.chain = 0; p.pressure = 0; }
            delay = pause + Math.max(160, Math.min(600, pace / 4)); p.cueAt = p.restUntil + 40 + player.worldObj.rand.nextInt(201);
            HauntingPlayerState.schedule(player, HauntingDirector.age(player.worldObj) + delay);
        }
        p.remainingFigure = delay; p.save(player); cue(player, figure.posX, figure.posY, figure.posZ);
    }
    static void published(EntityPlayerMP player, double px, double py, double pz) {
        HauntingPresence p = read(player); p.remainingFigure = 0; p.save(player); cue(player, px, py, pz);
    }
    static void evidence(EntityPlayerMP player, boolean perceptible, boolean payoff) {
        if (!perceptible) return;
        HauntingPresence p = read(player); p.lastBeat = p.clock; p.pressure = Math.max(0, p.pressure - 8); p.cueAt = Math.max(p.cueAt, p.clock + 600);
        long now = HauntingDirector.age(player.worldObj), next = HauntingPlayerState.next(player);
        if (payoff && next > now + 640) HauntingPlayerState.defer(player, now + 640);
        p.remainingFigure = Math.max(0, HauntingPlayerState.next(player) - now); p.save(player);
    }
    static void working(EntityPlayer player, boolean mining) {
        HauntingPresence p = read(player); if (!p.initialized) return;
        if (mining) p.workingUntil = p.clock + 60; else p.occupiedUntil = p.clock + 200;
        if (p.intent != Intent.WITHDRAW) p.intentUntil = p.clock; p.save(player);
    }
    static void familiar(EntityPlayer player, int fx, int fy, int fz) {
        HauntingPresence p = read(player); if (!p.initialized) return;
        p.familiar = true; p.familiarDimension = player.dimension; p.familiarX = fx + .5; p.familiarY = fy; p.familiarZ = fz + .5; p.save(player);
    }
    double[] cue(EntityPlayer player) {
        if (!initialized || !anchored || dimension != player.dimension || clock > anchorUntil
                || player.getDistanceSq(anchorX, anchorY, anchorZ) > 96 * 96) return null;
        return new double[] {anchorX, anchorZ};
    }
    double score(EntityPlayer player, double px, double py, double pz) {
        double dx = px - player.posX, dz = pz - player.posZ, len = Math.sqrt(dx * dx + dz * dz); if (len < .01) return 0;
        double score = intent == Intent.INTERCEPT ? (dx * headingX + dz * headingZ) / len * 16 : 0;
        if (anchored && clock < anchorUntil) {
            score += (dx * bearingX + dz * bearingZ) / len * 10;
            double moved = Math.sqrt((px - anchorX) * (px - anchorX) + (pz - anchorZ) * (pz - anchorZ));
            if (clock - lastNotice < 1200 && moved < 6) score -= 20;
        }
        if (intent == Intent.APPROACH) score -= len * .15; return score;
    }
    public static String describe(EntityPlayer player) {
        HauntingPresence p = read(player);
        return "; intent=" + p.intent.name().toLowerCase(java.util.Locale.ROOT) + "; context=" + p.context.name().toLowerCase(java.util.Locale.ROOT)
                + "; pressure=" + p.pressure + "; discoveries=" + p.discoveries + "; missed=" + p.misses + "; linked=" + p.chain
                + "; online=" + p.clock / 20 + "s; relief=" + Math.max(0, p.restUntil - p.clock) / 20 + "s";
    }
}
