package com.usanaem.occultic_ntm.entity.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

/**
 * Humanoid renderer for the Particulate Spectre.
 * Dark, slightly translucent resin with view-dependent wet surface reflections.
 */
@SideOnly(Side.CLIENT)
public class RenderTarborn extends RenderBiped {

    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation("occultic_ntm", "textures/entity/tar_spectre.png");

    public RenderTarborn() {
        super(new ModelTarborn(), 0.5F);
        // A second head shell would obscure the translucent surface with overlapping faces.
        this.modelBipedMain.bipedHeadwear.showModel = false;

		// Disable shadow rendering.
		this.shadowSize = 0.0F;
	}

    @Override
    public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTick) {
        // The body disperses immediately; vanilla still owns death timing, loot and XP.
        if (hasDispersed(entity)) return;
        super.doRender(entity, x, y, z, yaw, partialTick);
    }

    @Override
    public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float yaw, float partialTick) {
        // RenderManager submits this separately from the body draw.
        if (hasDispersed(entity)) return;
        super.doRenderShadowAndFire(entity, x, y, z, yaw, partialTick);
    }

    private static boolean hasDispersed(Entity entity) {
        return entity instanceof EntityLivingBase && (((EntityLivingBase) entity).getHealth() <= 0
                || ((EntityLivingBase) entity).deathTime > 0);
    }

    @Override
    protected void renderModel(final EntityLivingBase entity, final float swing, final float amount,
            final float age, final float headYaw, final float headPitch, final float scale) {
        if (entity.isInvisible()) {
            super.renderModel(entity, swing, amount, age, headYaw, headPitch, scale);
            return;
        }
        TarMaterial.INSTANCE.render(age, new Runnable() {
            @Override public void run() {
                RenderTarborn.super.renderModel(entity, swing, amount, age, headYaw, headPitch, scale);
            }
        });
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLiving entity) {
        return BASE_TEXTURE;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return BASE_TEXTURE;
    }
}
