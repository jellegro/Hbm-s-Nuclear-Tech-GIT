package com.usanaem.occultic_ntm.anomaly;

import com.usanaem.occultic_ntm.config.IntegrationConfig;
import net.minecraft.entity.player.EntityPlayerMP;

/** One flat descriptor; execution may use entities, packets, blocks or none of these. */
public interface Anomaly {
    enum Ownership { CLIENT, SERVER, MIXED }
    String id();
    Ownership ownership();
    boolean enabled(IntegrationConfig config);
    boolean eligible(EntityPlayerMP player);
    int minimumAttention();
    int chanceDenominator(EntityPlayerMP player, IntegrationConfig config);
    int weight();
    long cooldownTicks(EntityPlayerMP player, IntegrationConfig config);
    boolean execute(EntityPlayerMP player, boolean forced);
}
