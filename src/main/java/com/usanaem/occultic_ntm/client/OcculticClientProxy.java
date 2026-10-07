package com.usanaem.occultic_ntm.client;

import com.usanaem.occultic_ntm.bootstrap.OcculticProxy;
import com.usanaem.occultic_ntm.anomaly.client.*;
import com.usanaem.occultic_ntm.anomaly.impl.*;
import com.usanaem.occultic_ntm.haunting.EntityHerobrine;
import com.usanaem.occultic_ntm.haunting.client.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class OcculticClientProxy extends OcculticProxy {
    public void initialize() {
        ((net.minecraft.client.resources.IReloadableResourceManager) net.minecraft.client.Minecraft.getMinecraft().getResourceManager())
                .registerReloadListener(HerobrineSkinResources.INSTANCE);
        RenderingRegistry.registerEntityRenderingHandler(EntityHerobrine.class, new RenderHerobrine());
        RenderingRegistry.registerEntityRenderingHandler(EntityBlueFlame.class, new RenderBlueFlame());
    }
    public void checkHerobrine(EntityHerobrine figure) {
        if (HerobrineSkinResources.INSTANCE.ready(figure)) figure.reportArrival();
    }
}
