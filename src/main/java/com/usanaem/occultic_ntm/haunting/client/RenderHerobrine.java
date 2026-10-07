package com.usanaem.occultic_ntm.haunting.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

@SideOnly(Side.CLIENT)
public final class RenderHerobrine extends RendererLivingEntity {
    private final ModelBiped biped;
    public RenderHerobrine() { this(new ModelBiped()); }
    private RenderHerobrine(ModelBiped model) { super(model, 0F); biped = model; }
    protected ResourceLocation getEntityTexture(Entity entity) {
        return HerobrineSkinResources.INSTANCE.texture(((com.usanaem.occultic_ntm.haunting.EntityHerobrine) entity).skinVariant());
    }
    @Override public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTicks) {
        com.usanaem.occultic_ntm.haunting.EntityHerobrine figure = (com.usanaem.occultic_ntm.haunting.EntityHerobrine) entity;
        biped.heldItemRight = entity.getHeldItem() == null ? 0 : 1;
        if (!figure.isDead && HerobrineSkinResources.INSTANCE.ready(figure)) super.doRender(entity, x, y, z, yaw, partialTicks);
        biped.heldItemRight = 0;
    }
    @Override protected void renderEquippedItems(EntityLivingBase entity, float partialTicks) {
        renderEyes();
        net.minecraft.item.ItemStack held = entity.getHeldItem();
        if (held == null) return;
        org.lwjgl.opengl.GL11.glPushMatrix();
        try {
            biped.bipedRightArm.postRender(.0625F);
            org.lwjgl.opengl.GL11.glTranslatef(-.0625F, .625F, -.25F);
            org.lwjgl.opengl.GL11.glRotatef(20, 1, 0, 0);
            org.lwjgl.opengl.GL11.glRotatef(-45, 0, 0, 1);
            org.lwjgl.opengl.GL11.glScalef(.625F, -.625F, .625F);
            net.minecraft.client.renderer.entity.RenderManager.instance.itemRenderer.renderItem(entity, held, 0);
        } finally { org.lwjgl.opengl.GL11.glPopMatrix(); }
    }
    /** Two white eye patches on the classic 64x32 skin's front face; body stays naturally lit. */
    private void renderEyes() {
        float oldX = OpenGlHelper.lastBrightnessX, oldY = OpenGlHelper.lastBrightnessY;
        int oldUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS); GL11.glPushMatrix();
        try {
            biped.bipedHead.postRender(.0625F);
            GL11.glDisable(GL11.GL_LIGHTING); GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_CULL_FACE); GL11.glDepthMask(false);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            Tessellator t = Tessellator.instance;
            t.startDrawingQuads(); t.setColorRGBA_F(1, 1, 1, 1); t.setBrightness(0xF000F0);
            for (int left = -3; left <= 1; left += 4) {
                double x0 = left / 16D, x1 = (left + 2) / 16D;
                t.addVertex(x0, -4 / 16D, -4.01 / 16D);
                t.addVertex(x1, -4 / 16D, -4.01 / 16D);
                t.addVertex(x1, -3 / 16D, -4.01 / 16D);
                t.addVertex(x0, -3 / 16D, -4.01 / 16D);
            }
            t.draw();
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldX, oldY);
            GL11.glPopMatrix(); GL11.glPopAttrib(); OpenGlHelper.setActiveTexture(oldUnit);
        }
    }
    @Override protected boolean func_110813_b(EntityLivingBase entity) { return false; }
}
