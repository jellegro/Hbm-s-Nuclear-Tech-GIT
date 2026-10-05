package com.usanaem.occultic_ntm.block;

import java.util.Random;

import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.hazard.HazardEntry;
import com.hbm.hazard.HazardSystem;
import com.hbm.hazard.type.HazardTypeRadiation;
import com.hbm.items.ModItems;
import com.usanaem.occultic_ntm.OcculticNTM;
import com.usanaem.occultic_ntm.registry.OcculticItems;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.crafting.IInfusionStabiliser;

/**
 * The Black Sun (Sol Tenebris): An occult lead-void vessel constructed to capture,
 * condense, and catalyze the invisible light shed by radioactive decay.
 */
@Optional.InterfaceList({
    @Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
})
public class BlockBlackSun extends BlockContainer implements IInfusionStabiliser {

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    @SideOnly(Side.CLIENT)
    private IIcon iconSide;

    public BlockBlackSun() {
        super(Material.iron);
        setHardness(8.0F);
        setResistance(100.0F);
        setStepSound(soundTypeMetal);
        setBlockName("occultic_ntm.black_sun");
        setCreativeTab(OcculticNTM.CREATIVE_TAB);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileBlackSun();
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public boolean canStabaliseInfusion(World world, int x, int y, int z) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.iconTop = reg.registerIcon("occultic_ntm:black_sun_top");
        this.iconBottom = reg.registerIcon("occultic_ntm:black_sun_bottom");
        this.iconSide = reg.registerIcon("occultic_ntm:black_sun_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 1) return this.iconTop;
        if (side == 0) return this.iconBottom;
        return this.iconSide;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileBlackSun)) {
            return false;
        }
        TileBlackSun tile = (TileBlackSun) te;
        ItemStack held = player.getHeldItem();

