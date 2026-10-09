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
    private boolean enableAmbientAnomalies = true;
    private boolean enableBlueTreasureFlames = true;
    private boolean enableRadiantChalk = true;
    private boolean enableHerobrineHaunting;
    private boolean enableEnvironmentalHauntings = true;
    private boolean allowHauntingTerrainChanges = true;
    private boolean giveHimControl;
    private int hauntingGraceDays = 3;
    private int hauntingIntervalMinutes = 8;
    private String hauntingFrequency = "DEFAULT";
    private int hauntingMinimumSeconds = 60, hauntingMaximumSeconds = 120, hauntingNightmareOdds = 100;

    public void load(File file) {
        Configuration configuration = new Configuration(file);
        configuration.load();
        enableHerobrineHaunting = configuration.getBoolean("enableHerobrineHaunting", "haunting", false,
                "Opt in to the entire Herobrine haunting. Off means no scheduling, tracking or world changes. Server authoritative; restart required.");
        enableEnvironmentalHauntings = configuration.getBoolean("enableEnvironmentalHauntings", "haunting", true,
                "Subtle footsteps, known doors, rare chest notes and minor light changes. Requires the haunting master switch.");
        allowHauntingTerrainChanges = configuration.getBoolean("allowTerrainChanges", "haunting", true,
                "Allow exceptionally rare bounded natural leaf stripping and safe vanilla-stone Ghost Miner tunnels. Requires both haunting switches.");
        giveHimControl = configuration.getBoolean("giveHimControl", "haunting", false,
                "Choose a runtime activity profile at dawn, including quiet days. Never changes the config or bypasses master/grace.");
        hauntingGraceDays = configuration.getInt("graceDays", "haunting", 3, 0, 30,
                "Overworld elapsed days before natural activity. Deliberate invocation bypasses grace.");
        boolean legacyInterval = configuration.hasKey("haunting", "encounterIntervalMinutes");
        boolean explicitFrequency = configuration.hasKey("haunting", "sightingFrequency");
        if (legacyInterval) hauntingIntervalMinutes = configuration.getInt("encounterIntervalMinutes", "haunting", 8, 1, 1440,
                "Legacy CUSTOM minimum minutes. Historical default 8 is superseded by sightingFrequency=DEFAULT. Set CUSTOM explicitly to retain 8.");
        hauntingFrequency = configuration.get("haunting", "sightingFrequency",
                !explicitFrequency && legacyInterval && hauntingIntervalMinutes != 8 ? "CUSTOM" : "DEFAULT",
                "INSANE: 15-30s; COMMON: 30-60s; DEFAULT: 60-120s; RARE: 120-240s; SCARCE: 240-360s. Night halves the range. CUSTOM uses legacy encounterIntervalMinutes. Runtime profiles never edit this setting.")
                .getString().trim().toUpperCase(java.util.Locale.ROOT);
        if ("INSANE".equals(hauntingFrequency)) { hauntingMinimumSeconds = 15; hauntingMaximumSeconds = 30; hauntingNightmareOdds = 25; }
        else if ("COMMON".equals(hauntingFrequency)) { hauntingMinimumSeconds = 30; hauntingMaximumSeconds = 60; hauntingNightmareOdds = 25; }
        else if ("RARE".equals(hauntingFrequency)) { hauntingMinimumSeconds = 120; hauntingMaximumSeconds = 240; hauntingNightmareOdds = 200; }
        else if ("SCARCE".equals(hauntingFrequency)) { hauntingMinimumSeconds = 240; hauntingMaximumSeconds = 360; hauntingNightmareOdds = 500; }
        else if ("CUSTOM".equals(hauntingFrequency)) {
            if (!legacyInterval) hauntingIntervalMinutes = configuration.getInt("encounterIntervalMinutes", "haunting", 8, 1, 1440, "CUSTOM minimum minutes.");
            hauntingMinimumSeconds = hauntingIntervalMinutes * 60; hauntingMaximumSeconds = hauntingMinimumSeconds * 2; hauntingNightmareOdds = 100;
        } else { hauntingFrequency = "DEFAULT"; hauntingMinimumSeconds = 60; hauntingMaximumSeconds = 120; hauntingNightmareOdds = 100; }
        enableAmbientAnomalies = configuration.getBoolean("enableAmbientAnomalies", "anomalies", true,
                "Master switch for rare ambient anomalies. Server authoritative; requires restart.");
        enableBlueTreasureFlames = configuration.getBoolean("enableBlueTreasureFlames", "anomalies", true,
                "Very rare spectral markers above modest buried treasure. Requires restart.");
        enableThaumcraftIntegration = configuration.getBoolean("enableThaumcraftIntegration", "integration", true,
                "Register Occultic NTM's authored material aspects when Thaumcraft is present. Requires restart.");
        enableIrradiationPoppet = configuration.getBoolean("enableIrradiationPoppet", "witchery", true,
                "Enable Irradiation Poppet crafting and protection with Witchery. Existing items and hazards persist. Requires restart.");
        enableScapegoatRite = configuration.getBoolean("enableScapegoatRite", "witchery", true,
                "Register the native Rite of the Scapegoat to transfer bodily HBM radiation. Requires restart.");
        enableRadiantChalk = configuration.getBoolean("enableRadiantChalk", "witchery", true,
                "Register mild radioactive chalk. Scapegoat then requires a radiant native white ring. Requires restart.");
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
    public boolean areAmbientAnomaliesEnabled() { return enableAmbientAnomalies; }
    public boolean areBlueTreasureFlamesEnabled() { return enableBlueTreasureFlames; }
    public boolean isRadiantChalkEnabled() { return enableRadiantChalk; }
    public boolean isHerobrineHauntingEnabled() { return enableHerobrineHaunting; }
    public boolean areEnvironmentalHauntingsEnabled() { return enableEnvironmentalHauntings; }
    public boolean areHauntingTerrainChangesAllowed() { return allowHauntingTerrainChanges; }
    public boolean doesHerobrineHaveControl() { return giveHimControl; }
    public int getHauntingGraceDays() { return hauntingGraceDays; }
    public int getHauntingIntervalMinutes() { return hauntingIntervalMinutes; }
    public String getHauntingFrequency() { return hauntingFrequency; }
    public int getHauntingMinimumSeconds() { return hauntingMinimumSeconds; }
    public int getHauntingMaximumSeconds() { return hauntingMaximumSeconds; }
    public int getHauntingNightmareOdds() { return hauntingNightmareOdds; }
}
