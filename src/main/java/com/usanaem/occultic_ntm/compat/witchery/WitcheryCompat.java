package com.usanaem.occultic_ntm.compat.witchery;

import com.usanaem.occultic_ntm.compat.witchery.item.IrradiationPoppetRegistry;
import com.usanaem.occultic_ntm.compat.witchery.item.RadiantChalk;
import com.usanaem.occultic_ntm.compat.witchery.rites.OcculticRitualRegistry;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import org.apache.logging.log4j.Logger;

/** Loaded only after the optional presence/configuration guards. */
public final class WitcheryCompat {
    private WitcheryCompat() { }

    public static boolean initialize(IntegrationConfig config, Logger logger) {
        boolean poppetAvailable = false;
        try { RadiantChalk.initialize(config, logger); }
        catch (LinkageError | RuntimeException failure) { logger.error("Native Radiant Chalk unavailable.", failure); }
        try { poppetAvailable = IrradiationPoppetRegistry.register(config, logger); }
        catch (LinkageError | RuntimeException failure) {
            logger.error("Native Witchery Irradiation Poppet unavailable.", failure);
        }
        try { OcculticRitualRegistry.initialize(config, logger); }
        catch (LinkageError | RuntimeException failure) {
            logger.error("Native Witchery rituals unavailable.", failure);
        }
        return poppetAvailable;
    }
}