        // 1. Diagnostic examination with empty hand, sneak, or diagnostic tools
        if (held == null || player.isSneaking()) {
            if (!world.isRemote) {
                float rads = tile.getStoredRadiation();
                String statusColor = EnumChatFormatting.GREEN.toString();
                String statusText = "Dormant";
                if (rads >= 6000.0F) {
                    statusColor = EnumChatFormatting.DARK_RED.toString();
                    statusText = "SUPERCRITICAL - CONTAINMENT FAILING";
                } else if (rads >= 3000.0F) {
                    statusColor = EnumChatFormatting.RED.toString();
                    statusText = "Straining";
                } else if (rads >= 1000.0F) {
                    statusColor = EnumChatFormatting.YELLOW.toString();
                    statusText = "Primed";
                }

                player.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.DARK_PURPLE + "[Sol Tenebris] " +
                        EnumChatFormatting.GRAY + "Stored Dose: " +
                        statusColor + String.format("%.1f", rads) + " / " + (int) TileBlackSun.MAX_RADIATION + " RAD " +
                        EnumChatFormatting.DARK_GRAY + "(" + statusText + ")"));
            }
            return true;
        }

        // 2. Transmutation of nuclear waste / depleted actinides into Atramentous Ingot
        if (isTransmutableWaste(held)) {
            if (tile.getStoredRadiation() >= TileBlackSun.TRANSMUTATION_COST) {
                if (!world.isRemote) {
                    tile.setStoredRadiation(tile.getStoredRadiation() - TileBlackSun.TRANSMUTATION_COST);
                    held.stackSize--;
                    if (held.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }

                    ItemStack result = new ItemStack(OcculticItems.atramentous_ingot);
                    if (!player.inventory.addItemStackToInventory(result)) {
                        EntityItem drop = new EntityItem(world, x + 0.5D, y + 1.2D, z + 0.5D, result);
                        world.spawnEntityInWorld(drop);
                    }

                    // Alchemical tax: Soul warp and disorientation
                    player.addPotionEffect(new PotionEffect(Potion.confusion.id, 120, 0));
                    tryAddTemporaryWarp(player, 1);

                    player.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.DARK_PURPLE + "[Sol Tenebris] " +
                            EnumChatFormatting.DARK_GREEN + "The atoms collapse under ionizing pressure. Atramentous metal crystallizes."));

                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "portal.portal", 0.8F, 0.5F);
                }
                return true;
            } else {
                if (!world.isRemote) {
                    player.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.DARK_PURPLE + "[Sol Tenebris] " +
                            EnumChatFormatting.RED + "Insufficient stored radiation. Requires at least " +
                            (int) TileBlackSun.TRANSMUTATION_COST + " RAD to catalyze transmutation."));
                }
                return true;
            }
        }

        // 3. Physical feeding of radioactive materials into the vessel
        float offeredRad = getRadioactiveFeedValue(held);
        if (offeredRad > 0.0F) {
            if (tile.getStoredRadiation() < TileBlackSun.MAX_RADIATION) {
                if (!world.isRemote) {
                    float toAdd = Math.min(offeredRad, TileBlackSun.MAX_RADIATION - tile.getStoredRadiation());
                    tile.addRadiation(toAdd);

                    held.stackSize--;
                    if (held.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }

                    // Yields Atramentous Slag
                    ItemStack slag = new ItemStack(OcculticItems.atramentous_slag);
                    if (!player.inventory.addItemStackToInventory(slag)) {
                        EntityItem drop = new EntityItem(world, x + 0.5D, y + 1.2D, z + 0.5D, slag);
                        world.spawnEntityInWorld(drop);
                    }

                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.fizz", 0.6F, 0.4F);
                    player.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.DARK_PURPLE + "[Sol Tenebris] " +
                            EnumChatFormatting.GRAY + "The Black Sun devours the radioactive essence, adding " +
                            EnumChatFormatting.GREEN + String.format("%.1f", toAdd) + " RAD" +
                            EnumChatFormatting.GRAY + " to the nexus."));
                }
                return true;
            } else {
                if (!world.isRemote) {
                    player.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.DARK_PURPLE + "[Sol Tenebris] " +
                            EnumChatFormatting.DARK_RED + "The vessel is saturated to capacity. It can absorb no more without rupturing!"));
                }
                return true;
            }
        }

        return false;
    }

    private void tryAddTemporaryWarp(EntityPlayer player, int amount) {
        try {
            ThaumcraftApiHelper.addWarpToPlayer(player, amount, true);
        } catch (Throwable ignored) { }
    }

    private boolean isTransmutableWaste(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        if (stack.getItem() == ModItems.nuclear_waste_long
                || stack.getItem() == ModItems.nuclear_waste_short
                || stack.getItem() == ModItems.nuclear_waste_long_depleted
                || stack.getItem() == ModItems.waste_uranium
                || stack.getItem() == ModItems.billet_nuclear_waste) {
            return true;
        }
        return false;
    }

    private float getRadioactiveFeedValue(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0.0F;

        // Check HBM HazardSystem for radiation entries
        for (HazardEntry entry : HazardSystem.getHazardsFromStack(stack)) {
            if (entry.getType() instanceof HazardTypeRadiation) {
                return 150.0F;
            }
        }

        // Known core radioactive fuels/isotopes
        if (stack.getItem() == ModItems.ingot_plutonium) return 500.0F;
        if (stack.getItem() == ModItems.ingot_uranium) return 200.0F;
        if (stack.getItem() == ModItems.ingot_uranium_fuel) return 400.0F;
        if (stack.getItem() == ModItems.pellet_rtg) return 500.0F;
        if (stack.getItem() == ModItems.nuclear_waste_long) return 100.0F;
        if (stack.getItem() == ModItems.nuclear_waste_short) return 50.0F;

        return 0.0F;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBlackSun && !world.isRemote) {
            TileBlackSun tile = (TileBlackSun) te;
            float rads = tile.getStoredRadiation();
            if (rads > 500.0F) {
                // Containment shattered by excavation: Conserved radiation spills outward!
                ChunkRadiationManager.proxy.incrementRad(world, x, y, z, rads);
                world.createExplosion(null, x + 0.5D, y + 0.5D, z + 0.5D, 2.5F, true);
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileBlackSun)) return;

        TileBlackSun tile = (TileBlackSun) te;
        float rads = tile.getStoredRadiation();
        if (rads <= 0.0F) return;

        // Inward void suction particles
        double px = x + 0.5D + (rand.nextDouble() - 0.5D) * 0.8D;
        double py = y + 1.05D;
        double pz = z + 0.5D + (rand.nextDouble() - 0.5D) * 0.8D;
        world.spawnParticle("portal", px, py, pz, (x + 0.5D - px) * 0.5D, -0.1D, (z + 0.5D - pz) * 0.5D);

        if (rads >= 1000.0F && rand.nextInt(3) == 0) {
            world.spawnParticle("smoke", x + 0.5D, y + 1.0D, z + 0.5D, 0.0D, 0.02D, 0.0D);
        }

        if (rads >= 3000.0F && rand.nextInt(2) == 0) {
            world.spawnParticle("depthsuspend", x + 0.5D + (rand.nextDouble() - 0.5D) * 2.0D, y + 1.0D + rand.nextDouble(), z + 0.5D + (rand.nextDouble() - 0.5D) * 2.0D, 0.0D, 0.0D, 0.0D);
        }

        if (rads >= 6000.0F) {
            world.spawnParticle("largesmoke", x + 0.5D, y + 1.1D, z + 0.5D, (rand.nextDouble() - 0.5D) * 0.05D, 0.05D, (rand.nextDouble() - 0.5D) * 0.05D);
            if (rand.nextInt(8) == 0) {
                world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "ambient.cave.cave", 0.5F, 0.6F, false);
            }
        }
    }
}
