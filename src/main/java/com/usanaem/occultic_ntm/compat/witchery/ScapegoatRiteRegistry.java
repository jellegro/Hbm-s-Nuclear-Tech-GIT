package com.usanaem.occultic_ntm.compat.witchery;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.ritual.Circle;
import com.emoniph.witchery.ritual.RiteRegistry;
import com.emoniph.witchery.ritual.RitualTraits;
import com.emoniph.witchery.ritual.SacrificeItem;
import com.emoniph.witchery.ritual.SacrificeMultiple;
import com.emoniph.witchery.ritual.SacrificePower;
import com.hbm.items.ModItems;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;

/** Fixed native byte ID; collisions disable this rite instead of remapping saves. */
public final class ScapegoatRiteRegistry {
    public static final int RITUAL_ID = 120;
    private static boolean attempted;
    private static RiteRegistry.Ritual ritual;
    private ScapegoatRiteRegistry() { }

    public static boolean register(IntegrationConfig config, Logger logger) {
        if (attempted) return ritual != null;
        attempted = true;
        if (!config.isScapegoatRiteEnabled()) return false;
        for (RiteRegistry.Ritual existing : RiteRegistry.instance().getRituals()) {
            if (existing.getRitualID() == RITUAL_ID) {
                logger.error("Rite of the Scapegoat disabled: native Witchery ritual ID 120 is occupied. No remapping.");
                return false;
            }
        }
        if (config.isRadiantChalkEnabled() && RadiantChalk.item == null) {
            logger.error("Scapegoat rite disabled: its required Radiant Chalk could not register.");
            return false;
        }
        RiteScapegoat rite = new RiteScapegoat(new ScapegoatRecipients(config, logger), config.isRadiantChalkEnabled());
        ritual = RiteRegistry.addRecipe(RITUAL_ID, RITUAL_ID, rite,
                rite.guardOfferings(new SacrificeMultiple(
                        new SacrificeItem(new ItemStack(Blocks.wool), new ItemStack(ModItems.ingot_lead),
                                Witchery.Items.GENERIC.itemFoulFume.createStack(),
                                Witchery.Items.GENERIC.itemReekOfMisfortune.createStack(),
                                new ItemStack(Items.rotten_flesh), new ItemStack(Items.redstone)),
                        new SacrificePower(RiteScapegoat.ALTAR_POWER, 20))),
                EnumSet.noneOf(RitualTraits.class),
                // Native counts: 24 white glyphs (11x11), 12 Otherwhere glyphs (7x7).
                new Circle(24, 0, 0), new Circle(0, 12, 0))
                .setUnlocalizedName("occultic_ntm.rite.scapegoat");
        logger.info("Native Rite of the Scapegoat registered at ID 120: active familiar, one coven witch, 4000 altar power.");
        return true;
    }

    public static RiteRegistry.Ritual getRitual() { return ritual; }
}
