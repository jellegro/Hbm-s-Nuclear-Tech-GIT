package com.usanaem.occultic_ntm.haunting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

public final class HauntingSafety {
    private HauntingSafety() { }
    public static boolean unobserved(World world, double x, double y, double z, double exclusion) {
        if (!HauntingPlacement.loaded(world, x, z, x, z)) return false;
        for (Object object : world.playerEntities) {
            EntityPlayer player = (EntityPlayer) object;
            if (!player.isEntityAlive()) continue;
            if (player.getDistanceSq(x, y, z) < exclusion * exclusion || HauntingPerception.watching(player, x + .5, y + .5, z + .5)) return false;
        }
        return true;
    }
    public static boolean permitted(World world, EntityPlayerMP player, String kind, int x, int y, int z) {
        if (!HauntingDirector.enabled() || world.isRemote || player == null || player.worldObj != world || !player.isEntityAlive()
                || !HauntingPlacement.loaded(world, x, z, x, z) || !world.canMineBlock(player, x, y, z)
                || !player.canPlayerEdit(x, y, z, 1, player.getHeldItem())) return false;
        if (MinecraftForge.EVENT_BUS.post(new HauntingMutationEvent(world, player, kind, x, y, z))) return false;
        return !MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(x, y, z, world, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), player));
    }
}
