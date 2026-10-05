package com.usanaem.occultic_ntm.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Common configuration; optional API types must not be referenced here. */
public final class IntegrationConfig {

    private boolean enableThaumcraftIntegration = true;
    private boolean enableIrradiationPoppet = true;
    private double poppetCapacity = 200D;
    private boolean enableScapegoatRite = true;
    private boolean allowScapegoatPlayerRecipients;
    private String[] scapegoatRecipientEntityIds = { "Pig", "Sheep", "Chicken" };
    private boolean enableHerobrineEasterEgg;
    private int herobrineCooldownMinutes = 45;
    private int herobrineChanceDenominator = 20;

    public void load(File file) {
        Configuration configuration = new Configuration(file);
        configuration.load();
        enableHerobrineEasterEgg = configuration.getBoolean("enableHerobrineEasterEgg", "easterEggs", false,
                "Rare, silent, harmless Herobrine sightings outdoors at night. Server controls multiplayer sightings. Requires restart.");
        herobrineCooldownMinutes = configuration.getInt("herobrineCooldownMinutes", "easterEggs", 45, 1, 1440,
                "Minimum minutes between sightings per player session. First opportunity waits up to ten minutes. Requires restart.");
        herobrineChanceDenominator = configuration.getInt("herobrineChanceDenominator", "easterEggs", 20, 1, 10000,
                "One-in-N chance each eligible minute, after the cooldown; placement may still fail. Requires restart.");
        enableThaumcraftIntegration = configuration.getBoolean("enableThaumcraftIntegration", "integration", true,
                "Register Occultic NTM's authored material aspects when Thaumcraft is present. Requires restart.");
        enableIrradiationPoppet = configuration.getBoolean("enableIrradiationPoppet", "witchery", true,
                "Enable Irradiation Poppet crafting and protection with Witchery. Existing items and hazards persist. Requires restart.");
        enableScapegoatRite = configuration.getBoolean("enableScapegoatRite", "witchery", true,
                "Register the native Rite of the Scapegoat to transfer bodily HBM radiation. Requires restart.");
        allowScapegoatPlayerRecipients = configuration.getBoolean("allowScapegoatPlayerRecipients", "witchery", false,
                "Allow another player inside the rite area to receive radiation. Enables harmful multiplayer targeting; requires restart.");
        scapegoatRecipientEntityIds = configuration.getStringList("scapegoatRecipientEntityIds", "witchery",
                new String[] { "Pig", "Sheep", "Chicken" },
                "Exact Minecraft 1.7.10 EntityList registry names (e.g. Pig or modid.EntityName). Empty list permits no creatures; players use the separate option. Requires restart.");
        // configuration.getCategory("witchery").remove("absorptionFraction");
        poppetCapacity = finiteSetting(configuration, "capacityRAD", 200D, 1D, 1000000D,
                "Maximum stored HBM dose in RAD before sacrificial rupture. Stored dose is released locally, including after lowering capacity. Requires restart.");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public boolean isThaumcraftIntegrationEnabled() {
        return enableThaumcraftIntegration;
    }

    private static double finiteSetting(Configuration config, String key, double fallback, double min, double max, String comment) {
        Property property = config.get("witchery", key, fallback, comment, min, max);
        double value = property.getDouble(fallback);
        if (Double.isNaN(value) || Double.isInfinite(value)) value = fallback;
        value = Math.max(min, Math.min(max, value));
        property.set(value);
        return value;
    }

    public boolean isIrradiationPoppetEnabled() { return enableIrradiationPoppet; }
    public double getPoppetCapacity() { return poppetCapacity; }
    public boolean isScapegoatRiteEnabled() { return enableScapegoatRite; }
    public boolean areScapegoatPlayerRecipientsAllowed() { return allowScapegoatPlayerRecipients; }
    public String[] getScapegoatRecipientEntityIds() { return scapegoatRecipientEntityIds.clone(); }
    public boolean isHerobrineEasterEggEnabled() { return enableHerobrineEasterEgg; }
    public int getHerobrineCooldownMinutes() { return herobrineCooldownMinutes; }
    public int getHerobrineChanceDenominator() { return herobrineChanceDenominator; }
}
