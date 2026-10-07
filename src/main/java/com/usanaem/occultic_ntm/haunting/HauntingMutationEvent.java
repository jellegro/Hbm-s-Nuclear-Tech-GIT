package com.usanaem.occultic_ntm.haunting;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

/** Protection integrations can veto any haunting mutation without optional dependencies. */
@Cancelable
public final class HauntingMutationEvent extends Event {
    public final World world;
    public final EntityPlayerMP owner;
    public final String kind;
    public final int x, y, z;
    public HauntingMutationEvent(World world, EntityPlayerMP owner, String kind, int x, int y, int z) {
        this.world = world; this.owner = owner; this.kind = kind; this.x = x; this.y = y; this.z = z;
    }
}
