package com.usanaem.occultic_ntm.herobrine;

import java.util.Map;
import java.util.WeakHashMap;

import com.usanaem.occultic_ntm.config.IntegrationConfig;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/** Bounded server scheduling. Never spawns entities or changes terrain. */
public final class HerobrineSightings {
    private static final int MINUTE_TICKS = 1200;
    private static boolean initialized;
    private static HerobrineProxy proxy;
    private final IntegrationConfig config;
    private final SimpleNetworkWrapper network;
    private final Map<EntityPlayerMP, Long> nextOpportunity = new WeakHashMap<EntityPlayerMP, Long>();

    private HerobrineSightings(IntegrationConfig config, SimpleNetworkWrapper network) {
        this.config = config;
        this.network = network;
    }

    public static void initialize(IntegrationConfig config, HerobrineProxy sidedProxy) {
        if (initialized) return;
        initialized = true;
        proxy = sidedProxy;
        // Register on both sides even when disabled: server configuration is authoritative.
        SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("occultic_herobr");
        network.registerMessage(SightingMessage.Handler.class, SightingMessage.class, 0, Side.CLIENT);
        FMLCommonHandler.instance().bus().register(new HerobrineSightings(config, network));
        proxy.initialize();
    }

    static void receive(SightingMessage message) {
        if (proxy != null) proxy.receive(message);
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!config.isHerobrineEasterEggEnabled() || event.phase != TickEvent.Phase.END
                || event.side != Side.SERVER || !(event.player instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        long tick = player.ticksExisted;
        Long next = nextOpportunity.get(player);
        if (next == null) {
            nextOpportunity.put(player, tick + Math.min(10, config.getHerobrineCooldownMinutes()) * MINUTE_TICKS);
            return;
        }
        if (tick < next || tick % MINUTE_TICKS != 0) return;
        World world = player.worldObj;
        if (player.dimension != 0 || !player.isEntityAlive() || player.capabilities.isCreativeMode
                || player.isPlayerSleeping() || player.ridingEntity != null) return;
        long time = world.getWorldTime() % 24000L;
        if (time < 13000L || time > 23000L || Math.abs(player.rotationPitch) > 45F) return;
        if (!world.canBlockSeeTheSky(MathHelper.floor_double(player.posX),
                MathHelper.floor_double(player.posY + player.getEyeHeight()), MathHelper.floor_double(player.posZ))) return;
        if (world.rand.nextInt(config.getHerobrineChanceDenominator()) != 0) return;

        for (int attempt = 0; attempt < 8; attempt++) {
            double offset = (25D + world.rand.nextDouble() * 30D) * (world.rand.nextBoolean() ? 1D : -1D);
            double angle = Math.toRadians(player.rotationYaw + offset);
            double distance = 24D + world.rand.nextDouble() * 16D;
            int x = MathHelper.floor_double(player.posX - Math.sin(angle) * distance);
            int z = MathHelper.floor_double(player.posZ + Math.cos(angle) * distance);
            // Include adjacent chunks for collision/ray queries; never force terrain generation.
            if (!loadedArea(world, Math.min(x - 1, MathHelper.floor_double(player.posX)),
                    Math.max(x + 1, MathHelper.floor_double(player.posX)),
                    Math.min(z - 1, MathHelper.floor_double(player.posZ)),
                    Math.max(z + 1, MathHelper.floor_double(player.posZ)))) continue;
            int y = world.getHeightValue(x, z);
            if (y < 1 || y > world.getHeight() - 2 || Math.abs(y - player.posY) > 8D) continue;
            Block ground = world.getBlock(x, y - 1, z);
            if (!ground.getMaterial().isSolid() || ground.getMaterial() == Material.leaves
                    || !World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) continue;
            double centerX = x + 0.5D;
            double centerZ = z + 0.5D;
            AxisAlignedBB body = AxisAlignedBB.getBoundingBox(centerX - 0.3D, y, centerZ - 0.3D,
                    centerX + 0.3D, y + 1.8D, centerZ + 0.3D);
            if (!world.getCollidingBoundingBoxes(player, body).isEmpty() || world.isAnyLiquid(body)) continue;
            Vec3 eyes = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            if (world.rayTraceBlocks(eyes, Vec3.createVectorHelper(centerX, y + 1.6D, centerZ)) != null) continue;
            network.sendTo(new SightingMessage(player.dimension, centerX, y, centerZ), player);
            nextOpportunity.put(player, tick + (long) config.getHerobrineCooldownMinutes() * MINUTE_TICKS);
            return;
        }
    }

    private static boolean loadedArea(World world, int minX, int maxX, int minZ, int maxZ) {
        for (int x = minX >> 4; x <= maxX >> 4; x++) {
            for (int z = minZ >> 4; z <= maxZ >> 4; z++) {
                if (!world.getChunkProvider().chunkExists(x, z)) return false;
            }
        }
        return true;
    }
}
