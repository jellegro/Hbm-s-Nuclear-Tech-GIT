package com.usanaem.occultic_ntm.haunting.client;

import java.io.InputStream;
import java.io.IOException;
import com.usanaem.occultic_ntm.haunting.HerobrineCancellation;
import com.usanaem.occultic_ntm.haunting.EntityHerobrine;
import com.usanaem.occultic_ntm.haunting.HerobrineSkins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Preflight decoding/upload prevents Minecraft's missing-texture substitution. */
@SideOnly(Side.CLIENT)
public final class HerobrineSkinResources implements IResourceManagerReloadListener {
    public static final HerobrineSkinResources INSTANCE = new HerobrineSkinResources();
    private final int[] state = new int[HerobrineSkins.COUNT]; // 0 unchecked, 1 loaded, -1 failed
    private HerobrineSkinResources() { }
    public ResourceLocation texture(int variant) { return new ResourceLocation("occultic_ntm", HerobrineSkins.path(variant)); }
    public void onResourceManagerReload(IResourceManager manager) { java.util.Arrays.fill(state, 0); }
    public boolean ready(EntityHerobrine figure) {
        int variant = figure.skinVariant();
        if (state[variant] == 0) {
            Minecraft mc = Minecraft.getMinecraft();
            ResourceLocation texture = texture(variant);
            try {
                try (InputStream stream = mc.getResourceManager().getResource(texture).getInputStream()) {
                    HerobrineSkins.validate(stream);
                }
                if (!mc.getTextureManager().loadTexture(texture, new SimpleTexture(texture)))
                    throw new IOException("Texture upload failed: " + texture);
                state[variant] = 1;
            } catch (IOException | RuntimeException failure) {
                state[variant] = -1;
                LogManager.getLogger("Occultic NTM").error("Herobrine sighting cancelled: skin could not load: " + texture, failure);
            }
        }
        if (state[variant] == 1) return true;
        cancel(figure);
        return false;
    }
    public void cancel(EntityHerobrine figure) {
        if (!figure.isDead) {
            HerobrineCancellation.request(figure);
            figure.setDead();
        }
    }
}
