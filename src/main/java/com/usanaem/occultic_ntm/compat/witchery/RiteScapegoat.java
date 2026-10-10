package com.usanaem.occultic_ntm.compat.witchery;

import com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle.ActivatedRitual;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.ritual.Sacrifice;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.hbm.extprop.HbmLivingProps;
import com.usanaem.occultic_ntm.radiation.AccumulatedRadiationTransfer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Rite of the Scapegoat: transfers accumulated bodily radiation from the initiating player
 * into a single prepared creature within the ritual circle.
 */
public final class RiteScapegoat extends OcculticRite {

    public static final float ALTAR_POWER = 4000F;
    private final ScapegoatRecipients recipients;
    private final boolean requireRadiantChalk;

    public RiteScapegoat(ScapegoatRecipients recipients) {
        this(recipients, true);
    }

    public RiteScapegoat(ScapegoatRecipients recipients, boolean requireRadiantChalk) {
        super(ALTAR_POWER, 1 /* 1 coven witch */, true /* requires familiar */);
        this.recipients = recipients;
        this.requireRadiantChalk = requireRadiantChalk;
    }

    @Override
    protected String customValidate(World world, int x, int y, int z, ActivatedRitual ritual, EntityPlayer source) {
        if (!recipients.isInside(source, world, x, y, z)) return "occultic_ntm.rite.scapegoat.source_missing";
        if (!(HbmLivingProps.getRadiation(source) > 0)) return "occultic_ntm.rite.scapegoat.no_dose";
        EntityLivingBase recipient = recipients.findUnique(world, x, y, z, source);
        if (recipient == null) return "occultic_ntm.rite.scapegoat.recipient_count";
        if (!AccumulatedRadiationTransfer.canTransfer(source, recipient)) return "occultic_ntm.rite.scapegoat.incompatible";
        return null;
    }

    @Override
    public void addSteps(ArrayList<RitualStep> steps, int initialStage) {
        steps.add(new TransferStep());
    }

    /**
     * Prepend validation to the native offering/power lifecycle with Scapegoat-specific descriptions.
     */
    public Sacrifice guardOfferings(final Sacrifice nativeOfferings) {
        return guardOfferings(nativeOfferings,
                "occultic_ntm.rite.scapegoat.description",
                "occultic_ntm.rite.scapegoat.requirements",
                requireRadiantChalk ? "occultic_ntm.rite.scapegoat.radiant_requirement" : null);
    }

    private final class TransferStep extends RitualStep {
        private boolean committed;
        private int visualTick;
        private double sourceX, sourceY, sourceZ, recipientX, recipientY, recipientZ;

        private TransferStep() {
            super(false);
        }

        @Override
        public Result process(World world, int x, int y, int z, long ticks, ActivatedRitual ritual) {
            if (world.isRemote) return Result.STARTING;
            if (!committed) {
                if (ticks % 20L != 0) return Result.STARTING;
                String error = validatePrerequisites(world, x, y, z, ritual);
                if (error != null) return fail(error, world, ritual);

                EntityPlayer source = ritual.getInitiatingPlayer(world);
                EntityLivingBase recipient = recipients.findUnique(world, x, y, z, source);
                if (!(AccumulatedRadiationTransfer.transfer(source, recipient) > 0)) {
                    return fail("occultic_ntm.rite.scapegoat.incompatible", world, ritual);
                }

                committed = true;
                if (requireRadiantChalk) {
                    com.usanaem.occultic_ntm.anomaly.AnomalyAttention.radiantRitePerformed(source);
                }

                sourceX = source.posX;
                sourceY = source.posY + 1D;
                sourceZ = source.posZ;
                recipientX = recipient.posX;
                recipientY = recipient.posY + recipient.height * 0.5D;
                recipientZ = recipient.posZ;
                SoundEffect.RANDOM_FIZZ.playAt(world, x + 0.5D, y + 0.5D, z + 0.5D);
            }

            // Native particle packets trace source -> heart -> recipient over two seconds.
            // Nothing after the committed transaction can request a ritual refund.
            double fraction = (visualTick % 20) / 19D;
            boolean toCenter = visualTick < 20;
            double fromX = toCenter ? sourceX : x + 0.5D;
            double fromY = toCenter ? sourceY : y + 0.5D;
            double fromZ = toCenter ? sourceZ : z + 0.5D;
            double toX = toCenter ? x + 0.5D : recipientX;
            double toY = toCenter ? y + 0.5D : recipientY;
            double toZ = toCenter ? z + 0.5D : recipientZ;

            ParticleEffect.SPELL.send(SoundEffect.NONE, world,
                    fromX + (toX - fromX) * fraction,
                    fromY + (toY - fromY) * fraction,
                    fromZ + (toZ - fromZ) * fraction,
                    0.1D, 0.1D, 16);

            return ++visualTick >= 40 ? Result.COMPLETED : Result.STARTING;
        }
    }
}
