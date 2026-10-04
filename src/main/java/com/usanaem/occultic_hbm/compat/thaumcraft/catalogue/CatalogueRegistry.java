package com.usanaem.occultic_hbm.compat.thaumcraft.catalogue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * Master coordinator for the Occultic HBM Thaumcraft 4 aspect catalogue.
 * Gathers authored aspect compositions across all HBM systems, validates them against
 * registered Thaumcraft aspects, and registers them into ThaumcraftApi.
 */
public final class CatalogueRegistry {

    public static final int WILDCARD = OreDictionary.WILDCARD_VALUE;

    public static final class Entry {
        public final Item item;
        public final int metadata;
        public final AspectAmounts aspects;

        public Entry(Item item, int metadata, AspectAmounts aspects) {
            this.item = item;
            this.metadata = metadata;
            this.aspects = aspects;
        }
    }

    private static final Map<List<?>, Entry> ENTRIES = new LinkedHashMap<List<?>, Entry>();
    private static final Map<String, String> SKIPPED = new LinkedHashMap<String, String>();
    private static boolean built = false;

    private CatalogueRegistry() { }

    public static void register(Item item, int metadata, AspectAmounts aspects) {
        if (item == null || aspects == null || aspects.isEmpty()) {
            return;
        }
        List<?> key = Arrays.asList(item, metadata);
        ENTRIES.put(key, new Entry(item, metadata, aspects));
    }

    public static void registerWildcard(Item item, AspectAmounts aspects) {
        register(item, WILDCARD, aspects);
    }

    public static void register(Block block, int metadata, AspectAmounts aspects) {
        if (block == null) {
            return;
        }
        Item item = Item.getItemFromBlock(block);
        if (item != null) {
            register(item, metadata, aspects);
        }
    }

    public static void markSkipped(String name, String reason) {
        SKIPPED.put(name, reason);
    }

    public static Map<List<?>, Entry> getEntries() {
        return Collections.unmodifiableMap(ENTRIES);
    }

    public static Map<String, String> getSkipped() {
        return Collections.unmodifiableMap(SKIPPED);
    }

    public static synchronized void registerAll(Logger logger) {
        if (!built) {
            built = true;
            logger.info("Building Occultic HBM Thaumcraft aspect catalogue...");
            MaterialCatalogue.register();
            NuclearCatalogue.register();
            MachineCatalogue.register();
            ElectronicsCatalogue.register();
            WeaponCatalogue.register();
            EquipmentCatalogue.register();
            WorldCatalogue.register();
            logger.info("Catalogue built: {} authored object definitions, {} intentionally skipped.",
                    ENTRIES.size(), SKIPPED.size());
        }

        applyToThaumcraft(logger);
    }

    private static void applyToThaumcraft(Logger logger) {
        int registered = 0;
        int validationFailures = 0;
        int registrationFailures = 0;
        int replaced = 0;

        for (Entry entry : ENTRIES.values()) {
            AspectList aspectList = new AspectList();
            boolean valid = true;

            for (Map.Entry<String, Integer> e : entry.aspects.asMap().entrySet()) {
                Aspect aspect = Aspect.getAspect(e.getKey());
                if (aspect == null || e.getValue() <= 0) {
                    valid = false;
                    validationFailures++;
                    logger.warn("Invalid aspect for item {} meta={}: tag '{}' not found or non-positive amount {}.",
                            entry.item.getUnlocalizedName(), entry.metadata, e.getKey(), e.getValue());
                    break;
                }
                aspectList.add(aspect, e.getValue());
            }

            if (!valid || aspectList.size() == 0) {
                continue;
            }

            List<?> key = Arrays.asList(entry.item, entry.metadata);
            AspectList previous = ThaumcraftApi.objectTags.get(key);
            if (previous != null) {
                replaced++;
                logger.debug("Replacing previous aspect list for {} meta={}: {} -> {}",
                        entry.item.getUnlocalizedName(), entry.metadata, describe(previous), describe(aspectList));
            }

            ThaumcraftApi.registerObjectTag(new ItemStack(entry.item, 1, entry.metadata), aspectList);
            AspectList actual = ThaumcraftApi.objectTags.get(key);
            if (actual != null && aspectList.aspects.equals(actual.aspects)) {
                registered++;
            } else {
                registrationFailures++;
                logger.error("Registration failed/not retained for {} meta={}.",
                        entry.item.getUnlocalizedName(), entry.metadata);
            }
        }

        logger.info("Thaumcraft authored aspects applied: registered={}, replaced={}, validationFailures={}, registrationFailures={}, skipped={}.",
                registered, replaced, validationFailures, registrationFailures, SKIPPED.size());
    }

    private static String describe(AspectList aspects) {
        if (aspects == null || aspects.size() == 0) {
            return "[]";
        }
        List<String> list = new ArrayList<String>();
        for (Aspect a : aspects.getAspects()) {
            list.add(a == null ? "null" : a.getTag() + "=" + aspects.getAmount(a));
        }
        Collections.sort(list);
        return list.toString();
    }
}
