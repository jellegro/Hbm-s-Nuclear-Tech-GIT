package com.usanaem.occultic_ntm.radiation;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.util.ContaminationUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/** Redistributes existing bodily dose on the server; this is not fresh exposure. */
public final class AccumulatedRadiationTransfer {
    private AccumulatedRadiationTransfer() { }

    public static boolean canTransfer(EntityLivingBase source, EntityLivingBase recipient) {
        return validEntities(source, recipient)
                && resultingRecipientDose(HbmLivingProps.getRadiation(source), HbmLivingProps.getRadiation(recipient)) >= 0;
    }

    /** Returns the exact amount committed, or zero with both doses unchanged. */
    public static double transfer(EntityLivingBase source, EntityLivingBase recipient) {
        if (!validEntities(source, recipient)) return 0;
        float sourceBefore = HbmLivingProps.getRadiation(source);
        float recipientBefore = HbmLivingProps.getRadiation(recipient);
        float recipientAfter = resultingRecipientDose(sourceBefore, recipientBefore);
        if (recipientAfter < 0) return 0;
        double moved = (double) recipientAfter - recipientBefore;
        float sourceAfter = (float) ((double) sourceBefore - moved);
        boolean committed = false;
        try {
            // Explicit setters avoid hazmat attenuation, the increment clamp and
            // incoming-exposure poppets: neither can conserve this existing dose.
            HbmLivingProps.setRadiation(recipient, recipientAfter);
            if (HbmLivingProps.getRadiation(recipient) != recipientAfter) return 0;
            HbmLivingProps.setRadiation(source, sourceAfter);
            committed = HbmLivingProps.getRadiation(source) == sourceAfter
                    && HbmLivingProps.getRadiation(recipient) == recipientAfter;
            return committed ? moved : 0;
        } finally {
            if (!committed) {
                HbmLivingProps.setRadiation(recipient, recipientBefore);
                HbmLivingProps.setRadiation(source, sourceBefore);
            }
        }
    }

    private static boolean validEntities(EntityLivingBase source, EntityLivingBase recipient) {
        return source != null && recipient != null && source != recipient && source.worldObj != null
                && source.worldObj == recipient.worldObj && !source.worldObj.isRemote
                && source.isEntityAlive() && recipient.isEntityAlive() && !ContaminationUtil.isRadImmune(recipient)
                && (!(recipient instanceof EntityPlayer) || !((EntityPlayer) recipient).capabilities.isCreativeMode);
    }

    /** -1 means unsafe/unrepresentable. Any last float quantum stays in the source. */
    private static float resultingRecipientDose(float source, float recipient) {
        if (!(source > 0) || !Float.isFinite(source) || !Float.isFinite(recipient)
                || recipient < 0 || (double) source + recipient > 2500D) return -1;
        double total = (double) source + recipient;
        float after = (float) total;
        if (after > total) after = Math.nextAfter(after, Double.NEGATIVE_INFINITY);
        double moved = (double) after - recipient;
        double remaining = (double) source - moved;
        if (!(moved > 0) || remaining < 0 || (double) (float) remaining != remaining
                || (double) (float) remaining + after != total) return -1;
        return after;
    }
}
