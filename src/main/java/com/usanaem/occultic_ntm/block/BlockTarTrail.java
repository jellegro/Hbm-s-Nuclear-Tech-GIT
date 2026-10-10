package com.usanaem.occultic_ntm.block;

import java.util.Random;

import com.usanaem.occultic_ntm.OcculticNTM;
import com.usanaem.occultic_ntm.entity.EntityTarborn;
import com.usanaem.occultic_ntm.client.render.TarTrailIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Viscous, sticky residue shed by the Particulate Spectre.
 * Forms a shallow shiny puddle that heavily impedes movement like cobwebs/soul sand.
 * Slowly dissipates and dries up over time.
 */
public class BlockTarTrail extends Block {

    public BlockTarTrail() {
        super(Material.ground);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        this.setHardness(0.2F);
        this.setResistance(1.0F);
        this.setLightOpacity(0);
        this.setStepSound(Block.soundTypeGravel);
        this.setTickRandomly(true);
        this.setBlockName("occultic_ntm.tar_trail");
        this.setCreativeTab(OcculticNTM.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        // Generated sprite, not a resource-file registration: no tar trail PNG is needed.
        TextureMap atlas = (TextureMap) register;
        TarTrailIcon icon = new TarTrailIcon();
        if (atlas.setTextureEntry(icon.getIconName(), icon)) this.blockIcon = icon;
        else this.blockIcon = atlas.getTextureExtry(icon.getIconName());
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        // Null collision box allows entities to walk directly in, triggering contact slowdown
        return null;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        // In 1.7.10, true means pathfinding may pass through this block. The inherited
        // material check treats ground as blocked even though our collision box is null.
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        // Animated material is drawn by the TESR, outside compiled terrain batches.
        return -1;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileTarTrail();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return side == 1 ? true : super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        if (below == Blocks.air || below == this) {
            return false;
        }
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) || below.isOpaqueCube();
    }

    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return canPlaceBlockAt(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        if (!canBlockStay(world, x, y, z)) {
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityTarborn) {
            return; // Spectre is immune to its own tar
        }

        // Viscous movement damping (soul sand / cobweb feel)
        entity.motionX *= 0.35D;
        entity.motionZ *= 0.35D;
        if (entity.motionY > 0.0D) {
            entity.motionY *= 0.5D; // Inhibits jumping
        }

        // Apply Slowness II for tactile FOV and animation feedback
        if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 1));
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 80);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (!world.isRemote) {
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(6) == 0) {
            world.spawnParticle("smoke", x + rand.nextDouble(), y + 0.08D, z + rand.nextDouble(), 0.0D, 0.01D, 0.0D);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return null;
    }
}
