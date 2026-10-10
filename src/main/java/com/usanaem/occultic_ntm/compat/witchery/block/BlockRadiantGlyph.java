package com.usanaem.occultic_ntm.compat.witchery.block;

import java.util.Random;
import com.usanaem.occultic_ntm.OcculticNTM;

import com.usanaem.occultic_ntm.compat.witchery.item.RadiantChalk;
import com.usanaem.occultic_ntm.compat.witchery.rites.RadiantGlyphData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** A real fork-owned block; the installed native glyph supplies only pattern and support contracts. */
public final class BlockRadiantGlyph extends Block {
    private final Block pattern;
    public static final int COLOR = 0xAAD77A;

    public BlockRadiantGlyph(Block pattern) {
        super(pattern.getMaterial());
        this.pattern = pattern;
        setBlockName("occultic_ntm.radiant_glyph");
        setCreativeTab(OcculticNTM.CREATIVE_TAB);
        setHardness(2.0f);
        setResistance(1000.0f);
        setStepSound(soundTypeSand); 
        setLightLevel(3F / 15F);
        setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.015625f, 1.0f);
    }
    @SideOnly(Side.CLIENT) @Override public void registerBlockIcons(IIconRegister register) { }
    @SideOnly(Side.CLIENT) @Override public IIcon getIcon(int side, int metadata) { return pattern.getIcon(side, metadata); }
    @SideOnly(Side.CLIENT) @Override public int colorMultiplier(IBlockAccess world, int x, int y, int z) { return COLOR; }
    @SideOnly(Side.CLIENT) @Override public int getRenderColor(int metadata) { return COLOR; }
    @Override public boolean isOpaqueCube() { return false; }
    @Override public boolean renderAsNormalBlock() { return false; }
    @Override public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) { return null; }
    @Override public boolean canBlockStay(World world, int x, int y, int z) { return pattern.canBlockStay(world, x, y, z); }
    @Override public Item getItemDropped(int metadata, Random random, int fortune) { return null; }
    @Override public int quantityDropped(Random random) { return 0; }
    @Override public int damageDropped(int metadata) { return metadata; }
    @Override public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!world.isRemote && !canBlockStay(world, x, y, z)) world.setBlockToAir(x, y, z);
    }
    @Override public void onBlockAdded(World world, int x, int y, int z) {
        if (!world.isRemote) RadiantGlyphData.get(world).add(x, y, z);
    }
    @Override public void breakBlock(World world, int x, int y, int z, Block replacement, int metadata) {
        if (!world.isRemote) {
            RadiantGlyphData data = RadiantGlyphData.find(world);
            if (data != null) data.remove(x, y, z);
        }
        super.breakBlock(world, x, y, z, replacement, metadata);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        return RadiantChalk.item != null ? new ItemStack(RadiantChalk.item, 1, 0) : null;
    }
}
