package com.usanaem.occultic_ntm.compat.thaumcraft;

import org.apache.logging.log4j.Logger;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.usanaem.occultic_ntm.registry.OcculticBlocks;
import com.usanaem.occultic_ntm.registry.OcculticItems;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

/**
 * Registers The Black Sun (Sol Tenebris) forbidden research branch,
 * Infusion recipes, Warp values, and authored aspect values in Thaumcraft 4.
 */
public final class BlackSunResearch {

    public static final String CATEGORY = "SOL_TENEBRIS";

    public static final String KEY_INVISIBLE_LIGHT = "TC_NTM_INVISIBLE_LIGHT";
    public static final String KEY_BLACK_SUN = "TC_NTM_BLACK_SUN";
    public static final String KEY_ATRAMENTOUS_CATALYSIS = "TC_NTM_ATRAMENTOUS_CATALYSIS";
    public static final String KEY_ABYSSAL_CORE = "TC_NTM_ABYSSAL_CORE";

    private static boolean registered;

    private BlackSunResearch() { }

    public static synchronized void register(Logger logger) {
        if (registered) {
            return;
        }
        registered = true;

        logger.info("Registering Sol Tenebris forbidden research category and entries.");

        // 1. Research Category
        ResourceLocation icon = new ResourceLocation("occultic_ntm", "textures/items/black_sun_icon.png");
        ResourceLocation back = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png");
        ResearchCategories.registerCategory(CATEGORY, icon, back);

        // 2. Aspect Registrations for Fork Content
        registerForkAspects(logger);

        // 3. Infusion Recipes
        ItemStack thaumium = getTcItem("itemResource", 1, new ItemStack(Items.iron_ingot));
        ItemStack voidSeed = getTcItem("itemResource", 16, new ItemStack(Items.ender_pearl));

        ItemStack leadBlock = ModBlocks.block_lead != null ? new ItemStack(ModBlocks.block_lead) : new ItemStack(Blocks.obsidian);
        ItemStack uranium = ModItems.ingot_uranium != null ? new ItemStack(ModItems.ingot_uranium) : new ItemStack(Items.iron_ingot);
        ItemStack plutonium = ModItems.ingot_plutonium != null ? new ItemStack(ModItems.ingot_plutonium) : new ItemStack(Items.gold_ingot);
        ItemStack lead = ModItems.ingot_lead != null ? new ItemStack(ModItems.ingot_lead) : new ItemStack(Items.iron_ingot);

        InfusionRecipe recipeBlackSun = ThaumcraftApi.addInfusionCraftingRecipe(
                KEY_BLACK_SUN,
                new ItemStack(OcculticBlocks.black_sun),
                6,
                new AspectList()
                        .add(OcculticAspects.RADIO, 32)
                        .add(Aspect.VOID, 32)
                        .add(Aspect.METAL, 24)
                        .add(Aspect.ENTROPY, 16)
                        .add(Aspect.DARKNESS, 16),
                leadBlock,
                new ItemStack[] { uranium, plutonium, thaumium, voidSeed }
        );

        ItemStack waste = ModItems.nuclear_waste_long != null ? new ItemStack(ModItems.nuclear_waste_long)
                : (ModItems.waste_uranium != null ? new ItemStack(ModItems.waste_uranium) : new ItemStack(Items.coal));

        InfusionRecipe recipeAtramentous = ThaumcraftApi.addInfusionCraftingRecipe(
                KEY_ATRAMENTOUS_CATALYSIS,
                new ItemStack(OcculticItems.atramentous_ingot),
                5,
                new AspectList()
                        .add(OcculticAspects.RADIO, 16)
                        .add(OcculticAspects.STRONTIO, 12)
                        .add(Aspect.VOID, 16)
                        .add(Aspect.EXCHANGE, 8)
                        .add(Aspect.TAINT, 8),
                waste,
                new ItemStack[] { lead, uranium, thaumium, voidSeed }
        );

        ItemStack fuelInput = ModItems.ingot_uranium_fuel != null ? new ItemStack(ModItems.ingot_uranium_fuel) : uranium;
        ItemStack rtgPellet = ModItems.pellet_rtg != null ? new ItemStack(ModItems.pellet_rtg) : plutonium;

        InfusionRecipe recipeAbyssal = ThaumcraftApi.addInfusionCraftingRecipe(
                KEY_ABYSSAL_CORE,
                new ItemStack(OcculticItems.abyssal_core),
                8,
                new AspectList()
                        .add(OcculticAspects.RADIO, 48)
                        .add(OcculticAspects.STRONTIO, 32)
                        .add(OcculticAspects.NEBRISUM, 16)
                        .add(Aspect.ELDRITCH, 24)
                        .add(Aspect.ENERGY, 32),
                new ItemStack(OcculticItems.atramentous_ingot),
                new ItemStack[] { fuelInput, rtgPellet, voidSeed, new ItemStack(Items.nether_star) }
        );

        // 4. Research Entries & Warp Registrations
        // Entry 1: The Invisible Light
        ResearchItem resLight = new ResearchItem(
                KEY_INVISIBLE_LIGHT, CATEGORY,
                new AspectList()
                        .add(Aspect.ENERGY, 4)
                        .add(Aspect.POISON, 4)
                        .add(OcculticAspects.RADIO, 6)
                        .add(Aspect.DEATH, 3)
                        .add(Aspect.DARKNESS, 3),
                0, 0, 1,
                uranium
        );
        resLight.setRound();
        resLight.setSpecial();
        resLight.setPages(
                new ResearchPage("tc.research_page.TC_NTM_INVISIBLE_LIGHT.1"),
                new ResearchPage("tc.research_page.TC_NTM_INVISIBLE_LIGHT.2")
        );
        resLight.registerResearchItem();
        ThaumcraftApi.addWarpToResearch(KEY_INVISIBLE_LIGHT, 1);

        // Entry 2: Sol Tenebris / The Black Sun
        ResearchItem resBlackSun = new ResearchItem(
                KEY_BLACK_SUN, CATEGORY,
                new AspectList()
                        .add(OcculticAspects.RADIO, 8)
                        .add(Aspect.VOID, 6)
                        .add(Aspect.METAL, 6)
                        .add(Aspect.ENTROPY, 4)
                        .add(Aspect.DARKNESS, 4),
                2, 0, 2,
                new ItemStack(OcculticBlocks.black_sun)
        );
        resBlackSun.setParents(KEY_INVISIBLE_LIGHT);
        resBlackSun.setSpecial();
        resBlackSun.setLost();
        resBlackSun.setPages(
                new ResearchPage("tc.research_page.TC_NTM_BLACK_SUN.1"),
                new ResearchPage("tc.research_page.TC_NTM_BLACK_SUN.2"),
                new ResearchPage(recipeBlackSun)
        );
        resBlackSun.registerResearchItem();
        ThaumcraftApi.addWarpToResearch(KEY_BLACK_SUN, 3);
        ThaumcraftApi.addWarpToItem(new ItemStack(OcculticBlocks.black_sun), 2);

        // Entry 3: Atramentous Catalysis
        ResearchItem resCatalysis = new ResearchItem(
                KEY_ATRAMENTOUS_CATALYSIS, CATEGORY,
                new AspectList()
                        .add(OcculticAspects.RADIO, 6)
                        .add(OcculticAspects.STRONTIO, 6)
                        .add(Aspect.EXCHANGE, 6)
                        .add(Aspect.METAL, 4)
                        .add(Aspect.TAINT, 4),
                4, -1, 2,
                new ItemStack(OcculticItems.atramentous_ingot)
        );
        resCatalysis.setParents(KEY_BLACK_SUN);
        resCatalysis.setSpecial();
        resCatalysis.setLost();
        resCatalysis.setPages(
                new ResearchPage("tc.research_page.TC_NTM_ATRAMENTOUS_CATALYSIS.1"),
                new ResearchPage("tc.research_page.TC_NTM_ATRAMENTOUS_CATALYSIS.2"),
                new ResearchPage(recipeAtramentous)
        );
        resCatalysis.registerResearchItem();
        ThaumcraftApi.addWarpToResearch(KEY_ATRAMENTOUS_CATALYSIS, 3);
        ThaumcraftApi.addWarpToItem(new ItemStack(OcculticItems.atramentous_ingot), 1);

        // Entry 4: The Abyssal Fuel
        ResearchItem resAbyssal = new ResearchItem(
                KEY_ABYSSAL_CORE, CATEGORY,
                new AspectList()
                        .add(OcculticAspects.RADIO, 10)
                        .add(OcculticAspects.STRONTIO, 8)
                        .add(OcculticAspects.NEBRISUM, 6)
                        .add(Aspect.ELDRITCH, 6)
                        .add(Aspect.ENERGY, 8),
                6, 0, 3,
                new ItemStack(OcculticItems.abyssal_core)
        );
        resAbyssal.setParents(KEY_ATRAMENTOUS_CATALYSIS);
        resAbyssal.setSpecial();
        resAbyssal.setLost();
        resAbyssal.setPages(
                new ResearchPage("tc.research_page.TC_NTM_ABYSSAL_CORE.1"),
                new ResearchPage("tc.research_page.TC_NTM_ABYSSAL_CORE.2"),
                new ResearchPage(recipeAbyssal)
        );
        resAbyssal.registerResearchItem();
        ThaumcraftApi.addWarpToResearch(KEY_ABYSSAL_CORE, 4);
        ThaumcraftApi.addWarpToItem(new ItemStack(OcculticItems.abyssal_core), 3);

        logger.info("Sol Tenebris forbidden research branch registered successfully.");
    }

