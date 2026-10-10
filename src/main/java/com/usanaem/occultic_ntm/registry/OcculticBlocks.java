package com.usanaem.occultic_ntm.registry;

import com.usanaem.occultic_ntm.block.BlockBlackSun;
import com.usanaem.occultic_ntm.block.BlockTarTrail;
import com.usanaem.occultic_ntm.block.TileBlackSun;
import com.usanaem.occultic_ntm.block.TileTarTrail;

import cpw.mods.fml.common.registry.GameRegistry;

public final class OcculticBlocks {

    public static BlockBlackSun black_sun;
    public static BlockTarTrail tar_trail;

    private OcculticBlocks() { }

    public static void init() {
        black_sun = new BlockBlackSun();
        tar_trail = new BlockTarTrail();

        GameRegistry.registerBlock(black_sun, "black_sun");
        GameRegistry.registerBlock(tar_trail, "tar_trail");
        GameRegistry.registerTileEntity(TileBlackSun.class, "occultic_ntm_tile_black_sun");
        GameRegistry.registerTileEntity(TileTarTrail.class, "occultic_ntm_tile_tar_trail");
    }
}
