package com.usanaem.occultic_ntm.client.render;

import java.awt.image.BufferedImage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

/** Plain black inventory puddle; atlas reloads never request an image file. */
@SideOnly(Side.CLIENT)
public final class TarTrailIcon extends TextureAtlasSprite {
    public TarTrailIcon() {
        super("occultic_ntm:generated_tar_puddle");
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                double dx = (x + .5) / 16 - .5, dz = (z + .5) / 16 - .5;
                double radius = .405 + .025 * Math.sin(dx * 17 + dz * 9)
                        + .02 * Math.sin(dx * 7 - dz * 13);
                if (dx * dx + dz * dz < radius * radius) image.setRGB(x, z, 0xFF101010);
            }
        }
        // The native mipmap generator indexes each level even when generating it.
        // A 16px sprite supports levels 0-4; leave empty slots for those derived images.
        BufferedImage[] levels = new BufferedImage[5];
        levels[0] = image;
        loadSprite(levels, null, false);
        // Forge false means this custom loader supplied the sprite for normal stitching.
        return false;
    }
}
