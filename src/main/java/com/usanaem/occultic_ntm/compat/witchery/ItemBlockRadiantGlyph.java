package com.usanaem.occultic_ntm.compat.witchery;

import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/** Gives block-identification overlays a normal registered stack, without free glyph placement. */
public final class ItemBlockRadiantGlyph extends ItemBlock {
    public ItemBlockRadiantGlyph(Block block) { super(block); setHasSubtypes(true); }
    @Override public int getMetadata(int damage) { return damage; }
    @SideOnly(Side.CLIENT) @Override public void getSubItems(Item item, CreativeTabs tab, List stacks) { }
    @Override public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) { return false; }
}
