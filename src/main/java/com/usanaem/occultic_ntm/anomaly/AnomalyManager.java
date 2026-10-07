package com.usanaem.occultic_ntm.anomaly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.usanaem.occultic_ntm.config.IntegrationConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.player.PlayerEvent;

/** No terrain work before a sparse rarity roll; one execution at most per opportunity. */
public final class AnomalyManager extends CommandBase {
    private final IntegrationConfig config;
    private final AnomalyRegistry registry;
    public AnomalyManager(IntegrationConfig config, AnomalyRegistry registry) { this.config = config; this.registry = registry; }

    @SubscribeEvent
    public void clonePlayer(PlayerEvent.Clone event) { AnomalyAttention.copy(event.original, event.entityPlayer); }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != Side.SERVER || !(event.player instanceof EntityPlayerMP)
                || !config.areAmbientAnomaliesEnabled()) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        // Disabled descriptors are never asked about eligibility, rarity or terrain.
        boolean anyEnabled = false;
        for (Anomaly anomaly : registry.entries()) if (anomaly.enabled(config)) { anyEnabled = true; break; }
        if (!anyEnabled) return;
        AnomalyAttention.advance(player);
        long now = AnomalyAttention.clock(player);
        if (now % 1200L != 0 || now < AnomalyAttention.next(player, "ambient")) return;
        List<Anomaly> candidates = new ArrayList<Anomaly>();
        int totalWeight = 0;
        for (Anomaly anomaly : registry.entries()) {
            if (!anomaly.enabled(config)) continue;
            if (AnomalyAttention.next(player, anomaly.id()) == 0) {
                AnomalyAttention.defer(player, anomaly.id(), 12000L);
                continue;
            }
            if (now < AnomalyAttention.next(player, anomaly.id()) || AnomalyAttention.get(player) < anomaly.minimumAttention()
                    || !anomaly.eligible(player)) continue;
            if (player.worldObj.rand.nextInt(Math.max(1, anomaly.chanceDenominator(player, config))) != 0) continue;
            candidates.add(anomaly);
            totalWeight += anomaly.weight();
        }
        if (candidates.isEmpty()) return;
        int choice = player.worldObj.rand.nextInt(totalWeight);
        for (Anomaly anomaly : candidates) {
            choice -= anomaly.weight();
            if (choice < 0) { trigger(player, anomaly, false); return; }
        }
    }

    public boolean trigger(EntityPlayerMP player, Anomaly anomaly, boolean forced) {
        if (!config.areAmbientAnomaliesEnabled() || anomaly == null || !anomaly.enabled(config)) return false;
        return publish(player, anomaly, forced);
    }

    private boolean publish(EntityPlayerMP player, Anomaly anomaly, boolean forced) {
        if (!anomaly.execute(player, forced)) return false;
        AnomalyAttention.defer(player, anomaly.id(), anomaly.cooldownTicks(player, config));
        AnomalyAttention.defer(player, "ambient", 12000L);
        return true;
    }

    @Override public String getCommandName() { return "anomalies"; }
    @Override public List getCommandAliases() { return Arrays.asList("occultic_anomaly"); }
    @Override public String getCommandUsage(ICommandSender sender) { return "/anomalies herobrine <encounter> [player] | /anomalies blue_flame [player]"; }
    @Override public int getRequiredPermissionLevel() { return 2; }
    @Override public boolean isUsernameIndex(String[] args, int index) {
        return index == (args.length >= 3 && ("herobrine".equals(args[0]) || "herobrine_sighting".equals(args[0])) ? 2 : 1);
    }
    @Override public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) return getListOfStringsMatchingLastWord(args, "herobrine", "blue_flame");
        if (args.length == 2 && "herobrine".equals(args[0])) return getListOfStringsMatchingLastWord(args,
                "stalking", "lurking", "dwelling", "creeping", "window", "nightmare", "footsteps", "door", "donation", "light", "leafless_grove", "ghost_miner", "invoke", "status");
        if (args.length == 2 || args.length == 3 && "herobrine".equals(args[0])) return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        return null;
    }
    @Override public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0 && ("herobrine".equals(args[0]) || "herobrine_sighting".equals(args[0]))) {
            if (args.length > 3) { sender.addChatMessage(new ChatComponentText(getCommandUsage(sender))); return; }
            String encounter = args.length >= 2 ? args[1] : "stalking";
            boolean legacyTarget = args.length == 2 && !Arrays.asList("stalking", "lurking", "dwelling", "creeping", "window", "nightmare",
                    "footsteps", "door", "donation", "light", "leafless_grove", "ghost_miner", "invoke", "status", "invocation").contains(args[1]);
            EntityPlayerMP target = args.length == 3 ? getPlayer(sender, args[2]) : legacyTarget ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
            sender.addChatMessage(new ChatComponentText(com.usanaem.occultic_ntm.haunting.HauntingDirector.debug(target, legacyTarget ? "stalking" : encounter)));
            return;
        }
        if (args.length < 1 || args.length > 2) { sender.addChatMessage(new ChatComponentText(getCommandUsage(sender))); return; }
        String id = "herobrine".equals(args[0]) ? "herobrine_sighting"
                : "blue_flame".equals(args[0]) ? "blue_treasure_flame" : args[0];
        Anomaly anomaly = registry.get(id);
        if (anomaly == null) {
            sender.addChatMessage(new ChatComponentText("Unknown anomaly '" + args[0] + "'. " + getCommandUsage(sender)));
            return;
        }
        EntityPlayerMP player = args.length == 2 ? getPlayer(sender, args[1]) : getCommandSenderAsPlayer(sender);
        // Explicit operator previews work even when natural selection is disabled.
        // Safe loaded-terrain placement, active-event exclusion and cooldown recording remain.
        boolean result = publish(player, anomaly, true);
        sender.addChatMessage(new ChatComponentText(result ? "Previewed " + args[0] + " for " + player.getCommandSenderName()
                + ". Look ahead; normal cooldown recorded."
                : "Anomaly already active nearby, or no safe loaded position. Stand on open ground and try again."));
    }
}
