package com.usanaem.occultic_ntm.haunting;

import java.util.Random;

/** One runtime policy shared by sightings, environmental evidence and companions. */
public enum HauntingActivity {
    // minerOdds is rolled once per opportunity but only after the player's Ghost Miner budget has elapsed.
    NORMAL("normal", 1D, 2, 2, 2, 1, 12, 40, 24, 100, 1, 1, 1, 2),
    DISTANT("distant", 1D, 2, 7, 1, 1, 16, 60, 40, 120, 1, 1, 3, 1),
    DOMESTIC("domestic", .8D, 3, 1, 2, 1, 2, 80, 32, 80, 5, 3, 0, 4),
    SUBTERRANEAN("subterranean", 1.2D, 1, 1, 7, 1, 12, 20, 32, 80, 1, 4, 0, 5),
    QUIET("quiet", .35D, 2, 5, 1, 0, 3, 0, 48, 160, 1, 0, 0, 3);

    public final String label;
    public final double activity;
    public final int windowOdds, minerOdds, companionRange, growlTicks;
    private final int[] sightings, evidence;
    private static final Sighting[] KINDS = {Sighting.STALKING, Sighting.LURKING, Sighting.DWELLING, Sighting.CREEPING};
    HauntingActivity(String label, double activity, int stalking, int lurking, int dwelling, int creeping,
            int windows, int miner, int range, int growl, int door, int light, int grove, int footsteps) {
        this.label = label; this.activity = activity; windowOdds = windows; minerOdds = miner;
        companionRange = range; growlTicks = growl;
        sightings = new int[] {stalking, lurking, dwelling, creeping};
        evidence = new int[] {door, light, grove, footsteps};
    }
    public static HauntingActivity saved(int id) {
        return id < 0 || id > 3 ? NORMAL : values()[id + 1];
    }
    public int evidenceWeight(String kind) {
        return "door".equals(kind) ? evidence[0] : "light".equals(kind) ? evidence[1]
                : "leafless_grove".equals(kind) ? evidence[2] : "footsteps".equals(kind) ? evidence[3] : 0;
    }
    public int evidenceOdds(String kind, double surge) {
        int weight = evidenceWeight(kind);
        if (weight == 0) return 0;
        int base = "leafless_grove".equals(kind) ? 180 : "footsteps".equals(kind) ? 8 : 10;
        return Math.max("leafless_grove".equals(kind) ? 30 : 2, (int) Math.ceil(base / (weight * activity * surge)));
    }
    public Sighting choose(Random random, boolean underground, int previous) { return choose(random, underground, previous, -1); }
    public Sighting choose(Random random, boolean underground, int previous, int beforePrevious) {
        // Route before weighting history: otherwise three separate rolls can all repeat Dwelling.
        int[] weights = new int[KINDS.length];
        for (int i = 0; i < KINDS.length; i++) {
            int routed = underground && i < 2 ? 2 : !underground && i == 2 ? 0 : i;
            weights[routed] += sightings[i] * 4;
        }
        int total = 0;
        for (int i = 0; i < weights.length; i++) {
            if (KINDS[i].ordinal() == previous) weights[i] /= 2;
            if (KINDS[i].ordinal() == beforePrevious) weights[i] = weights[i] * 3 / 4;
            total += weights[i];
        }
        if (total <= 0) return Sighting.STALKING;
        int roll = random.nextInt(total);
        for (int i = 0; i < weights.length; i++) { roll -= weights[i]; if (roll < 0) return KINDS[i]; }
        return Sighting.STALKING;
    }
}
