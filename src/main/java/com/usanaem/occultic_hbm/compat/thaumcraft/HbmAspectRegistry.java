package com.usanaem.occultic_hbm.compat.thaumcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.hbm.items.ModItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/** Deliberate fork definitions; no inference or OreDictionary-wide registration. */
public final class HbmAspectRegistry {

    private HbmAspectRegistry() { }

	// Very simple aspects for now...
    static void registerAuthoredAspects(Logger logger) {
        Definition[] definitions = {
                new Definition("ingot_lead", ModItems.ingot_lead, 0,
                        new AspectList().add(Aspect.METAL, 2)),
                new Definition("ingot_uranium", ModItems.ingot_uranium, 0,
                        new AspectList().add(Aspect.METAL, 2).add(Aspect.ENERGY, 2).add(Aspect.POISON, 1)),
                new Definition("ingot_plutonium", ModItems.ingot_plutonium, 0,
                        new AspectList().add(Aspect.METAL, 2).add(Aspect.ENERGY, 3).add(Aspect.POISON, 2)
                                .add(Aspect.DEATH, 1))
        };

        boolean[] valid = new boolean[definitions.length];
        int validationFailures = 0;
        // Validate the entire small authored set before performing any writes.
        for (int i = 0; i < definitions.length; i++) {
            valid[i] = isValid(definitions[i]);
            if (!valid[i]) {
                validationFailures++;
                logger.error("Invalid authored aspects for {} meta={}: item must exist and aspects must be nonempty and positive.",
                        definitions[i].name, definitions[i].metadata);
            }
        }

        int registered = 0;
        int registrationFailures = 0;
        try {
            for (int i = 0; i < definitions.length; i++) {
                if (!valid[i]) {
                    continue;
                }
                Definition definition = definitions[i];
                List<?> key = Arrays.asList(definition.item, definition.metadata);
                AspectList previous = ThaumcraftApi.objectTags.get(key);
                if (previous != null) {
                    logger.info("Replacing earlier exact aspects for {} meta={}: {} -> {}",
                            definition.name, definition.metadata, describe(previous), describe(definition.aspects));
                }

                // Use the public method; leave unrelated wildcard/grouped registrations untouched.
                ThaumcraftApi.registerObjectTag(new ItemStack(definition.item, 1, definition.metadata), definition.aspects);
                AspectList actual = ThaumcraftApi.objectTags.get(key);
                if (actual != null && definition.aspects.aspects.equals(actual.aspects)) {
                    registered++;
                } else {
                    registrationFailures++;
                    logger.error("Exact authored aspect registration was not retained for {} meta={}.",
                            definition.name, definition.metadata);
                }
            }
        } finally {
            // A later API/linkage failure should not undo earlier successful public writes.
            logger.info("Thaumcraft authored aspects: registered={}, validation failures={}, registration failures={}.",
                    registered, validationFailures, registrationFailures);
        }
    }

    private static boolean isValid(Definition definition) {
        if (definition.item == null || definition.aspects == null || definition.aspects.size() == 0) {
            return false;
        }
        for (Aspect aspect : definition.aspects.getAspects()) {
            if (aspect == null || definition.aspects.getAmount(aspect) <= 0) {
                return false;
            }
        }
        return true;
    }

    private static String describe(AspectList aspects) {
        if (aspects.size() == 0) {
            return "[]";
        }
        List<String> entries = new ArrayList<>();
        for (Aspect aspect : aspects.getAspects()) {
            entries.add(aspect == null ? "invalid aspect" : aspect.getTag() + "=" + aspects.getAmount(aspect));
        }
        Collections.sort(entries);
        return entries.toString();
    }

    private static final class Definition {
        private final String name;
        private final Item item;
        private final int metadata;
        private final AspectList aspects;

        private Definition(String name, Item item, int metadata, AspectList aspects) {
            this.name = name;
            this.item = item;
            this.metadata = metadata;
            this.aspects = aspects;
        }
    }
}
