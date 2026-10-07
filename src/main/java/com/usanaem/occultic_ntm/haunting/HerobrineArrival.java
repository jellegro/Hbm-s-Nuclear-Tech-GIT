package com.usanaem.occultic_ntm.haunting;

import java.util.concurrent.ConcurrentHashMap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

/** Client entity/resource receipt, drained and validated on the server thread. */
public final class HerobrineArrival implements IMessage {
    private static final ConcurrentHashMap<EntityPlayerMP, Integer> PENDING = new ConcurrentHashMap<EntityPlayerMP, Integer>();
    private int entityId;
    public HerobrineArrival() { }
    private HerobrineArrival(int id) { entityId = id; }
    public void fromBytes(ByteBuf buffer) { entityId = buffer.readInt(); }
    public void toBytes(ByteBuf buffer) { buffer.writeInt(entityId); }
    static void initialize() {
        HerobrineArrival cleanup = new HerobrineArrival();
        FMLCommonHandler.instance().bus().register(cleanup);
        MinecraftForge.EVENT_BUS.register(cleanup);
    }
    public static void request(EntityHerobrine figure) {
        HerobrineCancellation.CHANNEL.sendToServer(new HerobrineArrival(figure.getEntityId()));
    }
    static void tick(World world) {
        for (EntityPlayerMP player : PENDING.keySet()) {
            if (player.worldObj != world && !player.isDead) continue;
            Integer id = PENDING.remove(player);
            if (id == null || player.isDead || player.worldObj != world || !HauntingDirector.enabled()) continue;
            Entity entity = world.getEntityByID(id);
            if (entity instanceof EntityHerobrine) ((EntityHerobrine) entity).confirmArrival(player);
        }
    }
    @SubscribeEvent public void logout(PlayerLoggedOutEvent event) { PENDING.remove(event.player); }
    @SubscribeEvent public void unload(WorldEvent.Unload event) {
        for (EntityPlayerMP player : PENDING.keySet()) if (player.worldObj == event.world) PENDING.remove(player);
    }
    public static final class Handler implements IMessageHandler<HerobrineArrival, IMessage> {
        public IMessage onMessage(HerobrineArrival message, MessageContext context) {
            PENDING.put(context.getServerHandler().playerEntity, message.entityId);
            return null;
        }
    }
}
