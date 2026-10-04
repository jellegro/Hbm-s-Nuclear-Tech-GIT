package com.usanaem.occultic_hbm.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

/** Common configuration; optional API types must not be referenced here. */
public final class IntegrationConfig {

    private boolean enableThaumcraftIntegration = true;
//    private boolean enableIrradiationPoppet = true;
//    private double poppetAbsorptionFraction = 0.25D;
//    private double poppetCapacity = 200D;

    public void load(File file) {
        Configuration configuration = new Configuration(file);
        configuration.load();
        enableThaumcraftIntegration = configuration.getBoolean("enableThaumcraftIntegration", "integration", true,
                "Register Occultic HBM's authored material aspects when Thaumcraft is present. Requires restart.");
//        enableIrradiationPoppet = configuration.getBoolean("enableIrradiationPoppet", "witchery", true,
//                "Enable Irradiation Poppet crafting and protection with Witchery. Existing items and hazards persist. Requires restart.");
//        poppetAbsorptionFraction = finiteSetting(configuration, "absorptionFraction", 0.25D, 0D, 0.95D,
//                "Fraction of positive accumulated HBM dose transferred to one carried bound poppet. Requires restart.");
//        poppetCapacity = finiteSetting(configuration, "capacityRAD", 200D, 1D, 1000000D,
//                "Maximum stored HBM dose in RAD. Reducing this never removes existing stored radiation. Requires restart.");
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public boolean isThaumcraftIntegrationEnabled() {
        return enableThaumcraftIntegration;
    }

//    private static double finiteSetting(Configuration config, String key, double fallback, double min, double max, String comment) {
//        net.minecraftforge.common.config.Property property = config.get("witchery", key, fallback, comment, min, max);
//        double value = property.getDouble(fallback);
//        if (Double.isNaN(value) || Double.isInfinite(value)) value = fallback;
//        value = Math.max(min, Math.min(max, value));
//        property.set(value);
//        return value;
//    }

//    public boolean isIrradiationPoppetEnabled() { return enableIrradiationPoppet; }
//    public double getPoppetAbsorptionFraction() { return poppetAbsorptionFraction; }
//    public double getPoppetCapacity() { return poppetCapacity; }
}
