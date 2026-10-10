package com.usanaem.occultic_ntm.haunting;

/** Authored encounters, never ordinary mob spawning. Distances are block units. */
public enum Sighting {
    // cone = look-vector dot product needed to count as directly observed. Smaller is wider:
    // Creeping is a wide peripheral catch, Lurking needs a centred, deliberate look.
    STALKING(25, 46, 600, 64, .78), LURKING(50, 100, 600, 128, .84),
    DWELLING(6, 12, 500, 48, .76), CREEPING(3, 5, 300, 20, .70),
    WINDOW(8, 24, 900, 48, .82), NIGHTMARE(2, 4, 600, 24, .82),
    INVOCATION(4, 8, 80, 32, .82), GHOST_MINER(8, 12, 600, 48, .82);
    public final int minDistance, maxDistance, lifetime, tether;
    public final double cone;
    Sighting(int min, int max, int life, int tether, double cone) {
        minDistance = min; maxDistance = max; lifetime = life; this.tether = tether; this.cone = cone;
    }
    /** Open-air figures that look elsewhere until noticed and may walk off instead of vanishing. */
    public boolean wandersOff() { return this == STALKING || this == LURKING || this == DWELLING; }
    public static Sighting named(String name) {
        for (Sighting sighting : values()) if (sighting.name().equalsIgnoreCase(name)) return sighting;
        return null;
    }
}
