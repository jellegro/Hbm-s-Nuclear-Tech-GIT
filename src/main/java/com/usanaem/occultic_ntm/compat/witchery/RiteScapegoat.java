package com.usanaem.occultic_ntm.compat.witchery;

import com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle.ActivatedRitual;
import com.emoniph.witchery.common.IPowerSource;
import com.emoniph.witchery.common.PowerSources;
import com.emoniph.witchery.familiar.Familiar;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RiteRegistry;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.ritual.Sacrifice;
import com.emoniph.witchery.util.Coord;
import com.emoniph.witchery.util.ParticleEffect;
import com.emoniph.witchery.util.SoundEffect;
import com.hbm.extprop.HbmLivingProps;
import com.usanaem.occultic_ntm.radiation.AccumulatedRadiationTransfer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/** Native Witchery steps and sacrifices; no parallel ritual scheduler. */
public final class RiteScapegoat extends Rite {
    public static final float ALTAR_POWER = 4000F;
    private final ScapegoatRecipients recipients;

    public RiteScapegoat(ScapegoatRecipients recipients) { this.recipients = recipients; }

    @Override
    public void addSteps(ArrayList<RitualStep> steps, int initialStage) {
        steps.add(new TransferStep());
    }

    /** Prepend validation to the native offering/power lifecycle, including Grasspers/refunds. */
    public Sacrifice guardOfferings(final Sacrifice nativeOfferings) {
        return new Sacrifice() {
            @Override
            public boolean isMatch(World world, int x, int y, int z, int maxDistance,
                    ArrayList<Entity> entities, ArrayList<ItemStack> grassperStacks) {
                return nativeOfferings.isMatch(world, x, y, z, maxDistance, entities, grassperStacks);
            }

            @Override
            public void addSteps(ArrayList<RitualStep> steps, AxisAlignedBB bounds, int maxDistance) {
                steps.add(new PreflightStep());
                nativeOfferings.addSteps(steps, bounds, maxDistance);
            }

            @Override
            public void addDescription(StringBuffer description) {
                nativeOfferings.addDescription(description);
                description.append('\n').append(StatCollector.translateToLocal("occultic_ntm.rite.scapegoat.description"));
                description.append('\n').append(StatCollector.translateToLocal("occultic_ntm.rite.scapegoat.requirements"));
            }
        };
    }

	/* 
	* Ritual requires a familiar and at the minimum 1 coven witch
	* */
    private String validate(World world, int x, int y, int z, ActivatedRitual ritual) {
        EntityPlayer source = ritual.getInitiatingPlayer(world);
        if (!recipients.isInside(source, world, x, y, z)) return "occultic_ntm.rite.scapegoat.source_missing";
        if (Familiar.getActiveFamiliarType(source) == Familiar.FAMILIAR_NONE) return "witchery.rite.missingfamiliar";
        if (ritual.covenSize < 1) return StatCollector.translateToLocalFormatted("witchery.rite.missingcoven", 1);
        if (!(HbmLivingProps.getRadiation(source) > 0)) return "occultic_ntm.rite.scapegoat.no_dose";
        EntityLivingBase recipient = recipients.findUnique(world, x, y, z, source);
        if (recipient == null) return "occultic_ntm.rite.scapegoat.recipient_count";
        if (!AccumulatedRadiationTransfer.canTransfer(source, recipient)) return "occultic_ntm.rite.scapegoat.incompatible";
        return null;
    }

    private RitualStep.Result fail(String error, World world, ActivatedRitual ritual) {
        RiteRegistry.RiteError(error, ritual.getInitiatingPlayerName(), world);
        return RitualStep.Result.ABORTED_REFUND;
    }

    private final class PreflightStep extends RitualStep {
        private PreflightStep() { super(false); }

        @Override
        public Result process(World world, int x, int y, int z, long ticks, ActivatedRitual ritual) {
            if (world.isRemote || ticks % 20L != 0) return Result.STARTING;
            String error = validate(world, x, y, z, ritual);
            if (error != null) return fail(error, world, ritual);
            // Same native lookup as the permitted addon example; no altar scan or power deduction here.
            List<PowerSources.RelativePowerSource> sources = PowerSources.instance().get(world, new Coord(x, y, z), 0);
            if (sources.isEmpty()) return fail("witchery.rite.missingpowersource", world, ritual);
            IPowerSource power = sources.get(0).source();
            if (power == null || power.isPowerInvalid()) return fail("witchery.rite.missingpowersource", world, ritual);
            if (!Float.isFinite(power.getCurrentPower()) || power.getCurrentPower() < ALTAR_POWER)
                return fail("witchery.rite.insufficientpower", world, ritual);
            return Result.COMPLETED;
        }
    }

    private final class TransferStep extends RitualStep {
        private boolean committed;
        private int visualTick;
        private double sourceX, sourceY, sourceZ, recipientX, recipientY, recipientZ;
        private TransferStep() { super(false); }

        @Override
        public Result process(World world, int x, int y, int z, long ticks, ActivatedRitual ritual) {
            if (world.isRemote) return Result.STARTING;
            if (!committed) {
                if (ticks % 20L != 0) return Result.STARTING;
                String error = validate(world, x, y, z, ritual);
                if (error != null) return fail(error, world, ritual);
                EntityPlayer source = ritual.getInitiatingPlayer(world);
                EntityLivingBase recipient = recipients.findUnique(world, x, y, z, source);
                if (!(AccumulatedRadiationTransfer.transfer(source, recipient) > 0))
                    return fail("occultic_ntm.rite.scapegoat.incompatible", world, ritual);
                committed = true;
                sourceX = source.posX; sourceY = source.posY + 1D; sourceZ = source.posZ;
                recipientX = recipient.posX; recipientY = recipient.posY + recipient.height * 0.5D; recipientZ = recipient.posZ;
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
                    fromX + (toX - fromX) * fraction, fromY + (toY - fromY) * fraction,
                    fromZ + (toZ - fromZ) * fraction, 0.1D, 0.1D, 16);
            return ++visualTick >= 40 ? Result.COMPLETED : Result.STARTING;
        }
    }
}
