package com.usanaem.occultic_ntm.compat.witchery.rites;

import com.usanaem.occultic_ntm.config.IntegrationConfig;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Exact registry-name policy, with no superclass or nearest-entity fallback. */
public final class ScapegoatRecipients {
    public static final double RADIUS = 4D;
    private final Set<String> entityIds = new HashSet<String>();
    private final boolean allowPlayers;

    public ScapegoatRecipients(IntegrationConfig config, Logger logger) {
        allowPlayers = config.areScapegoatPlayerRecipientsAllowed();
        for (String configuredId : config.getScapegoatRecipientEntityIds()) {
            String id = configuredId.trim();
            if (id.isEmpty()) continue;
            if (!EntityList.stringToClassMapping.containsKey(id)) {
                logger.warn("Rite of the Scapegoat: unknown recipient entity registry name '{}'; no fallback.", id);
            }
            entityIds.add(id);
        }
    }

    public boolean isAllowed(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) return allowPlayers;
        String id = EntityList.getEntityString(entity);
        return id != null && entityIds.contains(id);
    }

    public boolean isInside(EntityLivingBase entity, World world, int x, int y, int z) {
        if (entity == null || entity.worldObj != world || !entity.isEntityAlive()) return false;
        double dx = entity.posX - (x + 0.5D);
        double dz = entity.posZ - (z + 0.5D);
        return dx * dx + dz * dz <= RADIUS * RADIUS && entity.posY >= y && entity.posY < y + 3D;
    }

    /** null covers both no recipient and ambiguity; incompatible allowed mobs count. */
    public EntityLivingBase findUnique(World world, int x, int y, int z, EntityPlayer source) {
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(x + 0.5D - RADIUS, y, z + 0.5D - RADIUS,
                x + 0.5D + RADIUS, y + 3D, z + 0.5D + RADIUS);
        @SuppressWarnings("unchecked")
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        EntityLivingBase result = null;
        for (EntityLivingBase entity : entities) {
            if (entity == source || !isAllowed(entity) || !isInside(entity, world, x, y, z)) continue;
            if (result != null) return null;
            result = entity;
        }
        return result;
    }
}
