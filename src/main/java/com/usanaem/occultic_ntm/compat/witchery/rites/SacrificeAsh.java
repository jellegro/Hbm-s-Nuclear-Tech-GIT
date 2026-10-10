package com.usanaem.occultic_ntm.compat.witchery.rites;

import com.emoniph.witchery.blocks.BlockCircle.TileEntityCircle.ActivatedRitual;
import com.emoniph.witchery.ritual.RitualStep;
import com.emoniph.witchery.ritual.Sacrifice;
import com.hbm.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;

/**
 * Sacrifice requirement that accepts any form of ash:
 * - HBM NTM powder_ash with any metadata (Wood Ash, Coal Ash, Misc Ash, etc.)
 * - Witchery Wood Ash
 * - Any item registered under OreDictionary "dustAsh"
 */
public final class SacrificeAsh extends Sacrifice {

    public static boolean isAsh(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        if (stack.getItem() == ModItems.powder_ash) return true;
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids) {
            String name = OreDictionary.getOreName(id);
            if ("dustAsh".equals(name) || "ash".equals(name)) return true;
        }
        return false;
    }

    @Override
    public boolean isMatch(World world, int x, int y, int z, int maxDistance,
                           ArrayList<Entity> entities, ArrayList<ItemStack> grassperStacks) {
        for (Entity entity : entities) {
            if (entity instanceof EntityItem && !entity.isDead) {
                EntityItem itemEntity = (EntityItem) entity;
                if (isAsh(itemEntity.getEntityItem())) {
                    return true;
                }
            }
        }
        for (ItemStack stack : grassperStacks) {
            if (isAsh(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addSteps(ArrayList<RitualStep> steps, AxisAlignedBB bounds, int maxDistance) {
        steps.add(new ConsumeAshStep(bounds));
    }

    @Override
    public void addDescription(StringBuffer sb) {
        sb.append(StatCollector.translateToLocal("occultic_ntm.rite.kinetic_pestle.ash_offering"));
    }

    private static final class ConsumeAshStep extends RitualStep {
        private final AxisAlignedBB bounds;

        ConsumeAshStep(AxisAlignedBB bounds) {
            super(false);
            this.bounds = bounds;
        }

        @Override
        public Result process(World world, int x, int y, int z, long ticks, ActivatedRitual ritual) {
            if (world.isRemote) return Result.COMPLETED;

            @SuppressWarnings("unchecked")
            ArrayList<EntityItem> items = (ArrayList<EntityItem>) world.getEntitiesWithinAABB(EntityItem.class, bounds);
            for (EntityItem itemEntity : items) {
                if (!itemEntity.isDead && isAsh(itemEntity.getEntityItem())) {
                    ItemStack stack = itemEntity.getEntityItem();
                    stack.stackSize--;
                    if (stack.stackSize <= 0) {
                        itemEntity.setDead();
                    } else {
                        itemEntity.setEntityItemStack(stack);
                    }
                    world.playSoundEffect(itemEntity.posX, itemEntity.posY, itemEntity.posZ, "random.pop", 0.2F, 1.0F);
                    return Result.COMPLETED;
                }
            }
            return Result.COMPLETED;
        }
    }
}
