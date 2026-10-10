package com.usanaem.occultic_ntm.compat.thaumcraft.aspect;

import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

/**
 * Custom Thaumcraft aspects defined by Occultic NTM.
 * Registered early during FMLInitializationEvent to guarantee presence in aspect trees and caches
 * (such as Thaumcraft Research Tweaks).
 */
public final class OcculticAspects {

    public static Aspect RADIO;
    public static Aspect ELECTRUM;
    public static Aspect MAGNETO;
    public static Aspect DETONATIO;
    public static Aspect CHEMICA;
    public static Aspect STRONTIO;
    public static Aspect CONTAMINATIO;
    public static Aspect NEBRISUM;

    private static boolean initialized;

    private OcculticAspects() { }

    public static synchronized void init(Logger logger) {
        if (initialized) {
            return;
        }
        initialized = true;

        // Tier 1 Custom Aspects (made from vanilla TC4 aspects)
        RADIO = getOrRegister("radio", 0x97ff00,
                new Aspect[] { Aspect.ENERGY, Aspect.POISON },
                new ResourceLocation("occultic_ntm", "textures/aspects/radio.png"), 1, logger);

        ELECTRUM = getOrRegister("electrum", 0xe0e830,
                new Aspect[] { Aspect.ENERGY, Aspect.MECHANISM },
                new ResourceLocation("occultic_ntm", "textures/aspects/electrum.png"), 1, logger);

        MAGNETO = getOrRegister("magneto", 0xa0b0d0,
                new Aspect[] { Aspect.METAL, Aspect.MOTION },
                new ResourceLocation("occultic_ntm", "textures/aspects/magneto.png"), 1, logger);

        DETONATIO = getOrRegister("detonatio", 0xd03010,
                new Aspect[] { Aspect.FIRE, Aspect.ENTROPY },
                new ResourceLocation("occultic_ntm", "textures/aspects/detonatio.png"), 1, logger);

        CHEMICA = getOrRegister("chemica", 0x209860,
                new Aspect[] { Aspect.EXCHANGE, Aspect.WATER },
                new ResourceLocation("occultic_ntm", "textures/aspects/chemica.png"), 1, logger);

        // Tier 2 Custom Aspects (made from tier 1 custom aspects)
        STRONTIO = getOrRegister("strontio", 0x902090,
                new Aspect[] { RADIO, Aspect.ENTROPY },
                new ResourceLocation("occultic_ntm", "textures/aspects/strontio.png"), 1, logger);

        CONTAMINATIO = getOrRegister("contaminatio", 0x485820,
                new Aspect[] { RADIO, Aspect.TAINT },
                new ResourceLocation("occultic_ntm", "textures/aspects/contaminatio.png"), 1, logger);

        NEBRISUM = getOrRegister("nebrisum", 0x20d0d0,
                new Aspect[] { Aspect.ELDRITCH, RADIO },
                new ResourceLocation("occultic_ntm", "textures/aspects/nebrisum.png"), 1, logger);
    }

    private static Aspect getOrRegister(String tag, int color, Aspect[] components, ResourceLocation icon, int blend, Logger logger) {
        Aspect existing = Aspect.getAspect(tag);
        if (existing != null) {
            logger.info("Found existing aspect for tag '{}' ({}); reusing.", tag, existing.getName());
            return existing;
        }
        try {
            Aspect created = new Aspect(tag, color, components, icon, blend);
            logger.info("Registered custom aspect '{}' ({}) formed from {} + {}.",
                    tag, created.getName(), components[0].getTag(), components[1].getTag());
            return created;
        } catch (IllegalArgumentException ex) {
            logger.warn("Aspect tag collision for '{}': {}. Reusing existing.", tag, ex.getMessage());
            return Aspect.getAspect(tag);
        }
    }
}
