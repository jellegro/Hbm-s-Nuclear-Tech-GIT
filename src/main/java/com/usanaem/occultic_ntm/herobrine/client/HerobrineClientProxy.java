package com.usanaem.occultic_ntm.herobrine.client;

import com.usanaem.occultic_ntm.herobrine.HerobrineProxy;
import com.usanaem.occultic_ntm.herobrine.SightingMessage;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

@SideOnly(Side.CLIENT)
public final class HerobrineClientProxy extends HerobrineProxy {
    private final HerobrineAppearance appearance = new HerobrineAppearance();

    @Override
    public void initialize() {
        FMLCommonHandler.instance().bus().register(appearance);
        MinecraftForge.EVENT_BUS.register(appearance);
    }

    @Override
    public void receive(SightingMessage message) {
        appearance.enqueue(message);
    }
}
