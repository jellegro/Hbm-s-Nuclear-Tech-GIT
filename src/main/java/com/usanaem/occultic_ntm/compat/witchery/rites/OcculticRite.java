package com.usanaem.occultic_ntm.compat.witchery.rites;

import com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle.ActivatedRitual;
import com.emoniph.witchery.common.IPowerSource;
import com.emoniph.witchery.common.PowerSources;
import com.emoniph.witchery.familiar.Familiar;
import com.emoniph.witchery.ritual.Rite;
import com.emoniph.witchery.ritual.RiteRegistry;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.ritual.Sacrifice;
import com.emoniph.witchery.util.Coord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for Occultic NTM Witchery rites.
 * Handles prerequisite preflight checking, sacrifice protection, and altar power queries.
 */
public abstract class OcculticRite extends Rite {

    protected final float requiredPower;
    protected final int minCovenSize;
    protected final boolean requireFamiliar;

    public OcculticRite(float requiredPower, int minCovenSize, boolean requireFamiliar) {
        this.requiredPower = requiredPower;
        this.minCovenSize = minCovenSize;
        this.requireFamiliar = requireFamiliar;
    }

    /**
     * Standard preflight validation executed before native offerings and power are consumed.
     * Returns null if all prerequisites are met, or a localization key / formatted string on failure.
     */
    public String validatePrerequisites(World world, int x, int y, int z, ActivatedRitual ritual) {
        EntityPlayer player = ritual.getInitiatingPlayer(world);
        if (player == null) return "witchery.rite.missingplayer";
        if (requireFamiliar && Familiar.getActiveFamiliarType(player) == Familiar.FAMILIAR_NONE) {
            return "witchery.rite.missingfamiliar";
        }
        if (ritual.covenSize < minCovenSize) {
            return StatCollector.translateToLocalFormatted("witchery.rite.missingcoven", minCovenSize);
        }
        return customValidate(world, x, y, z, ritual, player);
    }

    /**
     * Validates that an altar with sufficient power is available.
     * Checked during PreflightStep before SacrificePower deducts power.
     */
    public String validatePower(World world, int x, int y, int z) {
        if (requiredPower <= 0) return null;
        List<PowerSources.RelativePowerSource> sources = PowerSources.instance().get(world, new Coord(x, y, z), 0);
        if (sources.isEmpty()) return "witchery.rite.missingpowersource";
        IPowerSource power = sources.get(0).source();
        if (power == null || power.isPowerInvalid()) return "witchery.rite.missingpowersource";
        if (!Float.isFinite(power.getCurrentPower()) || power.getCurrentPower() < requiredPower) {
            return "witchery.rite.insufficientpower";
        }
        return null;
    }

    /**
     * Subclasses implement rite-specific validation here (e.g. required living targets, radiation, etc.).
     */
    protected abstract String customValidate(World world, int x, int y, int z, ActivatedRitual ritual, EntityPlayer player);

    /**
     * Sends an error message to the player via native RiteRegistry and returns an aborted refund result.
     */
    public RitualStep.Result fail(String error, World world, ActivatedRitual ritual) {
        RiteRegistry.RiteError(error, ritual.getInitiatingPlayerName(), world);
        return RitualStep.Result.ABORTED_REFUND;
    }

    /**
     * Wraps native offerings with a preflight step so that items and power
     * are never consumed if ritual requirements (familiar, coven, power, targets) fail.
     */
    public Sacrifice guardOfferings(final Sacrifice nativeOfferings, final String... descriptionKeys) {
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
                if (descriptionKeys != null) {
                    for (String key : descriptionKeys) {
                        if (key != null && !key.isEmpty()) {
                            description.append('\n').append(StatCollector.translateToLocal(key));
                        }
                    }
                }
            }
        };
    }

    /**
     * Preflight validation step executed at the beginning of ritual execution.
     */
    protected class PreflightStep extends RitualStep {
        public PreflightStep() { super(false); }

        @Override
        public Result process(World world, int x, int y, int z, long ticks, ActivatedRitual ritual) {
            if (world.isRemote || ticks % 20L != 0) return Result.STARTING;
            String error = validatePrerequisites(world, x, y, z, ritual);
            if (error != null) {
                return fail(error, world, ritual);
            }
            error = validatePower(world, x, y, z);
            if (error != null) {
                return fail(error, world, ritual);
            }
            return Result.COMPLETED;
        }
    }
}
