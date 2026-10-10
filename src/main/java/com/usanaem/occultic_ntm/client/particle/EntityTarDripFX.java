package com.usanaem.occultic_ntm.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

/**
 * Viscous black tar droplet dripping from the Particulate Spectre.
 * Native water-drop atlas shapes, tinted tar black: hangs, stretches, then spatters.
 */
@SideOnly(Side.CLIENT)
public class EntityTarDripFX extends EntityFX {
    private final boolean splash;
    private int hangingTicks;

    public EntityTarDripFX(World world, double x, double y, double z) {
        this(world, x, y, z, 0.45F + world.rand.nextFloat() * 0.3F, false);
    }

    public EntityTarDripFX(World world, double x, double y, double z, float scale, boolean splash) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.splash = splash;
        this.hangingTicks = splash ? 0 : 2 + this.rand.nextInt(5);

        if (splash) {
            this.motionX = (this.rand.nextDouble() - 0.5D) * 0.055D;
            this.motionY = 0.025D + this.rand.nextDouble() * 0.035D;
            this.motionZ = (this.rand.nextDouble() - 0.5D) * 0.055D;
            this.particleMaxAge = 8 + this.rand.nextInt(5);
        } else {
            this.motionX = (this.rand.nextDouble() - 0.5D) * 0.01D;
            this.motionY = 0.0D;
            this.motionZ = (this.rand.nextDouble() - 0.5D) * 0.01D;
            this.particleMaxAge = 40 + this.rand.nextInt(20);
        }

        float shade = 0.045F + this.rand.nextFloat() * 0.025F;
        this.particleRed = shade;
        this.particleGreen = shade * 0.92F;
        this.particleBlue = shade * 0.85F;
        this.particleAlpha = 0.95F;

        this.setParticleTextureIndex(splash ? 113 : 112); // Native falling / hanging water beads
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.018F;
        this.particleScale = scale;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
            return;
        }

        if (hangingTicks > 0) {
            hangingTicks--;
            return;
        }
        this.setParticleTextureIndex(113);
        this.particleAlpha = Math.min(0.95F, (this.particleMaxAge - this.particleAge) / 8.0F);
        this.motionY -= this.particleGravity;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;

        if (this.onGround) {
            // Tiny beads rather than a spray; splash particles never recursively split.
            if (!this.splash) {
                for (int i = 0; i < 2; i++) {
                    EntityTarDripFX splash = new EntityTarDripFX(
                        this.worldObj, this.posX, this.posY + 0.02D, this.posZ, this.particleScale * 0.45F, true
                    );
                    net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(splash);
                }
            }
            this.setDead();
        }
    }

    @Override
    public void renderParticle(Tessellator tess, float partialTicks, float rightX, float upY,
            float rightZ, float upX, float upZ) {
        float u0 = this.particleTextureIndexX / 16.0F, u1 = u0 + 0.0624375F;
        float v0 = this.particleTextureIndexY / 16.0F, v1 = v0 + 0.0624375F;
        float halfWidth = 0.1F * this.particleScale;
        float stretch = this.splash || hangingTicks > 0 ? 1.0F : 1.25F + (float)Math.min(0.9D, Math.abs(this.motionY) * 3.0D);
        float halfHeight = halfWidth * stretch;
        float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
        tess.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tess.addVertexWithUV(x - rightX * halfWidth - upX * halfHeight, y - upY * halfHeight, z - rightZ * halfWidth - upZ * halfHeight, u1, v1);
        tess.addVertexWithUV(x - rightX * halfWidth + upX * halfHeight, y + upY * halfHeight, z - rightZ * halfWidth + upZ * halfHeight, u1, v0);
        tess.addVertexWithUV(x + rightX * halfWidth + upX * halfHeight, y + upY * halfHeight, z + rightZ * halfWidth + upZ * halfHeight, u0, v0);
        tess.addVertexWithUV(x + rightX * halfWidth - upX * halfHeight, y - upY * halfHeight, z + rightZ * halfWidth - upZ * halfHeight, u0, v1);
    }
}
