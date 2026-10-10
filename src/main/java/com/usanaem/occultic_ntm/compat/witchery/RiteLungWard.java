package com.usanaem.occultic_ntm.compat.witchery;

import com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle.ActivatedRitual;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.ritual.Sacrifice;
import com.hbm.blocks.ModBlocks;
import com.hbm.extprop.HbmLivingProps;
import com.usanaem.occultic_ntm.entity.EntityTarborn;
import com.usanaem.occultic_ntm.registry.OcculticBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;

/**
 * Rite of the Lung Ward: purges pulmonary soot, coal dust, and asbestos fibers
 * from the practitioner. Gas mask filters remain untouched.
 *
 * Purged contaminants risk condensing into a hostile Particulate Spectre,
 * with the apparition's likelihood scaling with contamination severity.
 */
public final class RiteLungWard extends OcculticRite {

    public static final float ALTAR_POWER = 500F;

    public RiteLungWard() {
        super(ALTAR_POWER, 0 /* 0 coven */, false /* no familiar */);
    }

    @Override
    protected String customValidate(World world, int x, int y, int z, ActivatedRitual ritual, EntityPlayer player) {
        double distSq = player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D);
        if (distSq > 20.0D) {
            return "occultic_ntm.rite.lung_ward.player_not_in_circle";
        }

        int blackLung = HbmLivingProps.getBlackLung(player);
        int asbestos = HbmLivingProps.getAsbestos(player);
        if (blackLung <= 0 && asbestos <= 0) {
            return "occultic_ntm.rite.lung_ward.clean";
        }

        return null;
    }

    @Override
    public void addSteps(ArrayList<RitualStep> steps, int initialStage) {
        steps.add(new LungWardStep());
    }

    public Sacrifice guardOfferings(final Sacrifice nativeOfferings) {
        return guardOfferings(nativeOfferings,
                "occultic_ntm.rite.lung_ward.description",
                "occultic_ntm.rite.lung_ward.requirements");
    }

    private final class LungWardStep extends RitualStep {
        private boolean executed;
        private int tickCount;

        private LungWardStep() {
            super(false);
        }

        @Override
        public Result process(World world, int x, int y, int z, long ticks, ActivatedRitual ritual) {
            if (world.isRemote) return Result.STARTING;

            // Gathering phase (ticks 0..19): subtle breathing and gentle smoke gathering
            if (tickCount < 20) {
                if (tickCount == 5 || tickCount == 15) {
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "mob.blaze.breathe", 0.5F, 1.5F);
                }
                if (world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) world;
                    ws.func_147487_a("smoke", x + 0.5D, y + 0.2D, z + 0.5D, 6, 1.5D, 0.2D, 1.5D, 0.01D);
                }
                tickCount++;
                return Result.STARTING;
            }

            if (!executed) {
                executed = true;

                EntityPlayer player = ritual.getInitiatingPlayer(world);
                if (player == null) {
                    return fail("witchery.rite.missingplayer", world, ritual);
                }

                int blackLung = HbmLivingProps.getBlackLung(player);
                int asbestos = HbmLivingProps.getAsbestos(player);

                if (blackLung <= 0 && asbestos <= 0) {
                    return fail("occultic_ntm.rite.lung_ward.clean", world, ritual);
                }

                // Compute relative contamination fraction
                double blFraction = (double) blackLung / (double) HbmLivingProps.maxBlacklung;
                double asbFraction = (double) asbestos / (double) HbmLivingProps.maxAsbestos;
                double totalSeverity = Math.min(1.0D, blFraction + asbFraction);

                // Purge bodily contamination completely
                HbmLivingProps.setBlackLung(player, 0);
                HbmLivingProps.setAsbestos(player, 0);

                // Sweep away any lingering airborne pulmonary gas blocks within the ritual area
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dz = -3; dz <= 3; dz++) {
                        for (int dy = 0; dy <= 3; dy++) {
                            Block b = world.getBlock(x + dx, y + dy, z + dz);
                            if (b == ModBlocks.gas_asbestos || b == ModBlocks.gas_coal || b == ModBlocks.gas_monoxide
                                    || b == ModBlocks.gas_radon || b == ModBlocks.gas_radon_dense || b == ModBlocks.gas_radon_tomb
                                    || (OcculticBlocks.tar_trail != null && b == OcculticBlocks.tar_trail)) {
                                world.setBlockToAir(x + dx, y + dy, z + dz);
                            }
                        }
                    }
                }

                // Gas masks and filter items are intentionally untouched.

                // Expulsion sound effects
                world.playSoundEffect(player.posX, player.posY + 1.0D, player.posZ, "hbm:player.cough", 1.0F, 1.0F);
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.fizz", 1.0F, 0.8F);

                // Expulsion visual effects around practitioner
                if (world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) world;
                    ws.func_147487_a("largesmoke", player.posX, player.posY + 1.0D, player.posZ, 25, 0.4D, 0.6D, 0.4D, 0.05D);
                    ws.func_147487_a("spell", player.posX, player.posY + 1.0D, player.posZ, 15, 0.5D, 0.5D, 0.5D, 0.02D);
                }

                // Chance of manifestation scales with purged contamination (base 40%, up to 100%)
                double spawnChance = 0.40D + (totalSeverity * 0.60D);

                if (world.rand.nextDouble() < spawnChance) {
                    EntityTarborn spectre = new EntityTarborn(world);
                    double sx = (player.posX + (x + 0.5D)) * 0.5D;
                    double sy = y + 0.1D;
                    double sz = (player.posZ + (z + 0.5D)) * 0.5D;

                    spectre.setLocationAndAngles(sx, sy, sz, world.rand.nextFloat() * 360.0F, 0.0F);
                    world.spawnEntityInWorld(spectre);
                    spectre.setAttackTarget(player);

                    world.playSoundEffect(sx, sy + 1.0D, sz, "mob.blaze.breathe", 1.0F, 0.6F);
                    if (world instanceof WorldServer) {
                        WorldServer ws = (WorldServer) world;
                        ws.func_147487_a("largesmoke", sx, sy + 1.0D, sz, 30, 0.5D, 0.8D, 0.5D, 0.05D);
                        ws.func_147487_a("flame", sx, sy + 0.5D, sz, 8, 0.3D, 0.3D, 0.3D, 0.02D);
                    }
                }
            }

            if (++tickCount >= 30) {
                return Result.COMPLETED;
            }
            return Result.STARTING;
        }
    }
}
