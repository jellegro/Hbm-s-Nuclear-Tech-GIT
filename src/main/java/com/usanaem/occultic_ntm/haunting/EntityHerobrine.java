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

    private long spawnTime;
    private boolean noticed, viewerNoticed;
    private boolean turned;
    private int turnDelay;
    private int stareDuration;
    private boolean retreated;
    private boolean presenceDriven, pursued, approachReported, crossing;
    private double initialDistance, approached, emergeX, emergeZ;
    private int emergeRemaining;

    public EntityHerobrine(World world) {
        super(world); setSize(.6F, 1.8F); isImmuneToFire = true;
        // Vanilla's default cutoff for this body is ~64 blocks; Lurking reaches 100.
        renderDistanceWeight = 2D;
        spawnTime = HauntingDirector.age(world);
    }
    @Override protected void entityInit() {
        super.entityInit(); dataWatcher.addObject(20, (byte) 0); dataWatcher.addObject(21, (byte) 0); dataWatcher.addObject(22, (byte) 0);
    }
    public int skinVariant() { return dataWatcher.getWatchableObjectByte(20) == 1 ? 1 : 0; }
    public boolean isViewer(EntityPlayer player) { return viewer != null && viewer.equals(player.getUniqueID()); }
    public Sighting sighting() { return sighting; }
    public long bornAt() { return spawnTime; }
    public boolean noticed() { return viewerNoticed; }
    public boolean pursued() { return pursued; }
    void presenceDriven(boolean natural) { presenceDriven = natural; }
    void emerge(double dx, double dz, int steps, boolean pass) {
        emergeX = dx; emergeZ = dz; emergeRemaining = steps; crossing = pass;
        if (pass) { rotationYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90); rotationYawHead = renderYawOffset = rotationYaw; }
    }
    private void notice(EntityPlayer player) {
        if (!noticed) { noticed = true; HauntingStats.record(sighting, HauntingStats.Outcome.NOTICED); }
        if (isViewer(player) && !viewerNoticed) {
            viewerNoticed = true;
            if (player instanceof EntityPlayerMP) HauntingPresence.noticed((EntityPlayerMP) player, this);
        }
    }
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
        spawnTime = HauntingDirector.age(worldObj);
        this.noticed = false;
        this.viewerNoticed = false;
        this.turned = false;
        this.retreated = false;
        turnDelay = 20 + worldObj.rand.nextInt(11);
        stareDuration = (escalated ? 120 : 60) + worldObj.rand.nextInt(41);
        dataWatcher.updateObject(20, (byte) worldObj.rand.nextInt(HerobrineSkins.COUNT));
        dataWatcher.updateObject(21, (byte) kind.ordinal());
        setPosition(x, y, z);
        initialDistance = player.getDistanceToEntity(this);
        face(player);
        if (sighting.wandersOff()) {
            float offset = (worldObj.rand.nextBoolean() ? 1 : -1) * (35 + worldObj.rand.nextInt(21));
            rotationYaw += offset;
            rotationYawHead = renderYawOffset = rotationYaw;
        }
        dwellingShadow = kind == Sighting.DWELLING && localLight() <= 8;
        if (dwellingShadow) {
            lifetime = Math.max(lifetime, 1200);
            face(player);
        }
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
        HauntingStats.record(sighting, HauntingStats.Outcome.ARRIVED);
        return true;
    }
    private void dissipate() {
        if (arrived && !isDead) {
            worldObj.setEntityState(this, DISSIPATE);
            worldObj.playSoundEffect(posX, posY + 1, posZ, "mob.endermen.portal", .12F, .7F);
            HauntingStats.record(sighting, HauntingStats.Outcome.DISSIPATED);
            if (!viewerNoticed) HauntingStats.record(sighting, HauntingStats.Outcome.EXPIRED_UNSEEN);
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
            double moved = Math.sqrt((posX - prevPosX) * (posX - prevPosX) + (posZ - prevPosZ));
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
        if (emergeRemaining > 0) {
            if (!HauntingPlacement.deliverable(player, posX + emergeX, posZ + emergeZ)
                    || player.getDistanceSqToEntity(this) < 3 * 3
                    || !HauntingPlacement.valid(worldObj, posX + emergeX, posY, posZ + emergeZ)) { dissipate(); return; }
            setPosition(posX + emergeX, posY, posZ + emergeZ); emergeRemaining--;
            if (crossing) {
                if (ticksExisted % 4 == 0) for (Object object : worldObj.playerEntities) {
                    EntityPlayer witness = (EntityPlayer) object;
                    if (!witness.isEntityAlive()) continue;
                    HauntingPerception.State seen = HauntingPerception.observe(witness, posX, posY + 1.6, posZ, sighting.cone);
                    if (seen == HauntingPerception.State.OBSERVED || seen == HauntingPerception.State.FIXATED
                            || seen == HauntingPerception.State.OBSERVED_THROUGH_GLASS) notice(witness);
                }
                if (emergeRemaining == 0) {
                    HauntingStats.record(sighting, HauntingStats.Outcome.CROSSED);
                    if (HauntingCover.concealed(this, posX, posZ)) dissipate();
                }
                return;
            }
            // Stop at first exposure. The next gaze check owns observation/withdrawal, not the entrance walk.
            if (HauntingPlacement.exposed(worldObj, posX, posY, posZ)) emergeRemaining = 0;
            return;
        }
        if (slipRemaining > 0) {
            if (!HauntingPlacement.valid(worldObj, posX + slipX, posY, posZ + slipZ)) { dissipate(); return; }
            setPosition(posX + slipX, posY, posZ + slipZ); slipRemaining--;
            if (retreated) {
                rotationYaw = (float) (Math.toDegrees(Math.atan2(slipZ, slipX)) - 90);
                rotationYawHead = renderYawOffset = rotationYaw;
            }
            if (ticksExisted % 2 == 0 && HauntingCover.concealed(this, posX, posZ)) dissipate();
            // If cover changed during the walk, fall back to ordinary dissipation.
            if (slipRemaining == 0) dissipate();
            return;
        }
        if (ticksExisted % 4 != 0) return;
        if (viewerNoticed && !pursued && player.getDistanceToEntity(this) < 18 && initialDistance - player.getDistanceToEntity(this) > 6) {
            pursued = true; HauntingStats.record(sighting, HauntingStats.Outcome.PURSUED);
        }
        if (player == null || !player.isEntityAlive() || player.getDistanceSqToEntity(this) > sighting.tether * sighting.tether
                || player.getDistanceSqToEntity(this) < (sighting == Sighting.CREEPING || sighting == Sighting.NIGHTMARE ? 1.5 * 1.5 : 3 * 3)
                || !HauntingPlacement.valid(worldObj, posX, posY, posZ)
                || sighting == Sighting.NIGHTMARE && !player.isPlayerSleeping()) { dissipate(); return; }
        if (turned || !sighting.wandersOff() || dwellingShadow) face(player);
        if (dwellingShadow) {
            if (HauntingPerception.observe(player, posX, posY + 1.6, posZ, sighting.cone).ordinal() >= HauntingPerception.State.OBSERVED.ordinal()) notice(player);
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
            HauntingPerception.State state = HauntingPerception.observe(witness, posX, posY + 1.6, posZ, sighting.cone);
            if (state != HauntingPerception.State.UNSEEN) visible = true;
            if (state == HauntingPerception.State.OBSERVED || state == HauntingPerception.State.FIXATED
                    || state == HauntingPerception.State.OBSERVED_THROUGH_GLASS) notice(witness);
            if (state == HauntingPerception.State.OBSERVED_THROUGH_GLASS) window = true;
            if (state == HauntingPerception.State.OBSERVED || state == HauntingPerception.State.FIXATED) observed = true;
        }
        // Acquisition uses the observation cone; retention includes every witness's
        // peripheral contact. Brief boundary crossings must not vanish on screen.
        if (visible) lookAwayTicks = 0;
        else if (observationTicks > 0 || seenThroughGlass) lookAwayTicks += 4;
        if (window) {
            seenThroughGlass = true; return;
        }
        if (seenThroughGlass) { if (lookAwayTicks >= 12) dissipate(); return; }
        if (observed && !coverChecked && sighting != Sighting.NIGHTMARE) {
            coverChecked = true;
            double[] slip = HauntingCover.find(this, player);
            if (slip != null) {
                slipX = slip[0]; slipZ = slip[1]; slipRemaining = (int) slip[2];
                HauntingStats.record(sighting, HauntingStats.Outcome.SLIPPED);
                return;
            }
        }
        if (observed) {
            if (sighting.wandersOff() && !turned) {
                if (observationTicks >= turnDelay) {
                    turned = true;
                    face(player);
                    HauntingStats.record(sighting, HauntingStats.Outcome.TURNED);
                }
            }
            observationTicks += 4;
        }
        int maxStare = sighting.wandersOff() ? (stareDuration + turnDelay) : (escalated ? 160 : 100);
        if (observationTicks >= maxStare) {
            if (sighting.wandersOff() && !retreated) {
                double[] retreat = HauntingCover.retreat(this, player);
                if (retreat != null) {
                    retreated = true;
                    slipX = retreat[0]; slipZ = retreat[1]; slipRemaining = (int) retreat[2];
                    HauntingStats.record(sighting, HauntingStats.Outcome.RETREATED);
                    rotationYaw = (float) (Math.toDegrees(Math.atan2(slipZ, slipX)) - 90);
                    rotationYawHead = renderYawOffset = rotationYaw;
                    return;
                }
            }
            dissipate(); return;
        }
        if (observationTicks > 0 && lookAwayTicks >= 12) dissipate();
        if (presenceDriven && !noticed && !isDead && ticksExisted % 12 == 0 && approached < 6
                && sighting.wandersOff() && !HauntingPlacement.exposed(worldObj, posX, posY, posZ)
                && HauntingPresence.read(player).intent == HauntingPresence.Intent.APPROACH) {
            double dx = player.posX - posX, dz = player.posZ - posZ, length = Math.sqrt(dx * dx + dz * dz);
            if (length > 8) {
                double tx = posX + dx / length * .16, tz = posZ + dz / length * .16;
                if (HauntingPlacement.deliverable(player, tx, tz) && HauntingPlacement.valid(worldObj, tx, posY, tz)
                        && !HauntingPlacement.exposed(worldObj, tx, posY, tz)) {
                    setPosition(tx, posY, tz); approached += .16;
                    if (!approachReported) { approachReported = true; HauntingStats.record(sighting, HauntingStats.Outcome.APPROACHED); }
                }
            }
        }
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
    @Override public boolean attackEntityFrom(DamageSource damage, float amount) {
        if (damage.getEntity() instanceof EntityPlayer) { notice((EntityPlayer) damage.getEntity()); dissipate(); }
        return false;
    }
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
