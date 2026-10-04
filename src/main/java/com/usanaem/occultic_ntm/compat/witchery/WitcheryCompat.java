package com.usanaem.occultic_ntm.compat.witchery;

import com.usanaem.occultic_ntm.config.IntegrationConfig;
import org.apache.logging.log4j.Logger;

/** Loaded only after the optional presence/configuration guards. */
public final class WitcheryCompat {
    private WitcheryCompat() { }

    public static boolean initialize(IntegrationConfig config, Logger logger) {
        return IrradiationPoppetRegistry.register(config, logger);
    }
}
