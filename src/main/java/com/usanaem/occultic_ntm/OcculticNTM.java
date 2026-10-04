package com.usanaem.occultic_ntm;

import org.apache.logging.log4j.Logger;

import com.usanaem.occultic_ntm.bootstrap.OptionalMods;
import com.usanaem.occultic_ntm.compat.thaumcraft.ThaumcraftCompat;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import com.usanaem.occultic_ntm.compat.witchery.WitcheryCompat;
import com.usanaem.occultic_ntm.registry.OcculticCreativeTab;
import com.usanaem.occultic_ntm.radiation.TemporaryRadiationRelease;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/** Fork-owned lifecycle with presence-gated optional integrations. */
@Mod(modid = OcculticNTM.MOD_ID, name = "Occultic NTM", version = OcculticNTM.VERSION,
        dependencies = "required-after:" + OptionalMods.HBM + ";after:" + OptionalMods.THAUMCRAFT + ";after:" + OptionalMods.WITCHERY,
        acceptedMinecraftVersions = "[1.7.10]")
public class OcculticNTM {

    public static final String MOD_ID = "occultic_ntm";
    public static final String VERSION = "0.1.0";
    public static final OcculticCreativeTab CREATIVE_TAB = new OcculticCreativeTab();

    private Logger logger;
    private final IntegrationConfig config = new IntegrationConfig();
    private boolean thaumcraftIntegrationFailed;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        config.load(event.getSuggestedConfigurationFile());
        FMLCommonHandler.instance().bus().register(this);
        logger.info("Occultic NTM bootstrap initialized.");
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.world.isRemote) TemporaryRadiationRelease.tick(event.world);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        if (OptionalMods.isWitcheryLoaded()) {
            try { WitcheryCompat.initialize(config, logger); }
            catch (LinkageError | RuntimeException failure) {
                logger.error("Native Witchery Irradiation Poppet unavailable; integration disabled.", failure);
            }
        }
        boolean enabled = config.isThaumcraftIntegrationEnabled();
        boolean detected = OptionalMods.isThaumcraftLoaded();
        if (enabled && detected && !thaumcraftIntegrationFailed) {
            try {
                ThaumcraftCompat.initAspects(logger);
            } catch (LinkageError error) {
                thaumcraftIntegrationFailed = true;
                logger.error("Thaumcraft custom aspect registration failed.", error);
            }
        }
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        if (OptionalMods.isWitcheryLoaded() && OptionalMods.isNeiLoaded() && FMLCommonHandler.instance().getSide().isClient()) {
            try {
                com.usanaem.occultic_ntm.compat.witchery.client.NativePoppetRenderer.initializeNei(logger);
            } catch (LinkageError | RuntimeException failure) {
                logger.warn("Optional NEI poppet presentation unavailable; native tooltip and gameplay remain active.", failure);
            }
        }
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
