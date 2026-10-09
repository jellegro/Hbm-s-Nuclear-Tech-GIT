package com.usanaem.occultic_ntm.anomaly.impl;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Marker only. It has no fire block, interaction, inventory, damage or reward callback. */
public final class EntityBlueFlame extends Entity {
    private int remainingTicks = 600;
    public EntityBlueFlame(World world) { super(world); setSize(0.4F, 0.6F); isImmuneToFire = true; }
    protected void entityInit() { }
    public void onUpdate() {
        super.onUpdate();
        if (--remainingTicks <= 0) { setDead(); return; }
        if (!worldObj.isRemote && ticksExisted % 5 == 0) {
            for (Object candidate : worldObj.playerEntities) {
                net.minecraft.entity.player.EntityPlayer player = (net.minecraft.entity.player.EntityPlayer) candidate;
                if (player.isEntityAlive() && player.getDistanceSqToEntity(this) <= 16) { setDead(); break; }
            }
        }
    }
    protected void readEntityFromNBT(NBTTagCompound tag) { setDead(); }
    protected void writeEntityToNBT(NBTTagCompound tag) { }
    public boolean writeToNBTOptional(NBTTagCompound tag) { return false; }
    public boolean canBeCollidedWith() { return false; }
}
