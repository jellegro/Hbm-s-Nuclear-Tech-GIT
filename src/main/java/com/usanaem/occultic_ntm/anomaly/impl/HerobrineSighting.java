package com.usanaem.occultic_ntm.anomaly.impl;

import com.usanaem.occultic_ntm.anomaly.Anomaly;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import com.usanaem.occultic_ntm.haunting.HauntingDirector;
import com.usanaem.occultic_ntm.haunting.Sighting;
import net.minecraft.entity.player.EntityPlayerMP;

/** Legacy manual bridge only; the Haunting Director owns all scheduling and state. */
public final class HerobrineSighting implements Anomaly {
    public String id() { return "herobrine_sighting"; }
    public Ownership ownership() { return Ownership.SERVER; }
    public boolean enabled(IntegrationConfig config) { return false; }
    public boolean eligible(EntityPlayerMP player) { return false; }
    public int minimumAttention() { return 0; }
    public int weight() { return 1; }
    public int chanceDenominator(EntityPlayerMP player, IntegrationConfig config) { return 1; }
    public long cooldownTicks(EntityPlayerMP player, IntegrationConfig config) { return 0; }
    public boolean execute(EntityPlayerMP player, boolean forced) { return HauntingDirector.spawn(player, Sighting.STALKING, forced); }
}
