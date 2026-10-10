package com.usanaem.occultic_ntm.compat.witchery;

import com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle.ActivatedRitual;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.ritual.Sacrifice;
import com.hbm.inventory.recipes.ShredderRecipes;
import com.hbm.items.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Rite of the Kinetic Pestle: harnesses radiant resonance and explosive shock
 * to pulverize raw ores into refined powders without manual hammer smashing.
 */
public final class RiteKineticPestle extends OcculticRite {

    public static final float ALTAR_POWER = 500F;
    public static final int MAX_BATCH_SIZE = 32;

    public RiteKineticPestle() {
        super(ALTAR_POWER, 0 /* 0 coven */, false /* no familiar */);
    }

    public static boolean isCrushable(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids) {
            String name = OreDictionary.getOreName(id);
            if (name.startsWith("ore") || name.startsWith("denseore") || name.startsWith("cluster") || name.startsWith("chunkOre")
                    || name.startsWith("ingot")) {
                ItemStack result = ShredderRecipes.getShredderResult(stack);
                return result != null && result.getItem() != null && result.getItem() != ModItems.scrap;
            }
        }
        return false;
    }

    @Override
    protected String customValidate(World world, int x, int y, int z, ActivatedRitual ritual, EntityPlayer player) {
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(x - 3.5D, y, z - 3.5D, x + 4.5D, y + 2.5D, z + 4.5D);
        @SuppressWarnings("unchecked")
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, area);
        for (EntityItem itemEntity : items) {
            if (!itemEntity.isDead && isCrushable(itemEntity.getEntityItem())) {
                return null;
            }
        }
        return "occultic_ntm.rite.kinetic_pestle.no_ores";
    }

    @Override
    public void addSteps(ArrayList<RitualStep> steps, int initialStage) {
        steps.add(new KineticPestleStep());
    }

    public Sacrifice guardOfferings(final Sacrifice nativeOfferings) {
        return guardOfferings(nativeOfferings,
                "occultic_ntm.rite.kinetic_pestle.description",
                "occultic_ntm.rite.kinetic_pestle.requirements");
    }

    private final class KineticPestleStep extends RitualStep {
        private boolean executed;
        private int tickCount;

        private KineticPestleStep() {
            super(false);
        }

        @Override
        public Result process(World world, int x, int y, int z, long ticks, ActivatedRitual ritual) {
            if (world.isRemote) return Result.STARTING;

            // Gathering phase (ticks 0..19): subtle sonic clicks and dust
            if (tickCount < 20) {
                if (tickCount % 5 == 0) {
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.click", 1.0F, 1.2F);
                }
                if (world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) world;
                    ws.func_147487_a("smoke", x + 0.5D, y + 0.2D, z + 0.5D, 4, 1.5D, 0.1D, 1.5D, 0.0D);
                }
                tickCount++;
                return Result.STARTING;
            }

            if (!executed) {
                executed = true;

                AxisAlignedBB area = AxisAlignedBB.getBoundingBox(x - 3.5D, y, z - 3.5D, x + 4.5D, y + 2.5D, z + 4.5D);
                @SuppressWarnings("unchecked")
                List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, area);

                int quota = MAX_BATCH_SIZE;
                int totalCrushed = 0;
                List<ItemStack> outputs = new ArrayList<ItemStack>();

                for (EntityItem itemEntity : items) {
                    if (itemEntity.isDead || quota <= 0) continue;
                    ItemStack stack = itemEntity.getEntityItem();
                    if (!isCrushable(stack)) continue;

                    int count = Math.min(stack.stackSize, quota);
                    ItemStack baseResult = ShredderRecipes.getShredderResult(stack);
                    if (baseResult != null && baseResult.getItem() != null && baseResult.getItem() != ModItems.scrap) {
                        ItemStack result = baseResult.copy();
                        result.stackSize = baseResult.stackSize * count;
                        outputs.add(result);

                        totalCrushed += count;
                        quota -= count;

                        stack.stackSize -= count;
                        if (stack.stackSize <= 0) {
                            itemEntity.setDead();
                        } else {
                            itemEntity.setEntityItemStack(stack);
                        }
                    }
                }

                if (totalCrushed == 0) {
                    return fail("occultic_ntm.rite.kinetic_pestle.no_ores", world, ritual);
                }

                // Byproduct: 1x sulfur and 1x powder_ash per 4 crushed ores
                int byproducts = Math.max(1, totalCrushed / 4);
                outputs.add(new ItemStack(ModItems.sulfur, byproducts));
                outputs.add(new ItemStack(ModItems.powder_ash, byproducts));

                // Spawn output items popping out of the blast
                for (ItemStack out : outputs) {
                    EntityItem spawned = new EntityItem(world, x + 0.5D, y + 0.6D, z + 0.5D, out);
                    spawned.motionX = (world.rand.nextDouble() - 0.5D) * 0.15D;
                    spawned.motionY = 0.25D + world.rand.nextDouble() * 0.1D;
                    spawned.motionZ = (world.rand.nextDouble() - 0.5D) * 0.15D;
                    spawned.delayBeforeCanPickup = 10;
                    world.spawnEntityInWorld(spawned);
                }

                // Authentic non-destructive vanilla explosion: isSmoking=false (NO blocks broken!), isFlaming=false
                world.newExplosion(null, x + 0.5D, y + 0.8D, z + 0.5D, 0.0F, false, false);

                // Sound effects: explosion + heavy metallic anvil crash
                world.playSoundEffect(x + 0.5D, y + 0.8D, z + 0.5D, "random.explode", 4.0F, 0.85F + world.rand.nextFloat() * 0.2F);
                world.playSoundEffect(x + 0.5D, y + 0.8D, z + 0.5D, "random.anvil_land", 1.2F, 1.1F);

                // Visual particle packets broadcast to tracking clients
                if (world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) world;
                    ws.func_147487_a("hugeexplosion", x + 0.5D, y + 0.8D, z + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                    ws.func_147487_a("smoke", x + 0.5D, y + 0.8D, z + 0.5D, 40, 0.6D, 0.4D, 0.6D, 0.05D);
                    ws.func_147487_a("crit", x + 0.5D, y + 0.8D, z + 0.5D, 20, 0.5D, 0.5D, 0.5D, 0.2D);
                }

                // Non-lethal acoustic shockwave knockback to nearby living entities (up to 5 blocks)
                @SuppressWarnings("unchecked")
                List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class,
                        AxisAlignedBB.getBoundingBox(x - 4.5D, y - 1.0D, z - 4.5D, x + 5.5D, y + 3.0D, z + 5.5D));
                for (EntityLivingBase entity : living) {
                    double dx = entity.posX - (x + 0.5D);
                    double dz = entity.posZ - (z + 0.5D);
                    double distSq = dx * dx + dz * dz;
                    if (distSq > 0.01D && distSq < 25.0D) {
                        double dist = Math.sqrt(distSq);
                        entity.motionX += (dx / dist) * 0.35D;
                        entity.motionZ += (dz / dist) * 0.35D;
                        entity.motionY += 0.15D;
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
