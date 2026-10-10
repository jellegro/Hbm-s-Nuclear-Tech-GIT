package com.usanaem.occultic_ntm.block;

import net.minecraft.tileentity.TileEntity;

/** Stateless render anchor; trail lifetime and contact effects remain block/entity owned. */
public final class TileTarTrail extends TileEntity {
    @Override
    public boolean canUpdate() {
        return false;
    }
}
