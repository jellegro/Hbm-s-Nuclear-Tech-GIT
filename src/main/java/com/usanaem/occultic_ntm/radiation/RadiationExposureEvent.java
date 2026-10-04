package com.usanaem.occultic_ntm.radiation;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.EntityLivingBase;

/** A finite positive, already clamped HBM dose increase on the logical server.
 * Not cancelable: redirects explicitly account for the representable decrease.
 */
public final class RadiationExposureEvent extends Event {

    public final EntityLivingBase entity;
    private final float previousRadiation;
    private final float originalRadiation;
    private float resultingRadiation;

    public RadiationExposureEvent(EntityLivingBase entity, float previousRadiation, float originalRadiation) {
        this.entity = entity;
        this.previousRadiation = previousRadiation;
        this.originalRadiation = originalRadiation;
        this.resultingRadiation = originalRadiation;
    }

    public double getOriginalExposure() {
        return (double) originalRadiation - previousRadiation;
    }

    public double getRemainingExposure() {
        return (double) resultingRadiation - previousRadiation;
    }

    public float getResultingRadiation() {
        return resultingRadiation;
    }

    /** Returns the actual decrease, rounded toward retaining dose on the entity.
     * Callers must store this return value, rather than the requested amount.
     */
    public double redirect(double requested) {
        if (!(requested > 0) || Double.isInfinite(requested) || Double.isNaN(requested)) return 0;
        requested = Math.min(requested, getRemainingExposure());
        float next = Math.max(previousRadiation, (float) (resultingRadiation - requested));
        if ((double) resultingRadiation - next > requested) next = Math.nextUp(next);
        if (next >= resultingRadiation) return 0;
        double transferred = (double) resultingRadiation - next;
        resultingRadiation = next;
        return transferred;
    }
}