    private static void registerForkAspects(Logger logger) {
        try {
            ThaumcraftApi.registerObjectTag(new ItemStack(OcculticBlocks.black_sun),
                    new AspectList().add(Aspect.VOID, 8).add(Aspect.METAL, 6).add(OcculticAspects.RADIO, 6).add(Aspect.DARKNESS, 4).add(Aspect.ENTROPY, 4));

            ThaumcraftApi.registerObjectTag(new ItemStack(OcculticItems.atramentous_ingot),
                    new AspectList().add(Aspect.METAL, 4).add(OcculticAspects.RADIO, 4).add(Aspect.VOID, 3).add(OcculticAspects.STRONTIO, 2).add(Aspect.TAINT, 2));

            ThaumcraftApi.registerObjectTag(new ItemStack(OcculticItems.abyssal_core),
                    new AspectList().add(OcculticAspects.RADIO, 8).add(OcculticAspects.STRONTIO, 6).add(OcculticAspects.NEBRISUM, 4).add(Aspect.ELDRITCH, 4).add(Aspect.ENERGY, 4));

            ThaumcraftApi.registerObjectTag(new ItemStack(OcculticItems.atramentous_slag),
                    new AspectList().add(Aspect.EARTH, 2).add(Aspect.ENTROPY, 2).add(OcculticAspects.CONTAMINATIO, 2).add(OcculticAspects.RADIO, 1));

            logger.info("Authored Thaumcraft aspects registered for Occultic NTM content.");
        } catch (Exception ex) {
            logger.warn("Could not register object tags for Occultic NTM content: {}", ex.getMessage());
        }
    }

    private static ItemStack getTcItem(String name, int meta, ItemStack fallback) {
        try {
            ItemStack stack = ItemApi.getItem(name, meta);
            if (stack != null) {
                return stack;
            }
        } catch (Exception ignored) { }
        return fallback;
    }
}
