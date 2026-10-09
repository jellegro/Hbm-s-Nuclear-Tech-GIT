package com.usanaem.occultic_ntm.haunting;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.MathHelper;

/** Server-authoritative manifestation anchor. No combat AI, gravity, loot or saves. */
public final class EntityHerobrine extends EntityLivingBase {
    public static final String ENTITY_NAME = "occultic_ntm.EdgeOfSight";
    private UUID viewer;
    private Sighting sighting = Sighting.STALKING;
    private int lifetime, observationTicks, lookAwayTicks;
    private int waitingTicks;
    private int activeTicks;
    private boolean arrived, arrivalReported, dwellingShadow;
    private static final byte DISSIPATE = 64;
    private boolean escalated, seenThroughGlass;
    private boolean coverChecked;
    private double slipX, slipZ;
    private int slipRemaining;
    private int clientSteps;
    private double clientX, clientY, clientZ;
    private float clientYaw, clientPitch;
    public EntityHerobrine(World world) {
        super(world); setSize(.6F, 1.8F); isImmuneToFire = true;
        // Vanilla's default cutoff for this body is ~64 blocks; Lurking reaches 100.
        renderDistanceWeight = 2D;
    }
    @Override protected void entityInit() {
        super.entityInit(); dataWatcher.addObject(20, (byte) 0); dataWatcher.addObject(21, (byte) 0); dataWatcher.addObject(22, (byte) 0);
    }
    public int skinVariant() { return dataWatcher.getWatchableObjectByte(20) == 1 ? 1 : 0; }
    public boolean isViewer(EntityPlayer player) { return viewer != null && viewer.equals(player.getUniqueID()); }
    public Sighting sighting() { return sighting; }
    @Override public void setDead() {
        if (!isDead) HauntingDirector.retired(this, arrived);
        super.setDead();
    }
    public void manifest(EntityPlayer player, double x, double y, double z, boolean noticed) {
        manifest(player, x, y, z, Sighting.STALKING, noticed);
    }
    public void manifest(EntityPlayer player, double x, double y, double z, Sighting kind, boolean noticed) {
        viewer = player.getUniqueID(); sighting = kind; escalated = noticed;
        lifetime = kind.lifetime + (noticed ? 120 : 0);
        dataWatcher.updateObject(20, (byte) worldObj.rand.nextInt(HerobrineSkins.COUNT));
        dataWatcher.updateObject(21, (byte) kind.ordinal());
        setPosition(x, y, z); face(player);
        dwellingShadow = kind == Sighting.DWELLING && localLight() <= 8;
        if (dwellingShadow) lifetime = Math.max(lifetime, 1200);
    }
    private int localLight() {
        return worldObj.getBlockLightValue(MathHelper.floor_double(posX), MathHelper.floor_double(posY + 1), MathHelper.floor_double(posZ));
    }
    public void reportArrival() {
        if (worldObj.isRemote && !isDead && dataWatcher.getWatchableObjectByte(22) == 0
                && (!arrivalReported || ticksExisted % 20 == 0)) {
            arrivalReported = true;
            HerobrineArrival.request(this);
        }
    }
    /** Acknowledgment cannot activate another player's figure or an untracked entity. */
    public boolean confirmArrival(EntityPlayerMP player) {
        if (!(worldObj instanceof WorldServer) || isDead || arrived || !isViewer(player) || player.worldObj != worldObj
                || !player.isEntityAlive() || player.getDistanceSqToEntity(this) > 160 * 160
                || !HauntingPlacement.deliverable(player, posX, posZ)
                || !((WorldServer) worldObj).getEntityTracker().getTrackingPlayers(this).contains(player)) return false;
        arrived = true;
        dataWatcher.updateObject(22, (byte) 1);
        return true;
    }
    private void dissipate() {
        if (arrived && !isDead) {
            worldObj.setEntityState(this, DISSIPATE);
            worldObj.playSoundEffect(posX, posY + 1, posZ, "mob.endermen.portal", .12F, .7F);
        }
        setDead();
    }
    @Override @cpw.mods.fml.relauncher.SideOnly(cpw.mods.fml.relauncher.Side.CLIENT)
    public void handleHealthUpdate(byte status) {
        if (status != DISSIPATE) { super.handleHealthUpdate(status); return; }
        for (int i = 0; i < 12; i++) worldObj.spawnParticle("smoke", posX + (rand.nextDouble() - .5) * .6,
                posY + rand.nextDouble() * 1.8, posZ + (rand.nextDouble() - .5) * .6, 0, .025, 0);
    }
    private void face(EntityPlayer player) {
        rotationYaw = (float) (Math.toDegrees(Math.atan2(player.posZ - posZ, player.posX - posX)) - 90);
        rotationYawHead = renderYawOffset = rotationYaw;
        rotationPitch = sighting == Sighting.NIGHTMARE ? 15 : 0;
    }
    @Override public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int steps) {
        if (!worldObj.isRemote) { super.setPositionAndRotation2(x, y, z, yaw, pitch, steps); return; }
        clientX = x; clientY = y; clientZ = z; clientYaw = yaw; clientPitch = pitch;
        clientSteps = Math.max(1, steps);
    }
    @Override public void onLivingUpdate() {
        // Native living interpolation normally shares the AI/gravity update which
        // this anchor omits. Retain packet interpolation without running mob physics.
        if (worldObj.isRemote && clientSteps > 0) {
            setPosition(posX + (clientX - posX) / clientSteps, posY + (clientY - posY) / clientSteps, posZ + (clientZ - posZ) / clientSteps);
            rotationYaw += net.minecraft.util.MathHelper.wrapAngleTo180_float(clientYaw - rotationYaw) / clientSteps;
            rotationPitch += (clientPitch - rotationPitch) / clientSteps;
            clientSteps--;
        }
    }
    @Override public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) {
            prevRenderYawOffset = renderYawOffset; renderYawOffset = rotationYaw;
            prevRotationYawHead = rotationYawHead; rotationYawHead = rotationYaw;
            double moved = Math.sqrt((posX - prevPosX) * (posX - prevPosX) + (posZ - prevPosZ) * (posZ - prevPosZ));
            prevLimbSwingAmount = limbSwingAmount;
            limbSwingAmount += ((float) Math.min(1, moved * 4) - limbSwingAmount) * .4F;
            limbSwing += limbSwingAmount;
            com.usanaem.occultic_ntm.OcculticNTM.proxy.checkHerobrine(this); return;
        }
        if (!HauntingDirector.enabled() || viewer == null) { setDead(); return; }
        EntityPlayer player = worldObj.func_152378_a(viewer);
        if (player == null || !player.isEntityAlive()) { setDead(); return; }
        if (!arrived) {
            // Delivery/resource receipt precedes lifetime, gaze, cover and audio.
            if (++waitingTicks >= 200 || ticksExisted % 4 == 0 && !HauntingPlacement.valid(worldObj, posX, posY, posZ)) setDead();
            return;
        }
        if (--lifetime <= 0) { dissipate(); return; }
        activeTicks++;
        if (activeTicks % 10 == 0) CompanionAwareness.react(this);
        atmosphericCue();
        if (slipRemaining > 0) {
            if (!HauntingPlacement.valid(worldObj, posX + slipX, posY, posZ + slipZ)) { dissipate(); return; }
            setPosition(posX + slipX, posY, posZ + slipZ); slipRemaining--;
            if (ticksExisted % 2 == 0 && HauntingCover.concealed(this, posX, posZ)) dissipate();
            // If cover changed during the walk, fall back to ordinary dissipation.
            if (slipRemaining == 0) dissipate();
            return;
        }
        if (ticksExisted % 4 != 0) return;
        if (player == null || !player.isEntityAlive() || player.getDistanceSqToEntity(this) > sighting.tether * sighting.tether
                || player.getDistanceSqToEntity(this) < (sighting == Sighting.CREEPING || sighting == Sighting.NIGHTMARE ? 1.5 * 1.5 : 3 * 3)
                || !HauntingPlacement.valid(worldObj, posX, posY, posZ)
                || sighting == Sighting.NIGHTMARE && !player.isPlayerSleeping()) { dissipate(); return; }
        face(player);
        if (dwellingShadow) {
            if (localLight() > 8) dissipate();
            else for (Object object : worldObj.playerEntities) {
                EntityPlayer witness = (EntityPlayer) object;
                if (witness.isEntityAlive() && witness.getDistanceSqToEntity(this) < 3 * 3) { dissipate(); break; }
            }
            return;
        }
        if (sighting == Sighting.INVOCATION) return;
        boolean observed = false, window = false, visible = false;
        for (Object object : worldObj.playerEntities) {
            EntityPlayer witness = (EntityPlayer) object;
            if (!witness.isEntityAlive() || witness.getDistanceSqToEntity(this) > 128 * 128) continue;
            HauntingPerception.State state = HauntingPerception.observe(witness, posX, posY + 1.6, posZ);
            if (state != HauntingPerception.State.UNSEEN) visible = true;
            if (state == HauntingPerception.State.OBSERVED_THROUGH_GLASS) window = true;
            if (state == HauntingPerception.State.OBSERVED || state == HauntingPerception.State.FIXATED) observed = true;
        }
        // Acquisition uses the observation cone; retention includes every witness's
        // peripheral contact. Brief boundary crossings must not vanish on screen.
        if (visible) lookAwayTicks = 0;
        else if (observationTicks > 0 || seenThroughGlass) lookAwayTicks += 4;
        if (window) { seenThroughGlass = true; return; }
        if (seenThroughGlass) { if (lookAwayTicks >= 12) dissipate(); return; }
        if (observed && !coverChecked && sighting != Sighting.NIGHTMARE) {
            coverChecked = true;
            double[] slip = HauntingCover.find(this, player);
            if (slip != null) { slipX = slip[0]; slipZ = slip[1]; slipRemaining = (int) slip[2]; return; }
        }
        if (observed) observationTicks += 4;
        if (observationTicks >= (escalated ? 160 : 100) || observationTicks > 0 && lookAwayTicks >= 12) dissipate();
    }
    private void atmosphericCue() {
        if (sighting == Sighting.CREEPING && activeTicks == 40) {
            net.minecraft.block.Block floor = worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(posY) - 1, MathHelper.floor_double(posZ));
            worldObj.playSoundEffect(posX, posY, posZ, floor.stepSound.getStepResourcePath(), .18F, .9F);
        }
        // Initial six taps followed by three short, spaced bursts, ending before 15s.
        if (sighting == Sighting.GHOST_MINER && activeTicks % 8 == 0
                && (activeTicks <= 48 || activeTicks >= 88 && activeTicks <= 104
                    || activeTicks >= 168 && activeTicks <= 184 || activeTicks >= 248 && activeTicks <= 264))
            worldObj.playSoundEffect(posX, posY + 1, posZ, "dig.stone", .35F, .8F);
    }
    @Override public boolean attackEntityFrom(DamageSource damage, float amount) { return false; }
    @Override public boolean canBePushed() { return false; }
    @Override public void applyEntityCollision(Entity entity) { }
    @Override protected void collideWithNearbyEntities() { }
    @Override protected void dropFewItems(boolean player, int looting) { }
    @Override public ItemStack getHeldItem() {
        return dataWatcher.getWatchableObjectByte(21) == Sighting.GHOST_MINER.ordinal() ? new ItemStack(net.minecraft.init.Items.iron_pickaxe) : null;
    }
    @Override public ItemStack getEquipmentInSlot(int slot) { return slot == 0 ? getHeldItem() : null; }
    @Override public ItemStack[] getLastActiveItems() { return new ItemStack[5]; }
    @Override public void setCurrentItemOrArmor(int slot, ItemStack stack) { }
    @Override public boolean writeToNBTOptional(NBTTagCompound tag) { return false; }
    @Override public void readEntityFromNBT(NBTTagCompound tag) { super.readEntityFromNBT(tag); setDead(); }
}
