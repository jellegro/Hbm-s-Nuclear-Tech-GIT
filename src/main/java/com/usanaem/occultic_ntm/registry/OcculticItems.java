package com.usanaem.occultic_ntm.registry;

import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.usanaem.occultic_ntm.OcculticNTM;
import com.usanaem.occultic_ntm.item.ItemAbyssalCore;
import com.usanaem.occultic_ntm.item.ItemAtramentousIngot;
import com.usanaem.occultic_ntm.item.ItemAtramentousSlag;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public final class OcculticItems {

    public static ItemAtramentousIngot atramentous_ingot;
    public static ItemAbyssalCore abyssal_core;
    public static ItemAtramentousSlag atramentous_slag;

    private OcculticItems() { }

    public static void init() {
        atramentous_ingot = new ItemAtramentousIngot();
        abyssal_core = new ItemAbyssalCore();
        atramentous_slag = new ItemAtramentousSlag();

        GameRegistry.registerItem(atramentous_ingot, "ingot_atramentous");
        GameRegistry.registerItem(abyssal_core, "core_abyssal");
        GameRegistry.registerItem(atramentous_slag, "slag_atramentous");

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
