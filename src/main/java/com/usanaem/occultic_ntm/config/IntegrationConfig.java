package com.usanaem.occultic_ntm.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** Common configuration; optional API types must not be referenced here. */
public final class IntegrationConfig {

    private boolean enableThaumcraftIntegration = true;
    private boolean enableIrradiationPoppet = true;
    private double poppetCapacity = 200D;

    public void load(File file) {
        Configuration configuration = new Configuration(file);
        configuration.load();
        enableThaumcraftIntegration = configuration.getBoolean("enableThaumcraftIntegration", "integration", true,
                "Register Occultic NTM's authored material aspects when Thaumcraft is present. Requires restart.");
        enableIrradiationPoppet = configuration.getBoolean("enableIrradiationPoppet", "witchery", true,
                "Enable Irradiation Poppet crafting and protection with Witchery. Existing items and hazards persist. Requires restart.");
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
}
