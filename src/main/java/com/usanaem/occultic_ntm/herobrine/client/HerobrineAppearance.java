package com.usanaem.occultic_ntm.herobrine.client;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;
import com.usanaem.occultic_ntm.herobrine.SightingMessage;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;

/** Detached model: absent from world entity lists, saves, targeting and collision. */
@SideOnly(Side.CLIENT)
public final class HerobrineAppearance {
    // Account identity verified against Mojang's profile API. Vanilla owns skin loading/caching.
    private static final GameProfile PROFILE = new GameProfile(
            UUID.fromString("9586e5ab-157a-4658-ad80-b07552a9ca63"), "MHF_Herobrine");
    private final AtomicReference<SightingMessage> pending = new AtomicReference<>();
    private final ModelBiped model = new ModelBiped(0F);
    private EntityOtherPlayerMP figure;
    private int remainingTicks;
    private int skinWaitTicks;
    private int noticedTicks;
    private double viewerX;
    private double viewerY;
    private double viewerZ;
    private float yaw;

    public void enqueue(SightingMessage message) {
        // Network callback only publishes data; all world/model access happens on the client thread.
        pending.set(message);
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
            clear();
            return;
        }
        SightingMessage message = pending.getAndSet(null);
        if (message != null && message.isFinite() && message.dimension == minecraft.thePlayer.dimension
                && minecraft.thePlayer.getDistanceSq(message.x, message.y, message.z) <= 64D * 64D) {
            figure = new EntityOtherPlayerMP(minecraft.theWorld, PROFILE);
            figure.setPosition(message.x, message.y, message.z);
            yaw = (float) (Math.toDegrees(Math.atan2(minecraft.thePlayer.posZ - message.z,
                    minecraft.thePlayer.posX - message.x)) - 90D);
            remainingTicks = 100;
            skinWaitTicks = 200;
            noticedTicks = 0;
            viewerX = minecraft.thePlayer.posX;
            viewerY = minecraft.thePlayer.posY;
            viewerZ = minecraft.thePlayer.posZ;
        }
        if (figure == null) return;
        if (figure.worldObj != minecraft.theWorld || !minecraft.thePlayer.isEntityAlive()
                || minecraft.thePlayer.getDistanceSqToEntity(figure) < 12D * 12D
                || minecraft.thePlayer.getDistanceSqToEntity(figure) > 64D * 64D) {
            figure = null;
            return;
        }
        if (minecraft.thePlayer.getDistanceSq(viewerX, viewerY, viewerZ) > 16D * 16D) {
            figure = null;
            return;
        }
        viewerX = minecraft.thePlayer.posX;
        viewerY = minecraft.thePlayer.posY;
        viewerZ = minecraft.thePlayer.posZ;
        // func_152123_o is vanilla's has-skin check. Do not expose its default Steve fallback.
        if (!figure.func_152123_o()) {
            if (--skinWaitTicks <= 0) figure = null;
            return;
        }
        if (--remainingTicks <= 0) {
            figure = null;
            return;
        }
        Vec3 toward = Vec3.createVectorHelper(figure.posX - viewerX, figure.posY + 1.6D
                - viewerY - minecraft.thePlayer.getEyeHeight(), figure.posZ - viewerZ).normalize();
        double facing = minecraft.thePlayer.getLookVec().dotProduct(toward);
        if (facing > 0.96D) noticedTicks++;
        else if (noticedTicks >= 6 && facing < 0.65D) figure = null;
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        if (event.world.isRemote) clear();
    }

    private void clear() {
        pending.set(null);
        figure = null;
        remainingTicks = 0;
        skinWaitTicks = 0;
        noticedTicks = 0;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (figure == null || !figure.func_152123_o() || minecraft.renderViewEntity == null
                || figure.worldObj != minecraft.theWorld) return;
        Entity camera = minecraft.renderViewEntity;
        double x = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * event.partialTicks;
        double y = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * event.partialTicks;
        double z = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * event.partialTicks;
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(figure.posX - x, figure.posY - y, figure.posZ - z);
            GL11.glRotatef(180F - yaw, 0F, 1F, 0F);
            GL11.glScalef(-1F, -1F, 1F);
            GL11.glTranslatef(0F, -1.501F, 0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            int light = figure.getBrightnessForRender(event.partialTicks);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light & 65535, light >> 16);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            minecraft.getTextureManager().bindTexture(figure.getLocationSkin());
            model.render(figure, 0F, 0F, 0F, 0F, 0F, 0.0625F);
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }
}
