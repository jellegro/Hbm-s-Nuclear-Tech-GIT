package com.usanaem.occultic_ntm.compat.witchery;

import com.emoniph.witchery.Witchery;
import com.usanaem.occultic_ntm.OcculticNTM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Uses the native glyph placement contract demonstrated by MIT addon-owned ItemElderChalk. */
public final class ItemRadiantChalk extends Item {
    public ItemRadiantChalk() {
        setUnlocalizedName("occultic_ntm.radiant_chalk"); setTextureName("occultic_ntm:radiant_chalk");
        setCreativeTab(OcculticNTM.CREATIVE_TAB); setMaxStackSize(1); setMaxDamage(64); setNoRepair();
    }
    @Override public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.getBlock(x, y, z) == RadiantChalk.glyph) return false;
        boolean existing = world.getBlock(x, y, z) == Witchery.Blocks.GLYPH_RITUAL;
        if (!existing) {
            if (side != 1) return false;
            y++;
            if (!world.isAirBlock(x, y, z)) return false;
        }
        if (!player.canPlayerEdit(x, y, z, side, stack) || !Witchery.Blocks.GLYPH_RITUAL.canBlockStay(world, x, y, z)) return false;
        if (world.isRemote) return true;
        if (RadiantChalk.isRadiantGlyph(world, x, y, z)) return false;
        // Permit cancellable Forge placement for claimed/protected ritual sites.
        net.minecraftforge.common.util.BlockSnapshot snapshot = net.minecraftforge.common.util.BlockSnapshot.getBlockSnapshot(world, x, y, z);
        int metadata = existing ? world.getBlockMetadata(x, y, z) : world.rand.nextInt(12);
        if (!world.setBlock(x, y, z, RadiantChalk.glyph, metadata, 3)) return false;
        net.minecraftforge.event.world.BlockEvent.PlaceEvent event = net.minecraftforge.event.ForgeEventFactory.onPlayerBlockPlace(player, snapshot, net.minecraftforge.common.util.ForgeDirection.getOrientation(side));
        if (event.isCanceled()) { snapshot.restore(true, false); return false; }
        if (!player.capabilities.isCreativeMode) stack.damageItem(1, player);
        return true;
    }
}
