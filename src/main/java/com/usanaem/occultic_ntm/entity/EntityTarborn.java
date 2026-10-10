package com.usanaem.occultic_ntm.entity;

import com.hbm.items.ItemEnums.EnumTarType;
import com.hbm.items.ModItems;
import com.usanaem.occultic_ntm.registry.OcculticBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Hostile manifestation of purged pulmonary contaminants (soot, coal dust, and asbestos).
 * Condensed into living coal tar, continually shedding heavy black droplets.
 */
public class EntityTarborn extends EntityMob {

    public EntityTarborn(World world) {
        super(world);
        this.setSize(0.6F, 1.8F);
        this.isImmuneToFire = true;

        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.25D, false));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.28D);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(24.0D);
    }

    private static final int MAX_TRAIL_LENGTH = 4;
    private boolean deathSmokeSpawned;
    private final java.util.LinkedList<TrailNode> tarTrail = new java.util.LinkedList<TrailNode>();

    private static class TrailNode {
        final int x, y, z;
        int life;
        TrailNode(int x, int y, int z, int life) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.life = life;
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.worldObj.isRemote) {
            if (this.getHealth() > 0) spawnClientEffects();
        } else {
            updateTarTrail();
        }
    }

    private void updateTarTrail() {
        if (OcculticBlocks.tar_trail == null) return;

        // 1. Fade out existing trail nodes over time
        java.util.Iterator<TrailNode> it = this.tarTrail.iterator();
        while (it.hasNext()) {
            TrailNode node = it.next();
            if (--node.life <= 0) {
                if (this.worldObj.getBlock(node.x, node.y, node.z) == OcculticBlocks.tar_trail) {
                    this.worldObj.setBlockToAir(node.x, node.y, node.z);
                    if (this.worldObj instanceof WorldServer) {
                        ((WorldServer) this.worldObj).func_147487_a("smoke", node.x + 0.5D, node.y + 0.1D, node.z + 0.5D, 3, 0.2D, 0.05D, 0.2D, 0.01D);
                    }
                }
                it.remove();
            }
        }

        // 2. Place single tile directly under foot center
        if (this.getHealth() <= 0 || !this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")) return;

        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);

        if (!this.tarTrail.isEmpty()) {
            TrailNode latest = this.tarTrail.getLast();
            if (latest.x == x && latest.y == y && latest.z == z) {
                return; // Already placed at this position
            }
        }

        if (this.worldObj.getBlock(x, y, z) == Blocks.air
                && OcculticBlocks.tar_trail.canPlaceBlockAt(this.worldObj, x, y, z)) {
            this.worldObj.setBlock(x, y, z, OcculticBlocks.tar_trail);
            this.tarTrail.addLast(new TrailNode(x, y, z, 70)); // ~3.5s lifespan
        }

        // 3. Strictly cap trail length to 3-5 tiles at most (MAX_TRAIL_LENGTH = 4)
        while (this.tarTrail.size() > MAX_TRAIL_LENGTH) {
            TrailNode oldest = this.tarTrail.removeFirst();
            if (this.worldObj.getBlock(oldest.x, oldest.y, oldest.z) == OcculticBlocks.tar_trail) {
                this.worldObj.setBlockToAir(oldest.x, oldest.y, oldest.z);
                if (this.worldObj instanceof WorldServer) {
                    ((WorldServer) this.worldObj).func_147487_a("smoke", oldest.x + 0.5D, oldest.y + 0.1D, oldest.z + 0.5D, 3, 0.2D, 0.05D, 0.2D, 0.01D);
                }
            }
        }
    }

    private void clearTarTrail() {
        if (OcculticBlocks.tar_trail == null) return;
        for (TrailNode node : this.tarTrail) {
            if (this.worldObj.getBlock(node.x, node.y, node.z) == OcculticBlocks.tar_trail) {
                this.worldObj.setBlockToAir(node.x, node.y, node.z);
            }
        }
        this.tarTrail.clear();
    }

    @SideOnly(Side.CLIENT)
    private void spawnClientEffects() {
        net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getMinecraft();
        if (client.renderViewEntity == null || this.getDistanceSqToEntity(client.renderViewEntity) > 32.0D * 32.0D) return;
        spawnClientSmoke(client);
        int interval = client.gameSettings.particleSetting == 2 ? 8 : client.gameSettings.particleSetting == 1 ? 3 : 1;
        if (this.ticksExisted % interval != 0) return;

        // Emit outside the skin. Drops generated inside the opaque body only appeared at its feet.
        double side = this.rand.nextBoolean() ? 1.0D : -1.0D;
        double localX, localZ, y;
        if (this.rand.nextInt(3) != 0) {
            float angle = MathHelper.cos(this.limbSwing * 0.6662F + (side < 0 ? (float)Math.PI : 0)) * this.limbSwingAmount;
            localX = side * 0.39D;
            localZ = -Math.sin(angle) * 0.65D;
            y = 1.4D - Math.cos(angle) * 0.65D;
        } else {
            localX = (this.rand.nextDouble() - 0.5D) * 0.45D;
            localZ = side * 0.15D;
            y = 0.65D + this.rand.nextDouble() * 0.85D;
        }
        double yaw = Math.toRadians(this.renderYawOffset);
        double dx = this.posX + localX * Math.cos(yaw) - localZ * Math.sin(yaw);
        double dz = this.posZ + localX * Math.sin(yaw) + localZ * Math.cos(yaw);
        client.effectRenderer.addEffect(new com.usanaem.occultic_ntm.client.particle.EntityTarDripFX(this.worldObj, dx, this.posY + y, dz));
    }

    @SideOnly(Side.CLIENT)
    private void spawnClientSmoke(net.minecraft.client.Minecraft client) {
        int interval = client.gameSettings.particleSetting == 2 ? 20 : client.gameSettings.particleSetting == 1 ? 8 : 4;
        // Stagger multiple Tarborn instead of emitting a synchronized cloud every few ticks.
        if ((this.ticksExisted + this.getEntityId()) % interval != 0) return;

        double angle = this.rand.nextDouble() * Math.PI * 2.0D;
        boolean aboveHead = this.rand.nextInt(3) == 0;
        double side = this.rand.nextBoolean() ? 1.0D : -1.0D;
        double radius = 0.1D + this.rand.nextDouble() * 0.1D;
        double localX = aboveHead ? Math.cos(angle) * radius : (this.rand.nextDouble() - 0.5D) * 0.42D;
        double localZ = aboveHead ? Math.sin(angle) * radius : side * 0.17D;
        double y = this.posY + (aboveHead ? this.height + 0.04D : this.height * (0.5D + this.rand.nextDouble() * 0.3D));
        double yaw = Math.toRadians(this.renderYawOffset);
        double offsetX = localX * Math.cos(yaw) - localZ * Math.sin(yaw);
        double offsetZ = localX * Math.sin(yaw) + localZ * Math.cos(yaw);
        double outwardX = aboveHead ? Math.cos(angle) : -side * Math.sin(yaw);
        double outwardZ = aboveHead ? Math.sin(angle) : side * Math.cos(yaw);
        // Birth outside the body keeps the smoke visible against its nearly opaque skin.
        net.minecraft.client.particle.EntitySmokeFX smoke = new net.minecraft.client.particle.EntitySmokeFX(
                this.worldObj, this.posX + offsetX, y, this.posZ + offsetZ,
                outwardX * 0.006D, 0.015D + this.rand.nextDouble() * 0.01D, outwardZ * 0.006D,
                0.9F + this.rand.nextFloat() * 0.25F);
        float shade = 0.18F + this.rand.nextFloat() * 0.1F;
        smoke.setRBGColorF(shade, shade, shade);
        smoke.setAlphaF(0.75F);
        client.effectRenderer.addEffect(smoke);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean hit = super.attackEntityAsMob(target);
        if (hit && target instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) target;
            // Blind the victim and cause debilitating weakness
            living.addPotionEffect(new PotionEffect(Potion.blindness.id, 60, 0));
            living.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 0));
            target.worldObj.playSoundAtEntity(target, "hbm:player.cough", 1.0F, 1.0F);
        }
        return hit;
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        if (effect != null && effect.getPotionID() == Potion.poison.id) {
            return false;
        }
        return super.isPotionApplicable(effect);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.inWall || source == DamageSource.drown) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void fall(float distance) {
        // The cohesive tar body absorbs falls.
    }

    @Override
    protected String getLivingSound() {
        return "mob.blaze.breathe";
    }

    @Override
    protected String getHurtSound() {
        return "random.fizz";
    }

    @Override
    protected String getDeathSound() {
        return "random.fizz";
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
//        int ashCount = 1 + this.rand.nextInt(2 + Math.max(0, looting));
//        this.entityDropItem(new ItemStack(ModItems.powder_ash, ashCount), 0.0F);
        if (this.rand.nextInt(3) == 0) {
            this.entityDropItem(new ItemStack(ModItems.powder_coal, 1), 0.0F);
        }
        // Possible Coal Tar drop (50% base chance, increased by looting)
        if (this.rand.nextInt(2) == 0 || (looting > 0 && this.rand.nextInt(3) <= looting)) {
            int tarCount = 1 + (looting > 0 ? this.rand.nextInt(looting + 1) : 0);
            this.entityDropItem(new ItemStack(ModItems.oil_tar, tarCount, EnumTarType.COAL.ordinal()), 0.0F);
        }
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (!this.deathSmokeSpawned && !this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
            this.deathSmokeSpawned = true;
            WorldServer ws = (WorldServer) this.worldObj;
            double centerY = this.posY + this.height * 0.5D;
            ws.func_147487_a("largesmoke", this.posX, centerY, this.posZ, 48, 0.32D, this.height * 0.38D, 0.32D, 0.04D);
            ws.func_147487_a("smoke", this.posX, centerY, this.posZ, 24, 0.4D, this.height * 0.4D, 0.4D, 0.06D);
        }
    }

    @Override
    protected void onDeathUpdate() {
        if (!this.worldObj.isRemote) {
            // Preserve vanilla/Forge experience rewards and server-side removal.
            super.onDeathUpdate();
        } else {
            // The initial smoke burst replaces vanilla's delayed white explosion puff.
            if (++this.deathTime >= 20) this.setDead();
        }
    }
}
