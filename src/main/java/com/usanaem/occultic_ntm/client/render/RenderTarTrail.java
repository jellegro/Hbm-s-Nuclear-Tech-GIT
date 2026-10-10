package com.usanaem.occultic_ntm.client.render;

import java.util.Map;
import java.util.WeakHashMap;

import com.usanaem.occultic_ntm.block.TileTarTrail;
import com.usanaem.occultic_ntm.entity.client.TarMaterial;
import com.usanaem.occultic_ntm.registry.OcculticBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

/** Direct animated puddles, deliberately outside the compiled chunk terrain. */
@SideOnly(Side.CLIENT)
public final class RenderTarTrail extends TileEntitySpecialRenderer {
    private final Map<TileTarTrail, CachedMesh> meshes = new WeakHashMap<TileTarTrail, CachedMesh>();

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
        final TileTarTrail trail = (TileTarTrail) tile;
        World world = trail.getWorldObj();
        if (world == null || world.getBlock(trail.xCoord, trail.yCoord, trail.zCoord) != OcculticBlocks.tar_trail) return;
        int neighbors = 0;
        if (world.getBlock(trail.xCoord - 1, trail.yCoord, trail.zCoord) == OcculticBlocks.tar_trail) neighbors |= TarPuddleMesh.WEST;
        if (world.getBlock(trail.xCoord + 1, trail.yCoord, trail.zCoord) == OcculticBlocks.tar_trail) neighbors |= TarPuddleMesh.EAST;
        if (world.getBlock(trail.xCoord, trail.yCoord, trail.zCoord - 1) == OcculticBlocks.tar_trail) neighbors |= TarPuddleMesh.NORTH;
        if (world.getBlock(trail.xCoord, trail.yCoord, trail.zCoord + 1) == OcculticBlocks.tar_trail) neighbors |= TarPuddleMesh.SOUTH;
        CachedMesh cached = meshes.get(trail);
        if (cached == null || cached.neighbors != neighbors) {
            cached = new CachedMesh(trail.xCoord, trail.zCoord, neighbors);
            meshes.put(trail, cached);
        }
        final TarPuddleMesh mesh = cached.mesh;
        final int brightness = world.getLightBrightnessForSkyBlocks(trail.xCoord, trail.yCoord, trail.zCoord, 0);
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        int shadeModel = GL11.glGetInteger(GL11.GL_SHADE_MODEL);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            float age = (world.getTotalWorldTime() % 240000L) + partialTick;
            TarMaterial.INSTANCE.renderPuddle(age, trail.xCoord, trail.yCoord, trail.zCoord, new Runnable() {
                public void run() {
                    mesh.draw(brightness);
                }
            });
        } finally {
            GL11.glShadeModel(shadeModel);
            if (lighting) GL11.glEnable(GL11.GL_LIGHTING); else GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }

    private static final class CachedMesh {
        final int neighbors;
        final TarPuddleMesh mesh;
        CachedMesh(int x, int z, int neighbors) {
            this.neighbors = neighbors;
            mesh = new TarPuddleMesh(x, z, neighbors);
        }
    }
}
