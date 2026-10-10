package com.usanaem.occultic_ntm.entity.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/** Native biped geometry/animation, submitted directly while the tar material is bound. */
@SideOnly(Side.CLIENT)
public final class ModelTarborn extends ModelBiped {
    public ModelTarborn() {
        super(0.0F);
        this.bipedHeadwear.showModel = false;
    }

    @Override
    public void render(Entity entity, float swing, float amount, float age, float headYaw, float headPitch, float scale) {
        this.setRotationAngles(swing, amount, age, headYaw, headPitch, scale, entity);
        // This entity is always adult. Avoid ModelRenderer.render/display lists: optimized
        // cuboid submission can defer the geometry past the lifetime of our custom program.
        drawPart(this.bipedHead, scale);
        drawPart(this.bipedBody, scale);
        drawPart(this.bipedRightArm, scale);
        drawPart(this.bipedLeftArm, scale);
        drawPart(this.bipedRightLeg, scale);
        drawPart(this.bipedLeftLeg, scale);
    }

    private static void drawPart(ModelRenderer part, float scale) {
        if (!part.showModel || part.isHidden) return;
        GL11.glPushMatrix();
        try {
            GL11.glTranslatef(part.offsetX, part.offsetY, part.offsetZ);
            GL11.glTranslatef(part.rotationPointX * scale, part.rotationPointY * scale, part.rotationPointZ * scale);
            float degrees = 180.0F / (float)Math.PI;
            if (part.rotateAngleZ != 0) GL11.glRotatef(part.rotateAngleZ * degrees, 0, 0, 1);
            if (part.rotateAngleY != 0) GL11.glRotatef(part.rotateAngleY * degrees, 0, 1, 0);
            if (part.rotateAngleX != 0) GL11.glRotatef(part.rotateAngleX * degrees, 1, 0, 0);
            for (Object box : part.cubeList) ((ModelBox)box).render(Tessellator.instance, scale);
        } finally {
            GL11.glPopMatrix();
        }
    }
}
