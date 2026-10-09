package com.usanaem.occultic_ntm.haunting;

import java.util.concurrent.ConcurrentHashMap;
import com.usanaem.occultic_ntm.haunting.EntityHerobrine;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.*;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/** A texture failure may cancel only the reporting viewer's own sighting. */
public final class HerobrineCancellation implements IMessage {
    static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("occultic_anomaly");
    // At most one pending request per connection; network threads never touch worlds.
    private static final ConcurrentHashMap<EntityPlayerMP, Integer> PENDING = new ConcurrentHashMap<EntityPlayerMP, Integer>();
    private int entityId;
    public HerobrineCancellation() { }
    private HerobrineCancellation(int entityId) { this.entityId = entityId; }
    public void fromBytes(ByteBuf buffer) { entityId = buffer.readInt(); }
    public void toBytes(ByteBuf buffer) { buffer.writeInt(entityId); }
    public static void initialize() {
        CHANNEL.registerMessage(Handler.class, HerobrineCancellation.class, 0, Side.SERVER);
        CHANNEL.registerMessage(HerobrineArrival.Handler.class, HerobrineArrival.class, 1, Side.SERVER);
        HerobrineArrival.initialize();
    }
    public static void request(EntityHerobrine figure) { CHANNEL.sendToServer(new HerobrineCancellation(figure.getEntityId())); }
    public static void tick(World world) {
        HerobrineArrival.tick(world);
        for (EntityPlayerMP player : PENDING.keySet()) {
            if (player.worldObj != world && !player.isDead) continue;
            Integer id = PENDING.remove(player);
            if (id == null || player.isDead || player.worldObj != world) continue;
            Entity entity = world.getEntityByID(id);
            if (entity instanceof EntityHerobrine && ((EntityHerobrine) entity).isViewer(player)) entity.setDead();
        }
    }
    public static final class Handler implements IMessageHandler<HerobrineCancellation, IMessage> {
        public IMessage onMessage(HerobrineCancellation message, MessageContext context) {
            PENDING.put(context.getServerHandler().playerEntity, message.entityId);
            return null;
        }
    }
}
