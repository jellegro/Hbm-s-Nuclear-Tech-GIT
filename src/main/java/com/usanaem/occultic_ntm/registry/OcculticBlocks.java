package com.usanaem.occultic_ntm.registry;

import com.usanaem.occultic_ntm.block.BlockBlackSun;
import com.usanaem.occultic_ntm.block.TileBlackSun;

import cpw.mods.fml.common.registry.GameRegistry;

public final class OcculticBlocks {

    public static BlockBlackSun black_sun;

    private OcculticBlocks() { }

    public static void init() {
        black_sun = new BlockBlackSun();
        GameRegistry.registerBlock(black_sun, "black_sun");
        GameRegistry.registerTileEntity(TileBlackSun.class, "occultic_ntm_tile_black_sun");
    }
}
