package com.usanaem.occultic_ntm.item;

import com.usanaem.occultic_ntm.OcculticNTM;
import com.usanaem.occultic_ntm.entity.EntityTarborn;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Creative and testing spawn egg for the Particulate Spectre.
 * Renders dynamically with vanilla spawn egg textures tinted with soot and smoke colors.
 */
public class ItemTarbornEgg extends Item {

    // Dark charcoal soot body (primary), pale ash/smoke flecks (secondary)
    public static final int PRIMARY_COLOR = 0x242424;
    public static final int SECONDARY_COLOR = 0xA8A8A8;

    @SideOnly(Side.CLIENT)
    private IIcon theIcon;

    public ItemTarbornEgg() {
        this.setUnlocalizedName("occultic_ntm.spawn_egg_tarborn");
        this.setCreativeTab(OcculticNTM.CREATIVE_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return pass == 0 ? PRIMARY_COLOR : SECONDARY_COLOR;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.itemIcon = reg.registerIcon("spawn_egg");
        this.theIcon = reg.registerIcon("spawn_egg_overlay");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass > 0 ? this.theIcon : this.itemIcon;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        Block block = world.getBlock(x, y, z);

        // Allow reprogramming vanilla mob spawners
        if (block == Blocks.mob_spawner) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityMobSpawner) {
                TileEntityMobSpawner spawner = (TileEntityMobSpawner) te;
                spawner.func_145881_a().setEntityName("occultic_ntm.ParticulateSpectre");
                spawner.markDirty();
                world.markBlockForUpdate(x, y, z);

                if (!player.capabilities.isCreativeMode) {
                    --stack.stackSize;
                }
                return true;
            }
        }

        ForgeDirection dir = ForgeDirection.getOrientation(side);
        x += dir.offsetX;
        y += dir.offsetY;
        z += dir.offsetZ;

        double spawnY = y;
        if (side == 1 && block.getRenderType() == 11) {
            spawnY += 0.5D;
        }

        Entity entity = spawnSpectre(world, x + 0.5D, spawnY, z + 0.5D);
        if (entity != null) {
            if (entity instanceof EntityLiving && stack.hasDisplayName()) {
                ((EntityLiving) entity).setCustomNameTag(stack.getDisplayName());
            }

            if (!player.capabilities.isCreativeMode) {
                --stack.stackSize;
            }
        }

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }

        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (mop == null) {
            return stack;
        }

        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;

            if (!world.canMineBlock(player, x, y, z) || !player.canPlayerEdit(x, y, z, mop.sideHit, stack)) {
                return stack;
            }

            if (world.getBlock(x, y, z) instanceof BlockLiquid) {
                Entity entity = spawnSpectre(world, x + 0.5D, y, z + 0.5D);
                if (entity != null) {
                    if (entity instanceof EntityLiving && stack.hasDisplayName()) {
                        ((EntityLiving) entity).setCustomNameTag(stack.getDisplayName());
                    }

                    if (!player.capabilities.isCreativeMode) {
                        --stack.stackSize;
                    }
                }
            }
        }

        return stack;
    }

    public static Entity spawnSpectre(World world, double x, double y, double z) {
        EntityTarborn spectre = new EntityTarborn(world);
        if (spectre != null) {
            spectre.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
            spectre.rotationYawHead = spectre.rotationYaw;
            spectre.renderYawOffset = spectre.rotationYaw;
            spectre.onSpawnWithEgg((IEntityLivingData) null);
            world.spawnEntityInWorld(spectre);
            spectre.playLivingSound();
        }
        return spectre;
    }
}
