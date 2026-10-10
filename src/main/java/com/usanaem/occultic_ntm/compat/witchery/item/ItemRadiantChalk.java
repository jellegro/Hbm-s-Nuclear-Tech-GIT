package com.usanaem.occultic_ntm.compat.witchery.item;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.util.SoundEffect;
import com.usanaem.occultic_ntm.OcculticNTM;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

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
        BlockSnapshot snapshot = BlockSnapshot.getBlockSnapshot(world, x, y, z);
        int metadata = existing ? world.getBlockMetadata(x, y, z) : world.rand.nextInt(12);
        if (!world.setBlock(x, y, z, RadiantChalk.glyph, metadata, 3)) return false;
		SoundEffect.WITCHERY_RANDOM_CHALK.playAt(world, x, y, z, 1.0F, 1.0F);
        BlockEvent.PlaceEvent event = ForgeEventFactory.onPlayerBlockPlace(player, snapshot, ForgeDirection.getOrientation(side));
        if (event.isCanceled()) { snapshot.restore(true, false); return false; }
        if (!player.capabilities.isCreativeMode) stack.damageItem(1, player);
        return true;
    }
}
