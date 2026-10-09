package com.usanaem.occultic_ntm.anomaly.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A camera-facing blue apparition using the installed vanilla flame particle sprite. */
@SideOnly(Side.CLIENT)
public final class RenderBlueFlame extends Render {
    private static final ResourceLocation ATLAS = new ResourceLocation("textures/particle/particles.png");
    private static final java.nio.FloatBuffer BLUE = BufferUtils.createFloatBuffer(4);
    // EntityFlameFX uses sprite 48: column 0, row 3 of the 16x16 particle atlas.
    private static final double U0 = 0, U1 = 0.0624375, V0 = 3.0 / 16, V1 = V0 + 0.0624375;
    static { BLUE.put(new float[] {0.18F, 0.75F, 1F, 1F}).flip(); }
    protected ResourceLocation getEntityTexture(Entity entity) { return ATLAS; }
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity.isDead) return;
        float oldX = OpenGlHelper.lastBrightnessX, oldY = OpenGlHelper.lastBrightnessY;
        int oldUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS); GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y + 0.325 + Math.sin((entity.ticksExisted + partialTicks) * 0.16) * 0.04, z);
            GL11.glRotatef(180F - renderManager.playerViewY, 0, 1, 0);
            GL11.glRotatef(-renderManager.playerViewX, 1, 0, 0);
            GL11.glDisable(GL11.GL_LIGHTING); GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            bindEntityTexture(entity);
            // Plain multiplication cannot turn orange pixels blue. Remap between
            // blue shades while preserving the installed sprite's original alpha.
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_COMBINE_RGB, GL13.GL_INTERPOLATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE0_RGB, GL13.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE1_RGB, GL13.GL_PRIMARY_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_SOURCE2_RGB, GL11.GL_TEXTURE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL13.GL_OPERAND2_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, BLUE);
            Tessellator t = Tessellator.instance;
            t.startDrawingQuads(); t.setColorRGBA_F(0.05F, 0.2F, 0.95F, 0.9F);
            t.addVertexWithUV(-0.325, -0.325, 0, U0, V1); t.addVertexWithUV(0.325, -0.325, 0, U1, V1);
            t.addVertexWithUV(0.325, 0.325, 0, U1, V0); t.addVertexWithUV(-0.325, 0.325, 0, U0, V0);
            t.draw();
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldX, oldY);
            GL11.glPopMatrix(); GL11.glPopAttrib();
            OpenGlHelper.setActiveTexture(oldUnit);
        }
    }
}
