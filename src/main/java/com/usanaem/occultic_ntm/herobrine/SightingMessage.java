package com.usanaem.occultic_ntm.herobrine;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/** A server-to-client visual cue; no entity spawn or client-to-server action. */
public final class SightingMessage implements IMessage {
    public int dimension;
    public double x;
    public double y;
    public double z;

    public SightingMessage() { }

    public SightingMessage(int dimension, double x, double y, double z) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        dimension = buffer.readInt();
        x = buffer.readDouble();
        y = buffer.readDouble();
        z = buffer.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(dimension);
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
    }

    public boolean isFinite() {
        return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
    }

    public static final class Handler implements IMessageHandler<SightingMessage, IMessage> {
        @Override
        public IMessage onMessage(SightingMessage message, MessageContext context) {
            if (message.isFinite()) HerobrineSightings.receive(message);
            return null;
        }
    }
}
