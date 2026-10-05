package com.usanaem.occultic_ntm.compat.thaumcraft;

import org.apache.logging.log4j.Logger;

import com.usanaem.occultic_ntm.compat.thaumcraft.catalogue.CatalogueRegistry;

/** Invoked only after the common bootstrap checks configuration and mod presence. */
public final class ThaumcraftCompat {

    private static boolean initializationAttempted;

    private ThaumcraftCompat() { }

    public static synchronized void initAspects(Logger logger) {
        OcculticAspects.init(logger);
    }

    public static synchronized void initialize(Logger logger) {
        if (initializationAttempted) {
            logger.debug("Thaumcraft integration initialization was already attempted; skipping repeat invocation.");
            return;
        }
        // Set before touching the API so a failed/partial attempt cannot be repeated.
        initializationAttempted = true;
        logger.info("Starting Thaumcraft authored aspect registration via CatalogueRegistry.");
        CatalogueRegistry.registerAll(logger);
        logger.info("Starting Sol Tenebris forbidden research branch registration.");
        BlackSunResearch.register(logger);
    }
}
