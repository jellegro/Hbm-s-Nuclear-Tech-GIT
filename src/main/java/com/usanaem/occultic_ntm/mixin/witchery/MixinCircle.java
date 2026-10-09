package com.usanaem.occultic_ntm.mixin.witchery;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.ritual.Circle;
import com.usanaem.occultic_ntm.compat.witchery.IRadiantCircle;
import com.usanaem.occultic_ntm.compat.witchery.RadiantChalk;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Circle.class, remap = false)
public abstract class MixinCircle implements IRadiantCircle {

    @Unique
    private boolean occultic$radiant;

    @Override
    public boolean occultic$isRadiant() {
        return this.occultic$radiant;
    }

    @Override
    public void occultic$setRadiant(boolean radiant) {
        this.occultic$radiant = radiant;
    }

    @Redirect(
        method = {
            "addGlyph(Lnet/minecraft/world/World;III)V",
            "addGlyph(Lnet/minecraft/world/World;IIIZ)V"
        },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;",
            remap = true
        ),
        require = 0,
        remap = false
    )
    private Block occultic$filterGlyph(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (RadiantChalk.glyph == null) {
            return block;
        }

        if (this.occultic$radiant) {
            // Radiant Circle:
            // 1. Radiant glyphs satisfy the required white count.
            if (block == RadiantChalk.glyph) {
                return Witchery.Blocks.GLYPH_RITUAL;
            }
            // 2. Real native white chalk must NOT satisfy the Radiant requirement.
            if (block == Witchery.Blocks.GLYPH_RITUAL) {
                return Blocks.air;
            }
            // 3. Other blocks pass through normally (e.g. Otherwhere, Infernal).
            return block;
        } else {
            // Standard Witchery Circle:
            // Radiant glyphs must NOT satisfy standard white or other chalk requirements.
            if (block == RadiantChalk.glyph) {
                return Blocks.air;
            }
            return block;
        }
    }
}
