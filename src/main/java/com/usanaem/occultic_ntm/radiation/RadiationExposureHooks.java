package com.usanaem.occultic_ntm.radiation;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

/** Generic bridge; explicit setters and radiation removal never call this. */
public final class RadiationExposureHooks {
    private RadiationExposureHooks() { }

    public static float apply(EntityLivingBase entity, float previous, float proposed) {
        if (entity.worldObj == null || entity.worldObj.isRemote || !(proposed > previous)
                || Float.isNaN(previous) || Float.isInfinite(previous)
                || Float.isNaN(proposed) || Float.isInfinite(proposed)) return proposed;
        RadiationExposureEvent event = new RadiationExposureEvent(entity, previous, proposed);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResultingRadiation();
    }
}
