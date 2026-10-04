package com.usanaem.occultic_hbm;

import org.apache.logging.log4j.Logger;

import com.usanaem.occultic_hbm.bootstrap.OptionalMods;
import com.usanaem.occultic_hbm.compat.thaumcraft.ThaumcraftCompat;
import com.usanaem.occultic_hbm.config.IntegrationConfig;
import com.usanaem.occultic_hbm.registry.OcculticItems;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/** Fork-owned lifecycle with presence-gated optional integrations. */
@Mod(modid = OcculticHBM.MOD_ID, name = "Occultic HBM", version = OcculticHBM.VERSION,
        dependencies = "required-after:" + OptionalMods.HBM + ";after:" + OptionalMods.THAUMCRAFT + ";after:" + OptionalMods.WITCHERY,
        acceptedMinecraftVersions = "[1.7.10]")
public class OcculticHBM {

    public static final String MOD_ID = "occultic_hbm";
    public static final String VERSION = "0.1.0";

    private Logger logger;
    private final IntegrationConfig config = new IntegrationConfig();
    private boolean thaumcraftIntegrationFailed;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        config.load(event.getSuggestedConfigurationFile());
        OcculticItems.register(config);
        logger.info("Occultic HBM bootstrap initialized.");
    }

	// TODO - Fun idea, but doesnt integrate properly yet, why couldn't witchery just provide a public api :/
//    @EventHandler
//    public void init(FMLInitializationEvent event) {
//        if (!OptionalMods.isWitcheryLoaded()) return;
//        try {
//            WitcheryCompat.initialize(config, logger);
//        } catch (LinkageError | RuntimeException error) {
//            OcculticItems.irradiationPoppet.setProtectionAvailable(false);
//            logger.error("Irradiation Poppet integration unavailable after a Witchery initialization failure. Earlier registrations, saved items and stored hazards remain.", error);
//        }
//    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        boolean enabled = config.isThaumcraftIntegrationEnabled();
        boolean detected = OptionalMods.isThaumcraftLoaded();
        logger.info("Thaumcraft integration enabled: {}; Thaumcraft detected: {}.", enabled, detected);
        if (!enabled || !detected || thaumcraftIntegrationFailed) {
            return;
        }
        try {
            ThaumcraftCompat.initialize(logger);
        } catch (LinkageError error) {
            thaumcraftIntegrationFailed = true;
            logger.error("Thaumcraft integration disabled after an API/linkage failure. Earlier successful registrations, if any, were not rolled back.",
                    error);
        }
    }
}
