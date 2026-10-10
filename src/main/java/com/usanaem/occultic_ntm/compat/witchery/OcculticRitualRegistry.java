package com.usanaem.occultic_ntm.compat.witchery;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.ritual.*;
import com.hbm.items.ModItems;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Central registry for all Occultic NTM Witchery rituals.
 * Manages ritual ID allocation, collision avoidance, and recipe registration.
 */
public final class OcculticRitualRegistry {

    public static final int RITUAL_SCAPEGOAT = 120;
    public static final int RITUAL_KINETIC_PESTLE = 121;
    public static final int RITUAL_LUNG_WARD = 122;
    private static boolean initialized;
    private static boolean scapegoatAttempted;
    private static boolean kineticPestleAttempted;
    private static boolean lungWardAttempted;
    private static RiteRegistry.Ritual scapegoatRitual;
    private static RiteRegistry.Ritual kineticPestleRitual;
    private static RiteRegistry.Ritual lungWardRitual;
    private static final Map<String, RiteRegistry.Ritual> registeredRituals = new HashMap<String, RiteRegistry.Ritual>();

    private OcculticRitualRegistry() { }

    public static void initialize(IntegrationConfig config, Logger logger) {
        if (initialized) return;
        initialized = true;

        if (config.isScapegoatRiteEnabled()) {
            scapegoatRitual = registerScapegoat(config, logger);
        }
        if (config.isKineticPestleRiteEnabled()) {
            kineticPestleRitual = registerKineticPestle(config, logger);
        }
        if (config.isLungWardRiteEnabled()) {
            lungWardRitual = registerLungWard(config, logger);
        }
    }

    public static RiteRegistry.Ritual registerScapegoat(IntegrationConfig config, Logger logger) {
        if (scapegoatAttempted) return scapegoatRitual;
        scapegoatAttempted = true;
        if (!config.isScapegoatRiteEnabled()) return null;

        if (!config.isRadiantChalkEnabled() || RadiantChalk.item == null) {
            logger.warn("Rite of the Scapegoat disabled: Radiant Chalk is required but disabled or failed to register.");
            return null;
        }

        RiteScapegoat rite = new RiteScapegoat(new ScapegoatRecipients(config, logger), true);

        Sacrifice sacrifice = rite.guardOfferings(
                new SacrificeMultiple(
                        new SacrificeItem(
                                new ItemStack(Blocks.wool),
                                new ItemStack(ModItems.ingot_lead),
                                Witchery.Items.GENERIC.itemFoulFume.createStack(),
                                Witchery.Items.GENERIC.itemReekOfMisfortune.createStack(),
                                new ItemStack(Items.rotten_flesh),
                                new ItemStack(Items.redstone)
                        ),
                        new SacrificePower(RiteScapegoat.ALTAR_POWER, 20)
                )
        );

        scapegoatRitual = registerRite(
                RITUAL_SCAPEGOAT,
                false, // strict: no dynamic remapping on collision
                "occultic_ntm.rite.scapegoat",
                rite,
                sacrifice,
                EnumSet.noneOf(RitualTraits.class),
                logger,
                OcculticCircles.radiantMedium(),
                OcculticCircles.otherwhereSmall()
        );

        return scapegoatRitual;
    }

    public static RiteRegistry.Ritual registerKineticPestle(IntegrationConfig config, Logger logger) {
        if (kineticPestleAttempted) return kineticPestleRitual;
        kineticPestleAttempted = true;
        if (!config.isKineticPestleRiteEnabled()) return null;

        if (!config.isRadiantChalkEnabled() || RadiantChalk.item == null) {
            logger.warn("Rite of the Kinetic Pestle disabled: Radiant Chalk is required but disabled or failed to register.");
            return null;
        }

        RiteKineticPestle rite = new RiteKineticPestle();

        Sacrifice sacrifice = rite.guardOfferings(
                new SacrificeMultiple(
                        new SacrificeItem(
                                new ItemStack(Items.flint),
                                new ItemStack(Items.gunpowder)
                        ),
                        new SacrificeAsh(),
                        new SacrificePower(RiteKineticPestle.ALTAR_POWER, 20)
                )
        );

        kineticPestleRitual = registerRite(
                RITUAL_KINETIC_PESTLE,
                true, // allow dynamic fallback remapping if ID 121 is taken
                "occultic_ntm.rite.kinetic_pestle",
                rite,
                sacrifice,
                EnumSet.noneOf(RitualTraits.class),
                logger,
                OcculticCircles.radiantSmall()
        );

        return kineticPestleRitual;
    }

