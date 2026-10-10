package com.usanaem.occultic_ntm.compat.thaumcraft.block;

import java.util.List;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.radiation.ChunkRadiationManager;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.usanaem.occultic_ntm.registry.OcculticItems;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

/**
 * TileEntity for the Black Sun (Sol Tenebris).
 * Accumulates real HBM chunk and entity radiation as a conserved charge.
 * Leaks radiation and inflicts eldritch warp/dread as charge rises.
 * Ruptures catastrophically when overcharged or shattered.
 */
public class TileBlackSun extends TileEntity {

    public static final float MAX_RADIATION = 10000.0F;
    public static final float TRANSMUTATION_COST = 1000.0F;

    private float storedRadiation = 0.0F;

    public float getStoredRadiation() {
        return storedRadiation;
    }

    public void setStoredRadiation(float rads) {
        this.storedRadiation = Math.max(0.0F, Math.min(MAX_RADIATION, rads));
        markDirty();
        if (worldObj != null && !worldObj.isRemote) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public void addRadiation(float amount) {
        if (amount > 0.0F && !Float.isNaN(amount) && !Float.isInfinite(amount)) {
            setStoredRadiation(storedRadiation + amount);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setFloat("StoredRadiation", storedRadiation);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        storedRadiation = nbt.getFloat("StoredRadiation");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }

        // Ticking logic runs every 20 ticks (1 second)
        if (worldObj.getTotalWorldTime() % 20L != 0L) {
            return;
        }

        // 1. Siphon ambient chunk radiation (conserved transfer)
        if (storedRadiation < MAX_RADIATION) {
            float chunkRad = ChunkRadiationManager.proxy.getRadiation(worldObj, xCoord, yCoord, zCoord);
            if (chunkRad > 0.0F) {
                float drainRate = Math.min(10.0F, Math.max(0.5F, chunkRad * 0.1F));
                float toDrain = Math.min(chunkRad, Math.min(drainRate, MAX_RADIATION - storedRadiation));
                if (toDrain > 0.0F) {
                    ChunkRadiationManager.proxy.decrementRad(worldObj, xCoord, yCoord, zCoord, toDrain);
                    storedRadiation += toDrain;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    markDirty();
                }
            }
        }

        // 2. Leeches bodily radiation from entities standing directly atop the altar aperture
        if (storedRadiation < MAX_RADIATION) {
            AxisAlignedBB topBox = AxisAlignedBB.getBoundingBox(
                    xCoord, yCoord + 1, zCoord,
                    xCoord + 1, yCoord + 2, zCoord + 1);
            List<EntityLivingBase> onTop = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, topBox);
            for (EntityLivingBase entity : onTop) {
                float entRad = HbmLivingProps.getRadiation(entity);
                if (entRad > 0.0F && storedRadiation < MAX_RADIATION) {
                    float leech = Math.min(entRad, Math.min(5.0F, MAX_RADIATION - storedRadiation));
                    HbmLivingProps.incrementRadiation(entity, -leech);
                    storedRadiation += leech;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    markDirty();
                }
            }
        }

        // 3. Physical radiation hazard & eldritch leakage
        applyRadiationHazards();

        // 4. Overcritical rupture
        if (storedRadiation >= MAX_RADIATION) {
            ruptureAndDischarge();
        }
    }

    @SuppressWarnings("unchecked")
    private void applyRadiationHazards() {
        if (storedRadiation < 1000.0F) {
            return;
        }

        double range = 4.0D;
        if (storedRadiation >= 6000.0F) {
            range = 8.0D;
        } else if (storedRadiation >= 3000.0F) {
            range = 6.0D;
        }

        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                xCoord + 0.5D - range, yCoord + 0.5D - range, zCoord + 0.5D - range,
                xCoord + 0.5D + range, yCoord + 0.5D + range, zCoord + 0.5D + range);

        List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        float leakRate = (storedRadiation / 1000.0F) * 0.25F; // 0.25 to 2.5 RAD/s

        for (EntityLivingBase victim : entities) {
            double dist = victim.getDistance(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D);
            if (dist <= range) {
                float distFactor = (float) Math.max(0.1D, 1.0D - (dist / range));
                ContaminationUtil.contaminate(victim, HazardType.RADIATION, ContaminationType.CREATIVE, leakRate * distFactor);

                // Occult tension and warp effects on nearby minds
                if (storedRadiation >= 3000.0F) {
                    victim.addPotionEffect(new PotionEffect(Potion.hunger.id, 60, 0));
                    if (worldObj.rand.nextInt(8) == 0 && victim instanceof EntityPlayer) {
                        victim.addPotionEffect(new PotionEffect(Potion.confusion.id, 100, 0));
                    }
                }
                if (storedRadiation >= 6000.0F) {
                    victim.addPotionEffect(new PotionEffect(Potion.weakness.id, 60, 0));
                    if (worldObj.rand.nextInt(12) == 0) {
                        victim.addPotionEffect(new PotionEffect(Potion.blindness.id, 60, 0));
                    }
                }
            }
        }

        // Critical containment strain leaks radiation back into chunk atmosphere
        if (storedRadiation >= 6000.0F) {
            ChunkRadiationManager.proxy.incrementRad(worldObj, xCoord, yCoord, zCoord, 0.2F);
        }
    }

    /**
     * Catastrophic rupture: Conserves radiation by discharging every stored RAD
     * back into the chunk atmosphere, exploding, and leaving behind vitrified slag.
     */
    public void ruptureAndDischarge() {
        if (worldObj.isRemote) {
            return;
        }

        float dischargeRads = storedRadiation;
        storedRadiation = 0.0F;

        // Vents conserved radiation into chunk
        ChunkRadiationManager.proxy.incrementRad(worldObj, xCoord, yCoord, zCoord, dischargeRads);

        // Explosion
        worldObj.createExplosion(null, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 3.5F, true);

        // Drop Atramentous Slag
        ItemStack slag = new ItemStack(OcculticItems.atramentous_slag, 2 + worldObj.rand.nextInt(3));
        EntityItem drop = new EntityItem(worldObj, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, slag);
        worldObj.spawnEntityInWorld(drop);

        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    }
}
