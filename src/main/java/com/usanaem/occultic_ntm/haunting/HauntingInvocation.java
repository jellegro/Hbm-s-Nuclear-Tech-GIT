package com.usanaem.occultic_ntm.haunting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/** Original coal/gold/obsidian invitation, checked only after deliberate ignition. */
public final class HauntingInvocation {
    private final Map<World, List<Pending>> pending = new WeakHashMap<World, List<Pending>>();
    private static final class Pending {
        final java.util.UUID player;
        final int x, y, z;
        Pending(EntityPlayer player, int x, int y, int z) { this.player = player.getUniqueID(); this.x = x; this.y = y; this.z = z; }
    }
    public static boolean structure(World world, int x, int y, int z) {
        if (y < 2 || !HauntingPlacement.loaded(world, x - 1, z - 1, x + 1, z + 1) || world.getBlock(x, y, z) != Blocks.netherrack) return false;
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            net.minecraft.block.Block required = dx == 0 && dz == 0 ? Blocks.coal_block : dx == 0 || dz == 0 ? Blocks.gold_block : Blocks.obsidian;
            if (world.getBlock(x + dx, y - 1, z + dz) != required || world.getTileEntity(x + dx, y - 1, z + dz) != null) return false;
        }
        return true;
    }
    public void interacted(PlayerInteractEvent event) {
        if (!HauntingDirector.enabled() || event.world.isRemote || event.isCanceled() || event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
                || event.face != 1 || event.entityPlayer.getHeldItem() == null || event.entityPlayer.getHeldItem().getItem() != Items.flint_and_steel
                || !structure(event.world, event.x, event.y, event.z)) return;
        List<Pending> list = pending.get(event.world);
        if (list == null) { list = new ArrayList<Pending>(); pending.put(event.world, list); }
        if (list.size() < 8) list.add(new Pending(event.entityPlayer, event.x, event.y, event.z));
    }
    public void tick(World world) {
        List<Pending> list = pending.remove(world);
        if (list == null || !HauntingDirector.enabled()) return;
        for (Pending attempt : list) {
            EntityPlayer player = world.func_152378_a(attempt.player);
            if (!(player instanceof EntityPlayerMP) || player.getDistanceSq(attempt.x, attempt.y, attempt.z) > 8 * 8
                    || !structure(world, attempt.x, attempt.y, attempt.z) || world.getBlock(attempt.x, attempt.y + 1, attempt.z) != Blocks.fire
                    || !HauntingSafety.permitted(world, (EntityPlayerMP) player, "invocation", attempt.x, attempt.y, attempt.z)) continue;
            activate((EntityPlayerMP) player);
        }
    }
    public static boolean activate(EntityPlayerMP player) {
        if (!HauntingDirector.enabled() || !player.isEntityAlive() || player.worldObj.isRemote) return false;
        World overworld = DimensionManager.getWorld(0);
        HauntingWorldState state = HauntingWorldState.get(overworld == null ? player.worldObj : overworld);
        state.invoked = true; state.surgeUntil = HauntingDirector.age(player.worldObj) + 3000;
        state.surgeDecayUntil = state.surgeUntil + 7200; state.markDirty();
        player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "ambient.cave.cave", .18F, .7F);
        HauntingDirector.spawn(player, Sighting.INVOCATION, true);
        return true;
    }
    public void unload(World world) { pending.remove(world); }
}
