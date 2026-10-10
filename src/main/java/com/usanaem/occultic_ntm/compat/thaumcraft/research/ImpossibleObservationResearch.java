package com.usanaem.occultic_ntm.compat.thaumcraft.research;

import com.usanaem.occultic_ntm.haunting.HauntingDirector;
import com.usanaem.occultic_ntm.haunting.HauntingPlayerState;
import com.usanaem.occultic_ntm.haunting.EntityHerobrine;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import net.minecraft.util.ResourceLocation;

/** Official API only. Native scanning owns success, aspect rewards and repeat suppression. */
public final class ImpossibleObservationResearch {
    public static final String KEY = "TC_NTM_IMPOSSIBLE_OBSERVATION";
    private static boolean registered;
    public static void register(org.apache.logging.log4j.Logger logger) {
        if (registered) return;
        if (ResearchCategories.getResearch(KEY) != null) {
            logger.error("Impossible Observation research disabled: key {} is already registered.", KEY);
            return;
        }
        registered = true;
        ThaumcraftApi.registerEntityTag(EntityHerobrine.ENTITY_NAME, new AspectList().add(Aspect.MAN, 2).add(Aspect.VOID, 1));
        new ResearchItem(KEY, BlackSunResearch.CATEGORY, new AspectList().add(Aspect.MAN, 1).add(Aspect.VOID, 1),
                -4, 0, 1, new ResourceLocation("occultic_ntm", "textures/research/impossible_observation.png"))
                .setLost().setHidden().setRound().setEntityTriggers(EntityHerobrine.ENTITY_NAME)
                .setPages(new ResearchPage("occultic_ntm.research.observation.1"), new ResearchPage("occultic_ntm.research.observation.2"))
                .registerResearchItem();
        FMLCommonHandler.instance().bus().register(new ImpossibleObservationResearch());
    }
    @SubscribeEvent public void tick(TickEvent.PlayerTickEvent event) {
        if (!HauntingDirector.enabled() || event.phase != TickEvent.Phase.END || event.side != Side.SERVER || event.player.ticksExisted % 100 != 0
                || HauntingPlayerState.observed(event.player)) return;
        // The API has no successful-scan callback or discovery query. Completion is the
        // earliest confirmed public signal; never infer success from holding a Thaumometer.
        if (ThaumcraftApiHelper.isResearchComplete(event.player.getCommandSenderName(), KEY)) HauntingPlayerState.markObserved(event.player);
    }
}