    public static RiteRegistry.Ritual registerLungWard(IntegrationConfig config, Logger logger) {
        if (lungWardAttempted) return lungWardRitual;
        lungWardAttempted = true;
        if (!config.isLungWardRiteEnabled()) return null;

        if (!config.isRadiantChalkEnabled() || RadiantChalk.item == null) {
            logger.warn("Rite of the Lung Ward disabled: Radiant Chalk is required but disabled or failed to register.");
            return null;
        }

        RiteLungWard rite = new RiteLungWard();

        Sacrifice sacrifice = rite.guardOfferings(
                new SacrificeMultiple(
                        new SacrificeItem(
                                new ItemStack(Items.paper),
                                new ItemStack(Items.clay_ball),
                                new ItemStack(Items.glass_bottle)
                        ),
                        new SacrificePower(RiteLungWard.ALTAR_POWER, 20)
                )
        );

        lungWardRitual = registerRite(
                RITUAL_LUNG_WARD,
                true, // allow dynamic fallback remapping if ID 122 is taken
                "occultic_ntm.rite.lung_ward",
                rite,
                sacrifice,
                EnumSet.noneOf(RitualTraits.class),
                logger,
                OcculticCircles.radiantSmall()
        );

        return lungWardRitual;
    }

    /**
     * Helper for registering custom rituals with automatic fallback collision avoidance.
     */
    public static RiteRegistry.Ritual registerRite(int preferredId, String unlocalizedName, Rite rite,
                                                   Sacrifice sacrifice, EnumSet<RitualTraits> traits,
                                                   Logger logger, Circle... circles) {
        return registerRite(preferredId, true, unlocalizedName, rite, sacrifice, traits, logger, circles);
    }

    /**
     * Primary registration helper for custom rituals into Witchery's RiteRegistry.
     *
     * @param preferredId Desired ritual byte ID
     * @param allowRemap True to scan and allocate a fallback ID if occupied; false to fail strictly on collision
     * @param unlocalizedName Recipe unlocalized name (e.g. "occultic_ntm.rite.my_ritual")
     * @param rite The Rite / OcculticRite implementation
     * @param sacrifice The sacrifice requirement (usually wrapped via rite.guardOfferings)
     * @param traits RitualTraits flags (or EnumSet.noneOf(RitualTraits.class))
     * @param circles Circles required for this ritual
     * @return Registered Ritual instance, or null if registration failed
     */
    public static RiteRegistry.Ritual registerRite(int preferredId, boolean allowRemap, String unlocalizedName,
                                                   Rite rite, Sacrifice sacrifice, EnumSet<RitualTraits> traits,
                                                   Logger logger, Circle... circles) {
        if (registeredRituals.containsKey(unlocalizedName)) {
            return registeredRituals.get(unlocalizedName);
        }

        int ritualId = allocateId(preferredId, allowRemap, unlocalizedName, logger);
        if (ritualId < 0) return null;

        EnumSet<RitualTraits> safeTraits = traits != null ? traits : EnumSet.noneOf(RitualTraits.class);
        RiteRegistry.Ritual registered = RiteRegistry.addRecipe(
                ritualId, ritualId, rite, sacrifice, safeTraits, circles
        ).setUnlocalizedName(unlocalizedName);

        registeredRituals.put(unlocalizedName, registered);
        logger.info("Registered ritual '{}' at ID {}.", unlocalizedName, ritualId);
        return registered;
    }

    /**
     * Allocates a safe ritual ID with collision detection and optional fallback scanning.
     */
    public static int allocateId(int preferredId, boolean allowRemap, String riteName, Logger logger) {
        Set<Integer> occupied = new HashSet<Integer>();
        for (RiteRegistry.Ritual r : RiteRegistry.instance().getRituals()) {
            occupied.add((int) (r.getRitualID() & 0xFF));
        }

        if (!occupied.contains(preferredId)) {
            return preferredId;
        }

        if (!allowRemap) {
            logger.error("Ritual '{}' disabled: native Witchery ritual ID {} is occupied. No remapping.", riteName, preferredId);
            return -1;
        }

        logger.warn("Ritual ID {} for '{}' is already occupied by another mod.", preferredId, riteName);
        for (int fallback = 120; fallback < 255; fallback++) {
            if (!occupied.contains(fallback)) {
                logger.info("Reassigned '{}' to free ritual ID {}.", riteName, fallback);
                return fallback;
            }
        }

        logger.error("Failed to register '{}': all ritual IDs in range 120..254 are occupied!", riteName);
        return -1;
    }

    public static RiteRegistry.Ritual getScapegoatRitual() {
        return scapegoatRitual;
    }

    public static RiteRegistry.Ritual getKineticPestleRitual() {
        return kineticPestleRitual;
    }

    public static RiteRegistry.Ritual getLungWardRitual() {
        return lungWardRitual;
    }

    public static RiteRegistry.Ritual getRitual(String unlocalizedName) {
        return registeredRituals.get(unlocalizedName);
    }
}
