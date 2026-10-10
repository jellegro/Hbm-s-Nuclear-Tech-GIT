package com.usanaem.occultic_ntm.haunting;

import java.util.EnumMap;
import java.util.Map;

/**
 * Session-only encounter outcome counters so pacing and "does anyone ever notice this" can be
 * tuned from evidence. Never saved, never networked; read through {@code /anomalies herobrine stats}.
 */
public final class HauntingStats {
    public enum Outcome { PUBLISHED, ARRIVED, NOTICED, TURNED, SLIPPED, RETREATED, APPROACHED, CROSSED, PURSUED, EXPIRED_UNSEEN, DISSIPATED, PLACEMENT_FAILED, STALE }
    private static final Map<Sighting, int[]> COUNTS = new EnumMap<Sighting, int[]>(Sighting.class);
    private static int cues, latent, perceptible, trails, digging;
    private HauntingStats() { }
    public static synchronized void record(Sighting kind, Outcome outcome) {
        int[] row = COUNTS.get(kind);
        if (row == null) { row = new int[Outcome.values().length]; COUNTS.put(kind, row); }
        row[outcome.ordinal()]++;
    }
    public static synchronized void evidence(boolean wasPerceptible) { if (wasPerceptible) perceptible++; else latent++; }
    public static synchronized void cue() { cues++; }
    public static synchronized void presenceSound(boolean mining) { if (mining) digging++; else trails++; }
    public static synchronized void reset() { COUNTS.clear(); cues = latent = perceptible = trails = digging = 0; }
    public static synchronized int count(Sighting kind, Outcome outcome) {
        int[] row = COUNTS.get(kind);
        return row == null ? 0 : row[outcome.ordinal()];
    }
    public static synchronized String report() {
        StringBuilder out = new StringBuilder("Herobrine stats (this session)");
        for (Map.Entry<Sighting, int[]> entry : COUNTS.entrySet()) {
            out.append("\n").append(entry.getKey().name().toLowerCase()).append(':');
            for (Outcome outcome : Outcome.values()) if (entry.getValue()[outcome.ordinal()] > 0)
                out.append(' ').append(outcome.name().toLowerCase()).append('=').append(entry.getValue()[outcome.ordinal()]);
        }
        return out.append("\nevidence: perceptible=").append(perceptible).append(" latent=").append(latent)
                .append(" cues=").append(cues).append(" reactive_trails=").append(trails).append(" mining_echoes=").append(digging).toString();
    }
}
