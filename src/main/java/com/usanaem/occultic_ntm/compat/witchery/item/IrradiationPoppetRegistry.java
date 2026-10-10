package com.usanaem.occultic_ntm.compat.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.item.ItemPoppet;
import com.hbm.hazard.HazardData;
import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.items.ModItems;
import com.usanaem.occultic_ntm.OcculticNTM;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import com.usanaem.occultic_ntm.compat.witchery.mixin.accessor.MixinItemPoppetAccessor;
import com.usanaem.occultic_ntm.compat.witchery.mixin.accessor.MixinPoppetTypeInvoker;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;

/** Owns native subtype identity and its registration, recipes and gameplay wiring. */
public final class IrradiationPoppetRegistry {
    private static final int IRRADIATION_ID = 12;
    private static ItemPoppet.PoppetType irradiationType;
    private static boolean attempted;
    private IrradiationPoppetRegistry() { }

    public static synchronized boolean register(IntegrationConfig config, Logger logger) {
        if (attempted) return irradiationType != null;
        attempted = true;
        ArrayList<ItemPoppet.PoppetType> types = ((MixinItemPoppetAccessor) Witchery.Items.POPPET).occultic$getPoppetTypes();
        for (ItemPoppet.PoppetType type : types) {
            if (type != null && type.damageValue == IRRADIATION_ID) {
                logger.error("Native Irradiation Poppet disabled: fixed Witchery subtype 12 is already occupied. No ID remapping performed.");
                return false;
            }
        }
        ItemPoppet.PoppetType type = MixinPoppetTypeInvoker.occultic$create(IRRADIATION_ID, "protectRadiation", "Irradiation Poppet");
        type.setDestroyOnUse(false);
        irradiationType = MixinPoppetTypeInvoker.occultic$register(type, types);
        if (irradiationType == null) return false;
        logger.info("Native Witchery Irradiation Poppet registered once at fixed subtype 12.");

        ItemStack stack = createPoppet();
        OcculticNTM.CREATIVE_TAB.setPoppetStack(stack);
        IrradiationPoppet poppet = new IrradiationPoppet(config, logger, stack);
        HazardData hazards = HazardSystem.stackMap.get(new ComparableStack(stack));
        if (hazards == null) hazards = new HazardData();
        hazards.addEntry(new HazardEntry(HazardRegistry.RADIATION).addMod(poppet));
        HazardSystem.register(stack, hazards);
        MinecraftForge.EVENT_BUS.register(poppet);

        ItemStack base = Witchery.Items.POPPET.unboundPoppet.createStack();
        if (cpw.mods.fml.common.FMLCommonHandler.instance().getSide().isClient()) {
            com.usanaem.occultic_ntm.compat.witchery.client.NativePoppetRenderer.initialize(config, logger);
        }
        if (!config.isIrradiationPoppetEnabled()) return true;
        ItemStack foulFume = Witchery.Items.GENERIC.itemFoulFume.createStack();
        if (Witchery.Items.TAGLOCK_KIT == null || base == null || foulFume == null || ModItems.ingot_lead == null) {
            logger.error("Irradiation Poppet integration unavailable: required crafting/taglock items missing.");
            return true;
        }
        // Standard recipe viewers can inspect the exact shaped ingredients and subtype output.
        GameRegistry.addShapedRecipe(stack,
                " C ", "LPL", " F ",
                'C', new ItemStack(Blocks.coal_block),
                'L', new ItemStack(ModItems.ingot_lead),
                'P', base,
                'F', foulFume);
        RecipeSorter.register(OcculticNTM.MOD_ID + ":bind_irradiation_poppet", IrradiationPoppet.class,
                RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
        GameRegistry.addRecipe(poppet);
        logger.info("Irradiation Poppet enabled on native Witchery subtype 12; finite radiation capacity with native inventory/shelf selection.");
        return true;
    }

    public static ItemPoppet.PoppetType getIrradiationType() { return irradiationType; }

    public static ItemStack createPoppet() { return irradiationType == null ? null : irradiationType.createStack(); }

    public static boolean isIrradiationPoppet(ItemStack stack) {
        return irradiationType != null && stack != null && stack.getItem() == Witchery.Items.POPPET
                && stack.getItemDamage() == IRRADIATION_ID;
    }
}
