package com.usanaem.occultic_ntm.haunting;

/** Authored encounters, never ordinary mob spawning. Distances are block units. */
public enum Sighting {
    STALKING(25, 46, 600, 64), LURKING(50, 100, 600, 128),
    DWELLING(6, 12, 500, 48), CREEPING(3, 5, 300, 20),
    WINDOW(8, 24, 900, 48), NIGHTMARE(2, 4, 600, 24),
    INVOCATION(4, 8, 80, 32), GHOST_MINER(8, 12, 600, 48);
    public final int minDistance, maxDistance, lifetime, tether;
    Sighting(int min, int max, int life, int tether) {
        minDistance = min; maxDistance = max; lifetime = life; this.tether = tether;
    }
    public static Sighting named(String name) {
        for (Sighting sighting : values()) if (sighting.name().equalsIgnoreCase(name)) return sighting;
        return null;
    }
}
