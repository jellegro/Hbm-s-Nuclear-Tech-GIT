package com.usanaem.occultic_ntm.registry;

import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.usanaem.occultic_ntm.OcculticNTM;
import com.usanaem.occultic_ntm.compat.thaumcraft.item.ItemAbyssalCore;
import com.usanaem.occultic_ntm.compat.thaumcraft.item.ItemAtramentousIngot;
import com.usanaem.occultic_ntm.compat.thaumcraft.item.ItemAtramentousSlag;
import com.usanaem.occultic_ntm.compat.witchery.item.ItemTarbornEgg;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public final class OcculticItems {

    public static ItemAtramentousIngot atramentous_ingot;
    public static ItemAbyssalCore abyssal_core;
    public static ItemAtramentousSlag atramentous_slag;
    public static ItemTarbornEgg spawn_egg_tarborn;

    private OcculticItems() { }

    public static void init() {
        atramentous_ingot = new ItemAtramentousIngot();
        abyssal_core = new ItemAbyssalCore();
        atramentous_slag = new ItemAtramentousSlag();
        spawn_egg_tarborn = new ItemTarbornEgg();

        GameRegistry.registerItem(atramentous_ingot, "ingot_atramentous");
        GameRegistry.registerItem(abyssal_core, "core_abyssal");
        GameRegistry.registerItem(atramentous_slag, "slag_atramentous");
        GameRegistry.registerItem(spawn_egg_tarborn, "spawn_egg_tarborn");

        BlockDispenser.dispenseBehaviorRegistry.putObject(spawn_egg_tarborn, new BehaviorDefaultDispenseItem() {
            @Override
            public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                EnumFacing facing = BlockDispenser.func_149937_b(source.getBlockMetadata());
                double x = source.getX() + (double) facing.getFrontOffsetX();
                double y = (double) ((float) source.getYInt() + 0.2F);
                double z = source.getZ() + (double) facing.getFrontOffsetZ();
                Entity entity = ItemTarbornEgg.spawnSpectre(source.getWorld(), x, y, z);
                if (entity instanceof EntityLiving && stack.hasDisplayName()) {
                    ((EntityLiving) entity).setCustomNameTag(stack.getDisplayName());
                }
                stack.splitStack(1);
                return stack;
            }
        });

        // Use original fork item as icon for Occultic NTM creative tab
        OcculticNTM.CREATIVE_TAB.setForkIcon(new ItemStack(atramentous_ingot));
    }

    public static void registerHazards() {
        // Register radioactivity in HBM's HazardSystem
        HazardSystem.register(atramentous_ingot, new HazardData().addEntry(new HazardTypeRadiation(), 2.5F));
        HazardSystem.register(abyssal_core, new HazardData().addEntry(new HazardTypeRadiation(), 25.0F));
        HazardSystem.register(atramentous_slag, new HazardData().addEntry(new HazardTypeRadiation(), 0.5F));
    }
}
